package ninja.javahacker.jaspasema.conn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;
import lombok.Synchronized;

/**
 * @author Victor Williams Stafusa da Silva
 */
public class Database {
    private static final AtomicReference<String> DEFAULT_CONNECTOR_NAME = new AtomicReference<>();
    private static final Map<String, Connector> MAP = new ConcurrentHashMap<>();

    private Database() {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Synchronized
    public static Connector getDefaultConnector() {
        String name = DEFAULT_CONNECTOR_NAME.get();
        if (name == null) throw new IllegalStateException("No registered default persistence unit.");
        return getConnector(name);
    }

    @NonNull
    @Synchronized
    public static Connector getConnector(@NonNull String persistenceUnitName) {
        Connector c = MAP.get(persistenceUnitName);
        if (c != null) return c;
        return addConnector(persistenceUnitName, persistenceUnitName.equals(DEFAULT_CONNECTOR_NAME.get()));
    }

    public static Connector setDefaultConnector(@NonNull String persistenceUnitName) {
        return addConnector(persistenceUnitName, true);
    }

    public static Connector setSecondaryConnector(@NonNull String persistenceUnitName) {
        return addConnector(persistenceUnitName, false);
    }

    @Synchronized
    public static Connector addConnector(@NonNull String persistenceUnitName, boolean defaultConnector) {
        Connector c = MAP.computeIfAbsent(persistenceUnitName, n -> new Connector(persistenceUnitName));
        if (defaultConnector) DEFAULT_CONNECTOR_NAME.set(persistenceUnitName);
        return c;
    }

    public static ExtendedEntityManager getEntityManager() {
        return getDefaultConnector().getEntityManager();
    }

    public static ExtendedEntityManager getEntityManager(@NonNull String persistenceUnitName) {
        return getConnector(persistenceUnitName).getEntityManager();
    }
}
