public class Coordinate {

    public static final int N = 4;
    public static final int NSquared = N * N;
    public static final int NCubed = N * N * N;

    private int position;


    // Constructors.

    private Coordinate(int position) {
        assert (position >= 0 && position < NCubed);
        this.position = position;
    }

    public static Coordinate valueOf(int x, int y, int z) {
        assert (x >= 0 && x < N);
        assert (y >= 0 && y < N);
        assert (z >= 0 && z < N);
        return coordinates[position(x, y, z)];
    }

    public static Coordinate valueOf(int position) {
        assert (position >= 0) && position < NCubed;
        return coordinates[position];
    }

    public static Coordinate forMask(long mask) {
        assert Bit.countOnes(mask) == 1;
        return coordinates[Bit.position(mask)];
    }


    // Validity checking.

    public static boolean isValid(int position) {
        return (position >= 0) && (position < NCubed);
    }

    public static boolean isValid(int x, int y, int z) {
        return (x >= 0 && x < N) && (y >= 0 && y < N) && (z >= 0 && z < N);
    }


    // Selector functions.

    public int getX() { return getX(this.position); }
    public int getY() { return getY(this.position); }
    public int getZ() { return getZ(this.position); }
    public int position() { return this.position; }

    public static int position(int x, int y, int z) {
        assert(x >= 0 && x < N);
        assert(y >= 0 && y < N);
        assert(z >= 0 && z < N);
        return z*NSquared + y*N + x;
    }

    public static int getX(int position) {
        assert (position >= 0 && position < NCubed);
        return position % N;
    }

    public static int getY(int position) {
        assert (position >= 0 && position < NCubed);
        return (position / N) % N;
    }

    public static int getZ(int position) {
        assert (position >= 0 && position < NCubed);
        return position / NSquared;
    }

    public long mask() {
        return Bit.mask(this.position);
    }


    // Equality.

    public boolean equals(int position) {
        return this.position == position;
    }

    public boolean equals(int x, int y, int z) {
        return this.position == position(x, y, z);
    }

    public boolean equals(Coordinate other) {
        return this.position == other.position;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Coordinate && this.equals((Coordinate) other);
    }

    @Override
    public int hashCode() {
        return this.position;
    }

    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ", " + getZ() + ")";
    }

    private static Coordinate[] coordinates = new Coordinate[NCubed]; static {
        for (int position = 0; position < coordinates.length; position++) {
            coordinates[position] = new Coordinate(position);
        }
    }
}
