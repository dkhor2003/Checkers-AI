package edu.iastate.cs472.proj2;

import java.util.ArrayList;

/**
 * @author Dylan Khor
 * 
 * Node type for the Monte Carlo search tree.
 */
public class MCNode
{
	private int player;
	private int enemy;
	private double wins;
	private double playouts; 
	private CheckersMove move_taken; 
	private CheckersData state; 
	private ArrayList<MCNode> child; 
	private MCNode parent; 
	
	/**
	 * Constructor method for a node in Monte Carlo search tree
	 * 
	 * @param player      player RED or BLACK
	 * @param enemy       enemy of the player, RED if player is BLACK, vice versa
	 * @param wins        the number of wins out of the number of playouts at this node
	 * @param playouts    the number of playouts at this node
	 * @param state       the game state for this node
	 * @param move_taken  the move taken to generate the game state stored in this node
	 */
	public MCNode(int player, int enemy, int wins, int playouts, CheckersData state, CheckersMove move_taken) {
		this.player = player; 
		this.enemy = enemy; 
		this.wins = wins; 
		this.playouts = playouts; 
		this.state= state; 
		this.move_taken = move_taken; 
		this.child = new ArrayList<MCNode>(); 
		this.parent = null; 
	}
	
	// Get the player of this node
	public int getPlayer() {
		return this.player;
	}
	
	// Get the enemy of this node
	public int getEnemy() {
		return this.enemy;
	}
	
	// Get the number of wins at this node
	public double getWins() {
		return this.wins; 
	}
	
	// Add a win for this node
	public void addWin() {
		this.wins += 1; 
	}
	
	// Add a draw for this node, in which the number of wins is increased by 0.5
	public void addDraw() {
		this.wins += 0.5; 
	}
	
	// Get the number of playouts at this node
	public double getPlayouts() {
		return this.playouts; 
	}
	
	// Add a playout for this node
	public void addPlayout() {
		this.playouts += 1; 
	}
	
	// Get the parent node of this node
	public MCNode getParent() {
		return this.parent; 
	}
	
	// Set the parent node of this node as the given node
	public void setParent(MCNode parent) {
		this.parent = parent;
	}
	
	// Indicates whether this node has any children nodes or not
	public boolean hasChild() {
		return this.child.size() > 0;
	}
	
	// Gets the list of children nodes this node has
	public ArrayList<MCNode> getChildren(){
		return this.child; 
	}
	
	// Add the given node as one of this node's children
	public void addChild(MCNode next_state) {
		this.child.add(next_state); 
	}
	
	// Gets the game state associated with this node
	public CheckersData getState() {
		return this.state; 
	}
	
	// Gets the move taken to generate the game state associated with this node
	public CheckersMove getMoveTaken() {
		return this.move_taken; 
	}
}