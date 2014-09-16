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

public abstract class AbstractHibernateDao <T extends Serializable> {
	static private final Log log = LogFactory.getLog(AbstractHibernateDao.class);
	
	private static enum OperationType{
		CREATE, MERGE, DELETE
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
			        case MERGE:
			        	session.merge(entity);
			        	break;
			        case DELETE:
			        	if (entity != null){
				        	session.delete(entity);
			        	}else{
			        		entity = (E) session.load(dao.clazz, id);
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
    
    private static ExecutorService delayedExecutor = Executors.newFixedThreadPool(5);

    
	private final Class<T> clazz;
	
    private SessionFactory sessionFactory;
    
    
    public AbstractHibernateDao(final Class< T> clazzToSet) {
        this.clazz = clazzToSet;
    }
    
    protected Class<T> getEntityClass(){
    	return clazz;
    }

    @SuppressWarnings("unchecked")
	public T getById(final Serializable id) {
        if (id == null){
        	throw new IllegalArgumentException();
        }
        Session session = this.getCurrentSession();
        T result = (T) session.get(this.clazz, id);
        return result;
    }
 
	public List<T> getAll() {
        return getAllByHql(null, null, null, (Integer)null, (Integer)null);
    }

	/**
	 * 
	 * @param secondHalfOfHql	parts after "from &lt;class_name&gt; "
	 * @return
	 */
	public List< T> getAllByHql(String secondHalfOfHql) {
		return getAllByHql(secondHalfOfHql, null, null, (Integer)null, (Integer)null);
	}
 
    /**
     * 
     * @param secondHalfOfHql
     * @param offset		the number of first record in the whole query result to be returned, records numbers start from 0
     * @param limit			the maximum number of records to return
     * @return
     */
	public List< T> getAllByHql(String secondHalfOfHql, Integer offset, Integer limit) {
		return getAllByHql(secondHalfOfHql, null, null, offset, limit);
	}
 
	/**
	 * 
	 * @param secondHalfOfHql	parts after "from &lt;class_name&gt; "
	 * @param paramValues
	 * @param paramTypes
	 * @return
	 */
	public List< T> getAllByHql(String secondHalfOfHql, Object[] paramValues, Type[] paramTypes) {
        return getAllByHql(secondHalfOfHql, paramValues, paramTypes, null, null);
    }
    
    /**
     * Get all by HQL with one pair of parameterType-value
     * @param secondHalfOfHql
     * @param paramValue
     * @param paramType
     * @return
     */
	public List< T> getAllByHql(String secondHalfOfHql, Object paramValue, Type paramType) {
        return getAllByHql(secondHalfOfHql, new Object[] {paramValue}, new Type[]{paramType}, null, null);
    }
    
	/**
	 * Get all by HQL with two pairs of parameterType-value
	 * @param secondHalfOfHql
	 * @param paramValue1
	 * @param paramType1
	 * @param paramValue2
	 * @param paramType2
	 * @return
	 */
	public List< T> getAllByHql(String secondHalfOfHql, Object paramValue1, Type paramType1, Object paramValue2, Type paramType2) {
        return getAllByHql(secondHalfOfHql, new Object[] {paramValue1, paramValue2}, new Type[]{paramType2, paramType2}, null, null);
    }
    
	/**
	 * Get all by HQL with three pairs of parameterType-value
	 * @param secondHalfOfHql
	 * @param paramValue1
	 * @param paramType1
	 * @param paramValue2
	 * @param paramType2
	 * @param paramValue3
	 * @param paramType3
	 * @return
	 */
	public List< T> getAllByHql(String secondHalfOfHql, Object paramValue1, Type paramType1, Object paramValue2, Type paramType2, Object paramValue3, Type paramType3) {
        return getAllByHql(secondHalfOfHql, new Object[] {paramValue1, paramValue2, paramValue3}, new Type[]{paramType1, paramType2, paramType3}, null, null);
    }
    
    /**
     * 
	 * @param secondHalfOfHql	parts after "from &lt;class_name&gt; "
     * @param paramValues
     * @param paramTypes
     * @param offset		the number of first record in the whole query result to be returned, records numbers start from 0
     * @param limit			the maximum number of records to return
     * @return
     */
    @SuppressWarnings("unchecked")
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
	public List< T> getAllBySql(String fullSql) {
		return getAllBySql(fullSql, null, null, (Integer)null, (Integer)null);
	}
	
    /**
     * Get all by SQL with specific offset and limit
     * @param fullSql
     * @param offset		the number of first record in the whole query result to be returned, records numbers start from 0
     * @param limit			the maximum number of records to return
     * @return
     */
	public List< T> getAllBySql(String fullSql, Integer offset, Integer limit) {
		return getAllBySql(fullSql, null, null, offset, limit);
	}
 
    /**
     * Get all by SQL with pairs of parameterType-value
     * @param fullSql		
     * @param paramValues
     * @param paramTypes
     * @return
     */
	public List< T> getAllBySql(String fullSql, Object[] paramValues, Type[] paramTypes) {
        return getAllBySql(fullSql, paramValues, paramTypes, null, null);
    }
	
	/**
	 * Get all by SQL with one pair of parameterType-value
	 * @param fullSql
	 * @param paramValue
	 * @param paramType
	 * @return
	 */
	public List< T> getAllBySql(String fullSql, Object paramValue, Type paramType) {
        return getAllBySql(fullSql, new Object[]{paramValue}, new Type[]{paramType}, null, null);
    }
	
	/**
	 * Get all by SQL with two pairs of parameterType-value
	 * @param fullSql
	 * @param paramValue1
	 * @param paramType1
	 * @param paramValue2
	 * @param paramType2
	 * @return
	 */
	public List< T> getAllBySql(String fullSql, Object paramValue1, Type paramType1, Object paramValue2, Type paramType2) {
        return getAllBySql(fullSql, new Object[]{paramValue1, paramValue2}, new Type[]{paramType1, paramType2}, null, null);
    }
	
	/**
	 * Get all by SQL with three pairs of parameterType-value
	 * @param fullSql
	 * @param paramValue1
	 * @param paramType1
	 * @param paramValue2
	 * @param paramType2
	 * @param paramValue3
	 * @param paramType3
	 * @return
	 */
	public List< T> getAllBySql(String fullSql, Object paramValue1, Type paramType1, Object paramValue2, Type paramType2, Object paramValue3, Type paramType3) {
        return getAllBySql(fullSql, new Object[]{paramValue1, paramValue2, paramValue3}, new Type[]{paramType1, paramType2, paramType3}, null, null);
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
    public long countAll(){
    	return countByHql(null);
    }
    
    /**
     * Get the count of all records in database
     * @param secondHalfOfHql	parts after "from &lt;class_name&gt; "
     * @return
     */
    public long countByHql(String secondHalfOfHql){
    	return countByHql(secondHalfOfHql, null, null);
    }
    
    /**
     * Get the count of all records in database
     * @param secondHalfOfHql	parts after "from &lt;class_name&gt; "
     * @param paramValues
     * @param paramTypes
     * @return
     */
    public long countByHql(String secondHalfOfHql, Object[] paramValues, Type[] paramTypes){
    	StringBuilder queryStr = new StringBuilder();
    	queryStr.append("select count(*) from ")
			.append(this.clazz.getName())
			.append(" ");
    	if (secondHalfOfHql != null){
    		queryStr.append(secondHalfOfHql);
     	}
    	
        Session session = this.getCurrentSession();
    	Query query = session.createQuery(queryStr.toString());
    	setupQuery(query, paramValues, paramTypes, null, null);
        return ((Number)query.uniqueResult()).longValue();
    }
    
    /**
     * Get the count of all records in database
     * @param fullSql
     * @return
     */
    public long countBySql(String fullSql){
    	return countBySql(fullSql, null, null);
    }
    
    /**
     * Get the count of all records in database
     * @param fullSql
     * @param paramValues
     * @param paramTypes
     * @return
     */
    public long countBySql(String fullSql, Object[] paramValues, Type[] paramTypes){
        Session session = this.getCurrentSession();
    	Query query = session.createSQLQuery(fullSql);
    	setupQuery(query, paramValues, paramTypes, null, null);
        return ((Number)query.uniqueResult()).longValue();
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
 

    public void create(final T entity) {
    	this.getCurrentSession().persist(entity);
    }
    
    public void update(final T entity) {
    	this.getCurrentSession().saveOrUpdate(entity); //.merge(entity);
    }
    
    public void merge(final T entity){
    	this.getCurrentSession().merge(entity);
    }
 
    public void delete(final T entity) {
    	this.getCurrentSession().delete(entity);
	}
 
    @SuppressWarnings("unchecked")
	public void deleteById(final Serializable entityId) {
		final T entity = (T) this.getCurrentSession().load(this.clazz, entityId);
        if (entity != null){
            this.delete(entity);
        }else{
        	log.warn("Entity with id '" + entityId + "' does not exist. Deletion skipped.");
        }
    }
    
    /**
     * Delete by criteria specified as HQL
     * @param secondHalfOfHql
     * @param paramValues
     * @param paramTypes
     * @return	the number of records deleted
     */
    public int deleteByHql(String secondHalfOfHql, Object[] paramValues, Type[] paramTypes){
    	StringBuilder queryStr = new StringBuilder();
    	queryStr.append("delete from ")
			.append(this.clazz.getName())
			.append(" ");
    	if (secondHalfOfHql != null){
    		queryStr.append(secondHalfOfHql);
     	}
    	
        Session session = this.getCurrentSession();
    	Query query = session.createQuery(queryStr.toString());
    	setupQuery(query, paramValues, paramTypes, null, null);
        return query.executeUpdate();
    }
    
    public int deleteByHql(String secondHalfOfHql, Object paramValue1, Type paramType1){
    	return deleteByHql(secondHalfOfHql, new Object[]{paramValue1}, new Type[]{paramType1});
    }
    public int deleteByHql(String secondHalfOfHql, Object paramValue1, Type paramType1, Object paramValue2, Type paramType2){
    	return deleteByHql(secondHalfOfHql, new Object[]{paramValue1, paramValue2}, new Type[]{paramType1, paramType2});
    }
    public int deleteByHql(String secondHalfOfHql, Object paramValue1, Type paramType1, Object paramValue2, Type paramType2, Object paramValue3, Type paramType3){
    	return deleteByHql(secondHalfOfHql, new Object[]{paramValue1, paramValue2, paramValue3}, new Type[]{paramType1, paramType2, paramType3});
    }
   
    /**
     * Update by criteria specified as HQL
     * @param secondHalfOfHql
     * @param paramValues
     * @param paramTypes
     * @return	the number of records updated
     */
    public int updateByHql(String secondHalfOfHql, Object[] paramValues, Type[] paramTypes){
    	StringBuilder queryStr = new StringBuilder();
    	queryStr.append("update ")
			.append(this.clazz.getName())
			.append(" ");
    	if (secondHalfOfHql != null){
    		queryStr.append(secondHalfOfHql);
     	}
    	
        Session session = this.getCurrentSession();
    	Query query = session.createQuery(queryStr.toString());
    	setupQuery(query, paramValues, paramTypes, null, null);
        return query.executeUpdate();
    }
    
    public int updateByHql(String secondHalfOfHql, Object paramValue1, Type paramType1){
    	return updateByHql(secondHalfOfHql, new Object[]{paramValue1}, new Type[]{paramType1});
    }
    public int updateByHql(String secondHalfOfHql, Object paramValue1, Type paramType1, Object paramValue2, Type paramType2){
    	return updateByHql(secondHalfOfHql, new Object[]{paramValue1, paramValue2}, new Type[]{paramType1, paramType2});
    }
    public int updateByHql(String secondHalfOfHql, Object paramValue1, Type paramType1, Object paramValue2, Type paramType2, Object paramValue3, Type paramType3){
    	return updateByHql(secondHalfOfHql, new Object[]{paramValue1, paramValue2, paramValue3}, new Type[]{paramType1, paramType2, paramType3});
    }
   

    /**
     * Get the first element in the list
     * @param list
     * @return  null if the list is null or is empty, otherwise the first element of the list
     */
    public static <E> E firstInList(List<E> list){
    	if (list == null || list.size() == 0){
    		return null;
    	}else{
    		return list.get(0);
    	}
    }
    
    public void delayedCreate(final T entity){
    	delayedExecutor.execute(new DelayedOperation<T>(this, entity, OperationType.CREATE));
    }
 
    public void delayedMerge(final T entity) {
    	delayedExecutor.execute(new DelayedOperation<T>(this, entity, OperationType.MERGE));
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
 
    /**
     * Get current session. However DAO should not expose this. That's why this method is protected.
     * @return current session
     */
    protected final Session getCurrentSession() {
        return this.sessionFactory.getCurrentSession();
    }
}