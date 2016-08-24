package net.sf.jabb.cache;

import static org.junit.Assert.*;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;

import org.junit.Test;

public class AbstractEhCachedKeyValueRepositoryTest {

	@Test
	public void testDeletion() throws InterruptedException {
		final AtomicInteger values = new AtomicInteger(0);
		final AtomicBoolean refreshNeeded = new AtomicBoolean(false);
		AbstractEhCachedKeyValueRepository<Integer, Integer> cachedRepo = new AbstractEhCachedKeyValueRepository<Integer, Integer>(){

			@Override
			public String getValueScope() {
				return "";
			}

			@Override
			public String getCacheName() {
				return "OrganizationIdByApiKeyCache";
			}

			@Override
			public Integer getDirectly(Integer key) throws Exception {
				if (key < 1000){
					int result = values.getAndIncrement();
					if (result == 4){
						return null;
					} else if (result == 3 || result == 5) {
						throw new RuntimeException("purposefully generated");
					}else{
						return result;
					}
				}else{
					return null;
				}
			}
			
			@Override
			protected boolean refreshAheadNeeded(long accessTime, long createdTime, long expirationTime){
				return refreshNeeded.get();
			}
			
		};
		cachedRepo.cacheManager = CacheManager.newInstance();
		cachedRepo.threadPool = Executors.newCachedThreadPool();
		cachedRepo.initializeCache();
		
		// 0 load
		assertEquals(Integer.valueOf(0), cachedRepo.get(100));
		assertNull(cachedRepo.get(1001));
		
		// 1 reload
		cachedRepo.onValueChanged(100);	
		assertEquals(Integer.valueOf(1), cachedRepo.get(100));
		
		// 2 reload
		cachedRepo.onValueChanged(100);	
		assertEquals(Integer.valueOf(2), cachedRepo.get(100));
		
		refreshNeeded.set(true);
		// 3 failed to update
		cachedRepo.onValueChanged(100);
		Thread.sleep(500);
		assertEquals(Integer.valueOf(2), cachedRepo.get(100));
		Thread.sleep(500);
		
		refreshNeeded.set(false);
		// 4 deleted
		cachedRepo.onValueChanged(100);
		Thread.sleep(500);
		assertNull(cachedRepo.get(100));
		Thread.sleep(500);

		// 5 failed to load
		try{
			assertNull(cachedRepo.get(100));
			fail("should throw CacheException");
		}catch(CacheException e){
			// good
		}
	}

}
