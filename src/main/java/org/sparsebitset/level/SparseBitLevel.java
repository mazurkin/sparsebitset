package org.sparsebitset.level;

import org.sparsebitset.index.SparseBitIndex;

/**
 * <p>Internal level interface</p>
 */
public interface SparseBitLevel {

    SparseBitLevelType getType();

    SparseBitLevel copy();

    boolean isEmpty();

    boolean isFull();

    void clearAll();

    void setAll();

    void flipAll();

    void validate();

    boolean get(SparseBitIndex index);

    boolean set(SparseBitIndex index);

    boolean clear(SparseBitIndex index);

    void flip(SparseBitIndex index);

    void set(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive);

    void clear(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive);

    void flip(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive);

}
