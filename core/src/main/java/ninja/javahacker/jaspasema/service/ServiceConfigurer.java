package ninja.javahacker.jaspasema.service;

import io.javalin.Javalin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.JaspasemaDiscoverableService;
import ninja.javahacker.jaspasema.exceptions.MalformedProcessorException;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;

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

    @NonNull
    public static ServiceConfigurer make(@NonNull Iterable<?> instances)
            throws BadServiceMappingException,
            MalformedProcessorException
    {
        List<ServiceBuilder> serviceBuilders = new ArrayList<>();
        for (var ins : instances) {
            serviceBuilders.add(ServiceBuilder.make(ins));
        }
        return new ServiceConfigurer(serviceBuilders);
    }

    @NonNull
    public static ServiceConfigurer forServices(@NonNull Object... instances)
            throws BadServiceMappingException,
            MalformedProcessorException
    {
        return ServiceConfigurer.make(Arrays.asList(instances));
    }

    /**
     * Creates an instance of the {@code ServiceConfigurer} containing all the instances of
     * {@link JaspasemaDiscoverableService} found using the {@link ServiceLoader} mechanism.
     * @return An instance of the {@code ServiceConfigurer} containing all the instances of
     *     {@link JaspasemaDiscoverableService} found using the {@link ServiceLoader} mechanism.
     * @throws BadServiceMappingException Some instance of {@link JaspasemaDiscoverableService} failed
     *     to be loaded because it is not well-defined.
     * @throws MalformedProcessorException Some instance of {@link JaspasemaDiscoverableService} failed
     *     to be loaded because it is uses ill-behaved processors.
     */
    @NonNull
    public static ServiceConfigurer loadAll()
            throws BadServiceMappingException,
            MalformedProcessorException
    {
        var loader = ServiceLoader.load(JaspasemaDiscoverableService.class);
        return ServiceConfigurer.make(loader);
    }

    @NonNull
    public ServiceConfigurer wrap(@NonNull Function<? super JaspasemaRoute, ? extends JaspasemaRoute> wrapper) {
        return new ServiceConfigurer(serviceBuilders.stream().map(m -> m.wrap(wrapper)).collect(Collectors.toList()));
    }

    public void configure(@NonNull Javalin service) {
        serviceBuilders.forEach(sb -> sb.configure(service));
        //service.config.enableCorsForAllOrigins();
        configureCors(service);
    }

    @SuppressWarnings("pmd:AvoidLiteralsInIfCondition") // PMD is being overzealous.
    private static void configureCors(@NonNull Javalin service) {
        service.options("/*", ctx -> {

            String accessControlRequestHeaders = ctx.header("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                ctx.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = ctx.header("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                ctx.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            ctx.result("OK");
        });

        service.before(ctx -> {
            String origin = ctx.header("Origin");
            if (origin == null || origin.isEmpty()) origin = "*";
            if ("null".equals(origin)) origin = "file://";
            ctx.header("Access-Control-Allow-Origin", origin);
            ctx.header("Access-Control-Allow-Credentials", "true");
            ctx.header("Access-Control-Request-Method", "HEAD, GET, POST, PUT, DELETE, PATCH");
            ctx.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        });
    }
}
