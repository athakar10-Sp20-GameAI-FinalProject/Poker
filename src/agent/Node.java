package agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import game.ActionEnum;
import game.Card;
import game.Dealer;
import game.Deck;
import game.HandEval;

public class Node implements Callable<Void> {
	
	public boolean isExpanded;
	public boolean isVisited;
	public Node parent;
	public List<Node> children;
	public int simulations;
	public double wins;
	public boolean isTerminal;
	public int winner;
	public ActionEnum moveToGetHere;
	public HandEval eval;
	private Card[] community = new Card[Dealer.COMMUNITY_SIZE];
	private Card[] hand = new Card[Dealer.HAND_SIZE];
	private int[] ranks = new int[10];
	
	
	protected Node(HandEval eval)
	{
		this.eval = eval;
		this.isExpanded = false;
		this.isVisited = false;
		this.isTerminal = false;
		this.parent = null;
		this.simulations = 0;
		this.wins = 0;
		this.children = new ArrayList<Node>();
	}
	
	public Node makeCopy() {
		Node copy = new Node(eval);
		copy.isExpanded = new Boolean(isExpanded);
		copy.isVisited = new Boolean(isVisited);
		copy.isTerminal = new Boolean(isTerminal);
		if(parent != null) copy.parent = parent.makeCopy();
		copy.simulations = new Integer(simulations);
		copy.wins = new Double(wins);
		copy.moveToGetHere = moveToGetHere;
		copy.children = new ArrayList<Node>(children);
		return copy;
	}
	
	@Override
	public Void call() throws Exception {
		doSims();
		return null;
	}
	
	public void initProbabilities(Card[] community, Card[] hand) {
		this.community = community;
		this.hand = hand;
	}
	
	public void doSims() {
		Deck copyDeck = new Deck();
		copyDeck.shuffle();
		Card[] joined = new Card[Dealer.COMMUNITY_SIZE + Dealer.HAND_SIZE];
		for(int i = 0; i < Dealer.HAND_SIZE; i++) {
			joined[i] = hand[i];
		}
		for(int i = 0; i < Dealer.COMMUNITY_SIZE; i++) {
			joined[i + Dealer.HAND_SIZE] = community[i];
		}
		for(int i = 0; i < joined.length; i++) {
			if(joined[i] == null) {
				joined[i] = copyDeck.draw();
			} else {
				copyDeck.remove(joined[i]);
			}
		}
		int rank = eval.computeRank(joined);
		this.ranks[rank-1]++;
	}
	
	public HashMap<Integer, Double> getProbabilities(long sims)
	{
		// Maps rank (1-10) to probabilities of that hand
		HashMap<Integer, Double> probabilities = new HashMap<>();
		long curr = 0;
		ranks = new int[10];
		while(curr < sims) {
			doSims();
			curr++;
		}
		
		for(int i = 0; i < ranks.length; i++) {
			System.out.println("rank " + ranks[i]);
			probabilities.put(i, (double)ranks[i] / (double)sims);
		}
		printProbabilities(probabilities);
		return probabilities;
	}
	
	public HashMap<Integer, Double> getProbabilitiesPar(Card[] community, Card[] hand, long sims)
	{
		// Maps rank (1-10) to probabilities of that hand
		HashMap<Integer, Double> probabilities = new HashMap<>();
		long curr = 0;
		ExecutorService executor = Executors.newFixedThreadPool(16);
		ranks = new int[10];
		while(curr < sims) {
			try {
				executor.submit(this).get();
			} catch (Exception ie) {
				ie.printStackTrace();
			}
			curr++;
		}
		executor.shutdown();
		for(int i = 0; i < ranks.length; i++) {
			System.out.println("rank " + ranks[i]);
			probabilities.put(i, (double)ranks[i] / (double)sims);
		}
		printProbabilities(probabilities);
		return probabilities;
	}
	
	public void printProbabilities(HashMap<Integer, Double> probabilities) {
		for(Integer key : probabilities.keySet()) {
			System.out.println("Rank : " + (key+1) + ", Probability = " + probabilities.get(key));
		}
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
