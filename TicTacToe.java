import java.io.Console;

public class TicTacToe {
    public static void main(String[] args) {
        int numOfMoves = 0;
        Board board = new Board(args[0]);
        Minimax minimax = new Minimax();
        Console console = System.console();
        boolean isSecond = false;

        while (board.numberEmptySquares() > 0) {

            if (board.wins(Player.O, board)) {
                System.out.println("You won !!!");
                break;
            } else if (board.numberEmptySquares() == 0) {
                System.out.println("Tie !!!");
                break;
            }
            int maxPlies = 3;
            if (args.length != 0) {
                maxPlies = Integer.parseInt(args[2]);
                if (args[1].equalsIgnoreCase("-first")) { //computer first
                    Coordinate c = minimax.bestMove(board, Player.X, maxPlies, numOfMoves++);
                    board = board.next(c, Player.X);
                    System.out.println(c);
                    board.print();
                } else if (args[1].equalsIgnoreCase("-second")){ //computer second
                    board.print();
                    isSecond = true;
                   // board = board.next(minimax.bestMove(board, Player.X, maxPlies, numOfMoves++), Player.X);
                }
            } else {
                System.out.println(board);
                board = board.next(minimax.bestMove(board, Player.X, maxPlies, numOfMoves++), Player.X);
            }

            if (board.wins(Player.X, board)) {
                System.out.println("I won !!!");
                break;
            } else if (board.numberEmptySquares() == 0) {
                System.out.println("Tie !!!");
                break;
            }

            int x = 0;
            int y = 0;
            int z = 0;
            //check if the coordinate is unoccupied
            String line = "";
            do {
                do {
                    line = console.readLine("X: ");
                    try {
                        x = Integer.parseInt(line) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid row: " + line);
                    }
                    if (x >= 0 && x < 4) break;
                    System.out.println("Invalid row: " + line);
                } while (true);

                do {
                    line = console.readLine("Y: ");
                    try {
                        y = Integer.parseInt(line) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid column: " + line);
                    }
                    if (y >= 0 && y < 4) break;
                    System.out.println("Invalid col: " + line);
                } while (true);
                
                do {
                    line = console.readLine("Z: ");
                    try {
                        z = Integer.parseInt(line) - 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid row: " + line);
                    }
                    if (z >= 0 && z < 4) break;
                    System.out.println("Invalid row: " + line);
                } while (true);

                if (board.isEmpty(x, y, z)) break;
                System.out.println("Square is not empty");
            } while (true);

            board = new Board(board.next(x,y,z, Player.O));

            if(isSecond){
                board.print();
                board = board.next(minimax.bestMove(board, Player.X, maxPlies, numOfMoves++), Player.X);
            }
        }
    }
}
