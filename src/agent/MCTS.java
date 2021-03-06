package agent;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import game.ActionEnum;
import game.Card;
import game.Dealer;

public class MCTS implements Agent, Comparable<Agent> {

	private final long timeLimit = 60000;
	
	private int chips;
	private Card[] hand;
	private int bet;
	private boolean allIn = false;
	private final String name = "BOT";
	private Dealer game;
	private Random rand = new Random();
	public Node root;
	
	
	public MCTS(int chips) {
		this.chips = chips;
		hand = new Card[Dealer.HAND_SIZE];
		bet = 0;
	}
	
	public Agent makeCopy() {
		MCTS copy = new MCTS(chips);
		copy.hand = hand.clone();
		copy.bet = new Integer(bet);
		copy.allIn = new Boolean(allIn);
		copy.game = game.makeCopy();
		copy.root = root.makeCopy();
		return copy;
	}
	
	public void setDealer(Dealer d) {
		game = d;
		root = new Node(d.getEval());
	}
	
	// MCTS Code
	
	public ActionEnum getMove(Dealer dealer) 
	{
		// initialize class variables and create root node (passed in game state)
		this.game = dealer;
		System.out.println(hand[0].getName() + " " + hand[1].getName());
		Node root = new Node(dealer.getEval());
		//root.turn = game.getTurn();
		
		// here's where the magic happens
		performMCTS(root, timeLimit);
		
		// choose the child with the best win percentage
		ActionEnum move = bestChildWinPct(root);
		System.out.println(name + " makes move: " + move.name());
		System.out.println("Has hand: ");
		for(Card c : hand) {
			System.out.println(c.getName() + " ");
		}
		return move;
	}
	
	private void performMCTS(Node root, long timeLimit)
	{
		System.out.println("starting MCTS...");
		
		// some time formatting so see how long the algorithm takes
		//DateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS"); 
		long startTime = System.currentTimeMillis();
        //Date startDate = new Date(startTime); 
		//System.out.println("start time: " + format.format(startDate));
		long timeDue = System.currentTimeMillis() + timeLimit;
        //Date timeDueDate = new Date(timeDue); 
		//System.out.println("time due: " + format.format(timeDueDate));
		
		// start from root
		Node node = root;
		int iterationCount = 0;
		// stop in timeDue milliseconds or after 10 million iterations
		while (System.currentTimeMillis() < timeDue && iterationCount++ < 10000000)
		{
			// select node to play from
			node = selection(root);
			// simulate play and get game result
			int rolloutResult = rollout(node);
			// propagate 
			backpropagate(node, rolloutResult);
			
			// check if selected node is fully expanded now
			// if it is, checkNodeExpansion will set isExpanded for us
			if (node.parent != null && !node.parent.isExpanded)
			{
				checkNodeExpansion(node.parent);
			}
		}
		// more time logging
		System.out.println("iteration count: " + iterationCount);
		long endTime = System.currentTimeMillis();
        //Date endDate = new Date(endTime); 
		//System.out.println("time end: " + format.format(endDate));
	}
	
	private Node selection(Node node)
	{
		// continue down the tree until we find a node that is not fully expanded
		// choose the highest uct child at each stage until we get there
		while(node.isExpanded && !node.isTerminal)
		{
			// get the highest uct value child from this expanded node
			node = node.bestUCT();
		}
		// now that we're here, we know the node is not expanded or is terminal
		
		// check if we've seen this node before, if not we need to create children
		// if it's terminal, will be visited but not expanded (return the terminal node)
		if (!node.isVisited)
		{
			if (!node.isTerminal)
			{
				// create children for each move
				addChildren(node);
			}
			// node is now visited
			node.isVisited = true;
		}
		
		// if node is terminal, simply return the terminal node
		// this handles end-of-game states when all children have been created
		if (node.isTerminal)
			return node;
		
		// pick unvisited child
		for (Node child : node.children)
		{
			if (child.isVisited)
			{
				continue;
			}
			else
			{
				// found an unvisited child
				// check if it's not terminal and add it's children
				// need to do this now so the algorithm doesn't get hung up on the next tree level
				if (!child.isTerminal)
					addChildren(child);
				// return the unvisited child
				return child;
			}
		}
		// something is wrong if we've hit this place
		System.out.println("returning null");
		return null;
	}
	
	public boolean checkProbabilities(Node leaf, Dealer rolloutBoard) {
		final long numSims = 5000;
		
		long startTime = System.currentTimeMillis();
		
		leaf.initProbabilities(game.getCommunity(), hand);
		HashMap<Integer, Double> ourProbs = leaf.getProbabilities(numSims);
		
		leaf.initProbabilities(game.getCommunity(), new Card[2]);
		HashMap<Integer, Double> otherProbs = leaf.getProbabilities(numSims);
		
		long endTime = System.currentTimeMillis();
		System.out.println("Time taken to calculate probabilites sequentially: " + ((double)(endTime - startTime) / 1000) + " seconds");
		
//		startTime = System.currentTimeMillis();
//		leaf.initProbabilities(game.getCommunity(), hand, numSims);
//		HashMap<Integer, Double> ourProbsPar = leaf.getProbabilitiesPar(game.getCommunity(), hand, numSims);
//		leaf.initProbabilities(game.getCommunity(), new Card[2], numSims);
//		HashMap<Integer, Double> otherProbsPar = leaf.getProbabilitiesPar(game.getCommunity(), new Card[2], numSims);
//		endTime = System.currentTimeMillis();
//		System.out.println("Time taken to calculate probabilites in parallel: " + ((double)(endTime - startTime) / 1000) + " seconds");
		
		
		// weighted sum of our probability distribution of good hands using standard poker ranks
		double ourSum = 0.0;
		double otherSum = 0.0;
		for(Integer rank : ourProbs.keySet()) {
			ourSum += (ourProbs.get(rank) * rank);
		}
		for(Integer rank : otherProbs.keySet()) {
			otherSum += (otherProbs.get(rank) * rank);
		}
		return ourSum >= otherSum;
	}
	
