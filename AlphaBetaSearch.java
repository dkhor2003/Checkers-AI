package edu.iastate.cs472.proj2;

import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author Dylan Khor
 *
 */
/**
 * This class implements the Alpha-Beta pruning algorithm to find the best 
 * move at current state.
*/
public class AlphaBetaSearch extends AdversarialSearch {
	
	int search_depth_limit = 8; // variable to determine how many steps ahead the agent can see
	
	int king_value = 3; // variable that determines the value of a king piece, which is going to be used in the evaluation function of a non-terminal node
	
	int normal_piece_value = 1; // variable that determines the value of a normal piece, which is going to be used in the evaluation function of a non-terminal node
	
	CheckersData[] next_state_board = new CheckersData[search_depth_limit + 1]; // array for storing the states at each search depth; + 1 to take into account initial state

    /**
     * The input parameter legalMoves contains all the possible moves.
     * It contains four integers:  fromRow, fromCol, toRow, toCol
     * which represents a move from (fromRow, fromCol) to (toRow, toCol).
     * It also provides a utility method `isJump` to see whether this
     * move is a jump or a simple move.
     * 
     * Update 03/18: each legalMove in the input now contains a single move
     * or a sequence of jumps: (rows[0], cols[0]) -> (rows[1], cols[1]) ->
     * (rows[2], cols[2]).
     *
     * @param legalMoves All the legal moves for the agent at current step.
     */
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
    	
        // The checker board state can be obtained from this.board,
        // which is a int 2D array. The numbers in the `board` are
        // defined as
        // 0 - empty square,
        // 1 - red man
        // 2 - red king
        // 3 - black man
        // 4 - black king
        System.out.println(board);
        System.out.println();

