package org.sparsebitset;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sparsebitset.index.SparseBitIntIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Ignore("Takes too much time - run manually")
public class GrantConcurrentTest {

    private SparseBitSet<SparseBitIntIndex> set;

    @Before
    public void setUp() {
        SparseBitSet<SparseBitIntIndex> delegate = DefaultSparseBitSet.createWithLevels(SparseBitIntIndex.LEVELS);

        this.set = ConcurrentSparseBitSetProxy.proxy(delegate);
    }

    @Test
    public void test() throws Exception {
        List<WorkerThread> threads = new ArrayList<>();

        // prepare workers
        // segments and ranges must be isolated and not to intersect each other

        threads.add(new WorkerThread(5000, () -> set.validate()));

        threads.add(new WorkerThread(2, new SingleBitJuggler(set, 0).createTasks()));
        threads.add(new WorkerThread(4, new SingleBitJuggler(set, 2).createTasks()));
        threads.add(new WorkerThread(1, new SingleBitJuggler(set, 53).createTasks()));
        threads.add(new WorkerThread(8, new SingleBitJuggler(set, 102).createTasks()));
        threads.add(new WorkerThread(3, new SingleBitJuggler(set, 143).createTasks()));
        threads.add(new WorkerThread(4, new SingleBitJuggler(set, 176).createTasks()));
        threads.add(new WorkerThread(5, new SingleBitJuggler(set, 234).createTasks()));
        threads.add(new WorkerThread(7, new SingleBitJuggler(set, 252).createTasks()));

        threads.add(new WorkerThread(4, new MultipleBitJuggler(set, 3, 10).createTasks()));
        threads.add(new WorkerThread(2, new MultipleBitJuggler(set, 12, 52).createTasks()));
        threads.add(new WorkerThread(6, new MultipleBitJuggler(set, 54, 100).createTasks()));
        threads.add(new WorkerThread(7, new MultipleBitJuggler(set, 177, 230).createTasks()));
        threads.add(new WorkerThread(9, new MultipleBitJuggler(set, 237, 249).createTasks()));

        // start all threads

        for (WorkerThread thread : threads) {
            thread.start();
        }

        // work for some time

        Thread.sleep(60000);

        // finalize

        for (WorkerThread thread : threads) {
            thread.interrupt();
        }

        for (WorkerThread thread : threads) {
            thread.join(1000);
        }

        for (WorkerThread thread : threads) {
            assertNull(thread.exception);
        }
    }

    /**
     * A sequence of commands on a single bit
     */
    private static final class SingleBitJuggler {

        private final int segment;

        private final SparseBitSet<SparseBitIntIndex> set;

        private final Random random;

        private int index;

        private SingleBitJuggler(SparseBitSet<SparseBitIntIndex> set, int segment) {
            this.set = set;
            this.segment = segment;
            this.random = new Random(segment);
        }

        private Runnable[] createTasks() {
            return new Runnable[] {
                    () -> index = random.nextInt(0x01000000) << 8 | segment,

                    // check the bit (unset)
                    () -> assertFalse(set.get(SparseBitIntIndex.of(index))),

                    // first set
                    () -> assertTrue(set.set(SparseBitIntIndex.of(index))),

                    // repeated set doesn't have any effect
                    () -> assertFalse(set.set(SparseBitIntIndex.of(index))),

                    // check the bit (set)
                    () -> assertTrue(set.get(SparseBitIntIndex.of(index))),

                    // clear the bit
                    () -> assertTrue(set.clear(SparseBitIntIndex.of(index))),

                    // repeated clear doesn't have any effect
                    () -> assertFalse(set.clear(SparseBitIntIndex.of(index))),

                    // check the bit (unset)
                    () -> assertFalse(set.get(SparseBitIntIndex.of(index))),

                    // flip the bit
                    () -> set.flip(SparseBitIntIndex.of(index)),

                    // check the bit (set)
                    () -> assertTrue(set.get(SparseBitIntIndex.of(index))),

                    // flip the bit once again
                    () -> set.clear(SparseBitIntIndex.of(index)),

                    // check the bit (unset)
                    () -> assertFalse(set.get(SparseBitIntIndex.of(index))),
            };
        }
    }

    /**
     * A sequence of commands on a range of bits
     */
    private static final class MultipleBitJuggler {

        private final int fromSegment;

        private final int toSegment;

        private final SparseBitSet<SparseBitIntIndex> set;

        private final Random random;

        private int fromIndex;

        private int toIndex;

        private MultipleBitJuggler(SparseBitSet<SparseBitIntIndex> set, int fromSegment, int toSegment) {
            this.set = set;
            this.fromSegment = fromSegment;
            this.toSegment = toSegment;
            this.random = new Random(fromSegment + toSegment);
        }

        private Runnable[] createTasks() {
            return new Runnable[] {
                    () -> {
                        int index = random.nextInt(0x01000000) << 8;
                        fromIndex = index | fromSegment;
                        toIndex = index | toSegment;
                    },

                    // check the bit (unset)
                    () -> checkRange(set, fromIndex, toIndex, false),

                    // first set
                    () -> set.set(SparseBitIntIndex.of(fromIndex), SparseBitIntIndex.of(toIndex)),

                    // check the bit (set)
                    () -> checkRange(set, fromIndex, toIndex, true),

                    // clear the bit
                    () -> set.clear(SparseBitIntIndex.of(fromIndex), SparseBitIntIndex.of(toIndex)),

                    // check the bit (unset)
                    () -> checkRange(set, fromIndex, toIndex, false),

                    // flip the bit
                    () -> set.flip(SparseBitIntIndex.of(fromIndex), SparseBitIntIndex.of(toIndex)),

                    // check the bit (set)
                    () -> checkRange(set, fromIndex, toIndex, true),

                    // flip the bit once again
                    () -> set.flip(SparseBitIntIndex.of(fromIndex), SparseBitIntIndex.of(toIndex)),

                    // check the bit (unset)
                    () -> checkRange(set, fromIndex, toIndex, false),
            };
        }
    }

    private static final class WorkerThread extends Thread {

        private final Runnable[] delegates;

        private final long sleepMs;

        private Throwable exception;

        private WorkerThread(long sleepMs, Runnable... delegates) {
            this.sleepMs = sleepMs;
            this.delegates = delegates;
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    runDelegates();
                } catch (InterruptedException e) {
                    return;
                } catch (Exception e) {
                    this.exception = e;
                }
            }
        }

        private void runDelegates() throws InterruptedException {
            for (Runnable delegate : delegates) {
                delegate.run();

                if (isInterrupted()) {
                    return;
                } else {
                    Thread.sleep(sleepMs);
                }
            }
        }
    }

    private static void checkRange(SparseBitSet<SparseBitIntIndex> set,
                                   int fromIndexInclusive, int toIndexInclusive, boolean expected)
    {
        for (int i = fromIndexInclusive; i <= toIndexInclusive; i++) {
            boolean result = set.get(SparseBitIntIndex.of(i));

            assertEquals(String.format("Mismatch on %08X", i), expected, result);
        }
    }

}
