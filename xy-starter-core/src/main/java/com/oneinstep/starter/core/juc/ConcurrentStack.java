package com.oneinstep.starter.core.juc;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Stack implemented using linked list with concurrent push and pop
 *
 * @param <E> the type of elements in this stack
 */
public class ConcurrentStack<E> {

    /**
     * Node in the linked list
     *
     * @param <E> the type of elements in this node
     */
    private static class Node<E> {
        final E item;
        Node<E> next;

        public Node(E item) {
            this.item = item;
        }
    }

    /**
     * The top of the stack
     */
    private final AtomicReference<Node<E>> top = new AtomicReference<>();

    /**
     * Pushes an element onto the stack.
     *
     * @param item the element to push
     */
    public void push(E item) {
        Node<E> newHead = new Node<>(item);
        Node<E> oldHead;
        do {
            oldHead = top.get();
            newHead.next = oldHead;
        } while (!top.compareAndSet(oldHead, newHead));
    }

    /**
     * Pops an element from the stack.
     *
     * @return the element popped from the stack
     */
    public E pop() {
        Node<E> oldHead;
        Node<E> newHead;
        do {
            oldHead = top.get();
            if (oldHead == null) {
                return null;
            }
            newHead = oldHead.next;
        } while (!top.compareAndSet(oldHead, newHead));
        return oldHead.item;
    }

}
