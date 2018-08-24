package ninja.javahacker.jaspasema.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import lombok.NonNull;
import ninja.javahacker.jaspasema.exceptions.MalformedProcessorException;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.service.JaspasemaRoute;
import ninja.javahacker.jaspasema.service.ServiceConfigurer;
import spark.Request;
import spark.Response;
import spark.Service;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class App {

    @Getter
    private final AppConfig config;

    @Getter
    private final Optional<Service> server;

    @Getter
    private final Optional<Service> adminServer;

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

        if (config.getMainPort() != 0) {
            Service s = Service.ignite().port(config.getMainPort());
            if (!config.getStaticFileLocation().isEmpty()) s.staticFileLocation("/" + config.getStaticFileLocation());
            this.server = Optional.of(s);

            ServiceConfigurer sc = ServiceConfigurer.loadAll().wrap(config.getDb()::transact).wrap(App::log);
            sc.configure(s);
        } else {
            this.server = Optional.empty();
        }

        if (config.getAdminPort() != 0) {
            AtomicReference<String> urlRef = new AtomicReference<>("http://localhost:" + config.getMainPort() + "/");
            Service s = Service.ignite().port(config.getAdminPort());
            s.get("/shutdown", this::shutdown);
            s.get("/url", (rq, rp) -> {
                urlRef.set(rq.queryParams("url"));
                return config.getUrlString();
            });
            this.adminServer = Optional.of(s);
        } else {
            this.adminServer = Optional.empty();
        }
    }

    private String shutdown(@NonNull Request rq, @NonNull Response rp) {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // Ignora.
            } finally {
                server.ifPresent(Service::stop);
                adminServer.ifPresent(Service::stop);
                //log.info("Gone.");
            }
        });
        t.start();
        return config.getShutdownString();
    }

    public void defaultExceptionHandler(@NonNull String errorTemplate) {
        server.orElseThrow(IllegalStateException::new).exception(Exception.class, (x, rq, rp) -> {
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
            + "<!DOCTYPE html>"
            + "<html>"
            + "  <head>"
            + "  <title>Error</title>"
            + "  <body>"
            + "    <h1>Error</h1>"
            + "    <p>"
            + "      <strong>OPS!</strong>"
            + "    </p>"
            + "    <pre>#STACK_TRACE#</pre>"
            + "  </body>"
            + "</html>";

    public void defaultExceptionHandler() {
        defaultExceptionHandler(DEFAULT_ERROR_HTML);
    }
}
