package ninja.javahacker.test.jaspasema;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import ninja.javahacker.jaspasema.service.ServiceConfigurer;
import ninja.javahacker.test.jaspasema.ApiTester.Header;
import org.junit.jupiter.api.Assertions;
import spark.Service;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class FwTester {

    private static final AtomicReference<FwTester> TESTER = new AtomicReference<>();
    private static final AtomicInteger PORT_COUNTER = new AtomicInteger(8899);
    private final int before;
    private volatile int after = 0;
    private final Service service;
    private final AtomicReference<Throwable> xxx = new AtomicReference<>();

    public void get(String qs, int expectedStatus, String resultBody, List<Header> headers) throws Throwable {
        http("GET", qs, "", expectedStatus, resultBody, headers);
    }

    public void post(String qs, String body, int expectedStatus, String resultBody, List<Header> headers) throws Throwable {
        http("POST", qs, body, expectedStatus, resultBody, headers);
    }

    public void http(String method, String qs, String body, int expectedStatus, String resultBody, List<Header> headers) throws Throwable {
        try {
            service.awaitInitialization();
            ApiTester.TestResponse tresult = ApiTester.builder().port(before).method(method).path(qs).body(body).headers(headers).build();
            //System.out.println(tresult);
            if (xxx.get() != null) throw xxx.get();
            Assertions.assertAll(
                    () -> Assertions.assertEquals(expectedStatus, tresult.getStatus()),
                    () -> Assertions.assertEquals(resultBody, tresult.getBody()),
                    () -> Assertions.assertEquals(before, after)
            );
        } finally {
            service.stop();
        }
    }

    public static FwTester reflect(Object c) {
        return reflect(c, Collections.emptyMap());
    }

    public static FwTester reflect(Object c, Map<String, Object> session) {
        FwTester fwt = new FwTester(c, session);
        TESTER.set(fwt);
        return fwt;
    }

    public static void confirm() {
        FwTester f = TESTER.get();
        f.after = f.before;
    }

    private FwTester(Object c, Map<String, Object> session) {
        service = Service.ignite();
        before = PORT_COUNTER.getAndIncrement();
        try {
            service.port(before);
            ServiceConfigurer.forServices(c).wrap(r -> (rq, rp) -> {
                for (Map.Entry<String, Object> entry : session.entrySet()) {
                    rq.session(true).attribute(entry.getKey(), entry.getValue());
                }
                try {
                    r.handleIt(rq, rp);
                } catch (Throwable x) {
                    //x.printStackTrace();
                    xxx.set(x);
                    rp.body(x.toString());
                }
            }).configure(service);
        } catch (Throwable ex) {
            //ex.printStackTrace();
            throw new AssertionError(ex);
        }
    }
}
