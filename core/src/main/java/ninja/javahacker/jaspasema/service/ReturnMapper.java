package ninja.javahacker.jaspasema.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
import lombok.NonNull;
import ninja.javahacker.jaspasema.ProducesFixed;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ConflictingAnnotationsReturnException;
import ninja.javahacker.jaspasema.exceptions.badmapping.ConflictingAnnotationsThrowsException;
import ninja.javahacker.jaspasema.exceptions.paramvalue.ParameterValueException;
import ninja.javahacker.jaspasema.exceptions.retproc.MalformedReturnProcessorException;
import ninja.javahacker.jaspasema.processor.AnnotatedMethod;
import ninja.javahacker.jaspasema.processor.ResultProcessor;
import ninja.javahacker.jaspasema.processor.ResultSerializer;
import ninja.javahacker.jaspasema.processor.ReturnedOk;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;
import ninja.javahacker.reifiedgeneric.Token;

/**
 * @author Victor Williams Stafusa da Silva
 */
@SuppressFBWarnings("IMC_IMMATURE_CLASS_NO_TOSTRING")
public class ReturnMapper<E> {

    /**
     * Default HTML output when an error 200 is produced.
     */
    public static final String DEFAULT_HTML_200 =
            """
            <!DOCTYPE html>
            <html>
              <head>
                <title>OK 200</title>
              </head>
              <body>
                <p>Whoa. It works. :)</p>
              </body>
            </html>
            """;

    /**
     * Default HTML output when an error 500 is produced.
     */
    public static final String DEFAULT_HTML_ERROR_500 =
            """
            <!DOCTYPE html>
            <html>
              <head>
                <title>ERROR 500</title>
              </head>
              <body>
                <p>An unexpected error happened. Sorry.</p>
              </body>
            </html>
            """;

    /**
     * Default HTML output when an error 400 is produced.
     */
    public static final String DEFAULT_HTML_ERROR_400 =
            """
            <!DOCTYPE html>
            <html>
              <head>
                <title>ERROR 400</title>
              </head>
              <body>
                <p>Duh! You sent a bad request.</p>
              </body>
            </html>
            """;

    private static final ReifiedGeneric<Class<? extends Throwable>> TYPE =
            new Token<Class<? extends Throwable>>() {}.getReified();

    @ProducesFixed(DEFAULT_HTML_200)
    @ProducesFixed(on = Throwable.class, value = DEFAULT_HTML_ERROR_500, status = 500)
    @ProducesFixed(on = ParameterValueException.class, value = DEFAULT_HTML_ERROR_400, status = 400)
    @SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
    private static void dummy() {}

    private static final Method DUMMY_METHOD;
    private static final ReturnMapper<Object> ROOT;

    static {
        Supplier<BadServiceMappingException> xxx1 = () -> {
            throw new AssertionError();
        };
        Function<Class<? extends Throwable>, ? extends BadServiceMappingException> xxx2 = c -> {
            throw new AssertionError(c);
        };
        try {
            DUMMY_METHOD = ReturnMapper.class.getDeclaredMethod("dummy");
            Annotation[] base = DUMMY_METHOD.getAnnotations();
            var t = mapIt(
                    DUMMY_METHOD,
                    ReifiedGeneric.of(Object.class),
                    MalformedReturnProcessorException.onMethod(DUMMY_METHOD),
                    Optional.empty(),
                    base,
                    xxx1,
                    xxx2);
            ROOT = t;
        } catch (NoSuchMethodException | BadServiceMappingException | MalformedReturnProcessorException e) {
            throw new AssertionError(e);
        }
    }

    @NonNull
    private final Map<Class<? extends Throwable>, ResultProcessor.Stub<? extends Throwable>> map;

    @NonNull
    private final ResultProcessor.Stub<E> forReturn;

    @NonNull
    private static ReturnMapper<Object> forClass(
            @NonNull Class<?> targetClass)
            throws BadServiceMappingException,
            MalformedReturnProcessorException
    {
        return mapIt(
                DUMMY_METHOD,
                ReifiedGeneric.of(Object.class),
                MalformedReturnProcessorException.onClass(targetClass),
                Optional.of(ROOT),
                targetClass.getAnnotations(),
                () -> new ConflictingAnnotationsReturnException(targetClass),
                t -> new ConflictingAnnotationsThrowsException(targetClass, t));
    }

    @NonNull
    public static <E> ReturnMapper<E> forMethod(
            @NonNull ReifiedGeneric<E> target,
            @NonNull Method method)
            throws BadServiceMappingException,
            MalformedReturnProcessorException
    {
        return mapIt(
                method,
                target,
                MalformedReturnProcessorException.onMethod(method),
                Optional.of(forClass(method.getDeclaringClass())),
                method.getAnnotations(),
                () -> new ConflictingAnnotationsReturnException(method),
                t -> new ConflictingAnnotationsThrowsException(method, t));
    }

