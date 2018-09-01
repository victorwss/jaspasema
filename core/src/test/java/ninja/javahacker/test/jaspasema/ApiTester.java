package ninja.javahacker.test.jaspasema;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
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
            String method,
            String path,
            String body,
            @Singular List<Header> headers)
            throws IOException
    {
        URL url = new URL("http://localhost:" + port + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        if (!BODYLESS.contains(method)) connection.setDoOutput(true);
        connection.setDoInput(true);
        for (Header h : headers) {
            connection.addRequestProperty(h.getName(), h.getValue());
        }
        connection.connect();
        if (!BODYLESS.contains(method)) connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        int rc = connection.getResponseCode();
        try (InputStream is = rc >= 400 ? connection.getErrorStream() : connection.getInputStream()) {
            String response = read(is);
            //String response = new String(is.readAllBytes(), StandardCharsets.UTF_8); // For Java 9
            return new TestResponse(connection.getResponseCode(), response);
        }
    }

    @Value
    public static class TestResponse {
        private int status;
        private String body;
    }

    @Value
    public static class Header {
        private String name;
        private String value;
    }

    private static String read(InputStream is) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }
}
