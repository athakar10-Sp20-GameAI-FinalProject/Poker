package agent;

import game.Card;
import game.Dealer;
import game.Move;

public abstract class Agent {
	private int chips;
	private Card[] hand;
	public abstract Move getMove(Dealer dealer);
}
