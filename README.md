# Sparse Bit Set

Sparse bit set implementation can keep a number of individual items of arbitrary length 
as well as their ranges.

For example:

* int
* long
* IPv4 address (32 bits)
* IPv6 address (128 bits) 
* byte array (arbitrary length)

# Sample with IPv6

    SparseBitSet<SparseBitInetAddressIndex> set = DefaultSparseBitSet.createWithBits(128);

    // set the individual two bits
    assertTrue(set.set(IPv6(0xFE80CD0000000000L, 0x00000000211E729CL)));
    assertTrue(set.set(IPv6(0xFE80CD0000000000L, 0x76330000211E729CL)));

    // set the huge range
    set.set(IPv6(0x0386330000000000L, 0x0000000000665320L), IPv6(0x0629872300000000L, 0x0089727327777777L));

    // check individual bits
    assertTrue(set.get(IPv6(0xFE80CD0000000000L, 0x00000000211E729CL)));
    assertTrue(set.get(IPv6(0xFE80CD0000000000L, 0x76330000211E729CL)));

    // check bits in the range
    assertTrue(set.get(IPv6(0x0386330000010000L, 0x0000000000000000L)));
    assertTrue(set.get(IPv6(0x0386330000020000L, 0x0000000000000000L)));
    assertTrue(set.get(IPv6(0x0629872300000000L, 0x0000000000000001L)));
    assertTrue(set.get(IPv6(0x0629872300000000L, 0x0000100000000000L)));
    assertTrue(set.get(IPv6(0x0629872300000000L, 0x0080000000000000L)));

    // check other random bits
    assertFalse(set.get(IPv6(0x0000100000000000L, 0x00000000211E729CL)));
    assertFalse(set.get(IPv6(0x0000000001000000L, 0x00000000211E729CL)));

    // invert the set
    set.flipAll();

    // check individual bits
    assertFalse(set.get(IPv6(0xFE80CD0000000000L, 0x00000000211E729CL)));
    assertFalse(set.get(IPv6(0xFE80CD0000000000L, 0x76330000211E729CL)));

    // check bits in the range
    assertFalse(set.get(IPv6(0x0386330000010000L, 0x0000000000000000L)));
    assertFalse(set.get(IPv6(0x0386330000020000L, 0x0000000000000000L)));
    assertFalse(set.get(IPv6(0x0629872300000000L, 0x0000000000000001L)));
    assertFalse(set.get(IPv6(0x0629872300000000L, 0x0000100000000000L)));
    assertFalse(set.get(IPv6(0x0629872300000000L, 0x0080000000000000L)));

    // check other random bits
    assertTrue(set.get(IPv6(0x0000100000000000L, 0x00000000211E729CL)));
    assertTrue(set.get(IPv6(0x0000000001000000L, 0x00000000211E729CL)));

# Thread-safe proxy

    SparseBitSet<SparseBitIntIndex> delegate = DefaultSparseBitSet.createWithLevels(SparseBitIntIndex.LEVELS);

    SparseBitSet<SparseBitIntIndex> set = ConcurrentSparseBitSetProxy.proxy(delegate);

# Alternatives

* Hash set - if you have small amount of individual indexes and no ranges, O(1) access

* Bloom filter - if you have huge amount of individual indexes and some false positives are allowed, O(1) access

* Sorted array with binary search - if you have relatively small number of ranges, O(log(n)) access