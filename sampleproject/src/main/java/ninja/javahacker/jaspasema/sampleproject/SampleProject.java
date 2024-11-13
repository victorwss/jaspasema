package ninja.javahacker.jaspasema.sampleproject;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.io.IOException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ninja.javahacker.jaspasema.exceptions.MalformedProcessorException;
import ninja.javahacker.jaspasema.exceptions.badmapping.BadServiceMappingException;
import ninja.javahacker.jaspasema.sampleproject.author.AuthorService;
import ninja.javahacker.jaspasema.sampleproject.book.BookService;
import ninja.javahacker.jaspasema.sampleproject.publisher.PublisherService;
import ninja.javahacker.jaspasema.service.JaspasemaRoute;
import ninja.javahacker.jaspasema.service.ServiceConfigurer;
import ninja.javahacker.jaspasema.template.AngularTemplate;
import ninja.javahacker.jpasimpletransactions.Connector;
import ninja.javahacker.jpasimpletransactions.Database;
import ninja.javahacker.jpasimpletransactions.config.SchemaGenerationAction;
import ninja.javahacker.jpasimpletransactions.eclipselink.EclipselinkConnectorFactory;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Slf4j
public class SampleProject {

    public static void main(String[] args) throws BadServiceMappingException, MalformedProcessorException {
        new SampleProject("SamplePU", 8083, 8084);
    }

    private final Connector conn;
    private final Javalin server;
    private final Javalin adminServer;

    public SampleProject(@NonNull String pu, int mainPort, int adminPort) throws BadServiceMappingException, MalformedProcessorException {
        log.info("Starting application...");

        conn = new EclipselinkConnectorFactory()
                .withUrl("jdbc:mysql://localhost:3306/sample?zeroDateTimeBehavior=convertToNull&amp;autoReconnect=true&amp;serverTimezone=UTC")
                .withUser("root")
                .withPassword("root")
                .withSchemaGenerationAction(SchemaGenerationAction.NONE)
                //.withShowSql(true)
                .withDriver(com.mysql.cj.jdbc.Driver.class)
                .connect();
                // <property name="hibernate.hbm2ddl.auto" value="validate" />

        Database.setDefaultConnector(conn);

        this.server = Javalin.create(ctx -> ctx.staticFiles.add("/html")).start(mainPort);

        var sc = ServiceConfigurer
                .forServices(new AuthorService(), new PublisherService(), new BookService())
                .wrap(jc -> conn.transact(JaspasemaRoute.class, jc));
        sc.configure(server);

        server.get("/hello", this::hello);
        String url = "http://localhost:" + mainPort + "/";
        server.get("/js", new AngularTemplate(() -> url, () -> "sample", () -> "services").createStub(sc));
        server.exception(Exception.class, (t, ctx) -> {
            t.printStackTrace();
            try {
                t.printStackTrace(ctx.res().getWriter());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        this.adminServer = Javalin.create().start(adminPort);
        adminServer.get("/shutdown", this::shutdown);

        log.info("Application started.");
    }

    private String shutdown(@NonNull Context ctx) {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // Ignora.
            }
            server.stop();
            adminServer.stop();
            conn.close();
            log.info("Application finished.");
        });
        t.start();
        return "<html><head><title>Bye.</title></head><body><p>See ya later.</p></body></html>";
    }

    private String hello(@NonNull Context ctx) {
        return "<html><head><title>Hi.</title></head><body><p>Hello and welcome. :)</p></body></html>";
    }
}
