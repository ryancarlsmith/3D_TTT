import java.util.Iterator;

public class Bit {

    public static boolean isSet(long x, int pos) {
        return (x & (1L << pos)) != 0;
    }

    public static long set(long x, int pos) {
        return x |= (1L << pos);
    }

    public static long clear(long x, int pos) {
        return x &= ~(1L << pos);
    }

    public static long mask(int position) {
        return 1L << position;
    }

    public static int position(long mask) {
        // Position of the leading one.
        // Inverse of the mask function.
        return 63 - countLeadingZeros(mask);
    }

    public static int countOnes(long x) { //squares taken in that line
        int count = 0;
        while (x != 0) {
            x &= (x - 1);
            count++;
        }
        return count;
    }

    public static int countLeadingZeros(long x) { //
        int count = 63;
        int shift = 32;
        long y;

        while (shift > 0) {
            y = x >> shift;
            if (y != 0) {
                count -= shift;
                x = y;
            }
            shift >>= 1;
        }
        return x == 0 ? count + 1 : count;
    }


    // Iterators: iterate over bit positions containing a one.
    //
    //     Allows loops such as
    //
    //     int numberOnes = 0;
    //     for (Integer position : Bit.ones(x)) {
    //         numberOnes++;
    //       }

    public static Iterator<Integer> iterator(long bits) {
        return new BitIterator(bits);
    }

    public static Iterable<Integer> ones(long bits) {
        return new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return new BitIterator(bits);
            }
        };
    }

    private static class BitIterator implements Iterator<Integer> {

        private long bits;

        public BitIterator(long bits) {
            this.bits = bits;
        }

        public boolean hasNext() {
            return this.bits != 0;
        }

        public Integer next() {
            long temp = this.bits & (this.bits - 1);   // Clear the least significant 1 bit
            long mask = this.bits - temp;              // Mask for the bit just cleared
            this.bits = temp;                          // Update the iterator state
            return position(mask);                     // Position of the bit just cleared
        }
    }
}
