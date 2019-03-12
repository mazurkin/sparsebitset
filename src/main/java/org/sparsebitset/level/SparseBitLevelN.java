package org.sparsebitset.level;

import org.sparsebitset.SparseBitSet;
import org.sparsebitset.index.SparseBitConstIndex;
import org.sparsebitset.index.SparseBitIndex;
import org.sparsebitset.util.SparseBitUtil;

import java.util.BitSet;

/**
 * <p>Subset represents an intermediate level of hierarchical sparse bit set</p>
 */
public class SparseBitLevelN implements SparseBitSet<SparseBitIndex> {

    private final int level;

    private final BitSet collapses;

    private final SparseBitSet<SparseBitIndex>[] underlyings;

    private final int maximumOccupancy;

    private int currentOccupancy;

    private int currentUsage;

    @SuppressWarnings("unchecked")
    SparseBitLevelN(int maximumOccupancy, int level) {
        this.maximumOccupancy = maximumOccupancy;
        this.level = level;

        this.collapses = new BitSet(SparseBitUtil.LEVEL_SIZE);
        this.underlyings = new SparseBitSet[SparseBitUtil.LEVEL_SIZE];

        this.currentOccupancy = 0;
        this.currentUsage = 0;
    }

    @Override
    public boolean isEmpty() {
        return (currentOccupancy == 0) && (currentUsage == 0);
    }

    @Override
    public boolean isFull() {
        return (currentOccupancy >= maximumOccupancy);
    }

    @Override
    public void clearAll() {
        collapses.clear();

        for (int i = 0; i < SparseBitUtil.LEVEL_SIZE; i++) {
            underlyings[i] = null;
        }

        currentOccupancy = 0;
        currentUsage = 0;
    }

    @Override
    public void setAll() {
        collapses.set(0, SparseBitUtil.LEVEL_SIZE);

        for (int i = 0; i < SparseBitUtil.LEVEL_SIZE; i++) {
            underlyings[i] = null;
        }

        currentOccupancy = SparseBitUtil.LEVEL_SIZE;
        currentUsage = 0;
    }

    @Override
    public void flipAll() {
        flip(SparseBitConstIndex.MIN, SparseBitConstIndex.MAX);
    }

    @Override
    public void validate() {
        int realOccupancy = 0;
        int realUsage = 0;

        for (int i = 0; i < SparseBitUtil.LEVEL_SIZE; i++) {
            SparseBitSet<SparseBitIndex> underlying = underlyings[i];

            boolean collapsed = collapses.get(i);

            if (underlyings[i] != null && collapsed) {
                throw new IllegalStateException("Both underlying and collapsed are set");
            }

            if (collapsed) {
                realOccupancy++;
            }

            if (underlying != null) {
                realUsage++;
                underlying.validate();
            }
        }

        if (currentOccupancy != realOccupancy) {
            throw new IllegalStateException("Effective occupancy and real occupancy don't match on level " + level);
        }

        if (currentUsage != realUsage) {
            throw new IllegalStateException("Effective usage and real usage don't match on level " + level);
        }
    }

