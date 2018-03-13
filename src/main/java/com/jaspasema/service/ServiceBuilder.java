package br.gov.sp.prefeitura.smit.cgtic.jaspasema.service;

import br.gov.sp.prefeitura.smit.cgtic.jaspasema.Path;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.ServiceName;
import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.BadServiceMappingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import lombok.Getter;
import lombok.NonNull;
import spark.Route;
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
    private final List<ServiceMethodBuilder> methods;

    public ServiceBuilder(@NonNull Object instance) throws BadServiceMappingException {
        this.instance = instance;
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
        serviceName = !name.isEmpty() ? name : className.substring(0, 1).toLowerCase(Locale.ROOT) + className.substring(1);
        this.methods = new ArrayList<>();
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
                methods.add(new ServiceMethodBuilder(this, m));
            }
        }
    }

    public void configure(@NonNull Service service, @NonNull Function<Route, Route> wrapper) {
        methods.forEach((m) -> m.configure(service, wrapper));
    }

    private static int compareMethods(Method m1, Method m2) {
        String x1 = m1.toString();
        String x2 = m2.toString();
        return x1.compareTo(x2);
    }
}
