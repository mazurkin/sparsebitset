package org.sparsebitset.level;

import org.sparsebitset.index.SparseBitIndex;
import org.sparsebitset.util.SparseBitUtil;

import java.util.BitSet;

/**
 * <p>Subset represents a final level of hierarchical sparse bit set</p>
 */
public class SparseBitLevel0 implements SparseBitLevel {

    private final BitSet bits;

    private final int maximumOccupancy;

    private int currentOccupancy;

    SparseBitLevel0(int maximumOccupancy) {
        this.maximumOccupancy = maximumOccupancy;

        this.bits = new BitSet(SparseBitUtil.LEVEL_SIZE);

        this.currentOccupancy = 0;
    }

    @Override
    public SparseBitLevelType getType() {
        return SparseBitLevelType.REAL;
    }

    @Override
    public boolean isEmpty() {
        return currentOccupancy == 0;
    }

    @Override
    public boolean isFull() {
        return currentOccupancy >= maximumOccupancy;
    }

    @Override
    public void clearAll() {
        bits.clear();

        currentOccupancy = 0;
    }

    @Override
    public void setAll() {
        bits.set(0, SparseBitUtil.LEVEL_SIZE);

        currentOccupancy = SparseBitUtil.LEVEL_SIZE;
    }

    @Override
    public void flipAll() {
        bits.flip(0, SparseBitUtil.LEVEL_SIZE);

        currentOccupancy = SparseBitUtil.LEVEL_SIZE - currentOccupancy;
    }

    @Override
    public void validate() {
        if (currentOccupancy != bits.cardinality()) {
            throw new IllegalStateException("Effective occupancy and real occupancy don't match on level 0");
        }
    }

    @Override
    public boolean get(SparseBitIndex index) {
        int segment = index.segment(0);
        SparseBitUtil.checkSegment(segment);

        return bits.get(segment);
    }

    @Override
    public boolean set(SparseBitIndex index) {
        int segment = index.segment(0);
        SparseBitUtil.checkSegment(segment);

        if (bits.get(segment)) {
            return false;
        } else {
            bits.set(segment);

            currentOccupancy++;

            return true;
        }
    }

    @Override
    public boolean clear(SparseBitIndex index) {
        int segment = index.segment(0);
        SparseBitUtil.checkSegment(segment);

        if (bits.get(segment)) {
            bits.clear(segment);

            currentOccupancy--;

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void flip(SparseBitIndex index) {
        int segment = index.segment(0);
        SparseBitUtil.checkSegment(segment);

        if (bits.get(segment)) {
            currentOccupancy--;
        } else {
            currentOccupancy++;
        }

        bits.flip(segment);
    }

    @Override
    public void set(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive) {
        int segmentFrom = fromIndexInclusive.segment(0);
        int segmentTo = toIndexInclusive.segment(0);
        SparseBitUtil.checkSegments(segmentFrom, segmentTo);

        bits.set(segmentFrom, segmentTo + 1);

        // recalculate occupancy after the last bulk operation
        currentOccupancy = bits.cardinality();
    }

    @Override
    public void clear(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive) {
        int segmentFrom = fromIndexInclusive.segment(0);
        int segmentTo = toIndexInclusive.segment(0);
        SparseBitUtil.checkSegments(segmentFrom, segmentTo);

        bits.clear(segmentFrom, segmentTo + 1);

        // recalculate occupancy after the last bulk operation
        currentOccupancy = bits.cardinality();
    }

    @Override
    public void flip(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive) {
        int segmentFrom = fromIndexInclusive.segment(0);
        int segmentTo = toIndexInclusive.segment(0);
        SparseBitUtil.checkSegments(segmentFrom, segmentTo);

        bits.flip(segmentFrom, segmentTo + 1);

        // recalculate occupancy after the last bulk operation
        currentOccupancy = bits.cardinality();
    }



}
