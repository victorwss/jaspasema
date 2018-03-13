package br.gov.sp.prefeitura.smit.cgtic.jaspasema.conn;

import javax.persistence.EntityManager;
import lombok.NonNull;
import lombok.experimental.Delegate;

/**
 * @author Victor Williams Stafusa da Silva
 */
@SuppressWarnings("rawtypes")
class SpecialEntityManager implements ExtendedEntityManager {
    @Delegate(types = EntityManager.class, excludes = DoNotDelegateEntityManager.class)
    private final EntityManager delegate;

    public SpecialEntityManager(@NonNull EntityManager em) {
        this.delegate = em;
    }

    @Override
    public void remove(Object obj) {
        if (obj != null && !isNew(obj)) delegate.remove(obj);
    }

    private static interface DoNotDelegateEntityManager {
        public void remove(Object obj);
    }
}
