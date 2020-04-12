package agent;

import game.Card;
import game.Dealer;
import game.Move;


public abstract class Agent {
	
//	public abstract Move getMove(Dealer dealer);
	public abstract void setHand(Card c1, Card c2);
	public abstract void printHand();
	public abstract void setBlind(int blind);
	public abstract void setBet(int blind);
	public abstract int getBetAmount();
	public abstract int getChips();
	public abstract void printBetAmount();
}
