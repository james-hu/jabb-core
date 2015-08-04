/**
 * 
 */
package net.sf.jabb.util.col;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A decoration on top of {@link BlockingQueue} providing the capability to apply a soft size limit.
 * Since the <code>size()</code> method of the underlying BlockingQueue instance is used to determine
 * whether the soft size limit has been reached, the underlying BlockingQueue instance is expected to have a <code>size()</code>
 * method with constant execution time. This assumption is valid for the following classes:
 * <ul>
 * 	<li>{@link ArrayBlockingQueue}</li>
 * 	<li>{@link DelayQueue}</li>
 * 	<li>{@link LinkedBlockingDeque}</li>
 * 	<li>{@link LinkedBlockingQueue}</li>
 * 	<li>{@link PriorityBlockingQueue}</li>
 * 	<li>{@link SynchronousQueue}</li>
 * </ul>
 * but not these:
 * <ul>
 * 	<li>{@link LinkedTransferQueue}</li>
 * </ul>
 * However, it only adds performance overhead.
 * 
 * @author James Hu
 *
 */
public class SizeLimitableBlockingQueue<E> implements BlockingQueue<E>, java.io.Serializable{
	private static final long serialVersionUID = -7962162138059475760L;
	
	protected BlockingQueue<E> queue;
	
	protected int sizeLimit;
	protected final ReentrantLock sizeLimitLock;
	protected final Condition withinSizeLimit;

	/**
	 * Set the soft limit of the actual size.
	 * The limit is soft which means it is just a target and sometimes may be exceeded especially in multi-thread environment.
	 * @param sizeLimit		(soft) limit of the actual size; zero means no limit will apply
	 * @throws IllegalArgumentException if the specified size is negative
	 */
	public void setSizeLimit(int sizeLimit) {
		if (sizeLimit < 0){
			throw new IllegalArgumentException("size limit cannot be negative");
		}
		this.sizeLimit = sizeLimit;
	}
	
	/**
	 * Get the soft size limit
	 * @return	(soft) size limit or zere if there is no limit
	 */
	public int getSizeLimit(){
		return this.sizeLimit;
	}
	
	/**
	 * Get the underlying queue instance
	 * @return	the underlying queue instance
	 */
	public BlockingQueue<E> getQueue(){
		return queue;
	}

    /**
     * Creates a decorated <tt>BlockingQueue</tt> from the given BlockingQueue instance and specified size limit.
     * @param queue the underlying BlockingQueue instance
     * @param fair if <tt>true</tt> then queue accesses for threads blocked
     *        on insertion or removal, are processed in FIFO order;
     *        if <tt>false</tt> the access order is unspecified.
     * @param sizeLimit the soft size limit
     * @throws IllegalArgumentException if <tt>sizeLimit</tt> is negative
     * @throws NullPointerException if the specified queue is null
     */
	public SizeLimitableBlockingQueue(BlockingQueue<E> queue, boolean fair, int sizeLimit) {
		if (queue == null){
			throw new NullPointerException("underlying queue cannot be null");
		}
		this.queue = queue;
		this.setSizeLimit(sizeLimit);
		this.sizeLimitLock = new ReentrantLock(fair);
		this.withinSizeLimit = this.sizeLimitLock.newCondition();
	}

	/**
     * Creates a decorated <tt>BlockingQueue</tt> from the given BlockingQueue instance and specified size limit.
     * @param queue the underlying BlockingQueue instance
     * @param fair if <tt>true</tt> then queue accesses for threads blocked
     *        on insertion or removal, are processed in FIFO order;
     *        if <tt>false</tt> the access order is unspecified.
     * @throws NullPointerException if the specified queue is null
	 */
	public SizeLimitableBlockingQueue(BlockingQueue<E> queue, boolean fair) {
		this(queue, fair, 0);
	}

    /**
     * Creates a decorated <tt>BlockingQueue</tt> from the given BlockingQueue instance and specified size limit.
     * Queue accesses for threads blocked on insertion or removal, are processed in unspecified order.
     * @param queue the underlying BlockingQueue instance
     * @param sizeLimit the soft size limit
     * @throws IllegalArgumentException if <tt>sizeLimit</tt> is negative
     * @throws NullPointerException if the specified queue is null
     */
	public SizeLimitableBlockingQueue(BlockingQueue<E> queue, int sizeLimit) {
		this(queue, false, sizeLimit);
	}

