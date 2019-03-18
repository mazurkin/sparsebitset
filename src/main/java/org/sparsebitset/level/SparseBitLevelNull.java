package org.sparsebitset.level;

import org.sparsebitset.index.SparseBitIndex;

/**
 * Null layer (all bits are set)
 */
public class SparseBitLevelNull implements SparseBitLevel {

    static final SparseBitLevel INSTANCE = new SparseBitLevelNull();

    private SparseBitLevelNull() {
    }

    @Override
    public SparseBitLevelType getType() {
        return SparseBitLevelType.NULL;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public void clearAll() {
        // nothing to do - all bits are 0
    }

    @Override
    public void setAll() {
        throw new UnsupportedOperationException("Special level");
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
        return false;
    }

    @Override
    public boolean set(SparseBitIndex index) {
        throw new UnsupportedOperationException("Special level");
    }

    @Override
    public boolean clear(SparseBitIndex index) {
        return false;
    }

    @Override
    public void flip(SparseBitIndex index) {
        throw new UnsupportedOperationException("Special level");
    }

    @Override
    public void set(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive) {
        throw new UnsupportedOperationException("Special level");
    }

    @Override
    public void clear(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive) {
        // nothing to do - all bits are 0
    }

    @Override
    public void flip(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive) {
        throw new UnsupportedOperationException("Special level");
    }
}
