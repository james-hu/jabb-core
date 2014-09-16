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
public class HibernateDaoFactory {

	public static <T extends Serializable> HibernateDao<T> createDao(final Class<T> entityClass, SessionFactory sessionFactory){
		HibernateDao<T> dao = new HibernateDao<T>(entityClass, sessionFactory);
		return dao;
	}
}
