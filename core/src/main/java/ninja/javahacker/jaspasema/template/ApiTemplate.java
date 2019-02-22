package ninja.javahacker.jaspasema.template;

import io.javalin.Handler;
import ninja.javahacker.jaspasema.service.ServiceConfigurer;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ApiTemplate {
    public Handler createStub(ServiceConfigurer sc);
}
