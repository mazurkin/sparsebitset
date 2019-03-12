package org.sparsebitset.index;

import java.net.InetAddress;

/**
 * Index for {@link InetAddress}
 */
public class SparseBitInetAddressIndex extends SparseBitBytesIndex {

    protected SparseBitInetAddressIndex(InetAddress address) {
        super(address.getAddress());
    }

    public static SparseBitInetAddressIndex of(InetAddress index) {
        return new SparseBitInetAddressIndex(index);
    }

}
