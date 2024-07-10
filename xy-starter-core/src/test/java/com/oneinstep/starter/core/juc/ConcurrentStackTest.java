package com.oneinstep.starter.core.juc;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ConcurrentStackTest {

    @Test
    void testPushAndPop() {
        ConcurrentStack<Integer> stack = new ConcurrentStack<>();

        // Test pushing and popping one element
        stack.push(1);
        assertEquals(1, stack.pop());

        // Test pushing multiple elements and popping them in LIFO order
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());

        // Test popping from an empty stack
        assertNull(stack.pop());
    }

    @Test
    void testPopFromEmptyStack() {
        ConcurrentStack<Integer> stack = new ConcurrentStack<>();
        assertNull(stack.pop());
    }

    @Test
    void testPushNullElement() {
        ConcurrentStack<String> stack = new ConcurrentStack<>();

        stack.push(null);
        assertNull(stack.pop());
    }

    @Test
    void testConcurrentPushAndPop() throws InterruptedException {
        final ConcurrentStack<Integer> stack = new ConcurrentStack<>();
        final int numThreads = 10;
        final int numElements = 1000;

        Thread[] pushThreads = new Thread[numThreads];
        Thread[] popThreads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            pushThreads[i] = new Thread(() -> {
                for (int j = 0; j < numElements; j++) {
                    stack.push(j);
                }
            });

            popThreads[i] = new Thread(() -> {
                for (int j = 0; j < numElements; j++) {
                    stack.pop();
                }
            });
        }

        for (Thread t : pushThreads) {
            t.start();
        }

        for (Thread t : popThreads) {
            t.start();
        }

        for (Thread t : pushThreads) {
            t.join();
        }

        for (Thread t : popThreads) {
            t.join();
        }

        assertNull(stack.pop());
    }

    /**
     * 运行多次以增加测试的可靠性
     *
     * @throws InterruptedException 如果线程被中断
     */
    @RepeatedTest(10)
    void testConcurrentSafety() throws InterruptedException {
        final ConcurrentStack<Integer> stack = new ConcurrentStack<>();
        final int numProducers = 10;
        final int numConsumers = 10;
        final int numElementsPerProducer = 1000;
        final AtomicInteger totalProduced = new AtomicInteger();
        final AtomicInteger totalConsumed = new AtomicInteger();

        Thread[] producerThreads = new Thread[numProducers];
        Thread[] consumerThreads = new Thread[numConsumers];

        // 创建并启动生产者线程
        for (int i = 0; i < numProducers; i++) {
            producerThreads[i] = new Thread(() -> {
                for (int j = 0; j < numElementsPerProducer; j++) {
                    stack.push(j);
                    totalProduced.incrementAndGet();
                }
            });
            producerThreads[i].start();
        }

        // 创建并启动消费者线程
        for (int i = 0; i < numConsumers; i++) {
            consumerThreads[i] = new Thread(() -> {
                while (totalConsumed.get() < numProducers * numElementsPerProducer) {
                    Integer value = stack.pop();
                    if (value != null) {
                        totalConsumed.incrementAndGet();
                    }
                }
            });
            consumerThreads[i].start();
        }

        // 等待所有生产者线程结束
        for (Thread t : producerThreads) {
            t.join();
        }

        // 等待所有消费者线程结束
        for (Thread t : consumerThreads) {
            t.join();
        }

        // 验证最终的栈为空
        assertNull(stack.pop());

        // 验证生产和消费的数量相等
        assertEquals(totalProduced.get(), totalConsumed.get());
    }
}
