/**
 * 
 */
package net.sf.jabb.cache;

import java.util.List;

/**
 * Value changes notifier for CachedKeyValueRepository.
 * The {@link #onChange(String, Object)} method can be used to
 * notify {@link CachedKeyValueRepository}s that an entry
 * in the cache is no longer up-to-date.
 * {@link #notifyRemoteRepositories(String, Object)}method can be overriden to
 * notify remote repositories.
 * @author James Hu
 *
 */
public abstract class AbstractCachedKeyValueRepositoriesNotifier{
	
	/**
	 * All the local instances of CachedKeyValueRepository. 
	 * When running inside Spring context, this field can be injected/autowired
	 */
	protected List<CachedKeyValueRepository<?, ?>> localRepositories = null;
	
	/**
	 * Notify both local and remote repositories about the value change
	 * @param valueScope	value scope
	 * @param key			the key
	 */
	public void onChange(String valueScope, Object key){
		notifyLocalRepositories(valueScope, key);
		notifyRemoteRepositories(valueScope, key);
	}
	
	/**
	 * method to be called when being notified by remote about the value change
	 * @param valueScope	value scope
	 * @param key			the key
	 */
	protected void onNotifiedByRemote(String valueScope, Object key){
		notifyLocalRepositories(valueScope, key);
	}
	
	protected void notifyLocalRepositories(String valueScope, Object key){
		if (localRepositories != null){
			for (CachedKeyValueRepository<?, ?> repo: localRepositories){
				if (valueScope.equals(repo.getValueScope())){
					repo.onValueChanged(key);
				}
			}
		}
	}
	
	/**
	 * Notify all remote repositories. Subclass normally should override this method because
	 * the default implementation of this method actually does nothing.
	 * If by design there is no remote repository then subclass can keep this method unchanged.
	 * @param valueScope	value scope key
	 * @param key			the key for which the value has changed
	 */
	protected void notifyRemoteRepositories(String valueScope, Object key){
		// do nothing
	}
}
