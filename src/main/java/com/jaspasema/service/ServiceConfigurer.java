package br.gov.sp.prefeitura.smit.cgtic.jaspasema.service;

import br.gov.sp.prefeitura.smit.cgtic.jaspasema.processor.BadServiceMappingException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import lombok.NonNull;
import spark.Route;
import spark.Service;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ServiceConfigurer {

    @Getter
    @NonNull
    private final List<ServiceBuilder> serviceBuilders;

    public ServiceConfigurer(@NonNull Object... instances) throws BadServiceMappingException {
        this.serviceBuilders = new ArrayList<>(instances.length);
        for (Object ins : instances) {
            serviceBuilders.add(new ServiceBuilder(ins));
        }
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

    public Route createStub(@NonNull ApiTemplate api) {
        return api.createStub(this);
    }
}
