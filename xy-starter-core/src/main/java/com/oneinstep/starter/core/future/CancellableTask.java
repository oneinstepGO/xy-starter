package com.oneinstep.starter.core.future;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

/**
 * A cancellable task
 *
 * @param <T> the type of the task
 */
public interface CancellableTask<T> extends Callable<T> {
    /**
     * Cancel the task
     */
    void cancel();

    /**
     * Create a new task
     *
     * @return a new task
     */
    RunnableFuture<T> newTask();
}
