package ninja.javahacker.jaspasema.app;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.javalin.Javalin;
import io.javalin.http.Context;
import lombok.NonNull;
import lombok.Value;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Value
@SuppressFBWarnings("MDM_THREAD_YIELD")
public class ShutdownApp {

    @NonNull ShutdownAppConfig config;

    @NonNull Javalin adminServer;

    public ShutdownApp(@NonNull ShutdownAppConfig config) {
        this.config = config;
        this.adminServer = Javalin.create().start(config.getAdminPort());
        adminServer.get("/shutdown", this::shutdown);
    }

    private String shutdown(@NonNull Context ctx) {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(50); // Wait the response get delivered.
            } catch (InterruptedException e) {
                // Ignore.
            } finally {
                shutdown();
                config.getLogBye().log(ctx);
            }
        });
        t.start();
        return config.getShutdownString().get();
    }

    public void shutdown() {
        config.getApps().get().forEach(App::shutdown);
    }
}
