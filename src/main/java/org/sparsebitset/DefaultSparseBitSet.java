package org.sparsebitset;

import org.sparsebitset.index.SparseBitIndex;
import org.sparsebitset.level.SparseBitLevel;
import org.sparsebitset.level.SparseBitLevels;
import org.sparsebitset.util.SparseBitUtil;

/**
 * <p>Default implementation of sparse hierarchical bit set</p>
 *
 * <p>Implementation can be optimized even more for the sake of precision. It support concept of
 * premature collapse of an underlying level. For example if you set {@code maximumOccupancy=132} then
 * an underlying level will be collapsed when it has just 132 bits (from 256). So this segment will generate
 * 256−132=124 false positives. If {@code maximumOccupancy=256} (by default) the instance will be precise and
 * no any false positive will be generated.</p>
 *
 * @param <I> Index type
 */
public class DefaultSparseBitSet<I extends SparseBitIndex> implements SparseBitSet<I> {

    private final int levels;

    private final SparseBitLevel base;

    /**
     * Constructs a default set
     *
     * @param levels How many levels are in the index (bits = levels * 8)
     * @param maximumOccupancy How many items (bits) must be in underlying layer in order to collapse it (2..256)
     */
    public DefaultSparseBitSet(int levels, int maximumOccupancy) {
        if (levels <= 0) {
            throw new IllegalArgumentException("Need positive level count: " + levels);
        }

        if (maximumOccupancy < 2 || maximumOccupancy > SparseBitUtil.LEVEL_SIZE) {
            throw new IllegalArgumentException("Maximum occupancy level is invalid: " + maximumOccupancy);
        }

        this.levels = levels;
        this.base = SparseBitLevels.createLevel(maximumOccupancy, levels);
    }

    /**
     * Constructs a default precise set with no false positives
     *
     * @param levels How many levels are in the index (bits = levels * 8)
     */
    public DefaultSparseBitSet(int levels) {
        this(levels, SparseBitUtil.LEVEL_SIZE);
    }

    /**
     * Constructs a default set
     *
     * @param levels How many levels are in the index (bits = levels * 8)
     *
     * @return Sparse set
     */
    public static <I extends SparseBitIndex> SparseBitSet<I> createWithLevels(int levels) {
        if (levels <= 0) {
            throw new IllegalArgumentException("Need positive level count: " + levels);
        }

        return new DefaultSparseBitSet<>(levels);
    }

    /**
     * Constructs a default set
     *
     * @param bits How many bits are in the index (must be divisible by 8 bits)
     *
     * @return Sparse set
     */
    public static <I extends SparseBitIndex> SparseBitSet<I> createWithBits(int bits) {
        if (bits <= 0) {
            throw new IllegalArgumentException("Need positive bit count: " + bits);
        }

        if (bits % 8 > 0) {
            throw new IllegalArgumentException("Bit count must be divisible by 8: " + bits);
        }

        return new DefaultSparseBitSet<>(bits / 8);
    }

    @Override
    public boolean isEmpty() {
        return base.isEmpty();
    }

    @Override
    public boolean isFull() {
        return base.isFull();
    }

    @Override
    public void clearAll() {
        base.clearAll();
    }

    @Override
    public void setAll() {
        base.setAll();
    }

    @Override
    public void flipAll() {
        base.flipAll();
    }

    @Override
    public void validate() {
        base.validate();
    }

    @Override
    public boolean get(I index) {
        checkIndex(index);

        return base.get(index);
    }

    @Override
    public boolean set(I index) {
        checkIndex(index);

        return base.set(index);
    }

    @Override
    public boolean clear(I index) {
        checkIndex(index);

        return base.clear(index);
    }

    @Override
    public void flip(I index) {
        checkIndex(index);

        base.flip(index);
    }

    @Override
    public void set(I fromIndexInclusive, I toIndexInclusive) {
        checkIndex(fromIndexInclusive);
        checkIndex(toIndexInclusive);

        base.set(fromIndexInclusive, toIndexInclusive);
    }

    @Override
    public void clear(I fromIndexInclusive, I toIndexInclusive) {
        checkIndex(fromIndexInclusive);
        checkIndex(toIndexInclusive);

        base.clear(fromIndexInclusive, toIndexInclusive);
    }

    @Override
    public void flip(I fromIndexInclusive, I toIndexInclusive) {
        checkIndex(fromIndexInclusive);
        checkIndex(toIndexInclusive);

        base.flip(fromIndexInclusive, toIndexInclusive);
    }

    private void checkIndex(I index) {
        if (index == null) {
            throw new IllegalArgumentException("Index can't be null");
        }

        if (index.levels() < this.levels) {
            throw new IllegalArgumentException("Index has less levels than the set " + index.levels());
        }
    }

}
