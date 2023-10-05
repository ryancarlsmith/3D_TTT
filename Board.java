
import java.util.ArrayList;
import java.util.Iterator;

public class Board {

    public static final int N = 4;

    private long x; // Boolean vector of positions containing X's
    private long o; // Boolean vector of positions containing O's


    // Constructors.

    public Board() {
        this.x = 0;
        this.o = 0;
    }

    public Board(Board board) {
        this.x = board.x;
        this.o = board.o;
    }

    public Board(String s) {
        int position = 0;
        this.x = 0;
        this.o = 0;

        for (int i= 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case 'x':
                case 'X':
                    this.set(position++, Player.X);
                    break;

                case 'o':
                case 'O':
                    this.set(position++, Player.O);
                    break;

                case '.':
                    position++;
                    break;

                case ' ':
                case '|':
                    break;

                default:
                    throw new IllegalArgumentException("Invalid player: " + c);
            }
        }
    }


    // Empty squares.

    public boolean isEmpty(int position) {
        assert Coordinate.isValid(position);
        return ! Bit.isSet(this.x | this.o, position);
    }

    public boolean isEmpty(Coordinate coordinate) {
        return this.isEmpty(coordinate.position());
    }

    public boolean isEmpty(int x, int y, int z) {
        return this.isEmpty(Coordinate.position(x, y, z));
    }

    public int numberEmptySquares() {
        return Bit.countOnes(~(this.x | this.o));
    }


    // Get value of a square on the board.

    public long get(Player player) {
        switch(player) {
            case EMPTY: return ~(this.x | this.o);
            case X: return this.x;
            case O: return this.o;
            default: return 0;
        }
    }

    public Player get(int position) {
        assert Coordinate.isValid(position);
        if (Bit.isSet(this.x, position)) return Player.X;
        if (Bit.isSet(this.o, position)) return Player.O;
        return Player.EMPTY;
    }

    public Player get(Coordinate coordinate) {
        return this.get(coordinate.position());
    }

    public Player get(int x, int y, int z) {
        return this.get(Coordinate.position(x, y, z));
    }


    // Set value of a square on the board.

    public void set(int position, Player player) {
        assert (isEmpty(position));
        switch (player) {
            case X:
                this.x = Bit.set(this.x, position);
                break;

            case O:
                this.o = Bit.set(this.o, position);
                break;

            default:
                break;
        }
    }

    public void set(Coordinate coordinate, Player player) {
        set(coordinate.position(), player);
    }

    public void set(int x, int y, int z, Player player) {
        set(Coordinate.position(x, y, z), player);
    }

    public void clear(int position) {
        this.x = Bit.clear(this.x, position);
        this.o = Bit.clear(this.o, position);
    }

    public void clear(Coordinate coordinate) {
        clear(coordinate.position());
    }

    public void clear(int x, int y, int z) {
        clear(Coordinate.valueOf(x, y, z));
    }


    // Equality.

    public boolean equals(Board other) {
        return this.o == other.o && this.x == other.x;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Board && this.equals((Board) other);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.x) * Long.hashCode(this.o);
    }


    // Image & printing functions.

    @Override
    public String toString() {
        String result = "";
        String separator = "";

        for (int position = 0; position < 64; position++) {
            result += separator;
            result += this.get(position).toString();
            if (position % 16 == 0) {
                separator = " | ";
            } else if (position % 4 == 0) {
                separator = " ";
            } else {
                separator = "";
            }
        }
        return result;
    }


    public static Board valueOf(String s) {
        return new Board(s);
    }


    public void print() {
        for (int y = N-1; y >= 0; y--) {
            for (int z = 0; z < N; z++) {
                for (int x = 0; x < N; x++) {
                    System.out.print(this.get(x, y, z));
                }
                System.out.print("    ");
            }
            System.out.println();
        }
    }


    // Generate new board for a given move.

    public Board next(Coordinate move, Player player) {
        assert this.isEmpty(move);
        Board result = new Board(this);
        result.set(move, player);
        return result;
    }

    public Board next(int position, Player player) {
        return next(Coordinate.valueOf(position), player);
        // return next(new Coordinate(position), player);
    }

    public Board next(int x, int y, int z, Player player) {
        return next(Coordinate.valueOf(x, y, z), player);
        // return next (new Coordinate(x, y, z), player);
    }

    public static int evaluate(Board board, Player player){
        /*
        list of 5 features:
        1. best squares to have
        2. best arrangment of squares to have (4 corners/4 central)
        3. # of 2 in a rows
        4. # of 3 in a rows
        5. a 4 in a row win
        6.
         */

        if (wins(player, board)){
            return Integer.MAX_VALUE;
        }

        if (wins(player.other(), board)){
            return Integer.MIN_VALUE;
        }

        int result = -1;

        int twosWeight = 4;
        int threesWeight = 5;
        int goodSquaresWeight = 5;
        int blockedLinesWeight = 2;
        int onesWeight = 2;


        int blockedLines = blockedLinesWeight * numOfOpponentBlockedLines(board, player);
        int goodSquares = goodSquaresWeight * numOfGoodSquaresTaken(board, player);
//        int ones = onesWeight * numOfOneInARows(board, player);
//        int twos = twosWeight * numOfTwoInARows(board,player);
//        int threes = threesWeight * numOfThreeInARows(board,player);
        int lines = numOfLinesInARows(board, player, onesWeight, twosWeight, threesWeight);

        int opponentVal = 3*numOfTwoInARows(board, player.other()) + 4*numOfThreeInARows(board, player.other());

        result = lines + goodSquares + blockedLines;

//        if(numOfThreeInARows(board, player) > 1){
//            result += 100;
//        }
        return result;
    }

    public static int numOfOpponentBlockedLines(Board b, Player player){ //advantage for player, they are the one blocking
        int numOfOpponentBlockedTwos = 0;
        long boardYou = 0;
        long boardOther = 0;
        long maskedLongYou = 0;
        long maskedLongOther = 0;

        if (player.equals(player.X)) {
            boardYou = b.x;
            boardOther = b.o;
        } else if (player.equals(player.O)) {
            boardYou = b.o;
            boardOther = b.x;
        } else {
            System.out.println("invalid player");
        }

        for (Line l : Line.lines) {
            maskedLongYou = l.positions() & boardYou;
            maskedLongOther = l.positions() & boardOther;

            if ((Bit.countOnes(maskedLongYou) == 2 || Bit.countOnes(maskedLongYou) == 3) && Bit.countOnes(maskedLongOther) > 0) {
                numOfOpponentBlockedTwos++;
            }
        }
        return numOfOpponentBlockedTwos;
    }

    public static int numOfGoodSquaresTaken(Board b, Player player){
        int numOfGoodSquares=0;

        int[] goodSquaresPositions = new int[16];
        goodSquaresPositions[0] = 0;
        goodSquaresPositions[1] = 3;
        goodSquaresPositions[2]= 12;
        goodSquaresPositions[3]= 15;
        goodSquaresPositions[4]= 21;
        goodSquaresPositions[5]= 22;
        goodSquaresPositions[6]= 25;
        goodSquaresPositions[7]= 26;
        goodSquaresPositions[8]= 36;
        goodSquaresPositions[9]= 37;
        goodSquaresPositions[10]= 41;
        goodSquaresPositions[11]= 42;
        goodSquaresPositions[12]= 48;
        goodSquaresPositions[13]= 51;
        goodSquaresPositions[14]= 60;
        goodSquaresPositions[15]= 63;

        for (int i : goodSquaresPositions){
            if (b.get(i) == player){
                numOfGoodSquares++;
            }
        }

        return numOfGoodSquares;
    }

    public static int numOfLinesInARows(Board b, Player player, int onesWeight, int twosWeight, int threesWeight) {
        int numOfOnes = 0;
        int numOfTwos = 0;
        int numOfThrees = 0;
        long boardYou = 0;
        long boardOther = 0;
        long maskedLongYou = 0;
        long maskedLongOther = 0;

        if (player.equals(player.X)) {
            boardYou = b.x;
            boardOther = b.o;
        } else if (player.equals(player.O)) {
            boardYou = b.o;
            boardOther = b.x;
        } else {
            System.out.println("invalid player");
        }

        for (Line l : Line.lines) {
            maskedLongYou = l.positions() & boardYou;
            maskedLongOther = l.positions() & boardOther;

            if (Bit.countOnes(maskedLongYou) == 1 && Bit.countOnes(maskedLongOther) == 0) {
                numOfOnes++;
            }
            if (Bit.countOnes(maskedLongYou) == 2 && Bit.countOnes(maskedLongOther) == 0) {
                numOfTwos++;
            }
            if (Bit.countOnes(maskedLongYou) == 3 && Bit.countOnes(maskedLongOther) == 0) {
                numOfThrees++;
            }
        }
        int result = (numOfOnes * onesWeight) + (numOfTwos * twosWeight) + (numOfThrees * threesWeight);
        return result;
    }

    public static int numOfTwoInARows(Board b, Player player) {
        int numOfTwos = 0;
        long boardYou = 0;
        long boardOther = 0;
        long maskedLongYou = 0;
        long maskedLongOther = 0;

        if (player.equals(player.X)) {
            boardYou = b.x;
            boardOther = b.o;
        } else if (player.equals(player.O)) {
            boardYou = b.o;
            boardOther = b.x;
        } else {
            System.out.println("invalid player");
        }

        for (Line l : Line.lines) {
            maskedLongYou = l.positions() & boardYou;
            maskedLongOther = l.positions() & boardOther;

            if (Bit.countOnes(maskedLongYou) == 2 && Bit.countOnes(maskedLongOther) == 0) {
                numOfTwos++;
            }
        }
        return numOfTwos;
    }

    public static int numOfThreeInARows(Board b, Player player) {
        int numOfThrees = 0;
        long boardYou = 0;
        long boardOther = 0;
        long maskedLongYou = 0;
        long maskedLongOther = 0;

        if (player.equals(player.X)) {
            boardYou = b.x;
            boardOther = b.o;
        } else if (player.equals(player.O)) {
            boardYou = b.o;
            boardOther = b.x;
        } else {
            System.out.println("invalid player");
        }

        for (Line l : Line.lines) {
            maskedLongYou = l.positions() & boardYou;
            maskedLongOther = l.positions() & boardOther;

            if (Bit.countOnes(maskedLongYou) == 3 && Bit.countOnes(maskedLongOther) == 0) {
                numOfThrees++;
            }
        }
        return numOfThrees;
    }


    public static boolean isTerminal(Board board){
        return wins(Player.X, board) | wins(Player.O, board) | board.numberEmptySquares() == 0;
    }

    public static boolean wins(Player player, Board b){
        long boardYou = 0;
        long boardOther = 0;
        long maskedLongYou = 0;
        long maskedLongOther = 0;

        if (player.equals(player.X)) {
            boardYou = b.x;
            boardOther = b.o;
        } else if (player.equals(player.O)) {
            boardYou = b.o;
            boardOther = b.x;
        } else {
            System.out.println("invalid player");
        }

        for (Line l : Line.lines) {
             maskedLongYou = l.positions() & boardYou;
             maskedLongOther = l.positions() & boardOther;

            if (Bit.countOnes(maskedLongYou) == 4 && Bit.countOnes(maskedLongOther) == 0){
                return true;
            }
        }
        return false;
    }


    // Iterators.

    private class EmptySquareIterator implements Iterator<Coordinate> {

        private Iterator<Integer> iterator;

        public EmptySquareIterator() {
            this.iterator = Bit.iterator(Board.this.get(Player.EMPTY));
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public Coordinate next() {
            return Coordinate.valueOf(this.iterator.next());
            // return new Coordinate(this.iterator.next());
        }
    }

    public Iterator<Coordinate> emptySquareIterator() {
        return new EmptySquareIterator();
    }

    public Iterable<Coordinate> emptySquares() {
        return new Iterable<Coordinate>() {
            @Override
            public Iterator<Coordinate> iterator() {
                return new EmptySquareIterator();
            }
        };
    }

    public static void main(String[] args) {
        for (String arg : args) {
            Board board = new Board(arg);
            board.print();
            if (wins(Player.X, board)){
                System.out.println("WOOHOO");
            }
            for (Coordinate coord : board.emptySquares()) {
                System.out.print(coord.position() + " ");
            }
            System.out.println();
        }
    }
}