	/**
     * Creates a decorated <tt>BlockingQueue</tt> from the given BlockingQueue instance and specified size limit.
     * @param queue the underlying BlockingQueue instance
     * @throws NullPointerException if the specified queue is null
	 */
	public SizeLimitableBlockingQueue(BlockingQueue<E> queue) {
		this(queue, false, 0);
	}
	
	protected void signalSizeReduced(){
		sizeLimitLock.lock();
		try{
			withinSizeLimit.signalAll();
		}finally{
			sizeLimitLock.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Queue#remove()
	 */
	@Override
	public E remove() {
		E e = queue.remove();
		signalSizeReduced();
		return e;
	}

	/* (non-Javadoc)
	 * @see java.util.Queue#poll()
	 */
	@Override
	public E poll() {
		E e = queue.poll();
		if (e != null){
			signalSizeReduced();
		}
		return e;
	}

	/* (non-Javadoc)
	 * @see java.util.Queue#element()
	 */
	@Override
	public E element() {
		return queue.element();
	}

	/* (non-Javadoc)
	 * @see java.util.Queue#peek()
	 */
	@Override
	public E peek() {
		return queue.peek();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	@Override
	public int size() {
		return queue.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		return queue.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray() {
		return queue.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(java.lang.Object[])
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		return queue.toArray(a);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return queue.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		return queue.addAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean b = queue.removeAll(c);
		if (b){
			signalSizeReduced();
		}
		return b;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		boolean b = queue.retainAll(c);
		if (b){
			signalSizeReduced();
		}
		return b;
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		queue.clear();
		signalSizeReduced();
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.BlockingQueue#add(java.lang.Object)
	 */
	@Override
	public boolean add(E e) {
		if (sizeLimit > 0 && size() >= sizeLimit){
			throw new IllegalStateException("Queue size limit reached: " + sizeLimit);
		}else{
			return queue.add(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.BlockingQueue#offer(java.lang.Object)
	 */
	@Override
	public boolean offer(E e) {
		if (sizeLimit > 0 && size() >= sizeLimit){
			return false;
		}else{
			return queue.offer(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.BlockingQueue#put(java.lang.Object)
	 */
	@Override
	public void put(E e) throws InterruptedException {
		if (sizeLimit > 0){
	        final ReentrantLock lock = this.sizeLimitLock;
	        lock.lockInterruptibly();
	        try {
	            try {
	                while (size() >= sizeLimit)
	                    withinSizeLimit.await();
	            } catch (InterruptedException ie) {
	            	withinSizeLimit.signal(); // propagate to non-interrupted thread
	                throw ie;
	            }
	            queue.put(e);
	        } finally {
	            lock.unlock();
	        }
		}else{
			queue.put(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.BlockingQueue#offer(java.lang.Object, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		if (sizeLimit > 0){
	        long nanos = unit.toNanos(timeout);
	        final ReentrantLock lock = this.sizeLimitLock;
	        lock.lockInterruptibly();
	        try {
	            for (;;) {
	                if (size() < sizeLimit) {
	                    return queue.offer(e, timeout, unit);
	                }
	                if (nanos <= 0)
	                    return false;
	                try {
	                    nanos = withinSizeLimit.awaitNanos(nanos);
	                } catch (InterruptedException ie) {
	                	withinSizeLimit.signal(); // propagate to non-interrupted thread
	                    throw ie;
	                }
	            }
	        } finally {
	            lock.unlock();
	        }
		}else{
			return queue.offer(e, timeout, unit);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.BlockingQueue#take()
	 */
	@Override
	public E take() throws InterruptedException {
		E e = queue.take();
		signalSizeReduced();
		return e;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.BlockingQueue#poll(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		E e = queue.poll(timeout, unit);
		if (e != null){
			signalSizeReduced();
		}
		return e;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.BlockingQueue#remainingCapacity()
	 */
	@Override
	public int remainingCapacity() {
		if (sizeLimit > 0){
			int remaining = sizeLimit - size();
			return remaining < 0 ? 0 : remaining;
		}else{
			return queue.remainingCapacity();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.BlockingQueue#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		boolean b = queue.remove(o);
		if (b){
			signalSizeReduced();
		}
		return b;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.BlockingQueue#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return queue.contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.BlockingQueue#drainTo(java.util.Collection)
	 */
	@Override
	public int drainTo(Collection<? super E> c) {
		int i = queue.drainTo(c);
		if (i > 0){
			signalSizeReduced();
		}
		return i;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.BlockingQueue#drainTo(java.util.Collection, int)
	 */
	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		int i = queue.drainTo(c, maxElements);
		if (i > 0){
			signalSizeReduced();
		}
		return i;
	}



}
