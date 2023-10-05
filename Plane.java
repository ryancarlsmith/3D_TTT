public class Plane {

    private long positions;
    private String name;

    private Plane(String name) {
        this.positions = 0;
        this.name = name;
    }

    private Plane(long positions) {
        this.positions = positions;
        this.name = "";
    }

    public long positions() {
        return this.positions;
    }

    public String name() {
        return this.name;
    }

    public boolean contains(Coordinate coordinate) {
        return Bit.isSet(this.positions, coordinate.position());
    }

    public boolean contains(Line line) {
        return Bit.countOnes(this.positions & line.positions()) == N;
    }

    public boolean intersects(Plane plane) {
        return (this.positions & plane.positions) != 0;
    }

    public boolean intersects(Line line) {
        return (this.positions & line.positions()) != 0;
    }

    public Line intersection(Plane plane) {
        if (this.intersects(plane) && !this.equals(plane)) {
            return Line.find(this.positions & plane.positions);
        } else {
            return null;
        }
    }

    public Coordinate intersection(Line line) {
        if (this.intersects(line) && !this.contains(line)) {
            return Coordinate.forMask(line.positions() & this.positions);
        } else {
            return null;
        }
    }

    public boolean equals(Plane other) {
        return this.positions == other.positions;
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof Plane) {
            return this.equals((Plane) other);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String result = "{";
        String separator = "";
        for (int position = 0; position < Coordinate.NCubed; position++) {
            Coordinate coordinate = Coordinate.valueOf(position);
            if (this.contains(coordinate)) {
                result += separator;
                result += coordinate;
                separator = ", ";
            }
        }
        result += "}";
        return result;
    }


    private static enum Axis { X, Y, Z }
    private static final int N = Coordinate.N;

    private void set(int x, int y, int z) {
        this.positions = Bit.set(this.positions, Coordinate.position(x, y, z));
    }

    private static Plane Straight(Axis axis, int value) {
        String name = "";
        switch (axis) {
            case X: name = "YZ-Plane, X = " + value; break;
            case Y: name = "XZ-Plane, Y = " + value; break;
            case Z: name = "XY-Plane, Z = " + value; break;
        }

        Plane plane = new Plane(name);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                switch (axis) {
                    case X: plane.set(value, i, j); break;
                    case Y: plane.set(i, value, j); break;
                    case Z: plane.set(i, j, value); break;
                }
            }
        }
        return plane;
    }

    private static Plane ForwardDiagonal(Axis axis) {
        String name = "";
        switch (axis) {
            case X: name = "YZ-Forward Diagonal"; break;
            case Y: name = "XZ-Forward Diagonal"; break;
            case Z: name = "XY-Forward Diagonal"; break;
        }

        Plane plane = new Plane(name);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                switch (axis) {
                    case X: plane.set(i, j, j); break;
                    case Y: plane.set(j, i, j); break;
                    case Z: plane.set(j, j, i); break;
                }
            }
        }
        return plane;
    }

    private static Plane ReverseDiagonal(Axis axis) {
        String name = "";
        switch (axis) {
            case X: name = "YZ-Reverse Diagonal"; break;
            case Y: name = "XZ-Reverse Diagonal"; break;
            case Z: name = "XY-Reverse Diagonal"; break;
        }

        Plane plane = new Plane(name);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                switch (axis) {
                    case X: plane.set(i, j, N-1-j); break;
                    case Y: plane.set(j, i, N-1-j); break;
                    case Z: plane.set(j, N-1-j, i); break;
                }
            }
        }
        return plane;
    }

    public static final Plane[] planes = new Plane[18];
    static {
        int count = 0;
        for (Axis axis : Axis.values()) {
            for (int value = 0; value < N; value++) {
                planes[count++] = Straight(axis, value);
            }
            planes[count++] = ForwardDiagonal(axis);
            planes[count++] = ReverseDiagonal(axis);
        }
        assert count == 18;
    }
}
