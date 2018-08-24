package ninja.javahacker.jaspasema.template;

import ninja.javahacker.jaspasema.service.ServiceConfigurer;
import spark.Route;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ApiTemplate {
    public Route createStub(ServiceConfigurer sc);
}
