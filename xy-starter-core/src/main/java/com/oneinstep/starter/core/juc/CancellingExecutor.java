package com.oneinstep.starter.core.juc;

import java.util.concurrent.*;

/**
 * An executor that can cancel tasks
 */
public class CancellingExecutor extends ThreadPoolExecutor {

    /**
     * Create a new CancellingExecutor
     *
     * @param corePoolSize    the number of threads to keep in the pool, even if they are idle
     * @param maximumPoolSize the maximum number of threads to allow in the pool
     * @param keepAliveTime   when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating
     * @param unit            the time unit for the keepAliveTime argument
     * @param workQueue       the queue to use for holding tasks before they are executed
     */
    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        // If the callable is a CancellableTask, create a new task
        if (callable instanceof CancellableTask) {
            return ((CancellableTask<T>) callable).newTask();
        } else {
            // Otherwise, use the default implementation
            return super.newTaskFor(callable);
        }
    }

}