        if (legalMoves == null) { // If there are not legal moves for the agent, return null since no move can be executed
        	return null;
        }
        else if (legalMoves.length == 1) { // If there is just one legal move, immediately return it so as to not waste time executing alpha-beta search
        	return legalMoves[0];
        }
        // There is more than one legal move, therefore, searching is needed to find optimal move
        else{
        	int index = getBestMove(legalMoves);
        	return legalMoves[index];
        }
    }
    
    /**
     * Given a list of moves the agent (BLACK) can execute, this function performs alpha-beta search to search for the move that has the maximum utility for the agent (BLACK). 
     * 
     * @param legalMoves  the available moves the agent (BLACK) can execute
     * 
     * @return            the index of the move in legalMoves that result in the maximum utility for the agent (BLACK)
     */
    private int getBestMove(CheckersMove[] legalMoves){
    	int search_depth = 0;
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;
    	double value = Double.NEGATIVE_INFINITY;
    	int index = 0;
		
    	// check if human (RED) player has valid moves
    	CheckersMove[] redLegalMoves = legalMoves(CheckersData.RED);
    	if (redLegalMoves == null){
    		// agent (BLACK) wins if human (RED) player has no more valid moves, so we can just return the first move in the list of legal moves
    		return index;
    	}
    	
    	next_state_board[search_depth] = this.board; // Initialize the first state to be the current state
    	search_depth++;
    	
    	// agent (BLACK) has valid moves
    	for (int i = 0; i < legalMoves.length; i++){
    		next_state_board[search_depth] = next_state_board[search_depth-1].cloneData(); // Make a deep copy of the previous state so as to not modify it
    		next_state_board[search_depth].makeMove(legalMoves[i]);
    		CheckersMove[] minLegalMoves = next_state_board[search_depth].getLegalMoves(CheckersData.RED);
        	double next_state_value = min_value(minLegalMoves, search_depth, alpha, beta);
        	if (next_state_value > value){
        		index = i;
        		value = next_state_value;
        	}  
        	if (value >= beta){
        		return index;
        	}
        	alpha = Math.max(alpha, value);
        }
        return index;
    }

    /**
     * Computes the maximum utility value the agent (BLACK) can achieve at the given search depth
     * 
     * @param legalMoves  the available moves the agent (BLACK) can execute
     * @param depth       the current search depth
     * @param alpha       the minimum utility that the agent (BLACK) is assured of
     * @param beta        the maximum utility that the human (RED) player is assured of
     * 
     * @return            the maximum utility value that the agent (BLACK) can achieve at this search depth
     */
    private double max_value(CheckersMove[] legalMoves, int depth, double alpha, double beta){
    	double value = Double.NEGATIVE_INFINITY;
  	
    	// Terminal node check: Check if agent (BLACK) has any moves to execute
    	if (legalMoves == null){
    		// human (RED) wins if agent (BLACK) has no more valid moves
    		return -1;
    	}
    	
    	// Terminal node check: Check if human (RED) player has valid moves
    	CheckersMove[] redLegalMoves = next_state_board[depth].getLegalMoves(CheckersData.RED);
    	if (redLegalMoves == null){
    		// agent (BLACK) wins if human (RED) player has no more valid moves
    		return 1;
    	}
    	
    	// if maximum search depth is reached, return the utility of the non-terminal state
    	if (depth >= search_depth_limit){ 
    		return improved_utility(next_state_board[depth]);
    	}
    	
    	int next_depth = depth + 1;
    	
    	// agent (BLACK) has valid moves
        for (CheckersMove nextMove: legalMoves){
        	next_state_board[next_depth] = next_state_board[depth].cloneData(); // Make a deep copy of the previous state so as to not modify it
        	next_state_board[next_depth].makeMove(nextMove);
        	CheckersMove[] minLegalMoves = next_state_board[next_depth].getLegalMoves(CheckersData.RED);
        	value = Math.max(min_value(minLegalMoves, next_depth, alpha, beta), value);

        	if (value >= beta){ // pruning
        		return value;
        	}
        	alpha = Math.max(alpha, value);
        }
        return value;
    }
    
    /**
     * Computes the minimum utility value the human (RED) player can achieve at the given search depth
     * 
     * @param legalMoves  the available moves the human (BLACK) player can execute
     * @param depth       the current search depth
     * @param alpha       the minimum utility that the agent (BLACK) is assured of
     * @param beta        the maximum utility that the human (RED) player is assured of
     * 
     * @return            the minimum utility value that the human (RED) player can achieve at this search depth
     */
    private double min_value(CheckersMove[] legalMoves, int depth, double alpha, double beta){
    	double value = Double.POSITIVE_INFINITY;

    	// Terminal node check: Check if human (RED) player has any moves to execute
    	if (legalMoves == null){
    		// agent (BLACK) wins if human (RED) player has no more moves
    		return 1;
    	}
 
    	// Terminal node check: check if agent (BLACK) has valid moves
    	CheckersMove[] blkLegalMoves = next_state_board[depth].getLegalMoves(CheckersData.BLACK);
    	if (blkLegalMoves == null){
    		// human (RED) player wins if agent (BLACK) player has no more moves
    		return -1;
    	}
    	
    	// if maximum search depth is reached, return the utility of the non-terminal state
    	if (depth >= search_depth_limit){ 
    		return improved_utility(next_state_board[depth]);
    	}
    	
    	int next_depth = depth + 1;
    	
    	// human (RED) player has valid moves
        for (CheckersMove nextMove: legalMoves){
        	next_state_board[next_depth] = next_state_board[depth].cloneData(); // Make a deep copy of the previous state so as to not modify it
        	next_state_board[next_depth].makeMove(nextMove);
        	CheckersMove[] maxLegalMoves = next_state_board[next_depth].getLegalMoves(CheckersData.BLACK);
        	value = Math.min(max_value(maxLegalMoves, next_depth, alpha, beta), value);
        	
        	if (value <= alpha){ // pruning
        		return value;
        	}
        	beta = Math.min(beta,  value);
        }
        return value;  
    }

    /**
     * Baseline evaluation function for a non-terminal state that returns the utility value of a state for the agent (BLACK)
     * 
     * @param state   a state containing information about the checkers board configuration
     * 
     * @return        the evaluated utility value of this state for the agent (BLACK), which is a value between -1 and 1
     */
    private double baseline_utility(CheckersData state){

    	double score = 0;
    	
    	for (int row = 0; row < 8; row++){
			for (int col = 0; col < 8; col++){
				if (row % 2 == col % 2) {
					switch (state.pieceAt(row, col)){
						// Deduct points for every human (RED) player piece while adding points for every agent (BLACK) piece
						case CheckersData.RED:
							score -= normal_piece_value; 
							break;
						case CheckersData.BLACK:
							score += normal_piece_value;
							break;
						case CheckersData.RED_KING:
							score -= king_value;
							break;
						case CheckersData.BLACK_KING:
							score += king_value;
							break;
						default:
							break;
					}
				}
			}
		}
    	return score/(12 * king_value); // Best possible case is that all agent's (BLACK) piece is a king and no enemy (RED) piece is present
    }
    
    /**
     * Improved evaluation function that builds upon the baseline evaluation function by adding another scoring mechanism
     * where agent will be motivated to use its advantage in numbers to try to eliminate enemy pieces. 
     * 
     * @param state   a state containing information about the checkers board configuration
     * 
     * @return        the evaluated utility value of this state for the agent (BLACK), which is a value between -1 and 1
     */
    private double improved_utility(CheckersData state){

    	double score = 0;
    	int blackPieces = 0;
    	int redPieces = 0; 
    	ArrayList<int[]> redPieceLocation = new ArrayList<int[]>();
    	ArrayList<int[]> blackKingLocation = new ArrayList<int[]>();
    	for (int row = 0; row < 8; row++){
			for (int col = 0; col < 8; col++){
				if (row % 2 == col % 2) {
					int[] loc = {-1, -1};
					switch (state.pieceAt(row, col)){
						// Deduct points for every human (RED) player piece while adding points for every agent (BLACK) piece
						case CheckersData.RED:
							score -= normal_piece_value;
							redPieces++; 
							loc[0] = row;
							loc[1] = col;
							redPieceLocation.add(loc); 
							break;
						case CheckersData.BLACK:
							score += normal_piece_value;
							blackPieces++;
							break;
						case CheckersData.RED_KING:
							score -= king_value;
							redPieces++;
							loc[0] = row;
							loc[1] = col;
							redPieceLocation.add(loc);
							break;
						case CheckersData.BLACK_KING:
							score += king_value;
							blackPieces++;
							loc[0] = row;
							loc[1] = col;
							blackKingLocation.add(loc); 
							break;
						default:
							break;
					}
				}
			}
		}
    	// Do the following following scoring mechanism only if there are more agent (BLACK) pieces than human (RED) pieces and there exists at least one agent (BLACK) king piece.
    	if (blackPieces > redPieces && blackKingLocation.size() > 0) { 
    		double minDist;  
    		double dist = 0;
    		double distScore = 0; 
    		for(int i = 0; i < blackKingLocation.size(); i++) {
    			int[] black_king_loc = blackKingLocation.get(i);
    			minDist = 100; 
    			for(int j = 0; j < redPieceLocation.size(); j++) {
    				dist = ManhattanDistance(black_king_loc, redPieceLocation.get(j)); 
    				if(dist < minDist) {
    					minDist = dist; 
    				}
    			}
    			// The smaller the distance between the agent's king piece with its closest enemy piece, the greater the distance score, which encourages the agent's king piece to be more aggressive in 
    			// closing up the distance with its enemies when the agent has more pieces on the board. 
    			distScore = (1.0 / minDist); 
    			score += distScore;  
    		}
    	}
    	return score/(12 * king_value); // Best possible case is that all agent's (BLACK) piece is a king and no enemy (RED) piece is present
    }
    
    /**
     * Computes the Manhattan distance between two checker pieces
     * 
     * @param loc1  an array containing the row and column index
     * @param loc2  another array containing the row and column index
     * 
     * @return      the Manhattan distance given the two coordinates
     */
    private double ManhattanDistance(int[] loc1, int[] loc2) {
    	return Math.abs(loc2[0] - loc1[0]) + Math.abs(loc2[1] - loc1[1]); 
    }
    
    
    
}
