package org.sparsebitset;

import org.sparsebitset.index.SparseBitIndex;

/**
 * <p>Sparse bit set abstraction. Unlike standard {@link java.util.BitSet} it can be addressed by very
 * long indexes including IPv6 which is 128-bit long</p>
 *
 * @param <I> Type of the index
 */
public interface SparseBitSet<I extends SparseBitIndex> {

    /**
     * Check is the set empty (no a single bit is set)

     * @return {@code true} if the set is empty
     */
    boolean isEmpty();

    /**
     * Check is the set is full (when {@code currentOccupancy >= maximumOccupancy})
     *
     * @return {@code true} if the set is full
     */
    boolean isFull();

    /**
     * Set all bits to 0
     */
    void clearAll();

    /**
     * Set all bits to 1
     */
    void setAll();

    /**
     * Flip all bits (inverse the set)
     */
    void flipAll();

    /**
     * Validate the internal structure of the set. May be costly operation and mostly is used in tests
     */
    void validate();

    /**
     * Get a bit from the set
     *
     * @param index Index of the bit
     *
     * @return Bit's value
     */
    boolean get(I index);

    /**
     * Set a single bit in the set
     *
     * @param index Index of the bit
     *
     * @return {@code true} if bit is switched
     */
    boolean set(I index);

    /**
     * Clear a single bit in the set
     *
     * @param index Index of the bit
     *
     * @return {@code true} if bit is switched
     */
    boolean clear(I index);

    /**
     * Toggle a single bit in the set
     *
     * @param index Index of the bit
     */
    void flip(I index);

    /**
     * Set a range of bits
     *
     * @param fromIndexInclusive Start of the range (inclusive)
     * @param toIndexInclusive Start of the range (inclusive)
     */
    void set(I fromIndexInclusive, I toIndexInclusive);

    /**
     * Clear a range of bits
     *
     * @param fromIndexInclusive Start of the range (inclusive)
     * @param toIndexInclusive Start of the range (inclusive)
     */
    void clear(I fromIndexInclusive, I toIndexInclusive);

    /**
     * Flip a range of bits
     *
     * @param fromIndexInclusive Start of the range (inclusive)
     * @param toIndexInclusive Start of the range (inclusive)
     */
    void flip(I fromIndexInclusive, I toIndexInclusive);

}
