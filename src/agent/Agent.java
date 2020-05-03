package agent;

import game.ActionEnum;
import game.Card;
import game.Dealer;


public interface Agent {
	
	public String getName();
	public ActionEnum getMove(Dealer dealer);
	public void setHand(Card c1, Card c2);
	public void printHand();
	public void setBlind(int blind);
	public void setBet(int blind);
	public int getBetAmount(ActionEnum action);
	public int getBet();
	public int getChips();
	public void printBetAmount();
	public void addChips(int pot);
	public Card[] getHand();
	public Agent makeCopy();
}
