/**
 * 
 */
package net.sf.jabb.cache;

/**
 * Repository of cached key values (read only).
 * @author James Hu
 *
 */
public interface CachedKeyValueRepository<K, V> {
	
	/**
	 * Get value by key.
	 * @param key	the key
	 * @return		the value
	 */
	V get(K key);
	
	/**
	 * Notify the repository that the value associated with the specified key has changed or has be deleted.
	 * @param key	the key
	 */
	void onValueChanged(Object key);
	
	/**
	 * Get the scope of the value that will be used by the change notification framework to determine whether
	 * this repository should be notified about a change in a scope.
	 * @return	name of the scope
	 */
	String getValueScope();
	
	/**
	 * Get the name of the underlying cache
	 * @return	name of the cache
	 */
	String getCacheName();
}
