package ninja.javahacker.jaspasema.app;

import java.util.List;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

/**
 * @author Victor Williams Stafusa da Silva
 */
@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShutdownAppConfig {
    private static final ShutdownAppConfig ROOT = new ShutdownAppConfig();

    private final int adminPort;

    @NonNull
    private final Supplier<? extends List<? extends App>> apps;

    @NonNull
    private final Supplier<String> shutdownString;

    @NonNull
    private final RequestLogger logBye;

    private ShutdownAppConfig() {
        this.adminPort = 0;
        this.apps = () -> List.of();
        this.logBye = ctx -> System.out.println("exiting");
        this.shutdownString = () ->
                """
                <!DOCTYPE html>
                <html>
                  <head>
                    <title>Jaspasema</title>
                  </head>
                  <body>
                    <p>Bye.</p>
                  </body>
                </html>
                """;
    }

    public static ShutdownAppConfig start() {
        return ROOT;
    }

    public ShutdownApp newApp() {
        return new ShutdownApp(this);
    }
}
