/**
 * 
 */
package net.sf.jabb.cache;

import java.util.concurrent.ExecutorService;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.BlockingCache;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import net.sf.jabb.cache.CachedKeyValueRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ehcache based implementation of CachedKeyValueRepository.
 * Before the subclass can be used, it must first populate {@link #cacheManager} and {@link #threadPool}, 
 * then call the {@link #initialize()} method.
 * 
 * @author James Hu
 *
 */
public abstract class AbstractEhCachedKeyValueRepository<K, V> implements CachedKeyValueRepository<K, V> {
	// Ehcache uses slf4j
	private static final Logger logger = LoggerFactory.getLogger(AbstractEhCachedKeyValueRepository.class);

	/**
	 * EhCache's CacheManager. When running inside a cache enabled Spring context,
	 * this field can be injected/autowired.
	 */
	protected CacheManager cacheManager;
	
	protected SelfPopulatingCache cache;
	
	/**
	 * The thread pool to execute background value population. When running inside a cache enabled Spring context,
	 * this field can be injected/autowired.
	 */
	protected ExecutorService threadPool;
	
	protected void initializeCache(){
		Ehcache originalCache = cacheManager.getEhcache(getCacheName());
		if (originalCache == null){
			throw new NullPointerException("No Ehcache configured with this name: " + getCacheName());
		}
		cache = replaceWithSelfPopulatingCacheIfNot(originalCache, new CacheEntryFactory(){
			@SuppressWarnings("unchecked")
			@Override
			public Object createEntry(Object key) throws Exception {
				return getDirectly((K)key);
			}
		});
	}
	
	/**
	 * Subclass should implement this method to get the value directly from underlying repository.
	 * @param key	the key
	 * @return		the value
	 * @throws Exception	if any error happens
	 */
	abstract public V getDirectly(K key) throws Exception;
	
	/**
	 * Determine if a refresh ahead is needed. 
	 * The default implementation checks if accessTime falls into the 3rd quarter of (expirationTime - createdTime)
	 * @param accessTime		last access time
	 * @param createdTime		created time
	 * @param expirationTime	end of TTL/TTI time
	 * @return	true if a refresh is needed, false otherwise
	 */
	protected boolean refreshAheadNeeded(long accessTime, long createdTime, long expirationTime){
		long ttl4 = (expirationTime - createdTime) / 4;
		if (ttl4 < 0){
			ttl4 = 0;
		}
		
		long start = createdTime + ttl4 * 2;
		long end = expirationTime - ttl4;
		
		return accessTime > start && accessTime < end;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) {
		V result = null;
		Element e = cache.get(key);
		if (e == null){  // cache miss
			logger.error("Cache miss should never happened in SelfPopulatingCache: {}", cache.getName());
		}else{	// cache hit
			result = (V) e.getObjectValue();
			triggerRefreshIfNeeded(e);
		}
		return result;
	}

	@Override
	public void onValueChanged(Object key) {
		Element e = cache.getQuiet(key);	// hit the underlying cache and does not change lastAccessTime
		if (e != null){
			if (!triggerRefreshIfNeeded(e)){
				cache.remove(key);
			}
		}
	}

	/**
	 * Trigger refresh if it is needed
	 * @param e	the cache entry element
	 * @return	true if refresh is needed and is handled in this method, false if refresh is not needed
	 */
	@SuppressWarnings("unchecked")
	protected boolean triggerRefreshIfNeeded(final Element e){
		if (refreshAheadNeeded(e.getLastAccessTime(), e.getCreationTime(), e.getExpirationTime())){
			// to apply a simple and not strict concurrency locking
			final long signature = Thread.currentThread().getId() * 1000000L + (System.currentTimeMillis() % 1000000L);
			if (e.getVersion() == 1){	// 1 is the default value of version field
				e.setVersion(signature);
				if (e.getVersion() == signature){	// double check
					threadPool.execute(new Runnable(){
						public void run(){
							if (e.getVersion() == signature){	// triple check
								try{
									V newValue = getDirectly((K)e.getObjectKey());
									if (newValue == null){
										cache.remove(e.getObjectKey());
									}else{
										cache.put(new Element((K)e.getObjectKey(), newValue));
									}
								}catch(Exception ex){
									logger.warn("Failed to update the value associated with specified key '{}' in cache '{}'", e.getObjectKey(), cache.getName(), ex);
								}
							}
						}
					});
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Replace the cache with a BlockingCache decorated one if this has not been done yet.
	 * The cacheLoaderTimeoutMillis of the original cache will be used as the timeoutMillis of the BlockingCache.
	 * @param ehcache			the original cache
	 * @return a BlockingCache wrapping the original one
	 */
	protected BlockingCache replaceWithBlockingCacheIfNot(Ehcache ehcache){
		if (ehcache instanceof BlockingCache){
			return (BlockingCache) ehcache;
		}
		
		BlockingCache blockingCache = new BlockingCache(ehcache);
        blockingCache.setTimeoutMillis((int)ehcache.getCacheConfiguration().getCacheLoaderTimeoutMillis());

        cacheManager.replaceCacheWithDecoratedCache(ehcache, blockingCache);
		return blockingCache;
	}


	/**
	 * Replace the cache with a SelfPopulatingCache decorated one if this has not been done yet.
	 * The cacheLoaderTimeoutMillis of the original cache will be used as the timeoutMillis of the BlockingCache.
	 * @param ehcache			the original cache
	 * @param factory			the cache entry value factory
	 * @return a BlockingCache wrapping the original one
	 */
	protected SelfPopulatingCache replaceWithSelfPopulatingCacheIfNot(Ehcache ehcache, CacheEntryFactory factory){
		if (ehcache instanceof SelfPopulatingCache){
			return (SelfPopulatingCache) ehcache;
		}
		
		SelfPopulatingCache selfPopulatingCache = new SelfPopulatingCache(ehcache, factory);
		selfPopulatingCache.setTimeoutMillis((int)ehcache.getCacheConfiguration().getCacheLoaderTimeoutMillis());

        cacheManager.replaceCacheWithDecoratedCache(ehcache, selfPopulatingCache);
		return selfPopulatingCache;
	}


}
