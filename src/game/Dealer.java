package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import agent.Agent;

public class Dealer {

	private enum Round {
		PREFLOP, FLOP, TURN, RIVER;
	}

	public static final int HAND_SIZE = 2;
	public static final int COMMUNITY_SIZE = 5;
	public static final int BIG_BLIND = 10;
	public static final int SMALL_BLIND = BIG_BLIND / 2;

	private Deck deck;
	private List<Agent> players;
	private List<Agent> playersInHand;
	private int numPlayers;
	private int numPlayersInHand;
	private int bigBlind;
	private int highestBet;
	private Agent highestBetter;
	private Round round;
	private int pot;
	private boolean startRound;
	private ArrayList<Boolean> havePlayed;
	private Card[] community;
	private HandEval handEval;

	public Dealer(List<Agent> players) {
		deck = new Deck();
		this.players = players;
		playersInHand = new ArrayList<Agent>(players);
		numPlayers = playersInHand.size();
		numPlayersInHand = numPlayers;
		bigBlind = 0;
		community = new Card[COMMUNITY_SIZE];
		highestBet = BIG_BLIND;
		highestBetter = getBigBlind();
		pot = BIG_BLIND + SMALL_BLIND;
		round = Round.PREFLOP;
		startRound = true;
		havePlayed = new ArrayList<Boolean>();
		for(Agent p : players) {
			havePlayed.add(false);
		}
	}

	public Dealer makeCopy() {
		Dealer ret = new Dealer(players);
		ret.deck = deck;
		ret.playersInHand = playersInHand;
		ret.numPlayers = numPlayers;
		ret.bigBlind = bigBlind;
		ret.highestBet = highestBet;
		ret.highestBetter = highestBetter;
		ret.round = round;
		ret.pot = pot;
		ret.community = community;
		ret.numPlayersInHand = numPlayersInHand;
		ret.startRound = startRound;
		ret.havePlayed = havePlayed;
		return ret;
	}

	public boolean isPotOver() {
		return numPlayersInHand <= 1 || round == Round.PREFLOP;
	}
	
	public Agent findWinner() {
		handEval.setCommunity(community);
		return handEval.findWinner();
	}
	
	public boolean isWinning(Agent player) {
		return handEval.findWinner() == player;
	}

	public List<ActionEnum> getValidActions(Agent player) {
		List<ActionEnum> moves = new ArrayList<>(6);
		moves.add(ActionEnum.FOLD);
		
		if(player.getBet() == highestBet) {
			moves.add(ActionEnum.CHECK);
		} else {
			moves.add(ActionEnum.CALL);
		}
		
		if((highestBet != BIG_BLIND && round == Round.PREFLOP) || (highestBet != 0 && round != Round.PREFLOP)) {
			moves.add(ActionEnum.RAISE);
		} else {
			moves.add(ActionEnum.BET);
		}
		return moves;
	}
	
	private void resetBets() {
		for(Agent player: players) {
			player.setBet(0);
		}
	}
	
	private void resetHavePlayed() {
		for(int i = 0; i < havePlayed.size(); i++) {
			havePlayed.set(i, false);
		}
	}
	
	private boolean allPlayed() {
		for(Boolean b : havePlayed) {
			if(!b) {
				return false;
			}
		}
		return true;
	}

	public void playRound() {
		resetBets();
		resetHavePlayed();
		highestBet = 0;
		highestBetter = getButton();
		switch (round) {
		case PREFLOP:
			System.out.println("PRE FLOP");
			getBigBlind().setBlind(BIG_BLIND);
			getSmallBlind().setBlind(SMALL_BLIND);
			highestBetter = getBigBlind();
			highestBet = BIG_BLIND;
			startRound = true;
			parseActions(1);
			round = Round.FLOP;
			break;
		case FLOP:
			System.out.println("FLOP");
			for (int i = 0; i < COMMUNITY_SIZE - 2; i++) {
				community[i] = deck.draw();
			}
			//community[2] = new Card(CardEnum.JACK, SuitEnum.HEART);
			printCommunity();
			startRound = true;
			parseActions(-1);
			round = Round.TURN;
			break;
		case TURN:
			System.out.println("TURN");
			community[3] = deck.draw();
			//community[3] = new Card(CardEnum.TEN, SuitEnum.HEART);
			printCommunity();
			startRound = true;
			parseActions(-1);
			round = Round.RIVER;
			break;
		case RIVER:
			System.out.println("RIVER");
			community[4] = deck.draw();
			//community[4] = new Card(CardEnum.ACE, SuitEnum.HEART);
			printCommunity();
			startRound = true;
			parseActions(-1);
			round = Round.PREFLOP;
			break;
		default:
			System.out.println("ERROR in dealer");
		}
	}

	private void printCommunity() {
		for (Card c : community) {
			if (c != null) {
				System.out.print(c.getName() + " ");
			}
		}
		System.out.println();
	}

