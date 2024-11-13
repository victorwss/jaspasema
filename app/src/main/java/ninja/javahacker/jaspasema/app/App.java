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

    @NonNull
    private final AppConfig config;

    @NonNull
    private final Javalin server;

    @NonNull
    private final ServiceConfigurer configurer;

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

        this.server = Javalin.create(jc -> {
            if (!config.getStaticFileLocation().isEmpty()) jc.staticFiles.add("/" + config.getStaticFileLocation());
        });

        this.configurer = ServiceConfigurer.loadAll().wrap(config.getDb()::transact).wrap(this::log);
        configurer.configure(server);
        server.start(config.getMainPort());
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

    private static final String DEFAULT_ERROR_HTML =
            """
            <!DOCTYPE html>
            <html>
              <head>
                <title>Error</title>
              </head>
              <body>
                <h1>Error</h1>
                <p>
                  <strong>OPS!</strong>
                </p>
                <pre>#ERROR_TYPE#</pre>
              </body>
            </html>
            """;

    public void defaultExceptionHandler() {
        defaultExceptionHandler(DEFAULT_ERROR_HTML);
    }
}
