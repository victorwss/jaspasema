package ninja.javahacker.test.jaspasema;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Value;
import ninja.javahacker.jaspasema.CookieParam;
import ninja.javahacker.jaspasema.Get;
import ninja.javahacker.jaspasema.HeaderJsonParam;
import ninja.javahacker.jaspasema.HeaderParam;
import ninja.javahacker.jaspasema.JsonBody;
import ninja.javahacker.jaspasema.JsonBodyPlainProperty;
import ninja.javahacker.jaspasema.JsonBodyProperty;
import ninja.javahacker.jaspasema.Path;
import ninja.javahacker.jaspasema.PlainBody;
import ninja.javahacker.jaspasema.Post;
import ninja.javahacker.jaspasema.ProducesJson;
import ninja.javahacker.jaspasema.ProducesPlain;
import ninja.javahacker.jaspasema.QueryPart;
import ninja.javahacker.jaspasema.SessionParam;
import ninja.javahacker.jaspasema.UriPart;
import ninja.javahacker.test.jaspasema.ApiTester.Header;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Victor Williams Stafusa da Silva
 */
@SuppressFBWarnings("UMAC")
public class PlainProcessorTest {

    @Value
    public static class TestValue {
        private int a;
        private String b;
        private Nested c;

        @Value
        public static class Nested {
            private int d;
            private long e;
        }
    }

    @Value
    public static class DateSet {
        @JsonFormat(pattern = "dd/MM/yyyy")
        private LocalDate a;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate b;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime c;
    }

    @Test
    public void testPlainBody() throws Throwable {
        FwTester.reflect(new Object() {
            @Post
            @Path("/go")
            public void blah(@PlainBody String a) {
                Assertions.assertEquals("Teste", a);
                FwTester.confirm();
            }
        }).post("/go", "Teste", 200, "", Collections.emptyList());
    }

    @Test
    public void testPlainReturn() throws Throwable {
        FwTester.reflect(new Object() {
            @Post
            @Path("/go")
            @ProducesPlain
            public String blah(@PlainBody String a) {
                Assertions.assertEquals("Teste", a);
                FwTester.confirm();
                return "XYZ";
            }
        }).post("/go", "Teste", 200, "XYZ", Collections.emptyList());
    }

    @Test
    public void testTwoParameters() throws Throwable {
        FwTester.reflect(new Object() {
            @Post
            @Path("/go")
            public void blah(@PlainBody String a, @PlainBody String b) {
                Assertions.assertEquals("Teste", a);
                Assertions.assertEquals("Teste", b);
                FwTester.confirm();
            }
        }).post("/go", "Teste", 200, "", Collections.emptyList());
    }

    @Test
    public void testNumericBody() throws Throwable {
        FwTester.reflect(new Object() {
            @Post
            @Path("/go")
            public void blah(@PlainBody int a, @PlainBody Integer b, @PlainBody byte c) {
                Assertions.assertEquals(123, a);
                Assertions.assertEquals(Integer.valueOf(123), b);
                Assertions.assertEquals(123, c);
                FwTester.confirm();
            }
        }).post("/go", "123", 200, "", Collections.emptyList());
    }

    @Test
    public void testJsonBody() throws Throwable {
        FwTester.reflect(new Object() {
            @Post
            @Path("/go")
            public void blah(@JsonBody TestValue x) {
                Assertions.assertEquals(123, x.getA());
                Assertions.assertEquals("Verde", x.getB());
                Assertions.assertEquals(321, x.getC().getD());
                Assertions.assertEquals(2233L, x.getC().getE());
                FwTester.confirm();
            }
        }).post("/go", "{\"a\": 123, \"b\": \"Verde\", \"c\": { \"d\": 321, \"e\": 2233}}", 200, "", Collections.emptyList());
    }

    @Test
    public void testJsonReturn() throws Throwable {
        FwTester.reflect(new Object() {
            @Post
            @Path("/go")
            @ProducesJson
            public TestValue.Nested blah(@PlainBody String a) {
                Assertions.assertEquals("Teste", a);
                FwTester.confirm();
                return new TestValue.Nested(2, 3);
            }
        }).post("/go", "Teste", 200, "{\"d\":2,\"e\":3}", Collections.emptyList());
    }

