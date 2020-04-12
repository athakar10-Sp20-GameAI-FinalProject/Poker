package game;

import java.util.List;
import java.util.Scanner;

import agent.Agent;

public class Dealer {

	public static final int HAND_SIZE = 2;
	public static final int BIG_BLIND = 10;
	public static final int SMALL_BLIND = BIG_BLIND / 2;

	private Deck deck;
	private List<Agent> players;
	private List<Agent> playersInHand;
	private int numPlayers;
	private int bigBlind;
	private int highestBet;
	private Agent highestBetter;
	private boolean preFlop;
	private int pot;

	public Dealer(List<Agent> players) {
		deck = new Deck();
		this.players = players;
		playersInHand = players;
		numPlayers = playersInHand.size();
		bigBlind = 0;
		preFlop = true;
		highestBet = BIG_BLIND;
		highestBetter = getBigBlind();
		pot = BIG_BLIND + SMALL_BLIND;
	}

	// TODO: add in pot vs overall game logic
	public void playRound() {
		if (preFlop) {
			getBigBlind().setBlind(BIG_BLIND);
			getSmallBlind().setBlind(SMALL_BLIND);
			int index = 1;
			Agent currentPlayer = getNextPlayer(index);
			while (!allBetsEqual() && (highestBetter != currentPlayer)) {

				System.out.println("What action will you take: Call, Raise, Bet, Check, or Fold.");
				currentPlayer.printHand();
				System.out.println("Action: ");

				Scanner sc = new Scanner(System.in);
				String parseAction = sc.nextLine();
				ActionEnum action = getAction(parseAction);
				
				int parseBet;

				if (ActionEnum.needsAmount(action)) {
					parseBet = sc.nextInt();
				} else {
					parseBet = 0;
				}

				Move currentMove = new Move(action, parseBet);

				currentMove.printMove();

				System.out.println(highestBet + " " + pot);

				if (action == ActionEnum.FOLD) {

					removePlayer(currentPlayer);
				} else if (action == ActionEnum.CALL) {

					currentPlayer.setBet(highestBet);
					pot += highestBet - currentPlayer.getBetAmount();
				} else if (action == ActionEnum.CHECK) {
					if (highestBet != currentPlayer.getBetAmount()) {
						index--;
						System.out.println("You do not have the sufficent chips playerd to check.");
					}
				} else if (action == ActionEnum.BET) {
					if (highestBet == BIG_BLIND && (currentMove.getAmount() - BIG_BLIND * 2) >= 0) {

						currentPlayer.setBet(currentMove.getAmount());
						highestBet = currentMove.getAmount();
						pot += highestBet;
						highestBetter = currentPlayer;
					} else {

						index--;
						System.out.println(
								"You can only bet when it is the first time increasing amount. YOu can try raising.");
					}
				} else if (action == ActionEnum.RAISE) {
					if (highestBet != BIG_BLIND && (currentMove.getAmount() - highestBet * 2) >= 0) {

						currentPlayer.setBet(currentMove.getAmount());
						highestBet = currentMove.getAmount();
						pot += highestBet;
						highestBetter = currentPlayer;
					} else {

						index--;
						if ((currentMove.getAmount() - highestBet * 2) < 0) {

							System.out.println(
									"You need to increase the size of the raise to at least the current highest bet times 2");
						} else if (highestBet == BIG_BLIND) {

							System.out.println(
									"You can only raise when there has already been a bet placed. Try again but place a bet.");
						}
					}
				}

				index++;
				currentPlayer = getNextPlayer(index);
			}
		}

	}

	private boolean allBetsEqual() {
		for (Agent player : playersInHand) {
			if (player.getBetAmount() < highestBet) {
				return false;
			}
		}
		return true;
	}

	public boolean isGameOver() {
		return numPlayers <= 1;
	}

	public void removePlayer(Agent player) {
		playersInHand.remove(player);
		numPlayers--;
	}

	public void deal() {
		deck.shuffle();
		for (Agent player : players) {
			Card c1 = deck.draw();
			Card c2 = deck.draw();
			player.setHand(c1, c2);
		}
	}

	public Agent getNextPlayer(int offset) {
		return playersInHand.get((bigBlind + offset) % numPlayers);
	}

	public Agent getBigBlind() {
		return playersInHand.get(bigBlind);
	}

	public Agent getSmallBlind() {
		return playersInHand.get((bigBlind + numPlayers - 1) % numPlayers);
	}

	public Agent getButton() {
		return playersInHand.get((bigBlind + numPlayers - 2) % numPlayers);
	}

	public Agent getHighestBetter() {
		return highestBetter;
	}

	public List<Agent> getPlayers() {
		return players;
	}
	
	public ActionEnum getAction(String action) {

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

}
