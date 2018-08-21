package ninja.javahacker.jaspasema.service;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.ProducesFixed;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ConflictingAnnotationsReturnException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ConflictingAnnotationsThrowsException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.ParameterValueException;
import ninja.javahacker.jaspasema.exceptions.retproc.BadExitDiscriminatorMethodException;
import ninja.javahacker.jaspasema.exceptions.retproc.MalformedReturnProcessorException;
import ninja.javahacker.jaspasema.exceptions.retproc.MultipleReturnProcessorsException;
import ninja.javahacker.jaspasema.exceptions.retproc.ReturnProcessorNotFoundException;
import ninja.javahacker.jaspasema.processor.ReturnProcessor;
import ninja.javahacker.jaspasema.processor.ReturnSerializer;
import ninja.javahacker.jaspasema.processor.ReturnedOk;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ReturnMapper {

    public static final String DEFAULT_HTML_200 = ""
            + "<!DOCTYPE html>"
            + "<html>"
            + "  <head>"
            + "    <title>OK 200</title>"
            + "  </head>"
            + "  <body>"
            + "    <p>Whoa. It works. :)</p>"
            + "  </body>"
            + "</html>";

    public static final String DEFAULT_HTML_ERROR_500 = ""
            + "<!DOCTYPE html>"
            + "<html>"
            + "  <head>"
            + "    <title>ERROR 500</title>"
            + "  </head>"
            + "  <body>"
            + "    <p>An unexpected error happened. Sorry.</p>"
            + "  </body>"
            + "</html>";

    public static final String DEFAULT_HTML_ERROR_400 = ""
            + "<!DOCTYPE html>"
            + "<html>"
            + "  <head>"
            + "    <title>ERROR 400</title>"
            + "  </head>"
            + "  <body>"
            + "    <p>Duh! You sent a bad request.</p>"
            + "  </body>"
            + "</html>";

    private static final ReifiedGeneric<Class<? extends Throwable>> TYPE =
            new ReifiedGeneric<Class<? extends Throwable>>() {};

    @ProducesFixed(DEFAULT_HTML_200)
    @ProducesFixed(on = Throwable.class, value = DEFAULT_HTML_ERROR_500, status = 500)
    @ProducesFixed(on = ParameterValueException.class, value = DEFAULT_HTML_ERROR_400, status = 400)
    private static void dummy() {}

    private static final ReturnMapper ROOT;

    static {
        try {
            Annotation[] base = ReturnMapper.class.getDeclaredMethod("dummy").getAnnotations();
            ROOT = new ReturnMapper(Optional.empty(), base, () -> null, t -> null);
        } catch (NoSuchMethodException | BadServiceMappingException | MalformedReturnProcessorException e) {
            throw new AssertionError(e);
        }
    }

    @Getter(AccessLevel.PRIVATE)
    private final ReturnProcessor.ProcessorConfiguration returnConfig;

    private final Map<Class<? extends Throwable>, ReturnProcessor.ProcessorConfiguration> exceptionsConfig;

    private ReturnMapper(
            @NonNull Optional<ReturnMapper> parent,
            @NonNull Annotation[] anotations,
            @NonNull Supplier<BadServiceMappingException> confictingAnnotationsErrorThrow,
            @NonNull Function<Class<? extends Throwable>, ? extends BadServiceMappingException> errorThrow)
            throws BadServiceMappingException,
            MalformedReturnProcessorException
    {
        List<Annotation> appliedAnnotations = new ArrayList<>(10);
        select(anotations, appliedAnnotations::add);

        Optional<ReturnProcessor.ProcessorConfiguration> rt = Optional.empty();
        Map<Class<? extends Throwable>, ReturnProcessor.ProcessorConfiguration> sketch
                = new HashMap<>(appliedAnnotations.size());
        for (Annotation a : appliedAnnotations) {
            Class<? extends Throwable> ct = getOn(a);
            if (ct == ReturnedOk.class) {
                if (rt.isPresent()) throw confictingAnnotationsErrorThrow.get();
                rt = Optional.of(ReturnProcessor.prepareConfig(a));
            } else if (sketch.containsKey(ct)) {
                throw errorThrow.apply(ct);
            } else {
                sketch.put(ct, ReturnProcessor.prepareConfig(a));
            }
        }

        returnConfig = rt.or(() -> parent.map(ReturnMapper::getReturnConfig)).orElseThrow(AssertionError::new);
        exceptionsConfig = new HashMap<>(20);
        parent.ifPresent(p -> exceptionsConfig.putAll(p.exceptionsConfig));
        exceptionsConfig.putAll(sketch);
    }

    public static ReturnMapper forClass(
            @NonNull Class<?> targetClass)
            throws BadServiceMappingException,
            MalformedReturnProcessorException
    {
        return new ReturnMapper(
                Optional.of(ROOT),
                targetClass.getAnnotations(),
                () -> ConflictingAnnotationsReturnException.create(targetClass),
                t -> ConflictingAnnotationsThrowsException.create(targetClass, t));
    }

    public static ReturnMap<?> forMethod(
            @NonNull Method method)
            throws BadServiceMappingException,
            MalformedReturnProcessorException
    {
        return new ReturnMapper(
                Optional.of(forClass(method.getDeclaringClass())),
                method.getAnnotations(),
                () -> ConflictingAnnotationsReturnException.create(method),
                t -> ConflictingAnnotationsThrowsException.create(method, t)
        ).makeMap(method);
    }

    public static <E> ReturnMap<E> forMethod(
            @NonNull ReifiedGeneric<E> target,
            @NonNull Method method)
            throws BadServiceMappingException,
            MalformedReturnProcessorException
    {
        return new ReturnMapper(
                Optional.of(forClass(method.getDeclaringClass())),
                method.getAnnotations(),
                () -> ConflictingAnnotationsReturnException.create(method),
                t -> ConflictingAnnotationsThrowsException.create(method, t)
        ).makeMap(target, method);
    }

    private static Class<? extends Throwable> getOn(Annotation a) throws MalformedReturnProcessorException {
        Class<? extends Annotation> c = a.annotationType();
        Class<? extends Throwable> discriminator = null;
        for (Method m : c.getMethods()) {
            if (!m.isAnnotationPresent(ReturnSerializer.ExitDiscriminator.class)) continue;
            if (discriminator != null) throw MultipleReturnProcessorsException.create(c);
            if (!TYPE.equals(ReifiedGeneric.forType(m.getGenericReturnType()))) throw BadExitDiscriminatorMethodException.create(c);
            m.setAccessible(true);
            try {
                discriminator = ((Class<?>) m.invoke(a)).asSubclass(Throwable.class);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new AssertionError(e);
            }
        }
        if (discriminator == null) throw ReturnProcessorNotFoundException.create(c);
        return discriminator;
    }

    private static void select(Annotation[] ans, Consumer<Annotation> c) throws MalformedReturnProcessorException {
        for (Annotation a : ans) {
            select(a, c);
        }
    }

    private static void select(Annotation a, Consumer<Annotation> c) throws MalformedReturnProcessorException {
        if (a.annotationType().isAnnotationPresent(ReturnSerializer.class)) {
            getOn(a); // For sanity check.
            c.accept(a);
        } else {
            Method m;
            try {
                m = a.getClass().getMethod("value");
            } catch (NoSuchMethodException e) {
                return;
            }

            m.setAccessible(true);
            Class<?> returnType = m.getReturnType();
            if (!returnType.isArray()) return;
            Class<?> component = returnType.getComponentType();
            if (!component.isAnnotation() || !component.isAnnotationPresent(Repeatable.class)) return;

            Annotation[] ar;
            try {
                ar = (Annotation[]) m.invoke(a);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new AssertionError(e);
            }
            select(ar, c);
        }
    }

    public <E> ReturnMap<E> makeMap(
            @NonNull ReifiedGeneric<E> target,
            @NonNull Method method)
            throws BadServiceMappingException
    {
        return new ReturnMap<>(target, method);
    }

    public ReturnMap<?> makeMap(
            @NonNull Method method)
            throws BadServiceMappingException
    {
        return new ReturnMap<>(ReifiedGeneric.forType(method.getGenericReturnType()), method);
    }

    public class ReturnMap<E> {
        private final Map<Class<? extends Throwable>, ReturnProcessor.Stub<? extends Throwable>> map;

        private final ReturnProcessor.Stub<E> forReturn;

        public ReturnMap(
                @NonNull ReifiedGeneric<E> target,
                @NonNull Method method)
                throws BadServiceMappingException
        {
            this.map = new HashMap<>(exceptionsConfig.size());
            for (Map.Entry<Class<? extends Throwable>, ReturnProcessor.ProcessorConfiguration> entry : exceptionsConfig.entrySet()) {
                if (entry.getKey() == ReturnedOk.class) continue;
                map.put(entry.getKey(), entry.getValue().config(ReifiedGeneric.forClass(entry.getKey()), method));
            }
            this.forReturn = returnConfig == null ? null : returnConfig.config(target, method);
        }

        public ReturnProcessor.Stub<E> onReturn() {
            return forReturn;
        }

        @SuppressWarnings("unchecked")
        public <X extends Throwable> ReturnProcessor.Stub<X> onException(X what) {
            for (Class<?> c = what.getClass();
                    c != Object.class;
                    c = c.getSuperclass().asSubclass(Throwable.class))
            {
                ReturnProcessor.Stub<? extends Throwable> p = map.get(c.asSubclass(Throwable.class));
                if (p != null) return (ReturnProcessor.Stub<X>) p;
            }
            throw new AssertionError();
        }
    }
}
