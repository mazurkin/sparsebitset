package org.sparsebitset.index;

/**
 * Represent arbitrary index including indexes with very big bit count (like IPv6 with 128 bits)
 */
public interface SparseBitIndex {

    /**
     * <p>Request segment of the index for the specified level</p>
     *
     * <p>For example of {@code int} index {@code 0x00112233} for level {@code 2} it will
     * return segment {@code 0x11} and for level {@code 0} it will return {@code 0x33}</p>
     *
     * <p>It is critical to return the lowest byte of the address on level 0 and the highest byte
     * of the index on the last level. Only in that case a sparse bit set will be compact when
     * you put ranges in it.</p>
     *
     * @param level Level of the address
     *
     * @return Segment byte, value from {@code 0} to {@code 255}
     */
    int segment(int level);

    /**
     * <p>Request the number of levels in the address</p>
     * 
     * <p>Sparse bit set fixes the count of levels on creation so this number must be consistent for
     * the same bit set.</p>
     *
     * <ul>
     *     <li>for int it will be 4</li>
     *     <li>for long it will be 8</li>
     *     <li>for IPv6 it will be 16</li>
     *     <li>etc</li>
     * </ul>
     *
     * @return How many levels (bytes) this address is consist of.
     */
    int levels();

}