	private void parseActions(int position) {
		Agent currentPlayer = getNextPlayer(position);
		while (!(allBetsEqual() && allPlayed()) || startRound) {

			
			System.out.println("\n" + currentPlayer.getName() + "'s bet: " + currentPlayer.getBet());
			
			ActionEnum action = currentPlayer.getMove(this);

			int currentBet = currentPlayer.getBetAmount(action);

			if (ActionEnum.needsAmount(action)) {
				System.out.println(ActionEnum.toString(action) + " " + currentBet);
			} else {
				System.out.println(ActionEnum.toString(action));
			}


			
			if (action == ActionEnum.FOLD) {

				removePlayerFromPot(currentPlayer);
				position--;
			} else if (action == ActionEnum.CALL) {
				havePlayed.set(playersInHand.indexOf(currentPlayer), true);
				pot += highestBet - currentPlayer.getBet();
				currentPlayer.setBet(highestBet);
			} else if (action == ActionEnum.CHECK) {
				System.out.println(currentBet);
				if (highestBet != currentPlayer.getBet()) {
					position--;
					System.out.println("You do not have the sufficent chips played to check.");
				} else {
					havePlayed.set(playersInHand.indexOf(currentPlayer), true);
				}
			} else if (action == ActionEnum.BET) {
				if (highestBet == BIG_BLIND || (currentBet - BIG_BLIND * 2) >= 0 || startRound) {
					for(int i = 0; i < havePlayed.size(); i++) {
						if(i != playersInHand.indexOf(currentPlayer)) {
							havePlayed.set(i, false);
						} else {
							havePlayed.set(i, true);
						}
					}
					currentPlayer.setBet(currentBet);
					highestBet = currentBet;
					pot += highestBet;
					highestBetter = currentPlayer;
				} else {

					position--;
					System.out.println(
							"You can only bet when it is the first time increasing amount. You can try raising.");
				}
			} else if (action == ActionEnum.RAISE) {
				if (highestBet != BIG_BLIND && (currentBet - highestBet * 2) >= 0) {
					for(int i = 0; i < havePlayed.size(); i++) {
						if(i != playersInHand.indexOf(currentPlayer)) {
							havePlayed.set(i, false);
						} else {
							havePlayed.set(i, true);
						}
					}
					currentPlayer.setBet(currentBet);
					highestBet = currentBet;
					pot += highestBet;
					highestBetter = currentPlayer;
				} else {

					position--;
					if ((currentBet - highestBet * 2) < 0) {

						System.out.println(
								"You need to increase the size of the raise to at least the current highest bet times 2");
					} else if (highestBet == BIG_BLIND) {

						System.out.println(
								"You can only raise when there has already been a bet placed. Try again but place a bet.");
					}
				}
			} else {
				position--;
			}
			startRound = false;
			System.out.println();
			System.out.println("Highest Bet: " + highestBet + " Pot: " + pot);		
			position++;
			
			currentPlayer = getNextPlayer(position);
			
		}
	}

	public void dollPot(Agent winner) {

		winner.addChips(pot);
		System.out.println("\n----------------------------------");
		System.out.println("Pot of " + pot + " goes to " + winner.getName());
		System.out.println("----------------------------------\n\nNew hand");
		round = Round.PREFLOP;
		playersInHand = players;
		bigBlind = (bigBlind + 1) % numPlayers;
		highestBet = BIG_BLIND;
		highestBetter = getBigBlind();
		pot = BIG_BLIND + SMALL_BLIND;
		
		resetBets();
	}

	private boolean allBetsEqual() {
		for (Agent player : playersInHand) {
			if (player.getBet() < highestBet) {
				return false;
			}
		}
		return true;
	}

	public boolean isGameOver() {
		return numPlayers <= 1;
	}

	public void removePlayerFromPot(Agent player) {
		havePlayed.remove(playersInHand.indexOf(player));
		playersInHand.remove(player);
		numPlayersInHand--;
	}

	public void removePlayerFromGame(Agent player) {
		players.remove(player);
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
		return playersInHand.get((bigBlind + offset + numPlayersInHand) % numPlayersInHand);
	}

	public Agent getBigBlind() {
		return playersInHand.get(bigBlind);
	}

	public Agent getSmallBlind() {
		return playersInHand.get((bigBlind + numPlayersInHand - 1) % numPlayersInHand);
	}

	public Agent getButton() {
		return playersInHand.get((bigBlind + numPlayersInHand - 2) % numPlayersInHand);
	}

	public Agent getHighestBetter() {
		return highestBetter;
	}

	public List<Agent> getPlayers() {
		return players;
	}
	
	public int getNumPlayersInHand() {
		return numPlayersInHand;
	}
	
	public List<Agent> getPlayersInHand() {
		return playersInHand;
	}

}
