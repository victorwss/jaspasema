package ninja.javahacker.jaspasema.template;

import io.javalin.http.Handler;
import lombok.NonNull;
import ninja.javahacker.jaspasema.service.ServiceConfigurer;

/**
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ApiTemplate {
    @NonNull
    public Handler createStub(@NonNull ServiceConfigurer sc);
}
