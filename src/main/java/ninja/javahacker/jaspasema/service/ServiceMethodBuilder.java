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
import ninja.javahacker.jaspasema.ProducesEmpty;
import ninja.javahacker.jaspasema.Put;
import ninja.javahacker.jaspasema.ServiceName;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.processor.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.HttpMethod;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ReturnProcessor;
import ninja.javahacker.jaspasema.processor.ReturnSerializer;
import ninja.javahacker.jaspasema.processor.ReturnedOk;
import ninja.javahacker.jaspasema.processor.TargetType;
import spark.Route;
import spark.Service;

/**
 * @author Victor Williams Stafusa da Silva
 */
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

    @Getter
    @NonNull
    private final TargetType<T> target;

    @Getter
    @NonNull
    private final Method method;

    @Getter
    @NonNull
    private final String httpMethod;

    @Getter
    @NonNull
    private final String path;

    @Getter
    @NonNull
    private final RouteConfig routeConfig;

    @Getter
    @NonNull
    private final String serviceName;

    @Getter
    @NonNull
    private final String callName;

    @Getter
    @NonNull
    private final Object instance;

    @Getter
    @NonNull
    @Delegate(types = JaspasemaRoute.class)
    private final JaspasemaRoute call;

    @Getter
    @NonNull
    private final List<ParamProcessor.Stub<?>> parameterProcessors;

    @Getter
    @NonNull
    private final ReturnProcessor.Stub<T> returnProcessor;

    private ServiceMethodBuilder(
            @NonNull String serviceName,
            @NonNull TargetType<T> target,
            @NonNull Object instance,
            @NonNull Method method)
            throws BadServiceMappingException
    {
        this.serviceName = serviceName;
        this.target = target;
        this.instance = instance;
        this.method = method;
        this.path = method.getAnnotation(Path.class).value();
        Class<? extends Annotation> annotation = null;
        String httpMethodName = null;

        for (Annotation a : method.getAnnotations()) {
            HttpMethod hm = a.annotationType().getAnnotation(HttpMethod.class);
            if (hm == null) continue;
            if (annotation != null) {
                throw new BadServiceMappingException(method, "Multiple @HttpMethod-annotated annotations on method.");
            }
            annotation = a.annotationType();
            httpMethodName = hm.value();
            if (httpMethodName.isEmpty()) httpMethodName = annotation.getSimpleName().toUpperCase(Locale.ROOT);
        }

        if (annotation == null) {
            throw new BadServiceMappingException(method, "No @HttpMethod-annotated annotations on method.");
        }
        this.routeConfig = CONFIGS.get(annotation);
        if (routeConfig == null) {
            throw new BadServiceMappingException(method, "Don't know how to handle @" + annotation.getSimpleName() + ".");
        }

        this.httpMethod = httpMethodName;
        ServiceName sn = method.getAnnotation(ServiceName.class);
        this.callName = ObjectUtils.choose(sn == null ? "" : sn.value(), method.getName());

        boolean isVoid = method.getReturnType() == void.class || method.getReturnType() == Void.class;
        Annotation produces = null;

        for (Annotation a : method.getAnnotations()) {
            if (a.annotationType().isAnnotationPresent(ReturnSerializer.class)) {
                if (isVoid) {
                    throw new BadServiceMappingException(
                            method,
                            "Methods returning void should not feature @ReturnSerializer-annotated annotations.");
                }
                if (produces != null) {
                    throw new BadServiceMappingException(method, "Multiple @HttpMethod-annotated annotations on method.");
                }
                produces = a;
            }
        }
        if (!isVoid && produces == null) {
            throw new BadServiceMappingException(method, "No @ReturnSerializer-annotated annotations on method.");
        }

        if (produces == null) {
            produces = new ProducesEmpty() {
                @Override
                public String format() {
                    return "";
                }

                @Override
                public String type() {
                    return "text/html;charset=utf-8";
                }

                @Override
                public Class<? extends Annotation> annotationType() {
                    return ProducesEmpty.class;
                }

                @Override
                public Class<? extends Throwable> on() {
                    return ReturnedOk.class;
                }
            };
        }

        this.returnProcessor = ReturnProcessor.forMethod(target, method, produces);

        // Can't use streams here due to BadServiceMappingException that ParamProcessor.forParameter(p) might throw.
        Parameter[] params = method.getParameters();
        this.parameterProcessors = new ArrayList<>(params.length);
        for (Parameter p : params) {
            parameterProcessors.add(ParamProcessor.forParameter(p));
        }

        this.call = new ServiceMethodRunner<>(target, instance, method, parameterProcessors, returnProcessor);
    }

    private ServiceMethodBuilder(ServiceMethodBuilder<T> original, JaspasemaRoute call) {
        this.target = original.target;
        this.callName = original.callName;
        this.path = original.path;
        this.instance = original.instance;
        this.method = original.method;
        this.httpMethod = original.httpMethod;
        this.parameterProcessors = original.parameterProcessors;
        this.returnProcessor = original.returnProcessor;
        this.routeConfig = original.routeConfig;
        this.serviceName = original.serviceName;
        this.call = call;
    }

    public static ServiceMethodBuilder<?> make(
            @NonNull String serviceName,
            @NonNull Object instance,
            @NonNull Method method)
            throws BadServiceMappingException
    {
        TargetType<?> target = TargetType.forType(method.getGenericReturnType());
        return make(serviceName, target, instance, method);
    }

    public static <T> ServiceMethodBuilder<T> make(
            @NonNull String serviceName,
            @NonNull TargetType<T> target,
            @NonNull Object instance,
            @NonNull Method method)
            throws BadServiceMappingException
    {
        TargetType<?> target2 = TargetType.forType(method.getGenericReturnType());
        if (!Objects.equals(target2, target)) throw new IllegalArgumentException();
        return new ServiceMethodBuilder<>(serviceName, target, instance, method);
    }

    public ServiceMethodBuilder<T> wrap(@NonNull Function<? super JaspasemaRoute, ? extends JaspasemaRoute> wrapper) {
        return new ServiceMethodBuilder<>(this, (rq, rp) -> wrapper.apply(call).handle(rq, rp));
    }

    public void configure(@NonNull Service service) {
        routeConfig.route(service, path, call);
    }
}
