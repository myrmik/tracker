package ga.asev.dao;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;


@Transactional
public abstract class BaseDao<K extends Serializable, D> {

    @PersistenceContext
    EntityManager entityManager;

    private Class<D> typeOfDomain;

    protected Session getCurrentSession()  {
        return entityManager.unwrap(Session.class);
    }

    @SuppressWarnings("unchecked")
    public BaseDao() {
        this.typeOfDomain = (Class<D>)
                ((ParameterizedType)getClass()
                        .getGenericSuperclass())
                        .getActualTypeArguments()[1];
    }

    @SuppressWarnings("unchecked")
    protected D insert(D domainObject) {
        getCurrentSession().saveOrUpdate(domainObject);
        return domainObject;
    }

    @SuppressWarnings("unchecked")
    protected List<D> insertAll(List<D> list) {
        list.forEach(d -> getCurrentSession().saveOrUpdate(d));
        return list;
    }

    @SuppressWarnings("unchecked")
    protected D select(K id) {
        return (D)getCurrentSession().get(typeOfDomain, id);
    }


    @SuppressWarnings("unchecked")
    protected D selectByCriteria(String criteriaName, Object criteriaValue) {
        return (D) getCurrentSession()
                .createCriteria(typeOfDomain)
                .add(Restrictions.eq(criteriaName, criteriaValue))
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    protected List<D> selectAll() {
        return getCurrentSession().createCriteria(typeOfDomain).list();
    }

    protected void delete(K id) {
        D domainObject = select(id);
        if (domainObject != null)
            getCurrentSession().delete(domainObject);
    }


}
