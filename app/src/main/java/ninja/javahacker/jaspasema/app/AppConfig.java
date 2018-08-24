package ninja.javahacker.jaspasema.app;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Wither;
import ninja.javahacker.jaspasema.exceptions.MalformedProcessorException;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Value
@Wither
@Getter
@AllArgsConstructor
@SuppressFBWarnings("ES_COMPARING_PARAMETER_STRING_WITH_EQ")
public class AppConfig {
    private static AppConfig ROOT = new AppConfig();

    @NonNull String staticFileLocation;
    @NonNull RequestLogger logBefore;
    @NonNull RequestLogger logOk;
    @NonNull RequestErrorLogger logError;
    @NonNull ConfiguredDatabase db;
    int mainPort;
    int adminPort;
    @NonNull String urlString;
    @NonNull String shutdownString;

    private AppConfig() {
        this.staticFileLocation = "";
        this.logBefore = (rq, rp) -> System.out.println("start");
        this.logOk = (rq, rp) -> System.out.println("ok");
        this.logError = (rq, rp, x) -> x.printStackTrace();
        this.db = ConfiguredDatabase.nop();
        this.mainPort = 0;
        this.adminPort = 0;

        this.urlString = ""
                + "<!DOCTYPE html>"
                + "<html>"
                + "  <head>"
                + "    <title>Jaspasema</title>"
                + "  </head>"
                + "  <body>"
                + "    <p>Ok.</p>"
                + "  </body>"
                + "</html>";

        this.shutdownString = ""
                + "<!DOCTYPE html>"
                + "<html>"
                + "  <head>"
                + "    <title>Jaspasema</title>"
                + "  </head>"
                + "  <body>"
                + "    <p>Bye.</p>"
                + "  </body>"
                + "</html>";
    }

    public static AppConfig start() {
        return ROOT;
    }

    public App newApp() throws BadServiceMappingException, MalformedProcessorException {
        return new App(this);
    }
}
