package ninja.javahacker.jaspasema.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.JaspasemaDiscoverableService;
import ninja.javahacker.jaspasema.processor.BadServiceMappingException;
import ninja.javahacker.jaspasema.processor.MalformedParameterProcessorException;
import ninja.javahacker.jaspasema.processor.MalformedReturnProcessorException;
import spark.Service;

/**
 * @author Victor Williams Stafusa da Silva
 */
public final class ServiceConfigurer {

    @Getter
    @NonNull
    private final List<ServiceBuilder> serviceBuilders;

    private ServiceConfigurer(@NonNull List<ServiceBuilder> serviceBuilders) {
        this.serviceBuilders = serviceBuilders;
    }

    public static ServiceConfigurer make(@NonNull Iterable<?> instances)
            throws BadServiceMappingException,
            MalformedReturnProcessorException,
            MalformedParameterProcessorException
    {
        List<ServiceBuilder> serviceBuilders = new ArrayList<>();
        for (Object ins : instances) {
            serviceBuilders.add(ServiceBuilder.make(ins));
        }
        return new ServiceConfigurer(serviceBuilders);
    }

    public static ServiceConfigurer forServices(@NonNull Object... instances)
            throws BadServiceMappingException,
            MalformedReturnProcessorException,
            MalformedParameterProcessorException
    {
        return ServiceConfigurer.make(Arrays.asList(instances));
    }

    public static ServiceConfigurer loadAll()
            throws BadServiceMappingException,
            MalformedReturnProcessorException,
            MalformedParameterProcessorException
    {
        ServiceLoader<JaspasemaDiscoverableService> loader = ServiceLoader.load(JaspasemaDiscoverableService.class);
        return ServiceConfigurer.make(loader);
    }

    public ServiceConfigurer wrap(@NonNull Function<? super JaspasemaRoute, ? extends JaspasemaRoute> wrapper) {
        return new ServiceConfigurer(serviceBuilders.stream().map(m -> m.wrap(wrapper)).collect(Collectors.toList()));
    }

    public void configure(@NonNull Service service) {
        serviceBuilders.forEach(sb -> sb.configure(service));
        configureCors(service);
    }

    private void configureCors(@NonNull Service service) {
        service.options("/*", (request, response) -> {

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        service.before((request, response) -> {
            String origin = request.headers("Origin");
            if (origin == null || origin.isEmpty()) origin = "*";
            if ("null".equals(origin)) origin = "file://";
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Allow-Credentials", "true");
            response.header("Access-Control-Request-Method", "HEAD, GET, POST, PUT, DELETE, PATCH");
            response.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
            //response.header("Access-Control-Allow-Headers", headers);
        });
    }
}
