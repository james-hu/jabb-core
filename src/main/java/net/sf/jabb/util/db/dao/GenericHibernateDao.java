/**
 * 
 */
package net.sf.jabb.util.db.dao;

import java.io.Serializable;

import org.hibernate.SessionFactory;

/**
 * The concrete subclass of AbstractHibernateDao
 * @author james.hu
 *
 */
public class GenericHibernateDao<T extends Serializable> extends
		AbstractHibernateDao<T> {

	public GenericHibernateDao(final Class< T> entityClass, final SessionFactory sessionFactory){
		super(entityClass);
		this.setSessionFactory(sessionFactory);
	}
}
