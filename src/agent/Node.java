package agent;

import java.util.ArrayList;
import java.util.List;

import game.ActionEnum;
import game.Dealer;

public class Node {
	
	public boolean isExpanded;
	public boolean isVisited;
	public Node parent;
	public List<Node> children;
	public int simulations;
	public double wins;
	public Dealer gameState = null;
	public int turn;
	public boolean isTerminal;
	public int winner;
	public ActionEnum moveToGetHere;
	
	protected Node()
	{
		this.isExpanded = false;
		this.isVisited = false;
		this.isTerminal = false;
		this.parent = null;
		this.simulations = 0;
		this.wins = 0;
		this.children = new ArrayList<Node>();
	}
	
	/*
	 * return the child with the highest UCT value
	 */
	public Node bestUCT()
	{
		double bestUCTVal = -1.0f;
		Node best = null;
		for (Node child : children)
		{
			double childUCT = child.uct();
			if (childUCT > bestUCTVal)
			{
				best = child;
				bestUCTVal = childUCT; 
			}
		}
		return best;
	}
	
	/*
	 * calculate the uct value
	 */
	public double uct()
	{
		// just return 0 for the root node, it's never used and won't cause null pointer exceptions
		if (this.parent == null)
			return 0.0;
		// if no simulations have been run, return max value
		if (this.simulations == 0)
			return Double.MAX_VALUE;
		// calculate uct
		// uct = (w_i / s_i) + (C * sqrt(log(S)/s_i))
		return (this.wins / (double) this.simulations) + 
				(1.41 * Math.sqrt(
						Math.log(this.parent.simulations) / (double) this.simulations)
						);
	}
	
}
