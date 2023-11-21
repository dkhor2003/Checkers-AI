package edu.iastate.cs472.proj2;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Dylan Khor
 * 
 *         An object of this class holds data about a game of checkers. It knows
 *         what kind of piece is on each square of the checkerboard. Note that
 *         RED moves "up" the board (i.e. row number decreases) while BLACK
 *         moves "down" the board (i.e. row number increases). Methods are
 *         provided to return lists of available legal moves.
 */
public class CheckersData {

	/*
	 * The following constants represent the possible contents of a square on the
	 * board. The constants RED and BLACK also represent players in the game.
	 */

	static final int EMPTY = 0, RED = 1, RED_KING = 2, BLACK = 3, BLACK_KING = 4;

	int[][] board; // board[r][c] is the contents of row r, column c.

	/**
	 * Constructor. Create the board and set it up for a new game.
	 */
	CheckersData() {
		board = new int[8][8];
		setUpGame();
	}

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final int[] DIR1 = { 1, -1 }; // Move bottom left
	public static final int[] DIR2 = { 1, 1 }; // Move bottom right
	public static final int[] DIR3 = { -1, -1 }; // Move top left
	public static final int[] DIR4 = { -1, 1 }; // Move top right

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < board.length; i++) {
			int[] row = board[i];
			sb.append(i).append(" ");
			for (int n : row) {
				if (n == 0) {
					sb.append(" ");
				} else if (n == 1) {
					sb.append(ANSI_RED + "R" + ANSI_RESET);
				} else if (n == 2) {
					sb.append(ANSI_RED + "K" + ANSI_RESET);
				} else if (n == 3) {
					sb.append(ANSI_YELLOW + "B" + ANSI_RESET);
				} else if (n == 4) {
					sb.append(ANSI_YELLOW + "K" + ANSI_RESET);
				}
				sb.append(" ");
			}
			sb.append(System.lineSeparator());
		}
		sb.append("  0 1 2 3 4 5 6 7");

		return sb.toString();
	}

	/**
	 * Set up the board with checkers in position for the beginning of a game. Note
	 * that checkers can only be found in squares that satisfy row % 2 == col % 2.
	 * At the start of the game, all such squares in the first three rows contain
	 * black checkers and all such squares in the last three rows contain red
	 * checkers.
	 */
	void setUpGame() {
		// Set up the board with pieces BLACK, RED, and EMPTY
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < board[row].length; col++) {
				if (row % 2 == col % 2) {
					board[row][col] = BLACK;
				} else {
					board[row][col] = EMPTY;
				}
			}
		}

		for (int row = 3; row < 5; row++) {
			for (int col = 0; col < board[row].length; col++) {
				board[row][col] = EMPTY;
			}
		}

		for (int row = 5; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				if (row % 2 == col % 2) {
					board[row][col] = RED;
				} else {
					board[row][col] = EMPTY;
				}
			}
		}

	}

	/**
	 * Return the contents of the square in the specified row and column.
	 */
	int pieceAt(int row, int col) {
		return board[row][col];
	}

	/**
	 * Make the specified move. It is assumed that move is non-null and that the
	 * move it represents is legal.
	 *
	 * Make a single move or a sequence of jumps recorded in rows and cols.
	 *
	 */
	void makeMove(CheckersMove move) {
		int l = move.rows.size();
		for (int i = 0; i < l - 1; i++)
			makeMove(move.rows.get(i), move.cols.get(i), move.rows.get(i + 1), move.cols.get(i + 1));
	}
	
	/**
	 * Set the board to be the given board
	 * @param board 8 by 8 board
	 */
	void setBoard(int[][] board) {
		this.board = board;
	}

	/**
	 * Make the move from (fromRow,fromCol) to (toRow,toCol). It is assumed that
	 * this move is legal. If the move is a jump, the jumped piece is removed from
	 * the board. If a piece moves to the last row on the opponent's side of the
	 * board, the piece becomes a king.
	 *
	 * @param fromRow row index of the from square
	 * @param fromCol column index of the from square
	 * @param toRow   row index of the to square
	 * @param toCol   column index of the to square
	 */
	void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
		// Update the board for the given move. You need to take care of the following
		// situations:
		// 1. move the piece from (fromRow,fromCol) to (toRow,toCol)
		// 2. if this move is a jump, remove the captured piece
		// 3. if the piece moves into the kings row on the opponent's side of the board,
		// crowned it as a king
		int row_diff = toRow - fromRow;
		int col_diff = toCol - fromCol;
		int player = pieceAt(fromRow ,fromCol); 

		if (Math.abs(col_diff) == 2 && Math.abs(col_diff) == 2) { // A jump
			int enemy_row = fromRow + (row_diff / 2);
			int enemy_col = fromCol + (col_diff / 2);
			board[enemy_row][enemy_col] = EMPTY;
		}

		board[toRow][toCol] = player;
		board[fromRow][fromCol] = EMPTY;

		if (player == RED && toRow == 0) {
			board[toRow][toCol] = RED_KING;
		}

		if (player == BLACK && toRow == board.length - 1) {
			board[toRow][toCol] = BLACK_KING;
		}

	}

	/**
	 * Return an array containing all the legal CheckersMoves for the specified
	 * player on the current board. If the player has no legal moves, null is
	 * returned. The value of player should be one of the constants RED or BLACK; if
	 * not, null is returned. If the returned value is non-null, it consists
	 * entirely of jump moves or entirely of regular moves, since if the player can
	 * jump, only jumps are legal moves.
	 *
	 * @param player color of the player, RED or BLACK
	 */
	CheckersMove[] getLegalMoves(int player) {
		ArrayList<CheckersMove> moves = new ArrayList<>();
		
		int king;
		
		if (player == RED) {
			king = RED_KING;
		} else {
			king = BLACK_KING;
		}

		boolean hasJump = false;
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				CheckersMove[] actions;
				if (pieceAt(row, col) == king) { // kings can move in either direction
					actions = getLegalJumpsFrom(king, row, col); 
					// There is a jump
					if(actions != null) {
						if(!hasJump) {
							moves.clear(); //Remove all previous regular moves the first time a jump move is discovered
						}
						hasJump = true; //Switch to include only jump moves
						for(CheckersMove m: actions) {
							moves.add(m); // Add every possible jump to our legal moves list
						}
					}
					// No jump actions available
					else {
						// No jump moves have been discovered yet, proceed with adding regular moves
						if(!hasJump) {
							// Try all possible directions
							CheckersMove[] actions_list = {
									step(row, col, DIR1),
									step(row, col, DIR2),
									step(row, col, DIR3),
									step(row, col, DIR4)
							};
							
							// Add only non-null moves
							for(CheckersMove m: actions_list) {
								if(m != null) {
									moves.add(m);
								}
							}
						}
					}
				} 
				else if (pieceAt(row, col) == player) { // normal pieces can only move forward
					actions = getLegalJumpsFrom(player, row, col);
					if(actions != null) {
						if(!hasJump) {
							moves.clear(); //Remove all previous regular moves the first time a jump move is discovered
						}
						hasJump = true; //Switch to include only jump moves
						for(CheckersMove m: actions) {
							moves.add(m); // Add every possible jump to our legal moves list
						}
					}
					// No jump actions available
					else {
						// No jump moves have been discovered yet, proceed with adding regular moves
						if(!hasJump) {
							CheckersMove[] actions_list = new CheckersMove[2]; 
							// RED normal pieces can only move upwards the board
							if(player == RED) {
								actions_list[0] = step(row, col, DIR3); 
								actions_list[1] = step(row, col, DIR4); 
							}
							// BLACK normal pieces can only move downwards the board
							else {
								actions_list[0] = step(row, col, DIR1); 
								actions_list[1] = step(row, col, DIR2); 
							}
							
							// Add only non-null moves
							for(CheckersMove m: actions_list) {
								if(m != null) {
									moves.add(m);
								}
							}
						}
					}
				}
			}
		}
		
		// No regular moves or jumps are found
		if(moves.size() == 0) {
			return null;
		}
		
		// Convert into array from array list 
		return convertToArrayFromArrayList(moves);
	}

	/**
	 * Return the action of moving the piece at the given row and column index in the specified direction
	 * 
	 * @param row  row index
	 * @param col  column index 
	 * @param dir  direction in the form of array of size two. E.g [1, 1] means adding 1 to the row and adding 1 to the column, which means moving the piece to the bottom right
	 * 
	 * @return     the action of moving the piece at the given row and column index in the specified direction
	 */
	CheckersMove step(int row, int col, int[] dir) {
		CheckersMove m = null;
		if(!outOfBounds(row + dir[0], col + dir[1])) {
			if(pieceAt(row + dir[0], col + dir[1]) == EMPTY) { // Check if the place we want to move the piece to is EMPTY or not
				m = new CheckersMove(row, col, row + dir[0], col + dir[1]); 
			}
		}
		return m;
	}
	
	/**
	 * Checks whether the given row and column index is out of bounds of the 8 by 8 board.
	 * 
	 * @param row  row index
	 * @param col  column index
	 * 
	 * @return     true if the given row and column index is out of the bounds of the 8 by 8 board
	 */
	boolean outOfBounds(int row, int col) {
		return row < 0 || row >= board.length || col < 0 || col >= board[0].length;
	}

	/**
	 * Return a list of the legal jumps that the specified player can make starting
	 * from the specified row and column. If no such jumps are possible, null is
	 * returned. The logic is similar to the logic of the getLegalMoves() method.
	 *
	 * Note that each CheckerMove may contain multiple jumps. Each move returned in
	 * the array represents a sequence of jumps until no further jump is allowed.
	 *
	 * @param player The player of the current jump, either RED or BLACK.
	 * @param row    row index of the start square.
	 * @param col    col index of the start square.
	 */
	CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
		ArrayList<int[]> legalDirections = new ArrayList<>();
		int enemy1 = 0;
		int enemy2 = 0;

		// Store information about the given player's enemies (normal and king enemy pieces) and its legal directions to move
		if (player == BLACK) {
			legalDirections.add(DIR1);
			legalDirections.add(DIR2);
			enemy1 = RED;
			enemy2 = RED_KING;
		} 
		
		else if (player == RED) {
			legalDirections.add(DIR3);
			legalDirections.add(DIR4);
			enemy1 = BLACK;
			enemy2 = BLACK_KING;
		} 
		
		else if (player == BLACK_KING || player == RED_KING) {
			legalDirections.add(DIR1);
			legalDirections.add(DIR2);
			legalDirections.add(DIR3);
			legalDirections.add(DIR4);
			
			if (player == BLACK_KING) {
				enemy1 = RED;
				enemy2 = RED_KING;
			} 
			
			else {
				enemy1 = BLACK;
				enemy2 = BLACK_KING;
			}
		}

		ArrayList<CheckersMove> jumps = new ArrayList<>();
		CheckersMove move = new CheckersMove(); // Starting point for a jump
		move.addMove(row, col);
		jumps.add(move);
		int[][] current_board = deepCopyBoard();
		
		// Recursively find each possible jumps for all directions
		findJumpsRecursive(player, enemy1, enemy2, row, col, DIR1, legalDirections, jumps, move.clone());
		setBoard(current_board);
		current_board = deepCopyBoard();
		
		findJumpsRecursive(player, enemy1, enemy2, row, col, DIR2, legalDirections, jumps, move.clone());
		setBoard(current_board);
		current_board = deepCopyBoard();
		
		findJumpsRecursive(player, enemy1, enemy2, row, col, DIR3, legalDirections, jumps, move.clone());
		setBoard(current_board);
		current_board = deepCopyBoard();
		
		findJumpsRecursive(player, enemy1, enemy2, row, col, DIR4, legalDirections, jumps, move.clone());
		setBoard(current_board);

		if (jumps.size() == 1) {
			// Contains only the starting point, meaning no jumps can be made
			if (jumps.get(0).rows.size() == 1) {
				return null;
			}
		}
		
		// Convert into array from array list 
		return convertToArrayFromArrayList(jumps);
	}

	/**
	 * Recursively generate a jump move while checking if the generated jump move already has been generated before. 
	 * 
	 * @param player    player RED or BLACK
	 * @param enemy1    first enemy of the given player (either normal or king piece)
	 * @param enemy2    second enemy of the given player (either normal or king piece)
	 * @param row       row index
	 * @param col       column index
	 * @param dir       direction in the form of array of size two
	 * @param legalDir  legal list of directions for the given player's piece at the given row and column
	 * @param jumps     the list of possible jump moves so far (database of jump moves)
	 * @param move      the jump move in process of being generated
	 */
	void findJumpsRecursive(int player, int enemy1, int enemy2, int row, int col, int[] dir, ArrayList<int[]> legalDir,
			ArrayList<CheckersMove> jumps, CheckersMove move) {
		// If the given direction is valid
		if (isLegalDir(dir, legalDir)) {
			
			// If the direction the piece is jumping to and its diagonal adjacent location are not out of bounds
			if (!outOfBounds(row + dir[0], col + dir[1]) && !outOfBounds(row + (2 * dir[0]), col + (2 * dir[1]))) {
				
				// If there exists an enemy in diagonally adjacent square and the square to jump onto is EMPTY
				if ((pieceAt(row + dir[0], col + dir[1]) == enemy1 || pieceAt(row + dir[0], col + dir[1]) == enemy2)
						&& pieceAt(row + (2 * dir[0]), col + (2 * dir[1])) == EMPTY) {
					int newRow = row + (2 * dir[0]);
					int newCol = col + (2 * dir[1]);
					makeMove(row, col, newRow, newCol);
					int[][] b = deepCopyBoard();
					
					// Make a jump and update the piece location
					move.addMove(newRow, newCol);
					
					// Update the database of possible jump moves based on the jump move that has been executed
					updateJump(jumps, move);
					
					// Recursively find each possible jumps for all directions at the new location
					findJumpsRecursive(player, enemy1, enemy2, newRow, newCol, DIR1, legalDir, jumps, move.clone());
					setBoard(b);
					b = deepCopyBoard();
					
					findJumpsRecursive(player, enemy1, enemy2, newRow, newCol, DIR2, legalDir, jumps, move.clone());
					setBoard(b);
					b = deepCopyBoard();
					
					findJumpsRecursive(player, enemy1, enemy2, newRow, newCol, DIR3, legalDir, jumps, move.clone());
					setBoard(b);
					b = deepCopyBoard();
					
					findJumpsRecursive(player, enemy1, enemy2, newRow, newCol, DIR4, legalDir, jumps, move.clone());
					setBoard(b);
				} 
			} 
		} 
	}

	/**
	 * 
	 * @param dir       direction in the form of array of size two
	 * @param legalDir  legal list of directions for some player's piece
	 * 
	 * @return true if the given direction is part of the legal directions in the list
	 */
	boolean isLegalDir(int[] dir, ArrayList<int[]> legalDir) {
		for (int[] d : legalDir) {
			if (d.equals(dir)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Update the database of jump moves based on the given generated move
	 * 
	 * @param jumps  the list of possible jump moves so far (database of jump moves)
	 * @param move   the jump move in process of being generated
	 */ 
	void updateJump(ArrayList<CheckersMove> jumps, CheckersMove move) {
		/* Check if given move is a subset of one of the moves in the jumps list.
		 * If yes, the jump move is still in process of generating, and we do not add
		 * it into our database. 
		 */
    	if(isSubsetOfJumps(jumps, move)) { 
    		return;
    	}
    	
    	// Compare given move with every single jump move in the database
    	for(int i = 0; i < jumps.size(); i++) {
    		CheckersMove jump = jumps.get(i); 
    		boolean similar = true; 
    		if(jump.rows.size() <= move.rows.size()) {
	    		for(int j = 0; j < jump.rows.size(); j++) {
	    			if(jump.rows.get(j) != move.rows.get(j) || jump.cols.get(j) != move.cols.get(j)) {
	    				// If they differ at the last jumping step, the given jump move is a different jumping path, so we add it to our database
	    				if(j == jump.rows.size() - 1 && jump.rows.size() == move.rows.size() && !isSubsetOfJumps(jumps, move)) {
	    					jumps.add(move); 
	    				}
	    				similar = false; 
	    				break; 
	    			}
	    		}
	    		if(similar && move.rows.size() > jump.rows.size()) { //Given move is built upon a constructed move in the jumps list
	    			//Remove and update the corresponding move with the given jumping move
	    			jumps.remove(i);
	    			jumps.add(move); 
	    			break; 
	    		}
    		}
    		else {
    			for(int j = 0; j < move.rows.size(); j++) {
	    			if(jump.rows.get(j) != move.rows.get(j) || jump.cols.get(j) != move.cols.get(j)) {
	    				// If they differ at the last jumping step, the given jump move is a different jumping path, so we add it to our database
	    				if(j == move.rows.size() - 1 && !isSubsetOfJumps(jumps, move)) {
	    					jumps.add(move); 
	    				}
	    				similar = false; 
	    				break; 
	    			}
	    		}
	    		if(similar) {
	    			continue; 
	    		}
    		}
    	}
	}

	/**
	 * 
	 * @param jumps  the list of possible jump moves so far (database of jump moves)
	 * @param move   the jump move in process of being generated
	 * 
	 * @return true if the given jump move is a subset of some jump move in the database, else false
	 */
	boolean isSubsetOfJumps(ArrayList<CheckersMove> jumps, CheckersMove move) {
		for (int i = 0; i < jumps.size(); i++) {
			boolean same = true;
			CheckersMove jump = jumps.get(i);
			if (jump.rows.size() >= move.rows.size()) {
				for (int j = 0; j < move.rows.size(); j++) {
					if (jump.rows.get(j) != move.rows.get(j) || jump.cols.get(j) != move.cols.get(j)) {
						same = false;
						break;
					}
				}
				// if same is still true, all jumping step in the given move matches some part of a move in the database
				if (same) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param moves list of possible moves to execute in the form of array list
	 * 
	 * @return the array format of the list of possible moves
	 */
	CheckersMove[] convertToArrayFromArrayList(ArrayList<CheckersMove> moves) {
		CheckersMove[] actions = new CheckersMove[moves.size()] ; 
		for(int i = 0; i < moves.size(); i++) {
			actions[i] = moves.get(i);
		}
		return actions;
	}
	
	/**
	 * Used to check whether a piece has been captured or not in MCTS
	 * 
	 * @return the total number of BLACK and RED pieces on the board
	 */
	int number_of_pieces() {
		int num_pieces = 0; 
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				if(i % 2 == j % 2) {
					if(pieceAt(i, j) != EMPTY) {
						num_pieces += 1; 
					}
				}
			}
		}
		return num_pieces; 
	}
	
	/**
	 * @return a deep copy of this CheckersData 
	 */
    CheckersData cloneData(){
    	CheckersData clone_board = new CheckersData();
    	for (int row = 0; row < 8; row++){
			for (int col = 0; col < 8; col++){
				if (row % 2 == col % 2){
					clone_board.board[row][col] = pieceAt(row, col);
				}
            }
        }   
        return clone_board;
    }
	
    /**
     * @return a deep copy of this  8 by 8 board
     */
	int[][] deepCopyBoard() {
		if (board == null) {
			return null;
		}
		int[][] copy = new int[board.length][];
		for (int i = 0; i < board.length; i++) {
			copy[i] = Arrays.copyOf(board[i], board[i].length);
		}
		return copy;
	}

	/**
	 * Print out the contents of an array list of moves
	 * Used for debugging purposes
	 * 
	 * @param jumps array list of possible moves
	 */
	void printMoves(ArrayList<CheckersMove> moves) {
		for (CheckersMove m : moves) {
			System.out.println(m.toString());
		}
		System.out.println("#########################");
	}

}
