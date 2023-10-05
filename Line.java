public class Line {

    private long positions;
    private String name;

    private Line(String name) {
        this.positions = 0;
        this.name = name;
    }

    private Line(long positions) {
        this.positions = positions;
        this.name = "";
    }

    public long positions() {
        // A bit vector containing the four positions
        // making up this line on the board.
        return this.positions;
    }

    public String name() {
        // A descriptive name for this line.
        return this.name;
    }

    public boolean contains(Coordinate coordinate) {
        return Bit.isSet(this.positions, coordinate.position());
    }

    public boolean intersects(Line other) {
        return (this.positions & other.positions) != 0;
    }

    public Coordinate intersection(Line other) {
        if (this.intersects(other)) {
            // return new Coordinate(this.positions & other.positions);
            return Coordinate.forMask(this.positions & other.positions);
        } else {
            return null;
        }
    }

    public boolean equals(Line other) {
        return this.positions == other.positions;
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof Line) {
            return this.equals((Line) other);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        String result = "{";
        String separator = "";
        for (int position : Bit.ones(this.positions)) {
            result += separator;
            result += position;
            separator = ", ";
        }
        result += "}";
        return result;
    }


    // Private methods to construct the various lines on the board

    private static enum Axis { X, Y, Z }
    private static final int N = Coordinate.N;

    private void set(int x, int y, int z) {
        this.positions = Bit.set(this.positions, Coordinate.position(x, y, z));
    }

    private static Line Straight(Axis axis, int row, int column) {
        String name = "Straight line: ";
        switch (axis) {
            case X: name += "Row: Y = " + row + " Z = " + column; break;
            case Y: name += "Column: X = " + row + " Z = " + column; break;
            case Z: name += "Pillar: X = " + row + " Y = " + column; break;
            default: name += "???: row = " + row + " col = " + column; break;
        }

        Line line = new Line(name);
        for (int i = 0; i < N; i++) {
            switch (axis) {
                case X: line.set(i, row, column); break;
                case Y: line.set(row, i, column); break;
                case Z: line.set(row, column, i); break;
            }
        }
        return line;
    }

    private static Line ForwardDiagonal(Axis axis, int value) {
        String name = "Forward diagonal: ";
        switch (axis) {
            case X: name += "YZ-Plane X = " + value; break;
            case Y: name += "XZ-Plane Y = " + value; break;
            case Z: name += "XY-Plane Z = " + value; break;
            default: name += "??-Plane (" + value + ")"; break;
        }

        Line line = new Line(name);
        for (int i = 0; i < N; i++) {
            switch (axis) {
                case X: line.set(value, i, i); break;
                case Y: line.set(i, value, i); break;
                case Z: line.set(i, i, value); break;
            }
        }
        return line;
    }


    private static Line ForwardDiagonal() {
        String name = "Main diagonal";
        Line line = new Line(name);
        for (int i = 0; i < N; i++) {
            line.set(i, i, i);
        }
        return line;
    }


    private static Line ReverseDiagonal(Axis axis, int value) {
        String name = "Reverse diagonal: ";
        switch (axis) {
            case X: name += "YZ-Plane X = " + value; break;
            case Y: name += "XZ-Plane Y = " + value; break;
            case Z: name += "XY-Plane Z = " + value; break;
            default: name += "??-Plane (" + value + ")"; break;
        }

        Line line = new Line(name);
        for (int i = 0; i < N; i++) {
            switch (axis) {
                case X: line.set(value, i, N-i-1); break;
                case Y: line.set(i, value, N-i-1); break;
                case Z: line.set(i, N-i-1, value); break;
            }
        }
        return line;
    }

    private static Line ReverseDiagonal(Axis axis) {
        String name = "Main diagonal: reverse";
        switch (axis) {
            case X: name += "X-Axis"; break;
            case Y: name += "Y-Axis"; break;
            case Z: name += "Z-Axis"; break;
        }

        Line line = new Line(name);
        for (int i = 0; i < N; i++) {
            switch (axis) {
                case X: line.set(N-i-1, i, i); break;
                case Y: line.set(i, N-i-1, i); break;
                case Z: line.set(i, i, N-i-1); break;
            }
        }
        return line;
    }


    // A list of all the possible lines on the board:

    public static final Line[] lines = new Line[76];
    static {
        int count = 0;
        for (Axis axis : Axis.values()) {
            for (int row = 0; row < N; row++) {
                for (int column = 0; column < N; column++) {
                    lines[count++] = Straight(axis, row, column);
                }
            }
            for (int value = 0; value < N; value++) {
                lines[count++] = ForwardDiagonal(axis, value);
                lines[count++] = ReverseDiagonal(axis, value);
            }
            lines[count++] = ReverseDiagonal(axis);
        }
        lines[count++] = ForwardDiagonal();
        assert count == 76;
    }

    public static Line find(long positions) {
        for (Line line : lines) {
            if (line.positions() == positions) return line;
        }
        return null;
    }
}
