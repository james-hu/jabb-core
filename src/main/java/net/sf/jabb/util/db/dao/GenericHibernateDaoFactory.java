/**
 * 
 */
package net.sf.jabb.util.db.dao;

import java.io.Serializable;

import org.hibernate.SessionFactory;

/**
 * Factory to create Hibernate DAO objects
 * @author james.hu
 *
 */
public class GenericHibernateDaoFactory {

	public static <T extends Serializable> GenericHibernateDao<T> createDao(final Class<T> entityClass, SessionFactory sessionFactory){
		GenericHibernateDao<T> dao = new GenericHibernateDao<T>(entityClass, sessionFactory);
		return dao;
	}
}
