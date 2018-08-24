package ninja.javahacker.jaspasema.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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

    private static JaspasemaRoute log(@NonNull JaspasemaRoute op) {
        return (rq, rp) -> {
            System.out.println(rq);
            try {
                op.handleIt(rq, rp);
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                System.out.println(rp);
            }
        };
    }

    public App(@NonNull AppConfig config) throws BadServiceMappingException, MalformedProcessorException {
        this.config = config;

        this.server = Service.ignite().port(config.getMainPort());
        if (!config.getStaticFileLocation().isEmpty()) server.staticFileLocation("/" + config.getStaticFileLocation());

        this.configurer = ServiceConfigurer.loadAll().wrap(config.getDb()::transact).wrap(App::log);
        configurer.configure(server);
    }

    public void shutdown() {
        server.stop();
    }

    public void defaultExceptionHandler(@NonNull String errorTemplate) {
        server.exception(Exception.class, (x, rq, rp) -> {
            x.printStackTrace();
            try {
                StringWriter errors = new StringWriter();
                x.printStackTrace(new PrintWriter(errors));
                String stackTrace = errors.toString();

                rp.raw().getWriter().write(errorTemplate.replace("#STACK_TRACE#", stackTrace));
            } catch (IOException ex) {
                ex.printStackTrace();
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
            + "    <pre>#STACK_TRACE#</pre>\n"
            + "  </body>\n"
            + "</html>";

    public void defaultExceptionHandler() {
        defaultExceptionHandler(DEFAULT_ERROR_HTML);
    }
}
