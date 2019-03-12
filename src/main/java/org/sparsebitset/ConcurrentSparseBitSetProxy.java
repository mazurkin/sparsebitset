package org.sparsebitset;

import org.sparsebitset.index.SparseBitIndex;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Proxy provides read-write concurrent access for an underlying delegate
 *
 * @param <I> Type of index
 */
public class ConcurrentSparseBitSetProxy<I extends SparseBitIndex> implements SparseBitSet<I> {

    private final SparseBitSet<I> delegate;

    private final ReadWriteLock lock;

    /**
     * Constructs a thread-safe proxy for an underlying delegate
     *
     * @param delegate Delegate instance
     */
    public ConcurrentSparseBitSetProxy(SparseBitSet<I> delegate) {
        this(delegate, new ReentrantReadWriteLock());
    }

    /**
     * Constructs a thread-safe proxy for an underlying delegate
     *
     * @param delegate Delegate instance
     * @param <I> Type of index
     *
     * @return Thread-safe proxy instance
     */
    public static <I extends SparseBitIndex> SparseBitSet<I> proxy(SparseBitSet<I> delegate) {
        return new ConcurrentSparseBitSetProxy<>(delegate);
    }

    /**
     * Constructs a thread-safe proxy for an underlying delegate
     *
     * @param delegate Delegate set
     * @param readWriteLock Read-Write lock
     */
    public ConcurrentSparseBitSetProxy(SparseBitSet<I> delegate, ReadWriteLock readWriteLock) {
        if (delegate == null) {
            throw new IllegalArgumentException("Delegate can't be null");
        }

        if (readWriteLock == null) {
            throw new IllegalArgumentException("Read-write lock can't be null");
        }

        this.delegate = delegate;
        this.lock = readWriteLock;
    }

    @Override
    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return delegate.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean isFull() {
        lock.readLock().lock();
        try {
            return delegate.isFull();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void clearAll() {
        lock.writeLock().lock();
        try {
            delegate.clearAll();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void setAll() {
        lock.writeLock().lock();
        try {
            delegate.setAll();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void flipAll() {
        lock.writeLock().lock();
        try {
            delegate.flipAll();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void validate() {
        lock.readLock().lock();
        try {
            delegate.validate();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean get(I index) {
        lock.readLock().lock();
        try {
            return delegate.get(index);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean set(I index) {
        lock.writeLock().lock();
        try {
            return delegate.set(index);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean clear(I index) {
        lock.writeLock().lock();
        try {
            return delegate.clear(index);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void flip(I index) {
        lock.writeLock().lock();
        try {
            delegate.flip(index);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void set(I fromIndexInclusive, I toIndexInclusive) {
        lock.writeLock().lock();
        try {
            delegate.set(fromIndexInclusive, toIndexInclusive);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void clear(I fromIndexInclusive, I toIndexInclusive) {
        lock.writeLock().lock();
        try {
            delegate.clear(fromIndexInclusive, toIndexInclusive);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void flip(I fromIndexInclusive, I toIndexInclusive) {
        lock.writeLock().lock();
        try {
            delegate.flip(fromIndexInclusive, toIndexInclusive);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
