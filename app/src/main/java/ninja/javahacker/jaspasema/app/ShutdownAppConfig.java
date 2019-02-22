package ninja.javahacker.jaspasema.app;

import java.util.List;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Value
@Wither
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShutdownAppConfig {
    private static final ShutdownAppConfig ROOT = new ShutdownAppConfig();

    int adminPort;
    @NonNull Supplier<? extends List<? extends App>> apps;
    @NonNull Supplier<String> shutdownString;
    @NonNull RequestLogger logBye;

    private ShutdownAppConfig() {
        this.adminPort = 0;
        this.apps = () -> List.of();
        this.logBye = ctx -> System.out.println("exiting");
        this.shutdownString = () -> ""
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "  <head>\n"
                + "    <title>Jaspasema</title>\n"
                + "  </head>\n"
                + "  <body>\n"
                + "    <p>Bye.</p>\n"
                + "  </body>\n"
                + "</html>";
    }

    public static ShutdownAppConfig start() {
        return ROOT;
    }

    public ShutdownApp newApp() {
        return new ShutdownApp(this);
    }
}
