package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import agent.Agent;

public class Dealer {

	public enum Round {
		PREFLOP, FLOP, TURN, RIVER, END;
	}

	public static final int HAND_SIZE = 2;
	public static final int COMMUNITY_SIZE = 5;
	public static final int BIG_BLIND = 10;
	public static final int SMALL_BLIND = BIG_BLIND / 2;

	private Deck deck;
	private List<Agent> players;
	private TreeMap<Agent, Boolean> playersInHand;  // boolean tells whether player has played yet
	private int numPlayers;
	private int numPlayersInHand;
	private int bigBlind;
	private int highestBet;
	private Agent highestBetter;
	private Round round;
	private int pot;
	private Card[] community;
	private HandEval handEval;

	public Dealer(List<Agent> players) {
		deck = new Deck();
		this.players = players;
		playersInHand = new TreeMap<Agent, Boolean>() {{
			for(Agent player : players) {
				put(player, false);
			}
		}};
		numPlayers = new Integer(playersInHand.size());
		numPlayersInHand = new Integer(numPlayers);
		bigBlind = 0;
		community = new Card[COMMUNITY_SIZE];
		highestBet = BIG_BLIND;
		highestBetter = getBigBlind();
		pot = BIG_BLIND + SMALL_BLIND;
		round = Round.PREFLOP;
		handEval = new HandEval(numPlayersInHand, Arrays.asList(playersInHand.keySet().toArray(new Agent[numPlayersInHand])));
	}

	public Dealer makeCopy() {
		Dealer ret = new Dealer(players);
		ret.deck = deck.makeCopy();
		ret.playersInHand = new TreeMap<>(playersInHand);
		ret.numPlayers = new Integer(numPlayers);
		ret.bigBlind = new Integer(bigBlind);
		ret.highestBet = new Integer(highestBet);
		ret.highestBetter = highestBetter; // don't copy, please
		ret.round = round;
		ret.pot = new Integer(pot);
		ret.community = getCommunity();
		ret.numPlayersInHand = new Integer(numPlayersInHand);
		return ret;
	}

	public boolean isPotOver() {
		return numPlayersInHand <= 1 || round == Round.END;
	}
	
	public Agent findWinner() {
		if(numPlayersInHand == 1) {
			return playersInHand.keySet().iterator().next();
		}
		handEval.setCommunity(community);
		return handEval.findWinner();
	}
	
	public boolean isWinning(Agent player) {
		return handEval.findWinner().equals(player);
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
		for(Agent player : playersInHand.keySet()) {
			playersInHand.put(player, false);
		}
	}
	
