package agent;

import game.ActionEnum;
import game.Card;
import game.Dealer;
import game.Move;

public class HumanAgent extends Agent {
	
	private int chips;
	private Card[] hand;
	private int bet;
	
	public HumanAgent(int chips)
	{
		this.chips = chips;
		hand = new Card[Dealer.HAND_SIZE];
		bet = 0;
	}
	
	public Move getMove(Dealer dealer)
	{
		return new Move(ActionEnum.FOLD);
	}
	
	public void setHand(Card c1, Card c2)
	{
		hand[0] = c1;
		hand[1] = c2;
	}
	
	public void printHand()
	{
		System.out.println("Current Hand: " + hand[0].getName() + " " + hand[1].getName());
	}
	
	public void printBetAmount()
	{
		System.out.println(bet);
	}
	
	public void setBlind(int blind)
	{
		bet = blind;
	}
	
	public int getBetAmount(Dealer dealer)
	{
		
		// TODO: Fix this
		return bet;
	}
}
