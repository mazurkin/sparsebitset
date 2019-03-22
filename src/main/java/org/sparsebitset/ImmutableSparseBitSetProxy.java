package org.sparsebitset;

import org.sparsebitset.index.SparseBitIndex;

/**
 * Proxy provides read-only access for an underlying delegate
 *
 * @param <I> Type of index
 */
public class ImmutableSparseBitSetProxy<I extends SparseBitIndex> implements SparseBitSet<I> {

    private final SparseBitSet<I> delegate;

    /**
     * Constructs an immutable proxy for an underlying delegate
     *
     * @param delegate Delegate instance
     */
    private ImmutableSparseBitSetProxy(SparseBitSet<I> delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("Delegate can't be null");
        }

        this.delegate = delegate;
    }

    /**
     * Constructs a immutable proxy for an underlying delegate
     *
     * @param delegate Delegate instance
     * @param <I> Type of index
     *
     * @return Immutable instance
     */
    public static <I extends SparseBitIndex> SparseBitSet<I> proxy(SparseBitSet<I> delegate) {
        return new ImmutableSparseBitSetProxy<>(delegate);
    }

    @Override
    public SparseBitSet<I> copy() {
        return ImmutableSparseBitSetProxy.proxy(delegate.copy());
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean isFull() {
        return delegate.isFull();
    }

    @Override
    public void clearAll() {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    public void setAll() {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    public void flipAll() {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    public void validate() {
        delegate.validate();
    }

    @Override
    public boolean get(I index) {
        return delegate.get(index);
    }

    @Override
    public boolean set(I index) {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    public boolean clear(I index) {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    public void flip(I index) {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    public void set(I fromIndexInclusive, I toIndexInclusive) {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    public void clear(I fromIndexInclusive, I toIndexInclusive) {
        throw new UnsupportedOperationException("Set is immutable");
    }

    @Override
    public void flip(I fromIndexInclusive, I toIndexInclusive) {
        throw new UnsupportedOperationException("Set is immutable");
    }
    
}
