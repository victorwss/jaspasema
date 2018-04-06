package ninja.javahacker.jaspasema.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Function;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.JaspasemaDiscoverableService;
import ninja.javahacker.jaspasema.processor.BadServiceMappingException;
import spark.Route;
import spark.Service;

/**
 * @author Victor Williams Stafusa da Silva
 */
public final class ServiceConfigurer {

    @Getter
    @NonNull
    private final List<ServiceBuilder> serviceBuilders;

    private ServiceConfigurer(@NonNull Iterable<?> instances) throws BadServiceMappingException {
        this.serviceBuilders = new ArrayList<>();
        for (Object ins : instances) {
            serviceBuilders.add(new ServiceBuilder(ins));
        }
    }

    public static ServiceConfigurer forServices(@NonNull Object... instances) throws BadServiceMappingException {
        return new ServiceConfigurer(Arrays.asList(instances));
    }

    public static ServiceConfigurer loadAll() throws BadServiceMappingException {
        ServiceLoader<JaspasemaDiscoverableService> loader = ServiceLoader.load(JaspasemaDiscoverableService.class);
        return new ServiceConfigurer(loader);
    }

    public void configure(
            @NonNull Service service,
            @NonNull Function<Route, Route> wrapper)
    {
        serviceBuilders.forEach(sb -> sb.configure(service, wrapper));
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
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Request-Method", "HEAD, GET, POST, PUT, DELETE, PATCH");
            //response.header("Access-Control-Allow-Headers", headers);
        });
    }
}
