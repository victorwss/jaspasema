package ninja.javahacker.jaspasema.app;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.With;
import ninja.javahacker.jaspasema.exceptions.MalformedProcessorException;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;

/**
 * @author Victor Williams Stafusa da Silva
 */
@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AppConfig {
    private static final AppConfig ROOT = new AppConfig();

    @NonNull
    private final String staticFileLocation;

    @NonNull
    private final RequestLogger logBefore;

    @NonNull
    private final RequestLogger logOk;

    @NonNull
    private final RequestErrorLogger logError;

    @NonNull
    private final ConfiguredDatabase db;

    private final int mainPort;

    @NonNull
    private final String urlString;

    private AppConfig() {
        this.staticFileLocation = "";
        this.logBefore = ctx -> System.out.println("start");
        this.logOk = ctx -> System.out.println("ok");
        this.logError = RequestErrorLogger.PRINT_STACK_TRACE;
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
