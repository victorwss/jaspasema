package ninja.javahacker.jaspasema.conn;

import com.xjfunctions.XConsumer;
import com.xjfunctions.XFunction;
import com.xjfunctions.XLongFunction;
import com.xjfunctions.XRunnable;
import com.xjfunctions.XSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import spark.Route;

/**
 * @author Victor Williams Stafusa da Silva
 */
@Slf4j
public class Connector implements AutoCloseable {

    @Getter
    private final String persistenceUnitName;

    private final EntityManagerFactory emf;

    private final ThreadLocal<ExtendedEntityManager> managers;

    /*package*/ Connector(@NonNull String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
        this.emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        this.managers = new ThreadLocal<>();
    }

    public ExtendedEntityManager getEntityManager() {
        ExtendedEntityManager em = managers.get();
        if (em == null) throw new IllegalStateException();
        return em;
    }

    private ExtendedEntityManager createNewEntityManager() {
        ExtendedEntityManager em = new SpecialEntityManager(emf.createEntityManager());
        managers.set(em);
        return em;
    }

    public <A, B> Function<A, B> transacionarFunction(@NonNull Function<A, B> trans) {
        return transacionarXFunction(XFunction.wrap(trans)).unchecked();
    }

    public <A, B> XFunction<A, B> transacionarXFunction(@NonNull XFunction<A, B> trans) {
        return in -> executar(() -> trans.apply(in));
    }

    public <B> LongFunction<B> transacionarLongFunction(@NonNull LongFunction<B> trans) {
        return transacionarXLongFunction(XLongFunction.wrap(trans)).unchecked();
    }

    public <B> XLongFunction<B> transacionarXLongFunction(@NonNull XLongFunction<B> trans) {
        return in -> executar(() -> trans.apply(in));
    }

    public <E> Consumer<E> transacionarConsumer(@NonNull Consumer<E> trans) {
        return transacionarXConsumer(XConsumer.wrap(trans)).unchecked();
    }

    public <E> XConsumer<E> transacionarXConsumer(@NonNull XConsumer<E> trans) {
        return in -> {
            executar(() -> {
                trans.accept(in);
                return this;
            });
        };
    }

    public Runnable transacionarRunnable(@NonNull Runnable trans) {
        return transacionarXRunnable(XRunnable.wrap(trans)).unchecked();
    }

    public XRunnable transacionarXRunnable(@NonNull XRunnable trans) {
        return () -> {
            executar(() -> {
                trans.run();
                return this;
            });
        };
    }

    public <E> Supplier<E> transacionarSupplier(@NonNull Supplier<E> trans) {
        return transacionarXSupplier(XSupplier.wrap(trans)).unchecked();
    }

    public <E> XSupplier<E> transacionarXSupplier(@NonNull XSupplier<E> trans) {
        return () -> executar(trans);
    }

    public Route transacionarRoute(@NonNull Route trans) {
        return (rq, rp) -> transacionarXSupplier(() -> trans.handle(rq, rp)).unchecked().get();
    }

    public <E> E executar(@NonNull XSupplier<E> trans) throws Throwable {
        ExtendedEntityManager jaExistente = managers.get();
        ExtendedEntityManager atual = jaExistente == null ? createNewEntityManager() : jaExistente;
        boolean ok = false;
        try {
            if (jaExistente == null) {
                managers.set(atual);
                try {
                    atual.getTransaction().begin();
                    log.info("Iniciando uma nova transação.");
                } catch (RuntimeException e) {
                    if (!e.getClass().getName().equals("org.hibernate.exception.JDBCConnectionException")
                            || !e.getMessage().equals("Unable to acquire JDBC Connection"))
                    {
                        throw e;
                    }
                    log.debug("Renovando conexão com o banco de dados.");
                    atual.close();
                    atual = createNewEntityManager();
                    managers.set(atual);
                    atual.getTransaction().begin();
                }
            }
            E resultado = trans.get();
            ok = true;
            return resultado;
        } finally {
            if (jaExistente == null) {
                try {
                    if (ok) {
                        atual.getTransaction().commit();
                        log.info("Transação finalizada com commit.");
                    } else {
                        atual.getTransaction().rollback();
                        log.warn("Transação finalizada com rollback.");
                    }
                } finally {
                    atual.clear();
                    atual.close();
                    managers.remove();
                }
            }
        }
    }

    @Override
    public void close() {
        emf.close();
    }
}