    @NonNull
    private static Class<? extends Throwable> getOn(
            Annotation a,
            MalformedReturnProcessorException.Factory x)
            throws MalformedReturnProcessorException
    {
        var c = a.annotationType();
        var r = c.getAnnotation(ResultSerializer.class);
        if (r == null) throw new AssertionError();
        var cc = r.processor();
        Class<? extends Throwable> discriminator = null;
        for (Method m : c.getMethods()) {
            if (!m.isAnnotationPresent(ResultSerializer.ExitDiscriminator.class)) continue;
            if (discriminator != null) throw x.multiple(c, cc);
            if (!TYPE.equals(ReifiedGeneric.of(m.getGenericReturnType()))) throw x.badExit(c, cc);
            try {
                discriminator = ((Class<?>) m.invoke(a)).asSubclass(Throwable.class);
            } catch (InvocationTargetException | IllegalAccessException | ClassCastException e) {
                throw new AssertionError(e);
            }
        }
        if (discriminator == null) throw x.notFound(c, cc);
        return discriminator;
    }

    private static void select(
            Annotation[] ans,
            Consumer<Annotation> c,
            MalformedReturnProcessorException.Factory x)
            throws MalformedReturnProcessorException
    {
        for (Annotation a : ans) {
            select(a, c, x);
        }
    }

    private static void select(
            Annotation a,
            Consumer<Annotation> c,
            MalformedReturnProcessorException.Factory x)
            throws MalformedReturnProcessorException
    {
        if (a.annotationType().isAnnotationPresent(ResultSerializer.class)) {
            getOn(a, x); // For sanity check.
            c.accept(a);
        } else {
            Method m;
            try {
                m = a.getClass().getMethod("value");
            } catch (NoSuchMethodException e) {
                return;
            }

            var returnType = m.getReturnType();
            if (!returnType.isArray()) return;
            var component = returnType.getComponentType();
            if (!component.isAnnotation() || !component.isAnnotationPresent(Repeatable.class)) return;

            Annotation[] ar;
            try {
                ar = (Annotation[]) m.invoke(a);
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                throw new AssertionError(e);
            }
            select(ar, c, x);
        }
    }

    private ReturnMapper(
            @NonNull Map<Class<? extends Throwable>, ResultProcessor.Stub<? extends Throwable>> exceptionsConfig,
            @NonNull ResultProcessor.Stub<E> forReturn)
    {
        this.map = new HashMap<>(exceptionsConfig.size());
        this.forReturn = forReturn;
    }

    @NonNull
    private static <E> ReturnMapper<E> of(
            @NonNull Map<Class<? extends Throwable>, ResultProcessor.Stub<? extends Throwable>> exceptionsConfig,
            @NonNull ResultProcessor.Stub<E> forReturn)
    {
        return new ReturnMapper<>(exceptionsConfig, forReturn);
    }

    @NonNull
    @SuppressFBWarnings("OI_OPTIONAL_ISSUES_CHECKING_REFERENCE")
    private static <E> ReturnMapper<E> mapIt(
            @NonNull Method m,
            @NonNull ReifiedGeneric<E> target,
            @NonNull MalformedReturnProcessorException.Factory x,
            @NonNull Optional<ReturnMapper<Object>> parent,
            @NonNull Annotation[] annotations,
            @NonNull Supplier<BadServiceMappingException> conflictingAnnotationsErrorThrow,
            @NonNull Function<Class<? extends Throwable>, ? extends BadServiceMappingException> errorThrow)
            throws BadServiceMappingException,
            MalformedReturnProcessorException
    {
        List<Annotation> appliedAnnotations = new ArrayList<>(10);
        select(annotations, appliedAnnotations::add, x);

        Optional<ResultProcessor.Stub<E>> rt = Optional.empty();
        Map<Class<? extends Throwable>, ResultProcessor.Stub<? extends Throwable>> sketch
                = new HashMap<>(appliedAnnotations.size());
        for (var a : appliedAnnotations) {
            var ct = getOn(a, x);
            if (sketch.containsKey(ct)) throw errorThrow.apply(ct);
            if (ct == ReturnedOk.class) {
                if (rt.isPresent()) throw conflictingAnnotationsErrorThrow.get();
                var ar = AnnotatedMethod.of(target, a, m);
                rt = Optional.of(ResultProcessor.prepareConfig(ar, x));
            } else {
                var ax = AnnotatedMethod.ofException(ReifiedGeneric.of(ct), a, m);
                sketch.put(ct, ResultProcessor.prepareConfig(ax, x));
            }
        }
        var def = parent.map(p -> {
            Map<Class<? extends Throwable>, ResultProcessor.Stub<? extends Throwable>> n = new HashMap<>(p.map);
            n.putAll(sketch);
            return n;
        }).orElse(sketch);

        var oo = rt.orElseGet(() -> parent.map(ReturnMapper::onReturn).map(g -> reduce(target, g)).orElseThrow(AssertionError::new));
        return of(def, oo);
    }

    @NonNull
    public ResultProcessor.Stub<E> onReturn() {
        return forReturn;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public <X extends Throwable> ResultProcessor.Stub<X> onException(X what) {
        Class<?> c = what.getClass();
        while (true) {
            var p = map.get(c.asSubclass(Throwable.class));
            if (p != null) return (ResultProcessor.Stub<X>) p;
            if (c == Throwable.class) throw new AssertionError();
            c = c.getSuperclass().asSubclass(Throwable.class);
        }
    }

    @SuppressFBWarnings("UP_UNUSED_PARAMETER")
    private static <E> ResultProcessor.Stub<E> reduce(ReifiedGeneric<E> newTarget, ResultProcessor.Stub<Object> oldTarget) {
        return new ResultProcessor.Stub<>(oldTarget.getWorker()::run, oldTarget.getExpectedReturnType());
    }
}
