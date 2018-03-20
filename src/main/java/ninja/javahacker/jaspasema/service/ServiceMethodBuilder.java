package ninja.javahacker.jaspasema.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.Delete;
import ninja.javahacker.jaspasema.Get;
import ninja.javahacker.jaspasema.Path;
import ninja.javahacker.jaspasema.Post;
import ninja.javahacker.jaspasema.ProducesEmpty;
import ninja.javahacker.jaspasema.Put;
import ninja.javahacker.jaspasema.ServiceName;
import ninja.javahacker.jaspasema.ext.ObjectUtils;
import ninja.javahacker.jaspasema.processor.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.HttpMethod;
import ninja.javahacker.jaspasema.processor.MalformedParameterException;
import ninja.javahacker.jaspasema.processor.ParamProcessor;
import ninja.javahacker.jaspasema.processor.ReturnProcessor;
import ninja.javahacker.jaspasema.processor.ReturnSerializer;
import spark.Route;
import spark.Service;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ServiceMethodBuilder {

    @FunctionalInterface
    private static interface RouteConfig {
        public void route(Service svc, String string, Route route);
    }

    private static final RouteConfig GET = Service::get;
    private static final RouteConfig POST = Service::post;
    private static final RouteConfig PUT = Service::put;
    private static final RouteConfig DELETE = Service::delete;

    private static final Map<Class<? extends Annotation>, RouteConfig> CONFIGS = ObjectUtils.makeMap(put -> {
        put.accept(Get.class, GET);
        put.accept(Post.class, POST);
        put.accept(Put.class, PUT);
        put.accept(Delete.class, DELETE);
    });

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
    private final List<ParamProcessor.Stub<?>> parameterProcessors;

    @Getter
    @NonNull
    private final RouteConfig routeConfig;

    @Getter
    @NonNull
    private final ReturnProcessor.Stub<Object> returnProcessor;

    @Getter
    @NonNull
    private final String callName;

    @Getter
    @NonNull
    private final ServiceBuilder service;

    @SuppressWarnings(value = "unchecked")
    public ServiceMethodBuilder(
            @NonNull ServiceBuilder service,
            @NonNull Method method)
            throws BadServiceMappingException
    {
        this.service = service;
        this.method = method;
        this.path = method.getAnnotation(Path.class).value();
        Class<? extends Annotation> annotation = null;

        for (Annotation a : method.getAnnotations()) {
            HttpMethod hm = a.annotationType().getAnnotation(HttpMethod.class);
            if (hm != null) {
                if (annotation != null) {
                    throw new BadServiceMappingException(method, "Multiple HttpMethod annotations on method.");
                }
                annotation = a.annotationType();
            }
        }

        if (annotation == null) {
            throw new BadServiceMappingException(method, "No HttpMethod annotations on method.");
        }
        this.routeConfig = CONFIGS.get(annotation);
        if (routeConfig == null) {
            throw new BadServiceMappingException(method, "Don't know how to handle @" + annotation.getSimpleName() + ".");
        }

        this.httpMethod = annotation.getSimpleName().toUpperCase(Locale.ROOT);
        ServiceName sn = method.getAnnotation(ServiceName.class);
        this.callName = sn != null ? sn.value() : method.getName();
        boolean isVoid = method.getReturnType() == void.class || method.getReturnType() == Void.class;
        Annotation produces = null;

        for (Annotation a : method.getAnnotations()) {
            if (a.annotationType().isAnnotationPresent(ReturnSerializer.class)) {
                if (isVoid) {
                    throw new BadServiceMappingException(
                            method,
                            "Methods returning void should not feature HttpMethod ReturnSerializer annotations.");
                }
                if (produces != null) {
                    throw new BadServiceMappingException(method, "Multiple HttpMethod annotations on method.");
                }
                produces = a;
            }
        }
        if (!isVoid && produces == null) {
            throw new BadServiceMappingException(method, "No ReturnSerializer annotations on method.");
        }

        if (produces == null) {
            produces = new ProducesEmpty() {
                @Override
                public String format() {
                    return "";
                }

                @Override
                public Class<? extends Annotation> annotationType() {
                    return ProducesEmpty.class;
                }
            };
        }

        this.returnProcessor = (ReturnProcessor.Stub<Object>) ReturnProcessor.forMethod(method, produces);
        this.parameterProcessors = new ArrayList<>();
        for (Parameter p : method.getParameters()) {
            parameterProcessors.add(ParamProcessor.forParameter(p));
        }
        method.setAccessible(true);
    }

    public void configure(@NonNull Service service, @NonNull Function<Route, Route> wrapper) {
        routeConfig.route(service, path, wrapper.apply(prepare()));
    }

    private Route prepare() {
        return (rq, rp) -> {
            List<Object> results = new ArrayList<>();
            try {
                for (ParamProcessor.Stub<?> ppw : parameterProcessors) {
                    results.add(ppw.getWorker().run(rq, rp));
                }
            } catch (MalformedParameterException e) {
                throw e;
            }
            try {
                return returnProcessor.getWorker().run(method.invoke(service.getInstance(), results.toArray()));
            } catch (IllegalAccessException | IllegalArgumentException ex) {
                throw new AssertionError(ex);
            } catch (InvocationTargetException e) {
                throw e;
            }
        };
    }
}
