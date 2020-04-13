package agent;

import java.util.List;
import java.util.Scanner;

import game.ActionEnum;
import game.Card;
import game.Dealer;

public class HumanAgent implements Agent {

	private int chips;
	private Card[] hand;
	private int bet;
	private boolean allIn = false;
	private Scanner sc = new Scanner(System.in);

	public HumanAgent(int chips) {
		this.chips = chips;
		hand = new Card[Dealer.HAND_SIZE];
		bet = 0;
	}
	

	public ActionEnum getMove(Dealer dealer) {
		List<ActionEnum> validActions = dealer.getValidActions(this);
		System.out.print("What action will you take: ");
		for(int i = 0; i < validActions.size(); i++) {
			if(i == validActions.size() - 1) {
				System.out.print(ActionEnum.toString(validActions.get(i)) + ".");
			}
			else {
				System.out.print(ActionEnum.toString(validActions.get(i)) + ", ");
			}
		}
		System.out.println();
		printHand();
		System.out.print("Action: ");

		
		String parseAction = sc.nextLine();
		ActionEnum action = getAction(parseAction);
		
		
		return action;
	}
	
	public void addChips(int pot) {
		chips += pot;
	}
	
	public Card[] getHand() {
		return hand;
	}

	public ActionEnum getAction(String action) {
		action = action.trim();
		if (action.toLowerCase().equals("call"))
			return ActionEnum.CALL;
		else if (action.toLowerCase().equals("bet"))
			return ActionEnum.BET;
		else if (action.toLowerCase().equals("raise"))
			return ActionEnum.RAISE;
		else if (action.toLowerCase().equals("check"))
			return ActionEnum.CHECK;
		else if (action.toLowerCase().equals("fold"))
			return ActionEnum.FOLD;
		else
			return ActionEnum.INVALID;
		
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
		int parseBet;
		if (ActionEnum.needsAmount(action)) {
			System.out.print("Amount? ");
			parseBet = sc.nextInt();
		} else {
			parseBet = 0;
		}
		return parseBet;
	}
}