    @Test
    public void testUriParams() throws Throwable {
        FwTester.reflect(new Object() {
            @Post
            @Path("/go/:a/xxx/:c/yyy/zzz/:b/qqqq")
            public void blah(@UriPart int a, @UriPart String b, @UriPart byte c) {
                Assertions.assertEquals(123, a);
                Assertions.assertEquals("Laranja", b);
                Assertions.assertEquals(33, c);
                FwTester.confirm();
            }
        }).post("/go/123/xxx/33/yyy/zzz/Laranja/qqqq", "", 200, "", Collections.emptyList());
    }

    @Test
    public void testQueryParams() throws Throwable {
        String url = "/go?a=123&b=Laranja&c=33&item=banana&item=abacaxi&item=uva&id=7777&id=44444&id=987654321";
        FwTester.reflect(new Object() {
            @Post
            @Path("/go")
            public void blah(
                    @QueryPart int a,
                    @QueryPart String b,
                    @QueryPart byte c,
                    @QueryPart List<String> item,
                    @QueryPart List<Long> id)
            {
                Assertions.assertEquals(123, a);
                Assertions.assertEquals("Laranja", b);
                Assertions.assertEquals(33, c);
                Assertions.assertEquals(Arrays.asList("banana", "abacaxi", "uva"), item);
                Assertions.assertEquals(Arrays.asList(7777L, 44444L, 987654321L), id);
                FwTester.confirm();
            }
        }).post(url, "", 200, "", Collections.emptyList());
    }

    @Test
    public void testJsonBodyParts() throws Throwable {
        String json = "{\"x\": {\"a\": 123, \"b\": \"Verde\", \"c\": { \"d\": 321, \"e\": 2233}}, \"a\": 999, \"b\": \"Azul\"}";
        FwTester.reflect(new Object() {
            @Post
            @Path("/go")
            public void blah(@JsonBodyProperty int a, @JsonBodyProperty String b, @JsonBodyProperty TestValue x) {
                Assertions.assertEquals(123, x.getA());
                Assertions.assertEquals("Verde", x.getB());
                Assertions.assertEquals(321, x.getC().getD());
                Assertions.assertEquals(2233L, x.getC().getE());
                Assertions.assertEquals(999, a);
                Assertions.assertEquals("Azul", b);
                FwTester.confirm();
            }
        }).post("/go", json, 200, "", Collections.emptyList());
    }

    @Test
    public void testCookieParams() throws Throwable {
        FwTester.reflect(new Object() {
            @Post
            @Path("/go")
            public void blah(@CookieParam int a, @CookieParam String b, @CookieParam byte c) {
                Assertions.assertEquals(123, a);
                Assertions.assertEquals("Laranja", b);
                Assertions.assertEquals(33, c);
                FwTester.confirm();
            }
        }).post("/go", "", 200, ReturnMapper., Arrays.asList(new Header("Cookie", "a=123; b=Laranja; c=33")));
    }

    @Test
    public void testHeaderParams() throws Throwable {
        FwTester.reflect(new Object() {
            @Get
            @Path("/go")
            public void blah(@HeaderParam int a, @HeaderParam String b, @HeaderParam byte c) {
                Assertions.assertEquals(123, a);
                Assertions.assertEquals("Laranja", b);
                Assertions.assertEquals(33, c);
                FwTester.confirm();
            }
        }).get("/go", 200, "", Arrays.asList(new Header("a", "123"), new Header("b", "Laranja"), new Header("c", "33")));
    }

