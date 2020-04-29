package ninja.javahacker.test.jaspasema;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class ApiTester {

    private static final List<String> BODYLESS = Arrays.asList("GET", "HEAD", "OPTIONS", "TRACE");

    private ApiTester() {
        throw new UnsupportedOperationException();
    }

    @Builder
    @SuppressFBWarnings("URLCONNECTION_SSRF_FD")
    public static TestResponse request(
            int port,
            @NonNull String method,
            @NonNull String path,
            @NonNull String body,
            @NonNull @Singular List<Header> headers)
            throws IOException
    {
        var url = new URL("http://localhost:" + port + path);
        var connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        if (!BODYLESS.contains(method)) connection.setDoOutput(true);
        connection.setDoInput(true);
        for (var h : headers) {
            connection.addRequestProperty(h.getName(), h.getValue());
        }
        connection.connect();
        if (!BODYLESS.contains(method)) connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        var rc = connection.getResponseCode();
        try (var is = rc >= 400 ? connection.getErrorStream() : connection.getInputStream()) {
            var response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return new TestResponse(connection.getResponseCode(), response);
        }
    }

    @Value
    public static class TestResponse {
        private final int status;

        @NonNull
        private final String body;
    }

    @Value
    public static class Header {
        @NonNull
        private final String name;

        @NonNull
        private final String value;
    }
}
