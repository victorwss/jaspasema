package ninja.javahacker.test.jaspasema;

import io.javalin.Javalin;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import lombok.NonNull;
import ninja.javahacker.jaspasema.service.ServiceConfigurer;
import ninja.javahacker.test.jaspasema.ApiTester.Header;
import org.junit.jupiter.api.Assertions;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class FwTester {

    private static final AtomicInteger PORT_COUNTER = new AtomicInteger(8899);

    private final int before;

    private volatile int after = 0;

    @NonNull
    private final Javalin service;

    @NonNull
    private final AtomicReference<Throwable> problem;

    private FwTester() {
        this.service = Javalin.create();
        this.before = PORT_COUNTER.getAndIncrement();
        this.problem = new AtomicReference<>();
        this.service.start(before);
    }

    public void get(
            @NonNull String qs,
            int expectedStatus,
            @NonNull String resultBody,
            @NonNull List<Header> headers)
            throws Throwable
    {
        http("GET", qs, "", expectedStatus, resultBody, headers);
    }

    public void post(
            @NonNull String qs,
            @NonNull String body,
            int expectedStatus,
            @NonNull String resultBody,
            @NonNull List<Header> headers)
            throws Throwable
    {
        http("POST", qs, body, expectedStatus, resultBody, headers);
    }

    @NonNull
    public void http(
            @NonNull String method,
            @NonNull String qs,
            @NonNull String body,
            int expectedStatus,
            @NonNull String resultBody,
            @NonNull List<Header> headers)
            throws Throwable
    {
        try {
            ApiTester.TestResponse tresult = ApiTester.builder().port(before).method(method).path(qs).body(body).headers(headers).build();
            //System.out.println(tresult);
            if (problem.get() != null) throw problem.get();
            Assertions.assertAll(
                    () -> Assertions.assertEquals(expectedStatus, tresult.getStatus()),
                    () -> Assertions.assertEquals(resultBody, tresult.getBody()),
                    () -> Assertions.assertEquals(before, after)
            );
        } finally {
            service.stop();
        }
    }

    public void confirm() {
        after = before;
    }

    @NonNull
    public static FwTester reflect(@NonNull Function<FwTester, Object> callback) {
        return reflect(callback, Collections.emptyMap());
    }

    @NonNull
    public static FwTester reflect(@NonNull Function<FwTester, Object> callback, @NonNull Map<String, Object> session) {
        var fwt = new FwTester();
        var obj = callback.apply(fwt);
        fwt.configure(obj, session);
        return fwt;
    }

    private void configure(Object obj, @NonNull Map<String, Object> session) {
        try {
            ServiceConfigurer.forServices(obj).wrap(r -> ctx -> {
                for (var entry : session.entrySet()) {
                    ctx.sessionAttribute(entry.getKey(), entry.getValue());
                }
                try {
                    r.handle(ctx);
                } catch (Throwable x) {
                    //x.printStackTrace();
                    problem.set(x);
                    ctx.result(x.toString());
                }
            }).configure(service);
        } catch (Throwable ex) {
            //ex.printStackTrace();
            throw new AssertionError(ex);
        }
    }
}
