package ninja.javahacker.jaspasema.app;

import lombok.Builder;
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
@Builder
@Wither
@Getter
public class AppConfig {
    @NonNull
    String staticFileLocation = "";

    @NonNull
    String packageRoot = "";

    @NonNull
    RequestLogger logBefore = (rq, rp) -> System.out.println("start");

    @NonNull
    RequestLogger logOk = (rq, rp) -> System.out.println("ok");

    @NonNull
    RequestErrorLogger logError = (rq, rp, x) -> x.printStackTrace();

    @NonNull
    ConfiguredDatabase db = ConfiguredDatabase.nop();

    int mainPort;

    int adminPort;

    @NonNull
    String urlString = ""
            + "<!DOCTYPE html>"
            + "<html>"
            + "  <head>"
            + "    <title>Jaspasema</title>"
            + "  </head>"
            + "  <body>"
            + "    <p>Ok.</p>"
            + "  </body>"
            + "</html>";

    @NonNull
    String shutdownString = ""
            + "<!DOCTYPE html>"
            + "<html>"
            + "  <head>"
            + "    <title>Jaspasema</title>"
            + "  </head>"
            + "  <body>"
            + "    <p>Bye.</p>"
            + "  </body>"
            + "</html>";

    public App newApp() throws BadServiceMappingException, MalformedProcessorException {
        return new App(this);
    }
}
