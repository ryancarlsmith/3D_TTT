public class Minimax {

    public Coordinate bestMove(Board b, Player player, int maxPlies, int numofTimesCalled) {
        if (numofTimesCalled % 6 == 0){
            maxPlies++;
        }

        Board board = new Board(b);
        Coordinate result = null;
        int max = Integer.MIN_VALUE;
        for (Coordinate a : board.emptySquares()){
            int value = maxValue(board.next(a, player), player, 1, maxPlies, Integer.MIN_VALUE, Integer.MAX_VALUE); //is this Max or Min, ask someone
            if (max < value){
                max = value;
                result = a;
            }
        }
        System.out.println(numofTimesCalled);
        return result;
    }

    public int maxValue(Board board, Player player, int plies, int maxPlies, int alpha, int beta) {

        if(Board.isTerminal(board) || plies == maxPlies){
            return board.evaluate(board, player);
        }
        //if isTerminal || plies == maxPlies return evaluate(baord, player)
        //delete the 2 if statements above
//CHECK FOR DRAW - isTermininal will do the win checks for each player and return corresponding values and checks for draw
        //implement alpha beta pruning from textboook


        int utilityValue = Integer.MIN_VALUE;

        for (Coordinate a : board.emptySquares()){
            utilityValue = Math.max(utilityValue, minValue(board.next(a, player), player.other(), plies+1, maxPlies, alpha, beta));
            if (utilityValue >= beta){
                return utilityValue;
            }
            alpha = Math.max(alpha, utilityValue);
        }
        return utilityValue;
    }

    public int minValue(Board board, Player player, int plies, int maxPlies, int alpha, int beta) {

        if(Board.isTerminal(board) || plies == maxPlies){
            return board.evaluate(board, player);
        }

        int utilityValue = Integer.MAX_VALUE;

        for (Coordinate a : board.emptySquares()){
            utilityValue = Math.min(utilityValue, maxValue(board.next(a, player), player.other(), plies+1, maxPlies, alpha, beta));
            if (utilityValue <= alpha){
                return utilityValue;
            }
            beta = Math.min(beta, utilityValue);
        }
        return utilityValue;
    }
}