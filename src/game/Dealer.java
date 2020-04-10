package game;

import java.util.List;

import agent.Agent;

public class Dealer {
	private Deck deck;
	private List<Agent> players;
	
	public Dealer(List<Agent> players)
	{
		deck = new Deck();
		this.players = players;
	}

}