	private boolean allPlayed() {
		for(Boolean b : playersInHand.values()) {
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
			for(int i = 0; i < numPlayers; i++) {
				Agent player = players.get(i);
				if(!playersInHand.keySet().contains(player)) {
					playersInHand.put(player, false);
				}
			}
			getBigBlind().setBlind(BIG_BLIND);
			getSmallBlind().setBlind(SMALL_BLIND);
			highestBetter = getBigBlind();
			highestBet = BIG_BLIND;
			parseActions(1);
			round = Round.FLOP;
			break;
		case FLOP:
			System.out.println("FLOP");
			for (int i = 0; i < COMMUNITY_SIZE - 2; i++) {
				community[i] = deck.draw();
			}
			printCommunity();
			parseActions(-1);
			round = Round.TURN;
			break;
		case TURN:
			System.out.println("TURN");
			community[3] = deck.draw();
			printCommunity();
			parseActions(-1);
			round = Round.RIVER;
			break;
		case RIVER:
			System.out.println("RIVER");
			community[4] = deck.draw();
			printCommunity();
			parseActions(-1);
			round = Round.END;
			break;
		case END:
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
		
		while (!allBetsEqual() || !allPlayed()) {
			if(isPotOver()) break;
			
			System.out.println("\n" + currentPlayer.getName() + "'s bet: " + currentPlayer.getBet());
			
			ActionEnum action = currentPlayer.getMove(this);

			int currentBet = currentPlayer.getBetAmount(action);

			if (ActionEnum.needsAmount(action)) {
				System.out.println(ActionEnum.toString(action) + " " + currentBet);
			} else {
				System.out.println(ActionEnum.toString(action));
			}


			switch (action) {
				case FOLD: 
					makeMove(currentPlayer, action, 0);
					position--;
				break;
				
				case CALL:
					System.out.println("highest bet when call: " + highestBet);
					makeMove(currentPlayer, action, 0);
				break;
				
				case CHECK:
					System.out.println(currentBet);
					if (highestBet != currentPlayer.getBet()) {
						position--;
						System.out.println("You do not have the sufficent chips played to check.");
					} else {
						makeMove(currentPlayer, action, 0);
					}
				break;
				
				case BET: 
					if (highestBet == BIG_BLIND || (currentBet - BIG_BLIND * 2) >= 0) {
						makeMove(currentPlayer, action, currentBet);
					} else {
						position--;
						System.out.println(
								"You can only bet when it is the first time increasing amount. You can try raising.");
					}
				break;
				
				case RAISE:
					if (highestBet != BIG_BLIND && (currentBet - highestBet * 2) >= 0) {
						makeMove(currentPlayer, action, currentBet);
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
				break;
				
				default:
					position--;
			}
			System.out.println();
			System.out.println("Highest Bet: " + highestBet + " Pot: " + pot);		
			position++;
			
			currentPlayer = getNextPlayer(position);
		}
		System.out.println("exit parse actions");
	}
	
	public void makeMove(Agent currentPlayer, ActionEnum action, int bet) {
		
		switch(action) {
			case FOLD:
				removePlayerFromPot(currentPlayer);
			break;
			
			case CALL:
				int highBet = new Integer(highestBet);
				playersInHand.put(currentPlayer, true);
				pot += highBet - currentPlayer.getBet();
				System.out.println(currentPlayer.getBet());
				currentPlayer.setBet(highBet);
				System.out.println(currentPlayer.getBet());
			break;
			
			case CHECK:
				playersInHand.put(currentPlayer, true);
			break;
			
			case BET: case RAISE:
				for(Agent player : playersInHand.keySet()) {
					if(!player.equals(currentPlayer)) {
						playersInHand.put(player, false);
					} else {
						playersInHand.put(player, true);
					}
				}
				currentPlayer.setBet(new Integer(bet));
				highestBet = bet;
				pot += bet;
				highestBetter = currentPlayer;
			break;		
		}
	}
	
	public Dealer simulateMove(Agent currentPlayer, ActionEnum action) {
//		resetBets();
//		resetHavePlayed();
//		highestBet = 0;
//		highestBetter = getButton();
		switch (round) {
		case PREFLOP:
//			getBigBlind().setBlind(BIG_BLIND);
//			getSmallBlind().setBlind(SMALL_BLIND);
//			highestBetter = getBigBlind();
//			highestBet = BIG_BLIND;
			makeMove(currentPlayer, action, currentPlayer.getBet());
			round = Round.FLOP;
			break;
		case FLOP:
			for (int i = 0; i < COMMUNITY_SIZE - 2; i++) {
				community[i] = deck.draw();
			}
			makeMove(currentPlayer, action, currentPlayer.getBet());
			round = Round.TURN;
			break;
		case TURN:
			community[3] = deck.draw();
			makeMove(currentPlayer, action, currentPlayer.getBet());
			round = Round.RIVER;
			break;
		case RIVER:
			community[4] = deck.draw();
			makeMove(currentPlayer, action, currentPlayer.getBet());
			round = Round.END;
			break;
		case END:
			round = Round.PREFLOP;
			break;
		}
		return this;
	}

	public void dollPot(Agent winner) {

		winner.addChips(pot);
		System.out.println("\n----------------------------------");
		System.out.println("Pot of " + pot + " goes to " + winner.getName());
		System.out.println("----------------------------------\n\nNew hand");
		round = Round.PREFLOP;
		playersInHand = new TreeMap<Agent, Boolean>() {{
			for(Agent player : players) {
				put(player, false);
			}
		}};
		numPlayersInHand = playersInHand.size();
		bigBlind = (bigBlind + 1) % numPlayers;
		highestBet = BIG_BLIND;
		highestBetter = getBigBlind();
		pot = BIG_BLIND + SMALL_BLIND;
		resetBets();
	}

	private boolean allBetsEqual() {
		for (Agent player : playersInHand.keySet()) {
			if (player.getBet() != highestBet) {
				return false;
			}
		}
		return true;
	}

	public boolean isGameOver() {
		return numPlayers <= 1;
	}

	public void removePlayerFromPot(Agent player) {
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
	
	public void resetDeck() {
		deck = new Deck();
	}

	public Agent getNextPlayer(int offset) {
		return playersInHand.keySet().toArray(new Agent[numPlayersInHand])[(bigBlind + offset + numPlayersInHand) % numPlayersInHand];
	}

	public Agent getBigBlind() {
		return playersInHand.keySet().toArray(new Agent[numPlayersInHand])[bigBlind];
	}

	public Agent getSmallBlind() {
		return playersInHand.keySet().toArray(new Agent[numPlayersInHand])[(bigBlind + numPlayersInHand - 1) % numPlayersInHand];
	}

	public Agent getButton() {
		return playersInHand.keySet().toArray(new Agent[numPlayersInHand])[(bigBlind + numPlayersInHand - 2) % numPlayersInHand];
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
		return Arrays.asList(playersInHand.keySet().toArray(new Agent[numPlayersInHand]));
	}
	
	public Deck getDeck() {
		return deck;
	}
	
	public Round getRound() {
		return round;
	}
	
	public HandEval getEval() {
		return handEval;
	}
	
	public Card[] getCommunity() {
		Card[] copy = new Card[COMMUNITY_SIZE];
		for(int i = 0; i < copy.length; i++) {
			if(community[i] == null) {
				copy[i] = null;
			} else {
				copy[i] = community[i].makeCopy();
			}
		}
		return copy;
	}

}
