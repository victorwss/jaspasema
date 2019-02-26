package ninja.javahacker.jaspasema.app;

import io.javalin.Javalin;
import lombok.NonNull;
import lombok.Value;
import ninja.javahacker.jaspasema.exceptions.MalformedProcessorException;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.service.JaspasemaRoute;
import ninja.javahacker.jaspasema.service.ServiceConfigurer;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Value
public class App {

    @NonNull AppConfig config;
    @NonNull Javalin server;
    @NonNull ServiceConfigurer configurer;

    private JaspasemaRoute log(@NonNull JaspasemaRoute op) {
        return ctx -> {
            config.getLogBefore().log(ctx);
            try {
                op.handle(ctx);
                config.getLogOk().log(ctx);
            } catch (Throwable t) {
                config.getLogError().log(ctx, t);
                throw t;
            }
        };
    }

    public App(@NonNull AppConfig config) throws BadServiceMappingException, MalformedProcessorException {
        this.config = config;

        this.server = Javalin.create().port(config.getMainPort());
        if (!config.getStaticFileLocation().isEmpty()) server.enableStaticFiles("/" + config.getStaticFileLocation());

        this.configurer = ServiceConfigurer.loadAll().wrap(config.getDb()::transact).wrap(this::log);
        configurer.configure(server);
    }

    public void shutdown() {
        server.stop();
    }

    public void defaultExceptionHandler(@NonNull String errorTemplate) {
        server.exception(Exception.class, (x, ctx) -> {
            ctx.result(errorTemplate.replace("#ERROR_TYPE#", x.getClass().getName()));
            ctx.status(500);
        });
    }

    private static final String DEFAULT_ERROR_HTML = ""
            + "<!DOCTYPE html>\n"
            + "<html>\n"
            + "  <head>\n"
            + "  <title>Error</title>\n"
            + "  <body>\n"
            + "    <h1>Error</h1>\n"
            + "    <p>\n"
            + "      <strong>OPS!</strong>\n"
            + "    </p>\n"
            + "    <pre>#ERROR_TYPE#</pre>\n"
            + "  </body>\n"
            + "</html>";

    public void defaultExceptionHandler() {
        defaultExceptionHandler(DEFAULT_ERROR_HTML);
    }
}
