package ninja.javahacker.jaspasema.service;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import ninja.javahacker.jaspasema.Path;
import ninja.javahacker.jaspasema.ServiceName;
import ninja.javahacker.jaspasema.exceptions.MalformedProcessorException;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.exceptions.badmapping.DontKnowHowToHandleAnnotationException;
import ninja.javahacker.jaspasema.exceptions.badmapping.MissingPathException;
import ninja.javahacker.jaspasema.exceptions.badmapping.MultipleHttpMethodAnnotationsException;
import ninja.javahacker.jaspasema.exceptions.badmapping.NoHttpMethodAnnotationsException;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.processor.HttpMethod;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.verbs.Delete;
import ninja.javahacker.jaspasema.verbs.Get;
import ninja.javahacker.jaspasema.verbs.Head;
import ninja.javahacker.jaspasema.verbs.Options;
import ninja.javahacker.jaspasema.verbs.Patch;
import ninja.javahacker.jaspasema.verbs.Post;
import ninja.javahacker.jaspasema.verbs.Put;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public final class ServiceMethodBuilder<T> implements JaspasemaRoute {

    @FunctionalInterface
    private static interface RouteConfig {
        public void route(Javalin svc, String path, Handler route);
    }

    private static final RouteConfig HEAD = Javalin::head;
    private static final RouteConfig GET = Javalin::get;
    private static final RouteConfig POST = Javalin::post;
    private static final RouteConfig PUT = Javalin::put;
    private static final RouteConfig DELETE = Javalin::delete;
    private static final RouteConfig PATCH = Javalin::patch;
    private static final RouteConfig OPTIONS = Javalin::options;

    private static final Map<Class<? extends Annotation>, RouteConfig> CONFIGS = Map.of(
            Head.class, HEAD,
            Get.class, GET,
            Post.class, POST,
            Put.class, PUT,
            Delete.class, DELETE,
            Patch.class, PATCH,
            Options.class, OPTIONS
    );

    @NonNull
    private final ReifiedGeneric<T> target;

    @NonNull
    private final Method method;

    @NonNull
    private final String httpMethod;

    @NonNull
    private final String path;

    @NonNull
    private final RouteConfig routeConfig;

    @NonNull
    private final String serviceName;

    @NonNull
    private final String callName;

    @NonNull
    private final Object instance;

    @NonNull
    @Delegate(types = JaspasemaRoute.class)
    private final JaspasemaRoute call;

    @NonNull
    private final List<ParamProcessor.Stub<?>> parameterProcessors;

    @NonNull
    private final ReturnMapper<T> returnMapper;

    private ServiceMethodBuilder(
            @NonNull String serviceName,
            @NonNull ReifiedGeneric<T> target,
            @NonNull Object instance,
            @NonNull Method method)
            throws BadServiceMappingException,
            MalformedProcessorException
    {
        this.serviceName = serviceName;
        this.target = target;
        this.instance = instance;
        this.method = method;
        var pt = method.getAnnotation(Path.class);
        if (pt == null) throw new MissingPathException(method);
        this.path = pt.value();
        Class<? extends Annotation> annotation = null;

        for (var a : method.getAnnotations()) {
            if (!a.annotationType().isAnnotationPresent(HttpMethod.class)) continue;
            if (annotation != null) throw new MultipleHttpMethodAnnotationsException(method);
            annotation = a.annotationType();
        }

        if (annotation == null) throw new NoHttpMethodAnnotationsException(method);
        this.routeConfig = CONFIGS.get(annotation);
        if (routeConfig == null) throw new DontKnowHowToHandleAnnotationException(method, annotation);

        var hm = annotation.getAnnotation(HttpMethod.class);
        var httpMethodName = hm.value();
        if (httpMethodName.isEmpty()) httpMethodName = annotation.getSimpleName().toUpperCase(Locale.ROOT);
        this.httpMethod = httpMethodName;
        var sn = method.getAnnotation(ServiceName.class);
        this.callName = ObjectUtils.choose(sn == null ? "" : sn.value(), method.getName());

        this.returnMapper = ReturnMapper.forMethod(target, method);

        // Can't use streams here due to BadServiceMappingException that ParamProcessor.forParameter(p) might throw.
        var params = method.getParameters();
        this.parameterProcessors = new ArrayList<>(params.length);
        for (var p : params) {
            parameterProcessors.add(ParamProcessor.forParameter(p));
        }

        this.call = new ServiceMethodRunner<>(target, instance, method, parameterProcessors, returnMapper);
    }

    private ServiceMethodBuilder(@NonNull ServiceMethodBuilder<T> original, @NonNull JaspasemaRoute call) {
        this.target = original.target;
        this.callName = original.callName;
        this.path = original.path;
        this.instance = original.instance;
        this.method = original.method;
        this.httpMethod = original.httpMethod;
        this.parameterProcessors = original.parameterProcessors;
        this.returnMapper = original.returnMapper;
        this.routeConfig = original.routeConfig;
        this.serviceName = original.serviceName;
        this.call = call;
    }

    @NonNull
    public static ServiceMethodBuilder<?> make(
            @NonNull String serviceName,
            @NonNull Object instance,
            @NonNull Method method)
            throws BadServiceMappingException,
            MalformedProcessorException
    {
        ReifiedGeneric<?> target = ReifiedGeneric.of(method.getGenericReturnType());
        return make(serviceName, target, instance, method);
    }

    @NonNull
    public static <T> ServiceMethodBuilder<T> make(
            @NonNull String serviceName,
            @NonNull ReifiedGeneric<T> target,
            @NonNull Object instance,
            @NonNull Method method)
            throws BadServiceMappingException,
            MalformedProcessorException
    {
        ReifiedGeneric<?> target2 = ReifiedGeneric.of(method.getGenericReturnType());
        if (!Objects.equals(target2, target)) throw new IllegalArgumentException();
        return new ServiceMethodBuilder<>(serviceName, target, instance, method);
    }

    @NonNull
    public ServiceMethodBuilder<T> wrap(@NonNull Function<? super JaspasemaRoute, ? extends JaspasemaRoute> wrapper) {
        return new ServiceMethodBuilder<>(this, ctx -> wrapper.apply(call).handle(ctx));
    }

    public void configure(@NonNull Javalin service) {
        routeConfig.route(service, path, ctx -> {
            call.handle(ctx);
            ctx.body();
        });
    }
}
