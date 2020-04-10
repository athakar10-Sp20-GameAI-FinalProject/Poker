package game;

import java.util.List;

import agent.Agent;

public class Dealer {
	
	public static final int HAND_SIZE = 2;
	public static final int BIG_BLIND = 10;
	public static final int SMALL_BLIND = BIG_BLIND / 2;
	
	private Deck deck;
	private List<Agent> players;
	private int numPlayers;
	private int bigBlind;
	private int highestBet;
	private Agent highestBetter;
	private boolean preFlop;
	
	public Dealer(List<Agent> players)
	{
		deck = new Deck();
		this.players = players;
		numPlayers = players.size();
		bigBlind = 0;
		preFlop = true;
		highestBet = BIG_BLIND;
		highestBetter = getBigBlind();
	}
	
	
	// TODO: add in pot vs overall game logic
	public void playRound()
	{
		if(preFlop)
		{
			getBigBlind().setBlind(BIG_BLIND);
			getSmallBlind().setBlind(SMALL_BLIND);
			int index = 1;
			Agent current = getNextPlayer(index);
			while(!allBetsEqual() && (highestBetter != current))
			{
				Move currentMove = current.getMove(this);
				
				index++;
				current = getNextPlayer(index);
			}
		}
		
	}
	
	private boolean allBetsEqual()
	{
		for(Agent player : players)
		{
			if(player.getBetAmount(this) < highestBet)
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean isGameOver()
	{
		return numPlayers <= 1;
	}
	
	public void removePlayer(Agent player)
	{
		players.remove(player);
		numPlayers--;
	}
	
	public void deal()
	{
		deck.shuffle();
		for(Agent player : players)
		{
			Card c1 = deck.draw();
			Card c2 = deck.draw();
			player.setHand(c1, c2);
		}
	}
	
	public Agent getNextPlayer(int offset)
	{
		return players.get((bigBlind + offset) % numPlayers);
	}
	
	public Agent getBigBlind()
	{
		return players.get(bigBlind);
	}
	
	public Agent getSmallBlind()
	{
		return players.get((bigBlind + numPlayers - 1) % numPlayers);
	}
	
	public Agent getButton()
	{
		return players.get((bigBlind + numPlayers - 2) % numPlayers);
	}
	
	public Agent getHighestBetter()
	{
		return highestBetter;
	}
	
	public List<Agent> getPlayers()
	{
		return players;
	}

}
