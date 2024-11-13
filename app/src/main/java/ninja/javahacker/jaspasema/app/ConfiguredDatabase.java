package ninja.javahacker.jaspasema.app;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import jakarta.persistence.Entity;
import lombok.NonNull;
import ninja.javahacker.jaspasema.service.JaspasemaRoute;
import ninja.javahacker.jpasimpletransactions.Database;
import ninja.javahacker.jpasimpletransactions.config.ProviderConnectorFactory;
import org.reflections.Reflections;

/**
 * Implementations wraps {@link JaspasemaRoute}s within transactions, adding the necessary logic for creating or
 * joining a transaction and properly finishing it.
 * @author Victor Williams Stafusa da Silva
 */
@FunctionalInterface
public interface ConfiguredDatabase {

    /**
     * Wraps a {@link JaspasemaRoute} within a transaction, adding the necessary logic for creating or joining
     * a transaction and properly finishing it.
     * @param op The operation to be wrapped within a transaction.
     * @return The wrapped operation.
     * @throws IllegalArgumentException If {@code op} is {@code null}.
     */
    public JaspasemaRoute transact(@NonNull JaspasemaRoute op);

    /**
     * Provides a implementation of {@link ConfiguredDatabase} that instead of properly adding transactions to
     * {@link JaspasemaRoute}, just does nothing.
     * @return a implementation of {@link ConfiguredDatabase} that does nothing.
     */
    @SuppressFBWarnings("FII_USE_FUNCTION_IDENTITY")
    public static ConfiguredDatabase nop() {
        return op -> op;
    }

    /**
     * Creates a {@link ConfiguredDatabase} instance that wraps {@link JaspasemaRoute}s into transactions
     *     created by the given {@code provider}, which will be defined as the primary database.
     * @param provider Provides the database connections.
     * @param packageRoots The packages that contains the {@code #64;Entity} classes.
     * @return A {@link ConfiguredDatabase} instance that wraps {@link JaspasemaRoute}s into transactions
     *     created by the given {@code provider}.
     * @throws IllegalArgumentException If {@code provider} or {@code packageRoots} are {@code null} or if
     *     {@code packageRoots} contains any {@code null} item.
     */
    public static ConfiguredDatabase primary(
            @NonNull ProviderConnectorFactory<?> provider,
            @NonNull String... packageRoots)
    {
        return singular(provider, true, packageRoots);
    }

    /**
     * Creates a {@link ConfiguredDatabase} instance that wraps {@link JaspasemaRoute}s into transactions
     *     created by the given {@code provider}, which will be defined as a non-primary database.
     * @param provider Provides the database connections.
     * @param packageRoots The packages that contains the {@code #64;Entity} classes.
     * @return A {@link ConfiguredDatabase} instance that wraps {@link JaspasemaRoute}s into transactions
     *     created by the given {@code provider}.
     * @throws IllegalArgumentException If {@code provider} or {@code packageRoots} are {@code null} or if
     *     {@code packageRoots} contains any {@code null} item.
     */
    public static ConfiguredDatabase secondary(
            @NonNull ProviderConnectorFactory<?> provider,
            @NonNull String... packageRoots)
    {
        return singular(provider, false, packageRoots);
    }

    /**
     * Creates a {@link ConfiguredDatabase} instance that wraps {@link JaspasemaRoute}s into transactions
     *     created by the given {@code provider}.
     * @param provider Provides the database connections.
     * @param primary Wether the {@code provider} will be defined as the primary database.
     * @param packageRoots The packages that contains the {@code #64;Entity} classes.
     * @return A {@link ConfiguredDatabase} instance that wraps {@link JaspasemaRoute}s into transactions
     *     created by the given {@code provider}.
     * @throws IllegalArgumentException If {@code provider} or {@code packageRoots} are {@code null} or if
     *     {@code packageRoots} contains any {@code null} item.
     */
    public static ConfiguredDatabase singular(
            @NonNull ProviderConnectorFactory<?> provider,
            boolean primary,
            @NonNull String... packageRoots)
    {
        var allClasses = new HashSet<Class<?>>(64);
        for (var s : packageRoots) {
            if (s == null) throw new IllegalArgumentException("Can't have a null package root.");
            allClasses.addAll(new Reflections(s).getTypesAnnotatedWith(Entity.class));
        }
        var f = provider.withEntities(allClasses);
        var conn = f.connect();
        Database.addConnector(conn, primary);
        return op -> conn.transact(JaspasemaRoute.class, op);
    }

    /**
     * Combines a lot of {@link ConfiguredDatabase} into a single one, wrapping one into the other, the laters
     * inside the formers.
     * Thus, each wrapped {@link JaspasemaRoute} is transacted according to all {@link ConfiguredDatabase}s instances.
     * @param databases The {@link ConfiguredDatabase}s instances to be combined into one.
     * @return A combined {@link ConfiguredDatabase}.
     */
    public static ConfiguredDatabase join(ConfiguredDatabase... databases) {
        return join(Arrays.asList(databases));
    }

    /**
     * Combines a lot of {@link ConfiguredDatabase} into a single one, wrapping one into the other, the laters
     * inside the formers.
     * Thus, each wrapped {@link JaspasemaRoute} is transacted according to all {@link ConfiguredDatabase}s instances.
     * @param databases The {@link ConfiguredDatabase}s instances to be combined into one.
     * @return A combined {@link ConfiguredDatabase}.
     */
    public static ConfiguredDatabase join(Collection<ConfiguredDatabase> databases) {
        return op -> {
            var newOp = op;
            for (ConfiguredDatabase cd : databases) {
                newOp = cd.transact(newOp);
            }
            return newOp;
        };
    }
}