    @Override
    public boolean get(SparseBitIndex index) {
        int segment = index.segment(level);
        SparseBitUtil.checkSegment(segment);

        if (collapses.get(segment)) {
            return true;
        } else {
            SparseBitSet<SparseBitIndex> underlying = underlyings[segment];
            if (underlying != null) {
                return underlying.get(index);
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean set(SparseBitIndex index) {
        int segment = index.segment(level);
        SparseBitUtil.checkSegment(segment);

        if (collapses.get(segment)) {
            return true;
        }

        SparseBitSet<SparseBitIndex> underlying = requireUnderlying(segment);

        boolean result = underlying.set(index);

        if (underlying.isFull()) {
            collapseUnderlying(segment);
        }

        return result;
    }

    @Override
    public boolean clear(SparseBitIndex index) {
        int segment = index.segment(level);
        SparseBitUtil.checkSegment(segment);

        if (collapses.get(segment)) {
            unfoldUnderlying(segment);
        }

        SparseBitSet<SparseBitIndex> underlying = underlyings[segment];
        if (underlying != null) {
            boolean result = underlying.clear(index);

            if (underlying.isEmpty()) {
                dismissUnderlying(segment);
            }

            return result;
        } else {
            return false;
        }
    }

    @Override
    public void flip(SparseBitIndex index) {
        int segment = index.segment(level);
        SparseBitUtil.checkSegment(segment);

        if (collapses.get(segment)) {
            unfoldUnderlying(segment);
        }

        SparseBitSet<SparseBitIndex> underlying = underlyings[segment];
        if (underlying == null) {
            underlying = requireUnderlying(segment);
        }

        underlying.flip(index);

        if (underlying.isFull()) {
            collapseUnderlying(segment);
        } else if (underlying.isEmpty()) {
            dismissUnderlying(segment);
        }
    }

    @Override
    public void set(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive) {
        int segmentFrom = fromIndexInclusive.segment(level);
        int segmentTo = toIndexInclusive.segment(level);
        SparseBitUtil.checkSegments(segmentFrom, segmentTo);

        if (segmentFrom != segmentTo) {
            setSegment(fromIndexInclusive, SparseBitConstIndex.MAX, segmentFrom);

            for (int segment = segmentFrom + 1, limit = segmentTo - 1; segment <= limit; segment++) {
                setSegment(SparseBitConstIndex.MIN, SparseBitConstIndex.MAX, segment);
            }

            setSegment(SparseBitConstIndex.MIN, toIndexInclusive, segmentTo);
        } else {
            setSegment(fromIndexInclusive, toIndexInclusive, segmentFrom);
        }
    }

    private void setSegment(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive, int segment) {
        if (collapses.get(segment)) {
            return;
        }

        int affected = 1 + toIndexInclusive.segment(level - 1) - fromIndexInclusive.segment(level - 1);

        if (affected >= maximumOccupancy) {
            SparseBitSet<SparseBitIndex> underlying = underlyings[segment];
            if (underlying != null) {
                collapseUnderlying(segment);
            } else {
                collapses.set(segment);
                currentOccupancy++;
            }
        } else {
            SparseBitSet<SparseBitIndex> underlying = requireUnderlying(segment);
            underlying.set(fromIndexInclusive, toIndexInclusive);

            if (underlying.isFull()) {
                collapseUnderlying(segment);
            }
        }
    }

    @Override
    public void clear(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive) {
        int segmentFrom = fromIndexInclusive.segment(level);
        int segmentTo = toIndexInclusive.segment(level);
        SparseBitUtil.checkSegments(segmentFrom, segmentTo);

        if (segmentFrom != segmentTo) {
            clearSegment(fromIndexInclusive, SparseBitConstIndex.MAX, segmentFrom);

            for (int segment = segmentFrom + 1, limit = segmentTo - 1; segment <= limit; segment++) {
                clearSegment(SparseBitConstIndex.MIN, SparseBitConstIndex.MAX, segment);
            }

            clearSegment(SparseBitConstIndex.MIN, toIndexInclusive, segmentTo);
        } else {
            clearSegment(fromIndexInclusive, toIndexInclusive, segmentFrom);
        }
    }

    private void clearSegment(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive, int segment) {
        int affected = 1 + toIndexInclusive.segment(level - 1) - fromIndexInclusive.segment(level - 1);

        if (affected >= SparseBitUtil.LEVEL_SIZE) {
            if (collapses.get(segment)) {
                collapses.clear(segment);
                currentOccupancy--;
            } else {
                SparseBitSet<SparseBitIndex> underlying = underlyings[segment];
                if (underlying != null) {
                    dismissUnderlying(segment);
                }
            }
        } else {
            if (collapses.get(segment)) {
                SparseBitSet<SparseBitIndex> underlying = unfoldUnderlying(segment);
                underlying.clear(fromIndexInclusive, toIndexInclusive);
            } else {
                SparseBitSet<SparseBitIndex> underlying = underlyings[segment];
                if (underlying != null) {
                    underlying.clear(fromIndexInclusive, toIndexInclusive);
                    if (underlying.isEmpty()) {
                        dismissUnderlying(segment);
                    }
                }
            }
        }
    }

    @Override
    public void flip(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive) {
        int segmentFrom = fromIndexInclusive.segment(level);
        int segmentTo = toIndexInclusive.segment(level);
        SparseBitUtil.checkSegments(segmentFrom, segmentTo);

        if (segmentFrom != segmentTo) {
            flipSegment(fromIndexInclusive, SparseBitConstIndex.MAX, segmentFrom);

            for (int segment = segmentFrom + 1, limit = segmentTo - 1; segment <= limit; segment++) {
                flipSegment(SparseBitConstIndex.MIN, SparseBitConstIndex.MAX, segment);
            }

            flipSegment(SparseBitConstIndex.MIN, toIndexInclusive, segmentTo);
        } else {
            flipSegment(fromIndexInclusive, toIndexInclusive, segmentFrom);
        }
    }

    private void flipSegment(SparseBitIndex fromIndexInclusive, SparseBitIndex toIndexInclusive, int segment) {
        int affected = 1 + toIndexInclusive.segment(level - 1) - fromIndexInclusive.segment(level - 1);

        if (affected >= SparseBitUtil.LEVEL_SIZE) {
            if (collapses.get(segment)) {
                collapses.clear(segment);
                currentOccupancy--;
            } else {
                SparseBitSet<SparseBitIndex> underlying = underlyings[segment];
                if (underlying != null) {
                    underlying.flip(fromIndexInclusive, toIndexInclusive);

                    if (underlying.isFull()) {
                        collapseUnderlying(segment);
                    } else if (underlying.isEmpty()) {
                        dismissUnderlying(segment);
                    }
                } else {
                    collapses.set(segment);
                    currentOccupancy++;
                }
            }
        } else {
            SparseBitSet<SparseBitIndex> underlying;

            if (collapses.get(segment)) {
                underlying = unfoldUnderlying(segment);
            } else {
                underlying = requireUnderlying(segment);
            }

            underlying.flip(fromIndexInclusive, toIndexInclusive);

            if (underlying.isFull()) {
                collapseUnderlying(segment);
            } else if (underlying.isEmpty()) {
                dismissUnderlying(segment);
            }
        }
    }

    /**
     * Collapse underlying level to a single bit (which means all bits on underlying level are set)
     *
     * @param segment Segment index
     */
    private void collapseUnderlying(int segment) {
        collapses.set(segment);

        underlyings[segment] = null;

        currentOccupancy++;
        currentUsage--;
    }

    /**
     * Unfold underlying level from a single bit (create a layer and set all individual bits in it)
     *
     * @param segment Segment index
     */
    private SparseBitSet<SparseBitIndex> unfoldUnderlying(int segment) {
        collapses.clear(segment);

        SparseBitSet<SparseBitIndex> underlying = SparseBitLevels.createUnderlying(maximumOccupancy, level);
        underlying.setAll();

        underlyings[segment] = underlying;

        currentOccupancy--;
        currentUsage++;

        return underlying;
    }

    /**
     * Get an existing underlying level or create a new one
     *
     * @param segment Segment index
     *                
     * @return Level object
     */
    private SparseBitSet<SparseBitIndex> requireUnderlying(int segment) {
        SparseBitSet<SparseBitIndex> underlying = underlyings[segment];

        if (underlying == null) {
            underlying = SparseBitLevels.createUnderlying(maximumOccupancy, level);

            underlyings[segment] = underlying;

            currentUsage++;
        }

        return underlying;
    }

    /**
     * Dismiss an underlying level
     *
     * @param segment Segment index
     */
    private void dismissUnderlying(int segment) {
        underlyings[segment] = null;

        currentUsage--;
    }

}