    @Test
    public void testHeaderJsonParams() throws Throwable {
        Header h1 = new Header("blue", "{\"a\": 444, \"b\": \"Azul\", \"c\": {\"d\": 555, \"e\": 7777}}");
        Header h2 = new Header("yellow", "{\"d\": 888, \"e\": 9999}");
        FwTester.reflect(new Object() {
            @Post
            @Path("/go")
            public void blah(@HeaderJsonParam TestValue blue, @HeaderJsonParam TestValue.Nested yellow) {
                Assertions.assertEquals(444, blue.getA());
                Assertions.assertEquals("Azul", blue.getB());
                Assertions.assertEquals(555, blue.getC().getD());
                Assertions.assertEquals(7777L, blue.getC().getE());
                Assertions.assertEquals(888, yellow.getD());
                Assertions.assertEquals(9999L, yellow.getE());
                FwTester.confirm();
            }
        }).post("/go", "", 200, "", Arrays.asList(h1, h2));
    }

    @Test
    public void testSessionParams() throws Throwable {
        Object sessionObjectA = new Object();
        Object sessionObjectB = new Object();
        Map<String, Object> session = new HashMap<>();
        session.put("a", sessionObjectA);
        session.put("b", sessionObjectB);
        FwTester.reflect(new Object() {
            @Post
            @Path("/go")
            public void blah(@SessionParam Object a, @SessionParam Object b) {
                Assertions.assertEquals(sessionObjectA, a);
                Assertions.assertEquals(sessionObjectB, b);
                FwTester.confirm();
            }
        }, session).post("/go", "", 200, "", Collections.emptyList());
    }

    @Test
    public void testEverything() throws Throwable {
        String json = "{\"a\": 123, \"b\": \"Verde\", \"c\": {\"d\": 321, \"e\": 2233}}";
        Header h1 = new Header("header1", "Homer");
        Header h2 = new Header("header2", "{\"d\": 888, \"e\": 9999}");
        Object sessionObjectA = new Object();
        Object sessionObjectB = new Object();
        Map<String, Object> session = new HashMap<>();
        session.put("sessionA", sessionObjectA);
        session.put("sessionB", sessionObjectB);
        FwTester.reflect(new Object() {
            @Post
            @Path("/go/:xxx")
            public void blah(
                    @SessionParam Object sessionA,
                    @HeaderParam String header1,
                    @HeaderJsonParam TestValue.Nested header2,
                    @SessionParam Object sessionB,
                    @JsonBodyProperty TestValue.Nested c,
                    @JsonBody TestValue x,
                    @PlainBody String json,
                    @QueryPart String test,
                    @UriPart String xxx)
            {
                Assertions.assertEquals(sessionObjectA, sessionA);
                Assertions.assertEquals(sessionObjectB, sessionB);
                Assertions.assertEquals("Homer", header1);
                Assertions.assertEquals(123, x.getA());
                Assertions.assertEquals("Verde", x.getB());
                Assertions.assertEquals(321, x.getC().getD());
                Assertions.assertEquals(2233L, x.getC().getE());
                Assertions.assertEquals(321, c.getD());
                Assertions.assertEquals(2233L, c.getE());
                Assertions.assertEquals("{\"a\": 123, \"b\": \"Verde\", \"c\": {\"d\": 321, \"e\": 2233}}", json);
                Assertions.assertEquals("abacaxi", xxx);
                Assertions.assertEquals("ABC", test);
                Assertions.assertEquals(888, header2.getD());
                Assertions.assertEquals(9999L, header2.getE());
                FwTester.confirm();
            }
        }, session).post("/go/abacaxi?test=ABC", json, 200, "", Arrays.asList(h1, h2));
    }

