package edu.iastate.cs472.proj2;

import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author Dylan Khor
 *
 */

/**
 * This class implements the Monte Carlo tree search method to find the best
 * move at the current state.
 */

public class MonteCarloTreeSearch extends AdversarialSearch {

	double constant = Math.sqrt(2); // The constant used in the upper confidence bound formula
	int N = 1000; // The number of simulations to execute
	
	/**
     * The input parameter legalMoves contains all the possible moves.
     * It contains four integers:  fromRow, fromCol, toRow, toCol
     * which represents a move from (fromRow, fromCol) to (toRow, toCol).
     * It also provides a utility method `isJump` to see whether this
     * move is a jump or a simple move.
     *
     * Each legalMove in the input now contains a single move
     * or a sequence of jumps: (rows[0], cols[0]) -> (rows[1], cols[1]) ->
     * (rows[2], cols[2]).
     *
     * @param legalMoves All the legal moves for the agent at current step.
     */
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        // The checker board state can be obtained from this.board,
        // which is an 2D array of the following integers defined below:
    	// 
        // 0 - empty square,
        // 1 - red man
        // 2 - red king
        // 3 - black man
        // 4 - black king
        //System.out.println(board);
        System.out.println();
        return MCTS(legalMoves, this.board, N);
    }
    
    /**
     * Performs the Monte Carlo Tree Search algorithm to generate the move that results in a state that is involved in the highest number of playouts
     * 
     * @param legalMoves       the array of moves that the agent (BLACK) can execute at the current game state
     * @param state            the current game state
     * @param num_simulation   number of simulations to execute
     * 
     * @return                 move that results in a state that is involved in the highest number of playouts
     */
    private CheckersMove MCTS(CheckersMove[] legalMoves, CheckersData state, int num_simulation) {
    	MCNode root = new MCNode(state.RED, state.BLACK,  0, 0, state, null); // Initialize the root of the Monte Carlo search tree
    	for(int i = 0; i < num_simulation; i++) {
    		MCNode leaf = selection(root); // selection step
    		boolean canBeExpanded = expand(leaf); // expansion step
    		MCNode simulated_node; 
    		// if the selected node can be expanded, choose one of its children that has the highest UCB value
    		if(canBeExpanded) { 
    			int child_index = chooseChildren(leaf); 
        		simulated_node = leaf.getChildren().get(child_index);
    		}
    		else {
    			// If the selected node cannot be expanded, use the selected node as the starting node for simulation
    			simulated_node = leaf; 
    		}
    		String result = simulation_result(simulated_node); // Simulation step
    		backpropagation(simulated_node, result); // Backpropagation step
    	}
    	
    	double max_playout = 0; 
    	CheckersMove move_taken = null; 
    	int counter = 1; 
    	int node_num = -1; 
    	System.out.println("Number of childrens: " + root.getChildren().size());
    	System.out.println(); 
    	
    	// Finds the move that results in a state that is involved in the highest number of playouts.
    	for(MCNode node: root.getChildren()) {
    		System.out.println("Node " + counter + ": "); 
    		System.out.println("\t wins: " + node.getWins()); 
    		System.out.println("\t playouts: " + node.getPlayouts()); 
    		System.out.println("\t Win ratio: " + (double)node.getWins() / (double)node.getPlayouts());
    		if(max_playout < node.getPlayouts()) {
    			max_playout = node.getPlayouts(); 
    			move_taken = node.getMoveTaken(); 
    			node_num = counter;
    			System.out.println("\t Move taken: " + move_taken.toString());
    		}
    		counter++; 
    	}
    	System.out.println(); 
    	System.out.println("------> Chosen move at node " + node_num);
    	return move_taken;
    }
    
    /**
     * Performs the selection step of the MCTS algorithm
     * 
     * @param root  the root node of the Monte Carlo search tree
     * 
     * @return      a leaf node that has the highest UCB value
     */
    private MCNode selection(MCNode root) {
    	MCNode node = root;
    	while(node.hasChild()) {
			node = node.getChildren().get(chooseChildren(node)); 
		}
    	return node; 
    }
    
    /**
     * Performs the expansion step of the MCTS algorithm
     * 
     * @param leaf  a leaf node in the Monte Carlo search tree
     * 
     * @return      whether the given leaf node is expandable or not, meaning whether the leaf node contains a terminal state or not
     */
    private boolean expand(MCNode leaf) {
    	CheckersMove[] legal_moves = leaf.getState().getLegalMoves(leaf.getEnemy());
    	// If the enemy of the node has no more moves to execute, it means this node is not expandable as it contains a terminal state
    	if(legal_moves == null) { 
    		return false;
    	}
    	
    	// Given node contains a non-terminal state, generate all of the node's children
    	for(CheckersMove m: legal_moves) {
    		CheckersData next_state = leaf.getState().cloneData();
    		next_state.makeMove(m);
    		MCNode node = new MCNode(leaf.getEnemy(), leaf.getPlayer(), 0, 0, next_state, m); 
    		leaf.addChild(node);
    		node.setParent(leaf);
    	}
    	return true; 
    }
    
    /**
     * Performs the simulation step of the MCTS algorithm
     * 
     * @param node  a node to be simulated on
     * 
     * @return      the result of the simulated playout: "WIN", "LOSE", or "DRAW"
     */
    private String simulation_result(MCNode node) {
    	CheckersData state = node.getState().cloneData();  // Clone the node's game state to not modify the original one
    	int player = node.getPlayer();
    	int enemy = node.getEnemy(); 
    	int counter = 0;
    	int stepsToDraw = 40;  // Based on tournament rules, 40 moves without any piece being captured will result in a DRAW
    	int num_pieces = state.number_of_pieces(); 
    	
    	// while the game has not end, keep on simulating...
    	while(!isTerminal(state, decodeCounter(counter, player, enemy))) {
    		CheckersMove[] legal_moves; 
    		
    		// Based on the architecture of the Monte Carlo search tree, the enemy of the node executes a move first
    		if(counter % 2 == 0) {
    			legal_moves = state.getLegalMoves(enemy); 
    		}
    		else {
    			legal_moves = state.getLegalMoves(player);
    		}
    		
    		Random rand = new Random();
    		int move_index = rand.nextInt(legal_moves.length); // Choose a random legal move
    		state.makeMove(legal_moves[move_index]);
    		int updated_num_pieces = state.number_of_pieces(); 
    		
    		// If no piece has been captured, start the count down for checking a draw
    		if(updated_num_pieces == num_pieces) {
    			stepsToDraw--; 
    			if(stepsToDraw <= 0) { // 40 moves without a single capture from both players is a DRAW
    				return "DRAW"; 
    			}
    		}
    		
    		// If a piece has been captured, reset the draw count down back to 40
    		else if(updated_num_pieces < num_pieces) {
    			stepsToDraw = 40; 
    			num_pieces = updated_num_pieces;
    		}
    		counter++;
    	}
    	// After the while loop, if the counter stops at an even number, it is this node's player's win
    	if(counter % 2 == 0) {
    		return "WIN";
    	}
    	// Else it's its lost
    	return "LOSE";
    }
    
    /**
     * Performs the backpropagation step of the MCTS algorithm
     * 
     * @param node    the node that simulation is performed on
     * @param result  the result from the simulation step: "WIN", "LOSE", or "DRAW"
     */
    private void backpropagation(MCNode node, String result) {
    	int winner;  
    	if(result == "WIN") {
    		winner = 0; 
    	}
    	else if(result == "LOSE") {
    		winner = 1; 
    	}
    	else {
    		winner = -1; //Draw
    	}
    	int counter = 0;
    	while(node != null) {
    		// Add a win for nodes that have the same player as the simulated node along the path to the root of the tree
    		if(counter % 2 == winner) {
    			node.addWin();
    		}
    		// Add a draw for every node along the path to the root
    		else if(winner == -1) {
    			node.addDraw();
    		}
    		// Increment the number of playouts for each node long the path to the root
    		node.addPlayout();
    		counter++; 
    		node = node.getParent(); 
    	}
    }
    
    /**
     * Computes the upper confidence bound (UCB) value for a given node
     * 
     * @param node       a node in the Monte Carlo search tree
     * @param constant   the balance between exploitation and exploration
     * 
     * @return           the upper confidence bound value for this node
     */
    private double UCB(MCNode node, double constant) {
    	 double num_playout = node.getPlayouts(); 
    	 // To avoid division by 0 error
    	 if(num_playout == 0) {
    		 return 1000000; // Exploration of a node that is has not been simulated on seems worth the try
    	 }
    	 return (node.getWins()/num_playout) + (constant * Math.sqrt(Math.log(node.getParent().getPlayouts()) / num_playout));
     }
     
    /**
     * Find the index of child of a given node with the highest UCB value
     * 
     * @param node   a node in the Monte Carlo search tree
     * 
     * @return       index of child of the given node with the highest UCB value
     */
     private int chooseChildren(MCNode node) {
    	 ArrayList<MCNode> children = node.getChildren(); 
    	 double max_value = Double.NEGATIVE_INFINITY; 
    	 int index = 0; 
    	 double UCB_val; 
    	 for(int i = 0; i < children.size(); i++) {
    		 UCB_val = UCB(children.get(i), constant); 
    		 if(UCB_val > max_value) {
    			 max_value = UCB_val; 
    			 index = i; 
    		 }
    	 }
    	 return index; 
     }
     
     /**
      * Checks whether the given state is terminal or not
      * 
      * @param state   a game state containing the board configuration details
      * @param player  player RED or BLACK
      * 
      * @return        true if given state is terminal, else false
      */
     private boolean isTerminal(CheckersData state, int player) {
    	 return state.getLegalMoves(player) == null; 
     }
     
     /**
      * Helper method for deciding which player's turn it is based on a counter value
      * 
      * @param counter  counter value
      * @param player   player RED or BLACK
      * @param enemy    opposite of player. if player is RED, this should be BLACK, vice versa
      * 
      * @return         which player's turn
      */
     private int decodeCounter(int counter, int player, int enemy) {
    	 if(counter % 2 == 0) {
    		 return enemy; 
    	 }
    	 return player; 
     }
}