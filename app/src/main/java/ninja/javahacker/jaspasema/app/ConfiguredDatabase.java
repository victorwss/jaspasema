package ninja.javahacker.jaspasema.app;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import javax.persistence.Entity;
import lombok.NonNull;
import ninja.javahacker.jaspasema.service.JaspasemaRoute;
import ninja.javahacker.jpasimpletransactions.Connector;
import ninja.javahacker.jpasimpletransactions.Database;
import ninja.javahacker.jpasimpletransactions.PersistenceProperties;
import org.reflections.Reflections;

/**
 * @author Victor Williams Syafusa da Silva
 */
@FunctionalInterface
public interface ConfiguredDatabase {

    public JaspasemaRoute transact(@NonNull JaspasemaRoute op);

    @SuppressFBWarnings("FII_USE_FUNCTION_IDENTITY")
    public static ConfiguredDatabase nop() {
        return op -> op;
    }

    public static ConfiguredDatabase primary(
            @NonNull String packageRoot,
            @NonNull PersistenceProperties p)
    {
        return singular(packageRoot, p, true);
    }

    public static ConfiguredDatabase secondary(
            @NonNull String packageRoot,
            @NonNull PersistenceProperties p)
    {
        return singular(packageRoot, p, false);
    }

    public static ConfiguredDatabase singular(
            @NonNull String packageRoot,
            @NonNull PersistenceProperties p,
            boolean primary)
    {
        Reflections reflections = new Reflections(packageRoot);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Entity.class);
        Connector conn = Connector.withoutXml(classes, p);
        Database.addConnector(conn, primary);
        return op -> conn.transact(JaspasemaRoute.class, op);
    }

    public static ConfiguredDatabase join(ConfiguredDatabase... databases) {
        return join(Arrays.asList(databases));
    }

    public static ConfiguredDatabase join(Collection<ConfiguredDatabase> databases) {
        return op -> {
            for (ConfiguredDatabase cd : databases) {
                op = cd.transact(op);
            }
            return op;
        };
    }
}
