package ninja.javahacker.jaspasema.service;

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
import ninja.javahacker.jaspasema.processor.BadServiceMappingException;
import spark.Service;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ServiceBuilder {

    @Getter
    @NonNull
    private final Object instance;

    @Getter
    @NonNull
    private final String serviceName;

    @Getter
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

    public static ServiceBuilder make(@NonNull Object instance) throws BadServiceMappingException {
        String className = instance.getClass().getSimpleName();
        if (className.isEmpty()) {
            className = instance.getClass().getName().replace(".", "_");
        }
        ServiceName sn = null;
        for (Class<?> c = instance.getClass(); c != null; c = c.getSuperclass()) {
            if (sn == null) {
                sn = c.getAnnotation(ServiceName.class);
            }
        }
        String name = sn == null ? "" : sn.value();
        String serviceName = !name.isEmpty() ? name : className.substring(0, 1).toLowerCase(Locale.ROOT) + className.substring(1);
        List<ServiceMethodBuilder<?>> methods = new ArrayList<>();
        for (Class<?> c = instance.getClass(); c != null; c = c.getSuperclass()) {
            List<Method> mm = Arrays.asList(c.getDeclaredMethods());
            mm.sort(ServiceBuilder::compareMethods);
            for (Method m : mm) {
                if (Modifier.isStatic(m.getModifiers())) {
                    continue;
                }
                Path pt = m.getAnnotation(Path.class);
                if (pt == null) {
                    continue;
                }
                methods.add(ServiceMethodBuilder.make(serviceName, instance, m));
            }
        }

        return new ServiceBuilder(instance, serviceName, methods);
    }

    public ServiceBuilder wrap(@NonNull Function<? super JaspasemaRoute, ? extends JaspasemaRoute> wrapper) {
        return new ServiceBuilder(instance, serviceName, methods.stream().map(m -> m.wrap(wrapper)).collect(Collectors.toList()));
    }

    public void configure(@NonNull Service service) {
        methods.forEach(m -> m.configure(service));
    }

    private static int compareMethods(Method m1, Method m2) {
        String x1 = m1.toString();
        String x2 = m2.toString();
        return x1.compareTo(x2);
    }
}
