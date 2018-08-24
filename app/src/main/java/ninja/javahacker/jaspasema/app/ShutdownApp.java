package ninja.javahacker.jaspasema.app;

import lombok.NonNull;
import lombok.Value;
import ninja.javahacker.jaspasema.exceptions.MalformedProcessorException;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import spark.Request;
import spark.Response;
import spark.Service;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Value
public class ShutdownApp {

    @NonNull ShutdownAppConfig config;

    @NonNull Service adminServer;

    public ShutdownApp(@NonNull ShutdownAppConfig config) throws BadServiceMappingException, MalformedProcessorException {
        this.config = config;
        this.adminServer = Service.ignite().port(config.getAdminPort());
        adminServer.get("/shutdown", this::shutdown);
    }

    private String shutdown(@NonNull Request rq, @NonNull Response rp) {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(50); // Wait the response get delivered.
            } catch (InterruptedException e) {
                // Ignore.
            } finally {
                shutdown();
                config.getLogBye().log(rq, rp);
            }
        });
        t.start();
        return config.getShutdownString().get();
    }

    public void shutdown() {
        config.getApps().get().forEach(App::shutdown);
    }
}