	private int rollout(Node leaf)
	{
		// simulating playouts to the end of the game with random moves
		
		// mark this node as visited in case we haven't done it before
		leaf.isVisited = true;
		// copy board so we don't mess any future nodes up
		Dealer rolloutBoard = game.makeCopy();
		// get possible next moves
		List<ActionEnum> validMoves = rolloutBoard.getValidActions(this);
		List<Agent> currentPlayers = rolloutBoard.getPlayersInHand();
		
		int position = currentPlayers.indexOf(this);
		
		// continue getting the next board until we reach a terminal state
		while (checkProbabilities(leaf, rolloutBoard) && !rolloutBoard.isPotOver())
		{
			position = (position + 1) % rolloutBoard.getNumPlayersInHand();
			int randInt = rand.nextInt(validMoves.size());
//			// simulate move with random validMove choice
			ActionEnum action = validMoves.get(randInt);
			Agent player = currentPlayers.get(position);
			if(action == ActionEnum.FOLD && player.equals(this)) {
				return 0;
			}
			rolloutBoard = rolloutBoard.simulateMove(player, action);
//			// set validMoves for next loop iteration
			validMoves = rolloutBoard.getValidActions(this);
		}

		// return the game result, 1 is winning, 0 is marginal, -1 is lost. if game is not over, base on probabilities
		if(rolloutBoard.isPotOver()) {
			if(rolloutBoard.findWinner().equals(this)) {
				return 1;
			}
			return 0;
		}
		if(checkProbabilities(leaf, rolloutBoard)) {
			return 1;
		}
		return -1;
	}
	
	private void backpropagate(Node node, int result)
	{
		// need to negate this for back propagation
		// turn is for the NEXT move, not who's move it was INTO this game state
		Node tmp = node;
		// update all parent nodes
		while (tmp != null)
		{
			// regardless of winner, add simulation
			tmp.simulations++;
			// if turn for this node wins, add a win
			if (result == 1)
				tmp.wins++;
			// if no one wins, add half a win
			// this differentiates between draws and losses
			if (result == 0)
				tmp.wins += .5;
			tmp = tmp.parent;
		}
	}
	
	private void addChildren(Node node)
	{
		List<ActionEnum> validMoves = game.getValidActions(this);
		// create children for each valid move
		for (ActionEnum p : validMoves)
		{
			Node newChild = new Node(game.getEval());
			newChild.moveToGetHere = p;
			
			
			newChild.parent = node;
			// get child's board state by simulating the valid move from current board state
			
			//newChild.gameState = game.makeCopy().simulateMove(this, p);
			
			// check if terminal node
			if (game.isPotOver())
			{
				newChild.isTerminal = true;
				newChild.isVisited = false;
				newChild.isExpanded = false;
			}
			
			// add child to parent's list
			node.children.add(newChild);
		}
	}
	
	private ActionEnum bestChildWinPct(Node parent)
	{
		// go through all children and print win percentages, return the one with the highest
		// draws == .5 wins lets us choose drawing moves more than losing moves we can't win
		System.out.println(":::::::::::::::Win Percentages:::::::::::");
		double bestWinPct = -1;
		Node bestChild = null;
		for (Node child : parent.children)
		{
			//child.printNode();
			double childWinPct = (child.simulations == 0) ? 0.0 : (childWinPct = child.wins / (double) child.simulations);
			if (childWinPct > bestWinPct)
			{
				bestWinPct = childWinPct;
				bestChild = child;
			}
		}
		
		// return the move with highest win percentage
		return bestChild.moveToGetHere;
	}
	
	private void checkNodeExpansion(Node node)
	{
		// nodes without children will never be expanded
		if (node.children.isEmpty())
		{
			return;
		}
		// if any child is not visited, the node can't be fully expanded
		for (Node child : node.children)
		{
			if (!child.isVisited)
			{
				return;
			}
				
		}
		// reached the end so all children are visited, node is fully expanded
		node.isExpanded = true;
		return;
	}

	// Normal implementations
	
	public void addChips(int pot) {
		chips += pot;
	}
	
	public Card[] getHand() {
		return hand;
	}

	public void setHand(Card c1, Card c2) {
		hand[0] = c1;
		hand[1] = c2;
	}

	public void printHand() {
		System.out.println("Current Hand: " + hand[0].getName() + " " + hand[1].getName());
	}

	public void printBetAmount() {
		System.out.println(bet);
	}
	
	public void reset() {
		bet = 0;
		allIn = false;
	}

	public void setBlind(int blind) {

		if (blind > chips) {
			bet = chips;
			allIn = true;
		} else {
			bet = blind;
		}
		chips -= bet;
	}

	public void setBet(int highestBet) {

		if (highestBet > chips) {
			bet = chips;
			allIn = true;
		} else {
			bet = highestBet;
		}
		chips -= bet;
	}

	public int getChips() {
		return this.chips;
	}
	
	public int getBet() {
		return bet;
	}
	
	public int getBetAmount(ActionEnum action) {
		return 100;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public int compareTo(Agent o) {
		return o.getName().compareTo(name);
	}
}
