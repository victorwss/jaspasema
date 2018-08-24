package ninja.javahacker.jaspasema.app;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressFBWarnings("ES_COMPARING_PARAMETER_STRING_WITH_EQ")
public class AppConfig {
    private static final AppConfig ROOT = new AppConfig();

    @NonNull String staticFileLocation;
    @NonNull RequestLogger logBefore;
    @NonNull RequestLogger logOk;
    @NonNull RequestErrorLogger logError;
    @NonNull ConfiguredDatabase db;
    int mainPort;
    @NonNull String urlString;

    private AppConfig() {
        this.staticFileLocation = "";
        this.logBefore = (rq, rp) -> System.out.println("start");
        this.logOk = (rq, rp) -> System.out.println("ok");
        this.logError = (rq, rp, x) -> x.printStackTrace();
        this.db = ConfiguredDatabase.nop();
        this.mainPort = 0;
        this.urlString = "http://localhost";
    }

    public static AppConfig start() {
        return ROOT;
    }

    public App newApp() throws BadServiceMappingException, MalformedProcessorException {
        return new App(this);
    }
}
