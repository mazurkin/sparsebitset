package org.sparsebitset.level;

import org.sparsebitset.index.SparseBitIndex;

/**
 * Squashed layer (no any bits are set)
 */
public final class SparseBitLevelFull implements SparseBitLevel {

    static final SparseBitLevel INSTANCE = new SparseBitLevelFull();

    private SparseBitLevelFull() {
    }

    @Override
    public SparseBitLevel copy() {
        return this;
    }

    @Override
    public SparseBitLevelType getType() {
        return SparseBitLevelType.FULL;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isFull() {
        return true;
    }

    @Override
    public void clearAll() {
        throw new UnsupportedOperationException("Special level");
    }

    @Override
    public void setAll() {
        // nothing to do - all bits are 1
    }

    @Override
    public void flipAll() {
        throw new UnsupportedOperationException("Special level");
    }

    @Override
    public void validate() {
        // nothing to do
    }

    @Override
    public boolean get(SparseBitIndex index) {
        return true;
    }

    @Override
    public boolean set(SparseBitIndex index) {
        return false;
    }

    @Override
    public boolean clear(SparseBitIndex index) {
        throw new UnsupportedOperationException("Special level");
    }

    @Override
    public void flip(SparseBitIndex index) {
        throw new UnsupportedOperationException("Special level");
    }

    @Override
    public void set(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive) {
        // nothing to do - all bits are 1
    }

    @Override
    public void clear(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive) {
        throw new UnsupportedOperationException("Special level");
    }

    @Override
    public void flip(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive) {
        throw new UnsupportedOperationException("Special level");
    }
}
