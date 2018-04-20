package ninja.javahacker.jaspasema.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import ninja.javahacker.jaspasema.Delete;
import ninja.javahacker.jaspasema.Get;
import ninja.javahacker.jaspasema.Patch;
import ninja.javahacker.jaspasema.Path;
import ninja.javahacker.jaspasema.Post;
import ninja.javahacker.jaspasema.Put;
import ninja.javahacker.jaspasema.ServiceName;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.processor.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.HttpMethod;
import ninja.javahacker.jaspasema.processor.MalformedParameterProcessorException;
import ninja.javahacker.jaspasema.processor.MalformedReturnProcessorException;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.reifiedgeneric.ReifiedGeneric;
import spark.Route;
import spark.Service;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public class ServiceMethodBuilder<T> implements JaspasemaRoute {

    @FunctionalInterface
    private static interface RouteConfig {
        public void route(Service svc, String string, Route route);
    }

    private static final RouteConfig GET = Service::get;
    private static final RouteConfig POST = Service::post;
    private static final RouteConfig PUT = Service::put;
    private static final RouteConfig DELETE = Service::delete;
    private static final RouteConfig PATCH = Service::patch;

    private static final Map<Class<? extends Annotation>, RouteConfig> CONFIGS =
            Map.of(Get.class, GET, Post.class, POST, Put.class, PUT, Delete.class, DELETE, Patch.class, PATCH);

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
    private final ReturnMapper.ReturnMap<T> returnMapper;

    private ServiceMethodBuilder(
            @NonNull String serviceName,
            @NonNull ReifiedGeneric<T> target,
            @NonNull Object instance,
            @NonNull Method method)
            throws BadServiceMappingException,
            MalformedReturnProcessorException,
            MalformedParameterProcessorException
    {
        this.serviceName = serviceName;
        this.target = target;
        this.instance = instance;
        this.method = method;
        this.path = method.getAnnotation(Path.class).value();
        Class<? extends Annotation> annotation = null;

        for (Annotation a : method.getAnnotations()) {
            if (!a.annotationType().isAnnotationPresent(HttpMethod.class)) continue;
            if (annotation != null) {
                throw new BadServiceMappingException(method, "Multiple @HttpMethod-annotated annotations on method.");
            }
            annotation = a.annotationType();
        }

        if (annotation == null) {
            throw new BadServiceMappingException(method, "No @HttpMethod-annotated annotations on method.");
        }
        this.routeConfig = CONFIGS.get(annotation);
        if (routeConfig == null) {
            throw new BadServiceMappingException(method, "Don't know how to handle @" + annotation.getSimpleName() + ".");
        }

        HttpMethod hm = annotation.getAnnotation(HttpMethod.class);
        String httpMethodName = hm.value();
        if (httpMethodName.isEmpty()) httpMethodName = annotation.getSimpleName().toUpperCase(Locale.ROOT);
        this.httpMethod = httpMethodName;
        ServiceName sn = method.getAnnotation(ServiceName.class);
        this.callName = ObjectUtils.choose(sn == null ? "" : sn.value(), method.getName());

        this.returnMapper = ReturnMapper.forMethod(target, method);

        // Can't use streams here due to BadServiceMappingException that ParamProcessor.forParameter(p) might throw.
        Parameter[] params = method.getParameters();
        this.parameterProcessors = new ArrayList<>(params.length);
        for (Parameter p : params) {
            parameterProcessors.add(ParamProcessor.forParameter(p));
        }

        this.call = new ServiceMethodRunner<>(target, instance, method, parameterProcessors, returnMapper);
    }

    private ServiceMethodBuilder(ServiceMethodBuilder<T> original, JaspasemaRoute call) {
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

    public static ServiceMethodBuilder<?> make(
            @NonNull String serviceName,
            @NonNull Object instance,
            @NonNull Method method)
            throws BadServiceMappingException,
            MalformedReturnProcessorException,
            MalformedParameterProcessorException
    {
        ReifiedGeneric<?> target = ReifiedGeneric.forType(method.getGenericReturnType());
        return make(serviceName, target, instance, method);
    }

    public static <T> ServiceMethodBuilder<T> make(
            @NonNull String serviceName,
            @NonNull ReifiedGeneric<T> target,
            @NonNull Object instance,
            @NonNull Method method)
            throws BadServiceMappingException,
            MalformedReturnProcessorException,
            MalformedParameterProcessorException
    {
        ReifiedGeneric<?> target2 = ReifiedGeneric.forType(method.getGenericReturnType());
        if (!Objects.equals(target2, target)) throw new IllegalArgumentException();
        return new ServiceMethodBuilder<>(serviceName, target, instance, method);
    }

    public ServiceMethodBuilder<T> wrap(@NonNull Function<? super JaspasemaRoute, ? extends JaspasemaRoute> wrapper) {
        return new ServiceMethodBuilder<>(this, (rq, rp) -> wrapper.apply(call).handleIt(rq, rp));
    }

    public void configure(@NonNull Service service) {
        routeConfig.route(service, path, (rq, rp) -> {
            call.handleIt(rq, rp);
            return rp.body();
        });
    }
}
