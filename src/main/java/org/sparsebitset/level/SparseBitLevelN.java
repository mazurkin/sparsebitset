package org.sparsebitset.level;

import org.sparsebitset.index.SparseBitConstIndex;
import org.sparsebitset.index.SparseBitIndex;
import org.sparsebitset.util.SparseBitUtil;

/**
 * <p>Subset represents an intermediate level of hierarchical sparse bit set</p>
 */
public class SparseBitLevelN implements SparseBitLevel {

    private final int level;

    private final SparseBitLevel[] underlyings;

    private final int maximumOccupancy;

    private int currentFullCount;

    private int currentRealCount;

    SparseBitLevelN(int maximumOccupancy, int level) {
        this.maximumOccupancy = maximumOccupancy;
        this.level = level;

        this.underlyings = new SparseBitLevel[SparseBitUtil.LEVEL_SIZE];

        clearAll();
    }

    @Override
    public SparseBitLevelType getType() {
        return SparseBitLevelType.REAL;
    }

    @Override
    public boolean isEmpty() {
        return (currentFullCount == 0) && (currentRealCount == 0);
    }

    @Override
    public boolean isFull() {
        return (currentFullCount >= maximumOccupancy);
    }

    @Override
    public void clearAll() {
        for (int i = 0; i < SparseBitUtil.LEVEL_SIZE; i++) {
            underlyings[i] = SparseBitLevels.NULL;
        }

        currentFullCount = 0;
        currentRealCount = 0;
    }

    @Override
    public void setAll() {
        for (int i = 0; i < SparseBitUtil.LEVEL_SIZE; i++) {
            underlyings[i] = SparseBitLevels.SQUASHED;
        }

        currentFullCount = SparseBitUtil.LEVEL_SIZE;
        currentRealCount = 0;
    }

    @Override
    public void flipAll() {
        flip(SparseBitConstIndex.MIN, SparseBitConstIndex.MAX);
    }

    @Override
    public void validate() {
        int calculatedFullCount = 0;
        int calculatedRealCount = 0;

        for (int i = 0; i < SparseBitUtil.LEVEL_SIZE; i++) {
            SparseBitLevel underlying = underlyings[i];
            underlying.validate();

            switch (underlying.getType()) {
                case FULL: {
                    calculatedFullCount++;
                    break;
                }
                case REAL: {
                    calculatedRealCount++;
                    break;
                }
            }
        }

        if (currentFullCount != calculatedFullCount) {
            throw new IllegalStateException("Effective occupancy and real occupancy don't match on level " + level);
        }

        if (currentRealCount != calculatedRealCount) {
            throw new IllegalStateException("Effective usage and real usage don't match on level " + level);
        }
    }

    @Override
    public boolean get(SparseBitIndex index) {
        int segment = index.segment(level);
        SparseBitUtil.checkSegment(segment);

        SparseBitLevel underlying = underlyings[segment];
        return underlying.get(index);
    }

    @Override
    public boolean set(SparseBitIndex index) {
        int segment = index.segment(level);
        SparseBitUtil.checkSegment(segment);

        SparseBitLevel underlying = underlyings[segment];
        switch (underlying.getType()) {
            case REAL: {
                boolean result = underlying.set(index);

                if (underlying.isFull()) {
                    squashUnderlying(segment);
                }

                return result;
            }
            case NULL: {
                underlying = requireUnderlying(segment);

                return underlying.set(index);
            }
            case FULL:
                return false;
        }

        throw new IllegalStateException("Illegal execution branch");
    }

    @Override
    public boolean clear(SparseBitIndex index) {
        int segment = index.segment(level);
        SparseBitUtil.checkSegment(segment);

        SparseBitLevel underlying = underlyings[segment];
        switch (underlying.getType()) {
            case REAL: {
                boolean result = underlying.clear(index);

                if (underlying.isEmpty()) {
                    dismissUnderlying(segment);
                }

                return result;
            }
            case NULL:
                return false;
            case FULL: {
                underlying = unfoldUnderlying(segment);

                return underlying.clear(index);
            }
        }

        throw new IllegalStateException("Illegal execution branch");
    }