    @Test
    public void testDates() throws Throwable {
        Header h1 = new Header("h1", "23/06/2017");
        Header h2 = new Header("h2", "2017-06-23");
        Header h3 = new Header("h3", "23/06/2017 17:01");
        Header h4 = new Header("h4", "2017-06-23-17-01-22");
        Header h5 = new Header("Cookie", "c1=23/06/2017;c2=2017-06-23;c3=23/06/2017.17:01;c4=2017-06-23-17-01-22");
        FwTester.reflect(new Object() {
            @Post
            @Path("/go/:u1/:u2")
            public void blah(
                    @HeaderParam(format = "dd/MM/uuuu") LocalDate h1,
                    @HeaderParam(format = "uuuu-MM-dd") LocalDate h2,
                    @HeaderParam(format = "dd/MM/uuuu HH:mm") LocalDateTime h3,
                    @HeaderParam(format = "uuuu-MM-dd-HH-mm-ss") LocalDateTime h4,
                    @CookieParam(format = "dd/MM/uuuu") LocalDate c1,
                    @CookieParam(format = "uuuu-MM-dd") LocalDate c2,
                    @CookieParam(format = "dd/MM/uuuu.HH:mm") LocalDateTime c3,
                    @CookieParam(format = "uuuu-MM-dd-HH-mm-ss") LocalDateTime c4,
                    @UriPart(format = "uuuu-MM-dd-HH-mm-ss") LocalDateTime u1,
                    @UriPart(format = "uuuu-MM-dd") LocalDate u2,
                    @PlainBody(format = "MM/dd/uuuu HH:mm:ss") LocalDateTime body)
            {
                LocalDate d1 = LocalDate.of(2017, Month.JUNE, 23);
                LocalDateTime d2 = LocalDateTime.of(2017, Month.JUNE, 23, 17, 01, 22);
                LocalDateTime d3 = LocalDateTime.of(2017, Month.JUNE, 23, 17, 01);
                Assertions.assertEquals(d1, h1);
                Assertions.assertEquals(d1, h2);
                Assertions.assertEquals(d3, h3);
                Assertions.assertEquals(d2, h4);
                Assertions.assertEquals(d1, c1);
                Assertions.assertEquals(d1, c2);
                Assertions.assertEquals(d3, c3);
                Assertions.assertEquals(d2, c4);
                Assertions.assertEquals(d1, u2);
                Assertions.assertEquals(d2, u1);
                Assertions.assertEquals(d2, body);
                FwTester.confirm();
            }
        }).post("/go/2017-06-23-17-01-22/2017-06-23", "06/23/2017 17:01:22", 200, "", Arrays.asList(h1, h2, h3, h4, h5));
    }

    @Test
    public void testDateSet() throws Throwable {
        String jsonIn = "{\"a\": \"23/06/2017\", \"b\": \"2017-06-22\", \"c\": \"2017-06-23 17:39\"}";
        String jsonOut = "{\"a\":\"12/02/2018\",\"b\":\"2018-02-13\",\"c\":\"2018-02-14 10:43\"}";
        FwTester.reflect(new Object() {
            @Post
            @ProducesJson
            @Path("/go")
            public DateSet blah(@JsonBody DateSet in) {
                LocalDate d1 = LocalDate.of(2017, Month.JUNE, 23);
                LocalDate d2 = LocalDate.of(2017, Month.JUNE, 22);
                LocalDateTime d3 = LocalDateTime.of(2017, Month.JUNE, 23, 17, 39);
                Assertions.assertEquals(d1, in.getA());
                Assertions.assertEquals(d2, in.getB());
                Assertions.assertEquals(d3, in.getC());
                FwTester.confirm();
                return new DateSet(
                        LocalDate.of(2018, Month.FEBRUARY, 12),
                        LocalDate.of(2018, Month.FEBRUARY, 13),
                        LocalDateTime.of(2018, Month.FEBRUARY, 14, 10, 43));
            }
        }).post("/go", jsonIn, 200, jsonOut, Collections.emptyList());
    }

    @Test
    public void testDateProp() throws Throwable {
        FwTester.reflect(new Object() {
            @Post
            @Path("/go")
            public void blah(
                    @JsonBodyPlainProperty(format = "dd/MM/uuuu") LocalDate a,
                    @JsonBodyPlainProperty() int b)
            {
                LocalDate d1 = LocalDate.of(2017, Month.JUNE, 23);
                Assertions.assertEquals(d1, a);
                Assertions.assertEquals(1234, b);
                FwTester.confirm();
            }
        }).post("/go", "{\"a\": \"23/06/2017\", \"b\": 1234}", 200, "", Collections.emptyList());
    }
}
