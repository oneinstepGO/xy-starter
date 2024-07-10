package com.oneinstep.starter.core.juc;

import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * A task that can be custom cancelled.
 * design for the task that need to be cancelled manually. like socket task.
 *
 * @param <T> the type of the task
 */
public abstract class AbsCancellableTask<T> implements CancellableTask<T> {

    @Override
    public RunnableFuture<T> newTask() {
        return new FutureTask<>(this) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                boolean result;
                try {
                    result = super.cancel(mayInterruptIfRunning);  // 取消任务
                } finally {
                    synchronized (AbsCancellableTask.this) {
                        AbsCancellableTask.this.cancel();  // 关闭 socket
                    }
                }
                return result;
            }
        };
    }

}