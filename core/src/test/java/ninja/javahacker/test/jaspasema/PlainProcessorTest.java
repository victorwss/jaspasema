package ninja.javahacker.test.jaspasema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.Value;
import ninja.javahacker.jaspasema.CookieParam;
import ninja.javahacker.jaspasema.HeaderJsonParam;
import ninja.javahacker.jaspasema.HeaderParam;
import ninja.javahacker.jaspasema.JsonBody;
import ninja.javahacker.jaspasema.JsonBodyPlainProperty;
import ninja.javahacker.jaspasema.JsonBodyProperty;
import ninja.javahacker.jaspasema.Path;
import ninja.javahacker.jaspasema.PlainBody;
import ninja.javahacker.jaspasema.ProducesJson;
import ninja.javahacker.jaspasema.ProducesPlain;
import ninja.javahacker.jaspasema.QueryPart;
import ninja.javahacker.jaspasema.SessionParam;
import ninja.javahacker.jaspasema.UriPart;
import ninja.javahacker.jaspasema.service.ReturnMapper;
import ninja.javahacker.jaspasema.verbs.Get;
import ninja.javahacker.jaspasema.verbs.Post;
import ninja.javahacker.test.jaspasema.ApiTester.Header;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Victor Williams Stafusa da Silva
 */
@SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
public class PlainProcessorTest {

    @Value
    public static class TestValue {
        private final int a;

        @NonNull
        private final String b;

        @NonNull
        private final Nested c;

        @Value
        public static class Nested {
            private final int d;
            private final long e;

            @JsonCreator
            public Nested(@JsonProperty("d") int d, @JsonProperty("e") int e) {
                this.d = d;
                this.e = e;
            }
        }

