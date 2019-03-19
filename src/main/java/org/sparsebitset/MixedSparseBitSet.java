package org.sparsebitset;

import org.sparsebitset.index.SparseBitIndex;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>Combines hashtable for individual indices and sparse bit set for ranges. Doesn't support {@code clear()}
 * and {@code flip()} methods. Which means you can use only {@code get()} and {@code set()} methods
 * for the sake of memory footprint if you have a lot of individual indices.</p>
 *
 * @param <I> type of index
 */
public class MixedSparseBitSet<I extends SparseBitIndex> implements SparseBitSet<I> {

    private final Set<I> individuals;

    private final SparseBitSet<I> ranges;

    public MixedSparseBitSet(int levels) {
        this.individuals = new HashSet<>();
        this.ranges = DeepSparseBitSet.createWithLevels(levels);
    }

    @Override
    public boolean isEmpty() {
        return individuals.isEmpty() && ranges.isEmpty();
    }

    @Override
    public boolean isFull() {
        return ranges.isFull();
    }

    @Override
    public void clearAll() {
        individuals.clear();
        ranges.clearAll();
    }

    @Override
    public void setAll() {
        individuals.clear();
        ranges.setAll();
    }

    @Override
    public void flipAll() {
        throw new UnsupportedOperationException("Not supported for a mixed set");
    }

    @Override
    public void validate() {
        ranges.validate();
    }

    @Override
    public boolean get(I index) {
        return individuals.contains(index) || ranges.get(index);
    }

    @Override
    public boolean set(I index) {
        return individuals.add(index);
    }

    @Override
    public boolean clear(I index) {
        boolean r1 = individuals.remove(index);
        boolean r2 = ranges.clear(index);

        return r1 || r2;
    }

    @Override
    public void flip(I index) {
        throw new UnsupportedOperationException("Not supported for a mixed set");
    }

    @Override
    public void set(I fromIndexInclusive, I toIndexInclusive) {
        ranges.set(fromIndexInclusive, toIndexInclusive);
    }

    @Override
    public void clear(I fromIndexInclusive, I toIndexInclusive) {
        throw new UnsupportedOperationException("Not supported for a mixed set");
    }

    @Override
    public void flip(I fromIndexInclusive, I toIndexInclusive) {
        throw new UnsupportedOperationException("Not supported for a mixed set");
    }
}