    @Override
    public void flip(SparseBitIndex index) {
        int segment = index.segment(level);
        SparseBitUtil.checkSegment(segment);

        SparseBitLevel underlying = underlyings[segment];
        switch (underlying.getType()) {
            case NULL: {
                underlying = requireUnderlying(segment);

                break;
            }
            case FULL: {
                underlying = unfoldUnderlying(segment);

                break;
            }
        }

        underlying.flip(index);

        if (underlying.isFull()) {
            squashUnderlying(segment);
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
        SparseBitLevel underlying = underlyings[segment];
        if (underlying.getType() == SparseBitLevelType.FULL) {
            return;
        }

        int affected = 1 + toIndexInclusive.segment(level - 1) - fromIndexInclusive.segment(level - 1);

        if (affected >= maximumOccupancy) {
            switch (underlying.getType()) {
                case REAL: {
                    squashUnderlying(segment);

                    break;
                }
                case NULL: {
                    underlyings[segment] = SparseBitLevels.SQUASHED;

                    currentFullCount++;

                    break;
                }
            }
        } else {
            switch (underlying.getType()) {
                case REAL: {
                    underlying.set(fromIndexInclusive, toIndexInclusive);

                    if (underlying.isFull()) {
                        squashUnderlying(segment);
                    }

                    break;
                }
                case NULL: {
                    underlying = requireUnderlying(segment);

                    underlying.set(fromIndexInclusive, toIndexInclusive);

                    break;
                }
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
        SparseBitLevel underlying = underlyings[segment];
        if (underlying.getType() == SparseBitLevelType.NULL) {
            return;
        }

        int affected = 1 + toIndexInclusive.segment(level - 1) - fromIndexInclusive.segment(level - 1);

        if (affected >= SparseBitUtil.LEVEL_SIZE) {
            switch (underlying.getType()) {
                case REAL: {
                    dismissUnderlying(segment);

                    break;
                }
                case FULL: {
                    underlyings[segment] = SparseBitLevels.NULL;

                    currentFullCount--;

                    break;
                }
            }
        } else {
            switch (underlying.getType()) {
                case REAL: {
                    underlying.clear(fromIndexInclusive, toIndexInclusive);

                    if (underlying.isEmpty()) {
                        dismissUnderlying(segment);
                    }

                    break;
                }
                case FULL: {
                    underlying = unfoldUnderlying(segment);

                    underlying.clear(fromIndexInclusive, toIndexInclusive);

                    break;
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
        SparseBitLevel underlying = underlyings[segment];

        int affected = 1 + toIndexInclusive.segment(level - 1) - fromIndexInclusive.segment(level - 1);

        if (affected >= SparseBitUtil.LEVEL_SIZE) {
            switch (underlying.getType()) {
                case REAL: {
                    underlying.flip(fromIndexInclusive, toIndexInclusive);

                    if (underlying.isFull()) {
                        squashUnderlying(segment);
                    } else if (underlying.isEmpty()) {
                        dismissUnderlying(segment);
                    }

                    break;
                }
                case NULL: {
                    underlyings[segment] = SparseBitLevels.SQUASHED;

                    currentFullCount++;

                    break;
                }
                case FULL: {
                    underlyings[segment] = SparseBitLevels.NULL;

                    currentFullCount--;

                    break;
                }
            }
        } else {
            switch (underlying.getType()) {
                case NULL: {
                    if (affected >= maximumOccupancy) {
                        underlyings[segment] = SparseBitLevels.SQUASHED;

                        currentFullCount++;

                        return;
                    } else {
                        underlying = requireUnderlying(segment);

                        break;
                    }
                }
                case FULL: {
                    if (SparseBitUtil.LEVEL_SIZE - affected >= maximumOccupancy) {
                        return;
                    } else {
                        underlying = unfoldUnderlying(segment);

                        break;
                    }
                }
            }
            
            underlying.flip(fromIndexInclusive, toIndexInclusive);

            if (underlying.isFull()) {
                squashUnderlying(segment);
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
    private void squashUnderlying(int segment) {
        if (underlyings[segment].getType() != SparseBitLevelType.REAL) {
            throw new IllegalStateException("Only REAL level can be squashed");
        }

        underlyings[segment] = SparseBitLevels.SQUASHED;

        currentFullCount++;
        currentRealCount--;
    }

    /**
     * Unfold underlying level from a single bit (create a layer and set all individual bits in it)
     *
     * @param segment Segment index
     */
    private SparseBitLevel unfoldUnderlying(int segment) {
        if (underlyings[segment].getType() != SparseBitLevelType.FULL) {
            throw new IllegalStateException("Only SQUASHED level can be unfold");
        }

        SparseBitLevel underlying = SparseBitLevels.createLevel(maximumOccupancy, level);
        underlying.setAll();

        underlyings[segment] = underlying;

        currentFullCount--;
        currentRealCount++;

        return underlying;
    }

    /**
     * Get an existing underlying level or create a new one
     *
     * @param segment Segment index
     *                
     * @return Level object
     */
    private SparseBitLevel requireUnderlying(int segment) {
        if (underlyings[segment].getType() != SparseBitLevelType.NULL) {
            throw new IllegalStateException("Only NULL level can be replaced with real level");
        }

        SparseBitLevel underlying = SparseBitLevels.createLevel(maximumOccupancy, level);

        underlyings[segment] = underlying;

        currentRealCount++;

        return underlying;
    }

    /**
     * Dismiss an underlying level
     *
     * @param segment Segment index
     */
    private void dismissUnderlying(int segment) {
        if (underlyings[segment].getType() != SparseBitLevelType.REAL) {
            throw new IllegalStateException("Only REAL level can be dismissed");
        }

        underlyings[segment] = SparseBitLevels.NULL;

        currentRealCount--;
    }

}