        @JsonCreator
        public TestValue(@JsonProperty("a") int a, @JsonProperty("b") String b, @JsonProperty("c") Nested c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    @Value
    public static class DateSet {
        @NonNull
        @JsonFormat(pattern = "dd/MM/yyyy")
        private final LocalDate a;

        @NonNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        private final LocalDate b;

        @NonNull
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private final LocalDateTime c;

        @JsonCreator
        public DateSet(
                @NonNull @JsonProperty("a") LocalDate a,
                @NonNull @JsonProperty("b") LocalDate b,
                @NonNull @JsonProperty("c") LocalDateTime c)
        {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    @Test
    public void testPlainBody() throws Throwable {
        FwTester.reflect(fwt -> new Object() {
            @Post
            @Path("/go")
            public void blah(@PlainBody String a) {
                Assertions.assertEquals("Teste", a);
                fwt.confirm();
            }
        }).post("/go", "Teste", 200, ReturnMapper.DEFAULT_HTML_200, List.of());
    }

    @Test
    public void testPlainReturn() throws Throwable {
        FwTester.reflect(fwt -> new Object() {
            @Post
            @Path("/go")
            @ProducesPlain
            public String blah(@PlainBody String a) {
                Assertions.assertEquals("Teste", a);
                fwt.confirm();
                return "XYZ";
            }
        }).post("/go", "Teste", 200, "XYZ", List.of());
    }

    @Test
    public void testTwoParameters() throws Throwable {
        FwTester.reflect(fwt -> new Object() {
            @Post
            @Path("/go")
            public void blah(@PlainBody String a, @PlainBody String b) {
                Assertions.assertAll(
                        () -> Assertions.assertEquals("Teste", a),
                        () -> Assertions.assertEquals("Teste", b)
                );
                fwt.confirm();
            }
        }).post("/go", "Teste", 200, ReturnMapper.DEFAULT_HTML_200, List.of());
    }

    @Test
    public void testNumericBody() throws Throwable {
        FwTester.reflect(fwt -> new Object() {
            @Post
            @Path("/go")
            public void blah(@PlainBody int a, @PlainBody Integer b, @PlainBody byte c) {
                Assertions.assertAll(
                        () -> Assertions.assertEquals(123, a),
                        () -> Assertions.assertEquals(Integer.valueOf(123), b),
                        () -> Assertions.assertEquals(123, c)
                );
                fwt.confirm();
            }
        }).post("/go", "123", 200, ReturnMapper.DEFAULT_HTML_200, List.of());
    }

    @Test
    public void testJsonBody() throws Throwable {
        FwTester.reflect(fwt -> new Object() {
            @Post
            @Path("/go")
            public void blah(@JsonBody TestValue x) {
                Assertions.assertAll(
                        () -> Assertions.assertEquals(123, x.getA()),
                        () -> Assertions.assertEquals("Verde", x.getB()),
                        () -> Assertions.assertEquals(321, x.getC().getD()),
                        () -> Assertions.assertEquals(2233L, x.getC().getE())
                );
                fwt.confirm();
            }
        }).post("/go", "{\"a\": 123, \"b\": \"Verde\", \"c\": { \"d\": 321, \"e\": 2233}}",
                200,
                ReturnMapper.DEFAULT_HTML_200,
                List.of());
    }

    @Test
    public void testJsonReturn() throws Throwable {
        FwTester.reflect(fwt -> new Object() {
            @Post
            @Path("/go")
            @ProducesJson
            public TestValue.Nested blah(@PlainBody String a) {
                Assertions.assertEquals("Teste", a);
                fwt.confirm();
                return new TestValue.Nested(2, 3);
            }
        }).post("/go", "Teste", 200, "{\"d\":2,\"e\":3}", List.of());
    }

    @Test
    public void testUriParams() throws Throwable {
        FwTester.reflect(fwt -> new Object() {
            @Post
            @Path("/go/:a/xxx/:c/yyy/zzz/:b/qqqq")
            public void blah(@UriPart int a, @UriPart String b, @UriPart byte c) {
                Assertions.assertAll(
                        () -> Assertions.assertEquals(123, a),
                        () -> Assertions.assertEquals("Laranja", b),
                        () -> Assertions.assertEquals(33, c)
                );
                fwt.confirm();
            }
        }).post("/go/123/xxx/33/yyy/zzz/Laranja/qqqq", "", 200, ReturnMapper.DEFAULT_HTML_200, List.of());
    }

    @Test
    public void testQueryParams() throws Throwable {
        var url = "/go?a=123&b=Laranja&c=33&item=banana&item=abacaxi&item=uva&id=7777&id=44444&id=987654321";
        FwTester.reflect(fwt -> new Object() {
            @Post
            @Path("/go")
            public void blah(
                    @QueryPart int a,
                    @QueryPart String b,
                    @QueryPart byte c,
                    @QueryPart List<String> item,
                    @QueryPart List<Long> id)
            {
                Assertions.assertAll(
                        () -> Assertions.assertEquals(123, a),
                        () -> Assertions.assertEquals("Laranja", b),
                        () -> Assertions.assertEquals(33, c),
                        () -> Assertions.assertEquals(List.of("banana", "abacaxi", "uva"), item),
                        () -> Assertions.assertEquals(List.of(7777L, 44444L, 987654321L), id)
                );
                fwt.confirm();
            }
        }).post(url, "", 200, ReturnMapper.DEFAULT_HTML_200, List.of());
    }

    @Test
    public void testJsonBodyParts() throws Throwable {
        var json = "{\"x\": {\"a\": 123, \"b\": \"Verde\", \"c\": { \"d\": 321, \"e\": 2233}}, \"a\": 999, \"b\": \"Azul\"}";
        FwTester.reflect(fwt -> new Object() {
            @Post
            @Path("/go")
            public void blah(@JsonBodyProperty int a, @JsonBodyProperty String b, @JsonBodyProperty TestValue x) {
                Assertions.assertAll(
                        () -> Assertions.assertEquals(123, x.getA()),
                        () -> Assertions.assertEquals("Verde", x.getB()),
                        () -> Assertions.assertEquals(321, x.getC().getD()),
                        () -> Assertions.assertEquals(2233L, x.getC().getE()),
                        () -> Assertions.assertEquals(999, a),
                        () -> Assertions.assertEquals("Azul", b)
                );
                fwt.confirm();
            }
        }).post("/go", json, 200, ReturnMapper.DEFAULT_HTML_200, List.of());
    }

    @Test
    public void testCookieParams() throws Throwable {
        FwTester.reflect(fwt -> new Object() {
            @Post
            @Path("/go")
            public void blah(@CookieParam int a, @CookieParam String b, @CookieParam byte c) {
                Assertions.assertAll(
                        () -> Assertions.assertEquals(123, a),
                        () -> Assertions.assertEquals("Laranja", b),
                        () -> Assertions.assertEquals(33, c)
                );
                fwt.confirm();
            }
        }).post("/go", "", 200, ReturnMapper.DEFAULT_HTML_200, List.of(new Header("Cookie", "a=123; b=Laranja; c=33")));
    }

    @Test
    public void testHeaderParams() throws Throwable {
        var headers = List.of(new Header("a", "123"), new Header("b", "Laranja"), new Header("c", "33"));
        FwTester.reflect(fwt -> new Object() {
            @Get
            @Path("/go")
            public void blah(@HeaderParam int a, @HeaderParam String b, @HeaderParam byte c) {
                Assertions.assertAll(
                        () -> Assertions.assertEquals(123, a),
                        () -> Assertions.assertEquals("Laranja", b),
                        () -> Assertions.assertEquals(33, c)
                );
                fwt.confirm();
            }
        }).get("/go", 200, ReturnMapper.DEFAULT_HTML_200, headers);
    }

    @Test
    public void testHeaderJsonParams() throws Throwable {
        var h1 = new Header("blue", "{\"a\": 444, \"b\": \"Azul\", \"c\": {\"d\": 555, \"e\": 7777}}");
        var h2 = new Header("yellow", "{\"d\": 888, \"e\": 9999}");
        FwTester.reflect(fwt -> new Object() {
            @Post
            @Path("/go")
            public void blah(@HeaderJsonParam TestValue blue, @HeaderJsonParam TestValue.Nested yellow) {
                Assertions.assertAll(
                        () -> Assertions.assertEquals(444, blue.getA()),
                        () -> Assertions.assertEquals("Azul", blue.getB()),
                        () -> Assertions.assertEquals(555, blue.getC().getD()),
                        () -> Assertions.assertEquals(7777L, blue.getC().getE()),
                        () -> Assertions.assertEquals(888, yellow.getD()),
                        () -> Assertions.assertEquals(9999L, yellow.getE())
                );
                fwt.confirm();
            }
        }).post("/go", "", 200, ReturnMapper.DEFAULT_HTML_200, List.of(h1, h2));
    }

    @Test
    public void testSessionParams() throws Throwable {
        var sessionObjectA = new Object();
        var sessionObjectB = new Object();
        Map<String, Object> session = new HashMap<>();
        session.put("a", sessionObjectA);
        session.put("b", sessionObjectB);
        FwTester.reflect(fwt -> new Object() {
            @Post
            @Path("/go")
            public void blah(@SessionParam Object a, @SessionParam Object b) {
                Assertions.assertAll(
                        () -> Assertions.assertEquals(sessionObjectA, a),
                        () -> Assertions.assertEquals(sessionObjectB, b)
                );
                fwt.confirm();
            }
        }, session).post("/go", "", 200, ReturnMapper.DEFAULT_HTML_200, List.of());
    }

    @Test
    public void testEverything() throws Throwable {
        var json = "{\"a\": 123, \"b\": \"Verde\", \"c\": {\"d\": 321, \"e\": 2233}}";
        var h1 = new Header("header1", "Homer");
        var h2 = new Header("header2", "{\"d\": 888, \"e\": 9999}");
        var sessionObjectA = new Object();
        var sessionObjectB = new Object();
        Map<String, Object> session = new HashMap<>();
        session.put("sessionA", sessionObjectA);
        session.put("sessionB", sessionObjectB);
        FwTester.reflect(fwt -> new Object() {
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
                Assertions.assertAll(
                        () -> Assertions.assertEquals(sessionObjectA, sessionA),
                        () -> Assertions.assertEquals(sessionObjectB, sessionB),
                        () -> Assertions.assertEquals("Homer", header1),
                        () -> Assertions.assertEquals(123, x.getA()),
                        () -> Assertions.assertEquals("Verde", x.getB()),
                        () -> Assertions.assertEquals(321, x.getC().getD()),
                        () -> Assertions.assertEquals(2233L, x.getC().getE()),
                        () -> Assertions.assertEquals(321, c.getD()),
                        () -> Assertions.assertEquals(2233L, c.getE()),
                        () -> Assertions.assertEquals("{\"a\": 123, \"b\": \"Verde\", \"c\": {\"d\": 321, \"e\": 2233}}", json),
                        () -> Assertions.assertEquals("abacaxi", xxx),
                        () -> Assertions.assertEquals("ABC", test),
                        () -> Assertions.assertEquals(888, header2.getD()),
                        () -> Assertions.assertEquals(9999L, header2.getE())
                );
                fwt.confirm();
            }
        }, session).post("/go/abacaxi?test=ABC", json, 200, ReturnMapper.DEFAULT_HTML_200, List.of(h1, h2));
    }

    @Test
    public void testDates() throws Throwable {
        var h1 = new Header("h1", "23/06/2017");
        var h2 = new Header("h2", "2017-06-23");
        var h3 = new Header("h3", "23/06/2017 17:01");
        var h4 = new Header("h4", "2017-06-23-17-01-22");
        var h5 = new Header("Cookie", "c1=23/06/2017;c2=2017-06-23;c3=23/06/2017.17:01;c4=2017-06-23-17-01-22");
        var headers = List.of(h1, h2, h3, h4, h5);
        FwTester.reflect(fwt -> new Object() {
            @Post
            @Path("/go/:u1/:u2")
            public void blah(
                    @HeaderParam(dateFormat = "dd/MM/uuuu") LocalDate h1,
                    @HeaderParam(dateFormat = "uuuu-MM-dd") LocalDate h2,
                    @HeaderParam(dateFormat = "dd/MM/uuuu HH:mm") LocalDateTime h3,
                    @HeaderParam(dateFormat = "uuuu-MM-dd-HH-mm-ss") LocalDateTime h4,
                    @CookieParam(dateFormat = "dd/MM/uuuu") LocalDate c1,
                    @CookieParam(dateFormat = "uuuu-MM-dd") LocalDate c2,
                    @CookieParam(dateFormat = "dd/MM/uuuu.HH:mm") LocalDateTime c3,
                    @CookieParam(dateFormat = "uuuu-MM-dd-HH-mm-ss") LocalDateTime c4,
                    @UriPart(dateFormat = "uuuu-MM-dd-HH-mm-ss") LocalDateTime u1,
                    @UriPart(dateFormat = "uuuu-MM-dd") LocalDate u2,
                    @PlainBody(dateFormat = "MM/dd/uuuu HH:mm:ss") LocalDateTime body)
            {
                LocalDate d1 = LocalDate.of(2017, Month.JUNE, 23);
                LocalDateTime d2 = LocalDateTime.of(2017, Month.JUNE, 23, 17, 01, 22);
                LocalDateTime d3 = LocalDateTime.of(2017, Month.JUNE, 23, 17, 01);
                Assertions.assertAll(
                        () -> Assertions.assertEquals(d1, h1),
                        () -> Assertions.assertEquals(d1, h2),
                        () -> Assertions.assertEquals(d3, h3),
                        () -> Assertions.assertEquals(d2, h4),
                        () -> Assertions.assertEquals(d1, c1),
                        () -> Assertions.assertEquals(d1, c2),
                        () -> Assertions.assertEquals(d3, c3),
                        () -> Assertions.assertEquals(d2, c4),
                        () -> Assertions.assertEquals(d1, u2),
                        () -> Assertions.assertEquals(d2, u1),
                        () -> Assertions.assertEquals(d2, body)
                );
                fwt.confirm();
            }
        }).post("/go/2017-06-23-17-01-22/2017-06-23", "06/23/2017 17:01:22", 200, ReturnMapper.DEFAULT_HTML_200, headers);
    }

    @Test
    public void testDateSet() throws Throwable {
        var jsonIn = "{\"a\": \"23/06/2017\", \"b\": \"2017-06-22\", \"c\": \"2017-06-23 17:39\"}";
        var jsonOut = "{\"a\":\"12/02/2018\",\"b\":\"2018-02-13\",\"c\":\"2018-02-14 10:43\"}";
        FwTester.reflect(fwt -> new Object() {
            @Post
            @ProducesJson
            @Path("/go")
            public DateSet blah(@JsonBody DateSet in) {
                LocalDate d1 = LocalDate.of(2017, Month.JUNE, 23);
                LocalDate d2 = LocalDate.of(2017, Month.JUNE, 22);
                LocalDateTime d3 = LocalDateTime.of(2017, Month.JUNE, 23, 17, 39);
                Assertions.assertAll(
                        () -> Assertions.assertEquals(d1, in.getA()),
                        () -> Assertions.assertEquals(d2, in.getB()),
                        () -> Assertions.assertEquals(d3, in.getC())
                );
                fwt.confirm();
                return new DateSet(
                        LocalDate.of(2018, Month.FEBRUARY, 12),
                        LocalDate.of(2018, Month.FEBRUARY, 13),
                        LocalDateTime.of(2018, Month.FEBRUARY, 14, 10, 43));
            }
        }).post("/go", jsonIn, 200, jsonOut, List.of());
    }

    @Test
    public void testDateProp() throws Throwable {
        FwTester.reflect(fwt -> new Object() {
            @Post
            @Path("/go")
            public void blah(
                    @JsonBodyPlainProperty(dateFormat = "dd/MM/uuuu") LocalDate a,
                    @JsonBodyPlainProperty() int b)
            {
                LocalDate d1 = LocalDate.of(2017, Month.JUNE, 23);
                Assertions.assertAll(
                        () -> Assertions.assertEquals(d1, a),
                        () -> Assertions.assertEquals(1234, b)
                );
                fwt.confirm();
            }
        }).post("/go", "{\"a\": \"23/06/2017\", \"b\": 1234}", 200, ReturnMapper.DEFAULT_HTML_200, List.of());
    }
}
