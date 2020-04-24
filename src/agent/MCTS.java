package agent;

import game.ActionEnum;
import game.Card;
import game.Dealer;

public class MCTS implements Agent {

	public MCTS() {}
	
	public ActionEnum getMove(Dealer dealer) { return null;}
	public void setHand(Card c1, Card c2) {}
	public void printHand() {}
	public void setBlind(int blind) {}
	public void setBet(int blind) {}
	public int getBetAmount(ActionEnum action) { return 0;}
	public int getBet() { return 0; }
	public int getChips() {return 0;}
	public void addChips(int pot) {}
	public void printBetAmount() {}
	public Card[] getHand() { return null; }
	public String getName() {
		return "BOT";
	}
}
