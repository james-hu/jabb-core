package net.sf.jabb.spring.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import org.apache.http.HttpRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jabb.util.parallel.BackoffStrategy;
import net.sf.jabb.util.parallel.WaitStrategy;

public class CustomHttpRequestRetryHandler extends DefaultHttpRequestRetryHandler{
	private static final Logger logger = LoggerFactory.getLogger(CustomHttpRequestRetryHandler.class);
	
	static public interface IdempotentPredicate{
		public boolean handleAsIdempotent(HttpRequest request);
	}
	
	protected CustomHttpRequestRetryHandler.IdempotentPredicate idempotentPredicate;
	protected BackoffStrategy backoffStrategy;
	protected WaitStrategy waitStrategy;
	
	protected CustomHttpRequestRetryHandler(int retryCount, boolean requestSentRetryEnabled, 
			Collection<Class<? extends IOException>> excludeExceptions,
			BackoffStrategy backoffStrategy, WaitStrategy waitStrategy, 
			CustomHttpRequestRetryHandler.IdempotentPredicate idempotentPredicate){
		super(retryCount, requestSentRetryEnabled, excludeExceptions);
		this.idempotentPredicate = idempotentPredicate;
		this.backoffStrategy = backoffStrategy;
		this.waitStrategy = waitStrategy;
	}
	
    @Override
    public boolean retryRequest(
            final IOException exception,
            final int executionCount,
            final HttpContext context) {
    	boolean retry = super.retryRequest(exception, executionCount, context);
        if (retry){
        	long delay = backoffStrategy.computeBackoffMilliseconds(executionCount);
        	logger.debug("IOException occurred: " + exception + "  Will do a retry in " + delay + " milliseconds for: " + HttpClientContext.adapt(context).getRequest());
        	try {
				waitStrategy.await(delay);
			} catch(InterruptedException e) {
				waitStrategy.handleInterruptedException(e);
			}
        }
    	return retry;
    }
    
    @Override
    protected boolean handleAsIdempotent(final HttpRequest request) {
    	if (idempotentPredicate != null){
	    	return idempotentPredicate.handleAsIdempotent(request);
    	}
    	/*
    	if (request instanceof HttpEntityEnclosingRequest){
    		return false;
    	}*/
        final String method = request.getRequestLine().getMethod().toUpperCase(Locale.ROOT);
        return "GET".equals(method);
    }
	
}