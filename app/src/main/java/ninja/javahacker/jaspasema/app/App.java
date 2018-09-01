package ninja.javahacker.jaspasema.app;

import java.io.IOException;
import lombok.NonNull;
import lombok.Value;
import ninja.javahacker.jaspasema.exceptions.MalformedProcessorException;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.service.JaspasemaRoute;
import ninja.javahacker.jaspasema.service.ServiceConfigurer;
import spark.Service;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Value
public class App {

    @NonNull AppConfig config;
    @NonNull Service server;
    @NonNull ServiceConfigurer configurer;

    private JaspasemaRoute log(@NonNull JaspasemaRoute op) {
        return (rq, rp) -> {
            config.getLogBefore().log(rq, rp);
            try {
                op.handleIt(rq, rp);
                config.getLogOk().log(rq, rp);
            } catch (Throwable t) {
                config.getLogError().log(rq, rp, t);
                throw t;
            }
        };
    }

    public App(@NonNull AppConfig config) throws BadServiceMappingException, MalformedProcessorException {
        this.config = config;

        this.server = Service.ignite().port(config.getMainPort());
        if (!config.getStaticFileLocation().isEmpty()) server.staticFileLocation("/" + config.getStaticFileLocation());

        this.configurer = ServiceConfigurer.loadAll().wrap(config.getDb()::transact).wrap(this::log);
        configurer.configure(server);
    }

    public void shutdown() {
        server.stop();
    }

    public void defaultExceptionHandler(@NonNull String errorTemplate) {
        server.exception(Exception.class, (x, rq, rp) -> {
            try {
                rp.raw().getWriter().write(errorTemplate.replace("#ERROR_TYPE#", x.getClass().getName()));
            } catch (IOException ex) {
                config.getLogError().log(rq, rp, ex);
            }
            rp.status(500);
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
