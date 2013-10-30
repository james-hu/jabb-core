package net.sf.jabb.util.db.dao;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractHibernateDao <T extends Serializable> {
	static private final Log log = LogFactory.getLog(AbstractHibernateDao.class);
	
	private static enum OperationType{
		CREATE, UPDATE, DELETE
	}

    private static class DelayedOperation <E extends Serializable> implements Runnable{
    	
    	E entity;
    	Serializable id;
    	OperationType operation;
    	AbstractHibernateDao<E> dao;
    	
    	public DelayedOperation(AbstractHibernateDao<E> dao, E entity, OperationType operation){
    		this(dao, entity, null, operation);
    	}

    	public DelayedOperation(AbstractHibernateDao<E> dao, E entity, Serializable id, OperationType operation){
    		this.dao = dao;
    		this.entity = entity;
    		this.id = id;
    		this.operation = operation;
    	}
    	
		@SuppressWarnings("unchecked")
		public void run() {
			// Sessions are manually got because the "dao" object here had not been wrapped by Spring's transaction AOP proxy
			Session session = null;
			Transaction tx = null;
			try{
				session = dao.sessionFactory.openSession(); //.getCurrentSession(); it has to be managed manually
				tx = session.beginTransaction();
		        switch(operation){
			        case CREATE:
			        	session.persist(entity);
			        	break;
			        case UPDATE:
			        	session.merge(entity);
			        	break;
			        case DELETE:
			        	if (entity != null){
				        	session.delete(entity);
			        	}else{
			        		entity = (E) session.get(dao.clazz, id);
			        		session.delete(entity);
			        	}
			        	break;
			        default:
			        	log.error("Unknown transactional operation: " + operation);
		        }
				tx.commit();
			}catch(Exception e){
				log.error("Failed to perform transactional operation: " + this, e);
				if (tx != null){
					tx.rollback();
				}
			}finally{
				if (session != null){
					session.close();
				}
			}
		}
		
		@Override
		public String toString(){
			return operation.toString() + ": " + dao.getEntityClass() 
					+ " - " + (entity == null? id : entity.toString());
		}
    }
    
    private static ExecutorService delayedExecutor = Executors.newFixedThreadPool(2);

    
	private final Class<T> clazz;
	
	@Autowired
    protected SessionFactory sessionFactory;
    
    
    public AbstractHibernateDao(final Class< T> clazzToSet) {
        this.clazz = clazzToSet;
    }
    
    protected Class<?> getEntityClass(){
    	return clazz.getClass();
    }

    @Transactional
    @SuppressWarnings("unchecked")
	public T getById(final Serializable id) {
        if (id == null){
        	throw new IllegalArgumentException();
        }
        Session session = this.getCurrentSession();
        T result = (T) session.get(this.clazz, id);
        return result;
    }
 
    @Transactional
	public List<T> getAll() {
        return getAllByHql(null, null, null, null, null);
    }

	/**
	 * 
	 * @param secondHalfOfHql	parts after "from <class_name> "
	 * @return
	 */
    @Transactional
	public List< T> getAllByHql(String secondHalfOfHql) {
		return getAllByHql(secondHalfOfHql, null, null, null, null);
	}
 
    /**
     * 
     * @param secondHalfOfHql
     * @param offset		the number of first record in the whole query result to be returned, records numbers start from 0
     * @param limit			the maximum number of records to return
     * @return
     */
    @Transactional
	public List< T> getAllByHql(String secondHalfOfHql, Integer offset, Integer limit) {
		return getAllByHql(secondHalfOfHql, null, null, offset, limit);
	}
 
	/**
	 * 
	 * @param secondHalfOfHql	parts after "from <class_name> "
	 * @param paramValues
	 * @param paramTypes
	 * @return
	 */
    @SuppressWarnings("unchecked")
    @Transactional
	public List< T> getAllByHql(String secondHalfOfHql, Object[] paramValues, Type[] paramTypes) {
        return getAllByHql(secondHalfOfHql, paramValues, paramTypes, null, null);
    }
    
    /**
     * 
	 * @param secondHalfOfHql	parts after "from <class_name> "
     * @param paramValues
     * @param paramTypes
     * @param offset		the number of first record in the whole query result to be returned, records numbers start from 0
     * @param limit			the maximum number of records to return
     * @return
     */
    @SuppressWarnings("unchecked")
    @Transactional
	public List< T> getAllByHql(String secondHalfOfHql, Object[] paramValues, Type[] paramTypes, Integer offset, Integer limit) {
    	StringBuilder queryStr = new StringBuilder();
    	queryStr.append("from ")
    		.append(this.clazz.getName())
    		.append(" ");
    	if (secondHalfOfHql != null){
    		queryStr.append(secondHalfOfHql);
     	}
    	
        Session session = this.getCurrentSession();
        //Transaction tx = session.beginTransaction();
    	Query query = session.createQuery(queryStr.toString());
    	setupQuery(query, paramValues, paramTypes, offset, limit);
    	List<T> result = (List<T>) query.list();
        //tx.commit();
        return result;
    }

    
    /**
     * 
     * @param fullSql		
     * @return
     */
    @Transactional
	public List< T> getAllBySql(String fullSql) {
		return getAllBySql(fullSql, null, null, null, null);
	}
	
    /**
     * 
     * @param fullSql
     * @param offset		the number of first record in the whole query result to be returned, records numbers start from 0
     * @param limit			the maximum number of records to return
     * @return
     */
    @Transactional
	public List< T> getAllBySql(String fullSql, Integer offset, Integer limit) {
		return getAllBySql(fullSql, null, null, offset, limit);
	}
 
    /**
     * 
     * @param fullSql		
     * @param paramValues
     * @param paramTypes
     * @return
     */
    @Transactional
	public List< T> getAllBySql(String fullSql, Object[] paramValues, Type[] paramTypes) {
        return getAllBySql(fullSql, paramValues, paramTypes, null, null);
    }
 
    /**
     * 
     * @param fullSql	
     * @param paramValues
     * @param paramTypes
     * @param offset		the number of first record in the whole query result to be returned, records numbers start from 0
     * @param limit			the maximum number of records to return
     * @return
     */
    @SuppressWarnings("unchecked")
    @Transactional
	public List< T> getAllBySql(String fullSql, Object[] paramValues, Type[] paramTypes, Integer offset, Integer limit) {
        Session session = this.getCurrentSession();
    	Query query = session.createSQLQuery(fullSql).addEntity(this.clazz);
    	setupQuery(query, paramValues, paramTypes, offset, limit);
    	List<T> result = (List<T>) query.list();
        return result;
    }
 
    /**
     * Get the count of all records in database
     * @return
     */
    @Transactional
    public int countAll(){
    	return countByHql(null);
    }
    
    /**
     * Get the count of all records in database
     * @param secondHalfOfSql	parts after "from <class_name> "
     * @return
     */
    @Transactional
    public int countByHql(String secondHalfOfSql){
    	return countByHql(secondHalfOfSql, null, null);
    }
    
    /**
     * Get the count of all records in database
     * @param secondHalfOfSql	parts after "from <class_name> "
     * @param paramValues
     * @param paramTypes
     * @return
     */
    @Transactional
    public int countByHql(String secondHalfOfSql, Object[] paramValues, Type[] paramTypes){
    	StringBuilder queryStr = new StringBuilder();
    	queryStr.append("select count(*) from ")
			.append(this.clazz.getName())
			.append(" ");
   		queryStr.append(secondHalfOfSql);
    	
        Session session = this.getCurrentSession();
    	Query query = session.createQuery(queryStr.toString());
    	setupQuery(query, paramValues, paramTypes, null, null);
        return (Integer)query.uniqueResult();
    }
    
    /**
     * Get the count of all records in database
     * @param fullSql
     * @return
     */
    @Transactional
    public int countBySql(String fullSql){
    	return countBySql(fullSql, null, null);
    }
    
    /**
     * Get the count of all records in database
     * @param fullSql
     * @param paramValues
     * @param paramTypes
     * @return
     */
    @Transactional
    public int countBySql(String fullSql, Object[] paramValues, Type[] paramTypes){
        Session session = this.getCurrentSession();
    	Query query = session.createSQLQuery(fullSql);
    	setupQuery(query, paramValues, paramTypes, null, null);
        return (Integer)query.uniqueResult();
    }
    
    /**
     * Setup a query with parameters and other configurations.
     * @param query
     * @param paramValues
     * @param paramTypes
     * @param offset
     * @param limit
     */
    private void setupQuery(Query query, Object[] paramValues, Type[] paramTypes, Integer offset, Integer limit){
    	if (paramValues != null && paramTypes != null){
        	query.setParameters(paramValues, paramTypes);
    	}
    	if (offset != null){
        	query.setFirstResult(offset);
    	}
    	if (limit != null){
        	query.setMaxResults(limit);
    	}
    }
 

    @Transactional
    public void create(final T entity) {
    	this.getCurrentSession().persist(entity);
    }
    
    @Transactional
    public void update(final T entity) {
    	this.getCurrentSession().saveOrUpdate(entity); //.merge(entity);
    }
 
    @Transactional
    public void delete(final T entity) {
    	this.getCurrentSession().persist(entity);
   }
 
    @Transactional
    public void deleteById(final Serializable entityId) {
        final T entity = this.getById(entityId);
        if (entity != null){
            this.delete(entity);
        }else{
        	log.warn("Entity with id '" + entityId + "' does not exist. Deletion skipped.");
        }
    }
    
    public void delayedCreate(final T entity){
    	delayedExecutor.execute(new DelayedOperation<T>(this, entity, OperationType.CREATE));
    }
 
    public void delayedUpdate(final T entity) {
    	delayedExecutor.execute(new DelayedOperation<T>(this, entity, OperationType.UPDATE));
    }
 
    public void delayedDelete(final T entity) {
    	delayedExecutor.execute(new DelayedOperation<T>(this, entity, OperationType.DELETE));
	}
 
    public void delayedDeleteById(final Long entityId) {
    	delayedExecutor.execute(new DelayedOperation<T>(this, null, entityId, OperationType.DELETE));
    }
    

 
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
 
    protected final Session getCurrentSession() {
        return this.sessionFactory.getCurrentSession();
    }
}