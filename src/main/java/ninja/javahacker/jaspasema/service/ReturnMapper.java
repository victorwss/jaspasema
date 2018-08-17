package ninja.javahacker.jaspasema.service;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
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
import ninja.javahacker.jaspasema.exceptions.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.ParameterValueException;
import ninja.javahacker.jaspasema.exceptions.MalformedReturnProcessorException;
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

    private static final String MORE_THAN_ONE_EXIT =
            "The annotation @$XXX$ should not have more than one @ExitDiscriminator method.";

    private static final String NO_EXIT =
            "The annotation @$XXX$ do not have an @ExitDiscriminator method.";

    private static final String BAD_EXIT =
            "The annotation @$XXX$ have an ill-formed @ExitDiscriminator method.";

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

    private Class<? extends Throwable> getOn(Annotation a) throws MalformedReturnProcessorException {
        Class<? extends Annotation> c = a.annotationType();
        Class<? extends Throwable> disc = null;
        for (Method m : c.getMethods()) {
            if (!m.isAnnotationPresent(ReturnSerializer.ExitDiscriminator.class)) continue;
            if (disc != null) {
                throw new MalformedReturnProcessorException(
                        c,
                        MORE_THAN_ONE_EXIT.replace("$XXX$", a.annotationType().getSimpleName()));
            }
            String bad = BAD_EXIT.replace("$XXX$", c.getSimpleName());
            Type t = m.getGenericReturnType();
            if (!(t instanceof ParameterizedType)) throw new MalformedReturnProcessorException(c, bad);
            ParameterizedType p = (ParameterizedType) t;
            if (p.getRawType() != Class.class) throw new MalformedReturnProcessorException(c, bad);
            Type[] f = p.getActualTypeArguments();
            if (f.length != 1) throw new MalformedReturnProcessorException(c, bad);
            Type pp = f[0];
            if (!(pp instanceof WildcardType)) throw new MalformedReturnProcessorException(c, bad);
            WildcardType w = (WildcardType) pp;
            if (w.getLowerBounds().length != 0 || w.getUpperBounds().length != 1 || w.getUpperBounds()[0] != Throwable.class) {
                throw new MalformedReturnProcessorException(c, bad);
            }
            m.setAccessible(true);
            try {
                disc = ((Class<?>) m.invoke(a)).asSubclass(Throwable.class);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new AssertionError(e);
            }
        }
        if (disc == null) {
            throw new MalformedReturnProcessorException(
                    c,
                    NO_EXIT.replace("$XXX$", a.annotationType().getSimpleName()));
        }
        return disc;
    }

    private void select(Annotation[] ans, Consumer<Annotation> c) throws MalformedReturnProcessorException {
        for (Annotation a : ans) {
            select(a, c);
        }
    }

    private void select(Annotation a, Consumer<Annotation> c) throws MalformedReturnProcessorException {
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

    @Getter
    public static class ConflictingAnnotationsReturnException extends BadServiceMappingException {
        private static final long serialVersionUID = 1L;

        private static final String MESSAGE_TEMPLATE =
                "Conflicting @ReturnSerializer-annotated annotations on method for return type.";

        protected ConflictingAnnotationsReturnException(/*@NonNull*/ Class<?> targetClass) {
            super(targetClass, MESSAGE_TEMPLATE);
        }

        protected ConflictingAnnotationsReturnException(/*@NonNull*/ Method method) {
            super(method, MESSAGE_TEMPLATE);
        }

        public static ConflictingAnnotationsReturnException create(@NonNull Class<?> targetClass) {
            return new ConflictingAnnotationsReturnException(targetClass);
        }

        public static ConflictingAnnotationsReturnException create(@NonNull Method method) {
            return new ConflictingAnnotationsReturnException(method);
        }
    }

    @Getter
    public static class ConflictingAnnotationsThrowsException extends BadServiceMappingException {
        private static final long serialVersionUID = 1L;

        private static final String MESSAGE_TEMPLATE =
                "Conflicting @ReturnSerializer-annotated annotations on method for exception $X$.";

        @NonNull
        private final Class<? extends Throwable> type;

        protected ConflictingAnnotationsThrowsException(
                /*@NonNull*/ Class<?> targetClass,
                /*@NonNull*/ Class<? extends Throwable> type)
        {
            super(targetClass, MESSAGE_TEMPLATE.replace("$X$", type.getSimpleName()));
            this.type = type;
        }

        protected ConflictingAnnotationsThrowsException(
                /*@NonNull*/ Method method,
                /*@NonNull*/ Class<? extends Throwable> type)
        {
            super(method, MESSAGE_TEMPLATE.replace("$X$", type.getSimpleName()));
            this.type = type;
        }

        public static ConflictingAnnotationsThrowsException create(
                @NonNull Class<?> targetClass,
                @NonNull Class<? extends Throwable> type)
        {
            return new ConflictingAnnotationsThrowsException(targetClass, type);
        }

        public static ConflictingAnnotationsThrowsException create(
                @NonNull Method method,
                @NonNull Class<? extends Throwable> type)
        {
            return new ConflictingAnnotationsThrowsException(method, type);
        }
    }
}
