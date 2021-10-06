package ninja.javahacker.jaspasema.service;

import io.javalin.Javalin;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.Path;
import ninja.javahacker.jaspasema.ServiceName;
import ninja.javahacker.jaspasema.exceptions.MalformedProcessorException;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Getter
public final class ServiceBuilder {

    /**
     * The object (of a class with a lot of methods to expose as endpoints) to serve as the
     *     implementation of the service.
     * -- GETTER --
     * Retrieves the object (of a class with a lot of methods to expose as endpoints) to serve as the
     *     implementation of the service.
     * @return The object (of a class with a lot of methods to expose as endpoints) to serve as the
     *     implementation of the service.
     */
    @NonNull
    private final Object instance;

    /**
     * The name of the exposed service.
     * -- GETTER --
     * Retrieves the name of the exposed service.
     * @return The name of the exposed service.
     */
    @NonNull
    private final String serviceName;

    @NonNull
    private final List<ServiceMethodBuilder<?>> methods;

    private ServiceBuilder(
            @NonNull Object instance,
            @NonNull String serviceName,
            @NonNull List<ServiceMethodBuilder<?>> methods)
    {
        this.instance = instance;
        this.serviceName = serviceName;
        this.methods = methods;
    }

    /**
     * Creates a service to expose for HTTP verbs.
     * @param instance An object (of a class with a lot of methods to expose as endpoints) to serve as the
     *     implementation of the service.
     * @return A builder object to further configurations of the service.
     * @throws BadServiceMappingException If there is some problem to describe some method as an HTTP endpoint.
     * @throws MalformedProcessorException If some method uses some ill-defined processor.
     */
    public static ServiceBuilder make(@NonNull Object instance) throws BadServiceMappingException, MalformedProcessorException {
        var className = instance.getClass().getSimpleName();
        if (className.isEmpty()) {
            className = instance.getClass().getName().replace('.', '_');
        }
        ServiceName sn = null;
        for (Class<?> c = instance.getClass(); c != null; c = c.getSuperclass()) {
            sn = c.getAnnotation(ServiceName.class);
            if (sn != null) break;
        }
        var name = sn == null ? "" : sn.value();
        var serviceName = !name.isEmpty() ? name : className.substring(0, 1).toLowerCase(Locale.ROOT) + className.substring(1);
        List<ServiceMethodBuilder<?>> methods = new ArrayList<>();
        for (Class<?> c = instance.getClass(); c != null; c = c.getSuperclass()) {
            var mm = Arrays.asList(c.getDeclaredMethods());
            mm.sort(ServiceBuilder::compareMethods);
            for (var m : mm) {
                if (Modifier.isStatic(m.getModifiers())) continue;
                var pt = m.getAnnotation(Path.class);
                if (pt == null) continue;
                methods.add(ServiceMethodBuilder.make(serviceName, instance, m));
            }
        }

        return new ServiceBuilder(instance, serviceName, methods);
    }

    @NonNull
    public ServiceBuilder wrap(@NonNull Function<? super JaspasemaRoute, ? extends JaspasemaRoute> wrapper) {
        return new ServiceBuilder(instance, serviceName, methods.stream().map(m -> m.wrap(wrapper)).collect(Collectors.toList()));
    }

    public void configure(@NonNull Javalin service) {
        methods.forEach(m -> m.configure(service));
    }

    private static int compareMethods(@NonNull Method m1, @NonNull Method m2) {
        var x1 = m1.toString();
        var x2 = m2.toString();
        return x1.compareTo(x2);
    }
}
