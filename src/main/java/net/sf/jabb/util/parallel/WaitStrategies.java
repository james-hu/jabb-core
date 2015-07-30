/*
 * Copyright 2012-2015 Ray Holder
 * Copyright 2015 James Hu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.jabb.util.parallel;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;




/**
 * Factory class for {@link WaitStrategy} instances.
 */
public final class WaitStrategies {

    private static final WaitStrategy THREAD_SLEEP_STRATEGY = new ThreadSleepWaitStrategy();
    private static WaitStrategy STRAND_SLEEP_STRATEGY;

    private WaitStrategies() {
    }

    /**
     * Returns a wait strategy that puts the current thread to sleep while waiting
     *
     * @return a wait strategy that puts the current thread to sleep while waiting
     */
    public static WaitStrategy threadSleepStrategy() {
        return THREAD_SLEEP_STRATEGY;
    }

    /**
     * Returns a wait strategy that puts the current {@link co.paralleluniverse.strands.Strand} to sleep while waiting.
     * This makes it suitable for being called from {@link co.paralleluniverse.fibers.Fiber}s.
     *
     * @return a wait strategy that puts the current {@link co.paralleluniverse.strands.Strand} to sleep while waiting
     */
    public static WaitStrategy strandSleepStrategy() {
    	if (STRAND_SLEEP_STRATEGY == null){
    		synchronized(WaitStrategies.class){
    			if (STRAND_SLEEP_STRATEGY == null){
    				STRAND_SLEEP_STRATEGY = new StrandSleepWaitStrategy();
    			}
    		}
    	}
        return STRAND_SLEEP_STRATEGY;
    }

    private static class ThreadSleepWaitStrategy implements WaitStrategy {

        @Override
        public void await(long timeInMilliseconds) throws InterruptedException {
            Thread.sleep(timeInMilliseconds);
        }

		@Override
		public void handleInterruptedException(InterruptedException e) {
			Thread.currentThread().interrupt();
		}
    }
    
    private static class StrandSleepWaitStrategy implements WaitStrategy{

    	@Override
    	public void await(long timeInMilliseconds) throws InterruptedException {
    		try {
    			Strand.sleep(timeInMilliseconds);
    		} catch (SuspendExecution e) {
    			// this is not a real exception
    			// leave this method without instrumentation should be fine
    		}
    	}

    	@Override
    	public void handleInterruptedException(InterruptedException e) {
    		Strand.currentStrand().interrupt();
    	}
    	
    }
}