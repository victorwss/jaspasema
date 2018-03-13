package br.gov.sp.prefeitura.smit.cgtic.jaspasema.conn;

import javax.persistence.EntityManager;
import lombok.NonNull;

/**
 * @author Victor Williams Stafusa da Silva
 */
public interface ExtendedEntityManager extends EntityManager {

    public default boolean isNew(@NonNull Object obj) {
        return getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(obj) == null;
    }

    public default <T> T save(@NonNull T obj) {
        if (!isNew(obj)) {
            T obj2 = merge(obj);
            if (obj != obj2) refresh(obj);
        } else if (!contains(obj)) {
            persist(obj);
        }
        return obj;
    }

    public static ExtendedEntityManager wrap(@NonNull EntityManager em) {
        return new SpecialEntityManager(em);
    }
}
