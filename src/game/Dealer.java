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
	private Card[] community;

	public Dealer(List<Agent> players) {
		deck = new Deck();
		this.players = players;
		playersInHand = players;
		numPlayers = playersInHand.size();
		numPlayersInHand = numPlayers;
		bigBlind = 0;
		community = new Card[COMMUNITY_SIZE];
		highestBet = BIG_BLIND;
		highestBetter = getBigBlind();
		pot = BIG_BLIND + SMALL_BLIND;
		round = Round.PREFLOP;
		startRound = true;
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
		return ret;
	}

	public boolean isPotOver() {
		return numPlayersInHand <= 1;
	}

	public List<ActionEnum> getValidActions(Agent player) {
		List<ActionEnum> moves = new ArrayList<>(7);

		// TODO: add logic
		moves.add(ActionEnum.BET);
		moves.add(ActionEnum.FOLD);
		moves.add(ActionEnum.RAISE);
		moves.add(ActionEnum.CHECK);
		moves.add(ActionEnum.CALL);
		return moves;
	}

	public void playRound() {
		switch (round) {
		case PREFLOP:
			System.out.println("PRE FLOP");
			getBigBlind().setBlind(BIG_BLIND);
			getSmallBlind().setBlind(SMALL_BLIND);
			startRound = true;
			parseActions(1);
			round = Round.FLOP;
			break;
		case FLOP:
			System.out.println("FLOP");
			for (int i = 0; i < COMMUNITY_SIZE - 2; i++) {
				community[i] = deck.draw();
			}
			printCommunity();
			startRound = true;
			parseActions(-2);
			round = Round.TURN;
			break;
		case TURN:
			System.out.println("TURN");
			community[3] = deck.draw();
			printCommunity();
			startRound = true;
			parseActions(-2);
			round = Round.RIVER;
			break;
		case RIVER:
			System.out.println("RIVER");
			community[4] = deck.draw();
			printCommunity();
			startRound = true;
			parseActions(-2);
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
		while (!allBetsEqual() && (highestBetter != currentPlayer) || startRound) {

			ActionEnum action = currentPlayer.getMove(this);

			int currentBet = currentPlayer.getBetAmount(action);

			if (ActionEnum.needsAmount(action)) {
				System.out.println(ActionEnum.toString(action) + " " + currentBet);
			} else {
				System.out.println(ActionEnum.toString(action));
			}

			System.out.println("Highest Bet: " + highestBet + " Pot: " + pot);

			if (action == ActionEnum.FOLD) {

				removePlayerFromPot(currentPlayer);
			} else if (action == ActionEnum.CALL) {

				currentPlayer.setBet(highestBet);
				pot += highestBet - currentBet;
			} else if (action == ActionEnum.CHECK) {
				if (highestBet != currentBet) {
					position--;
					System.out.println("You do not have the sufficent chips played to check.");
				}
			} else if (action == ActionEnum.BET) {
				if (highestBet == BIG_BLIND && (currentBet - BIG_BLIND * 2) >= 0) {

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
			System.out.println("Highest Bet: " + highestBet + " Pot: " + pot);
			position++;
			currentPlayer = getNextPlayer(position);
		}
	}

	public void dollPot(Agent winner) {

		winner.addChips(pot);
		round = Round.PREFLOP;
		playersInHand = players;
		bigBlind = (bigBlind + 1) % numPlayers;
		highestBet = BIG_BLIND;
		highestBetter = getBigBlind();
		pot = BIG_BLIND + SMALL_BLIND;
	}

	public Agent findWinner() {
		if (numPlayersInHand <= 1)
			return playersInHand.get(0);
		Card[] joined = new Card[COMMUNITY_SIZE + HAND_SIZE];
		for (int i = 0; i < COMMUNITY_SIZE; i++) {
			joined[i] = community[i];
		}
		HashMap<Agent, Integer> ranks = new HashMap<>();
		for (Agent player : playersInHand) {
			Card[] hand = player.getHand();
			for (int i = 0; i < HAND_SIZE; i++) {
				joined[i + COMMUNITY_SIZE] = hand[i];
			}
			// HashSet<Card> joinedSet = new HashSet<>(Arrays.asList(joined));
			ranks.put(player, computeRank(joined));
		}
		List<Agent> bestPlayers = bestHands(ranks);

		if (bestPlayers.size() > 1) {
			return highestValueHand(bestPlayers, joined);
		}

		return bestPlayers.get(0);
	}

	/*
	 * + 10 - Royal Flush + 9 - Straight Flush ++ 8 - Four of a kind ++ 7 - Full
	 * House ++ 6 - Flush + 5 - Straight ++ 4 - Trips ++ 3 - Two Pair ++ 2 - Pair ++
	 * 1 - High
	 */

	private Agent highestValueHand(List<Agent> players, Card[] joined) {

		Agent bestPlayer = null;
		int highestScore = Integer.MIN_VALUE;

		Card[] hand = players.get(0).getHand();
		for (int i = 0; i < HAND_SIZE; i++) {
			joined[i + COMMUNITY_SIZE] = hand[i];
		}
		int handType = computeRank(joined);

		switch (handType) {
		case 8:
		case 4:
		case 2: {
			int pairedCardsValue;
			for (Agent player : players) {

				hand = player.getHand();
				for (int i = 0; i < HAND_SIZE; i++) {
					joined[i + COMMUNITY_SIZE] = hand[i];
				}

				pairedCardsValue = onlyDuplicates(CardEnum.sortCards(Arrays.asList(joined)), Collections.emptyList())
						.stream().reduce(0, (a, b) -> a + b);

				if (pairedCardsValue > highestScore) {
					highestScore = pairedCardsValue;
					bestPlayer = player;
				}

			}
		}
			break;

		case 7:
		case 3: {

			List<Integer> matchingCards;
			int highestFirstMatch = -1;
			int highestSecondMatch = -1;

			for (Agent player : players) {

				hand = player.getHand();
				for (int i = 0; i < HAND_SIZE; i++) {
					joined[i + COMMUNITY_SIZE] = hand[i];
				}

				matchingCards = onlyDuplicates(CardEnum.sortCards(Arrays.asList(joined)), Collections.emptyList());
				
				int firstOccurance = 0;
				int firstMatch, secondMatch; // firstMatch can be triple for fullHouse
				
				for(int i = 0; i < matchingCards.size(); i++) {
					int checker = matchingCards.get(0);
					if(matchingCards.get(i) == checker) {
						firstOccurance++;
					}
				}
				
				if(firstOccurance == 2) {
					firstMatch = matchingCards.get(matchingCards.size() - 1);
					secondMatch = matchingCards.get(0);
				} else {
					secondMatch = matchingCards.get(matchingCards.size() - 1);
					firstMatch = matchingCards.get(0);
				}
				
				if(firstMatch > highestFirstMatch) {
					bestPlayer = player;
					highestFirstMatch = firstMatch;
					highestSecondMatch = secondMatch;
				} else if(firstMatch == highestFirstMatch && secondMatch > highestSecondMatch) {
					bestPlayer = player;
					highestFirstMatch = firstMatch;
					highestSecondMatch = secondMatch;
				}
				
				
			}
		}

			break;

		case 6: {

			hand = players.get(0).getHand();
			for (int i = 0; i < HAND_SIZE; i++) {
				joined[i + COMMUNITY_SIZE] = hand[i];
			}

			SuitEnum flushSuit = Card.getSameSuit(joined, SuitEnum.allSuits());

			for (Agent player : players) {
				for (Card card : player.getHand()) {
					if (card.getSuit() == flushSuit) {
						Card[] temp = new Card[1];
						temp[0] = card;
						List<Integer> values = CardEnum.sortCards(Arrays.asList(temp));
						if (values.size() > 1) {
							highestScore = 14;
							bestPlayer = player;
						}

						if (values.get(0) > highestScore) {
							highestScore = values.get(0);
							bestPlayer = player;

						}
					}
				}
			}

		}

			break;
			
		case 5: {
			
			for(Agent player : players) {
				
				hand = players.get(0).getHand();
				for (int i = 0; i < HAND_SIZE; i++) {
					joined[i + COMMUNITY_SIZE] = hand[i];
				}
				
				int value = findStraightValue(CardEnum.sortCards(Arrays.asList(joined)));
				
				if(value > highestScore) {
					highestScore = value;
					bestPlayer = player;
				}
				
			}
		}
			
		break;
		
		case 1: {
			
			for(Agent player : players) {
				for(Card card : player.getHand()) {
					Card[] temp = new Card[1];
					temp[0] = card;
					List<Integer> value = CardEnum.sortCards(Arrays.asList(temp));
					if (value.size() == 2) {
						return player;
					}

					if (value.get(0) > highestScore) {
						bestPlayer = player;
						highestScore = value.get(0);
					}
				}
			}
		}

			break;
		}
		return bestPlayer;
	}

	private int findStraightValue(List<Integer> joined){
		
		List<Integer> straight = joined.subList(0,5);
		int value = (straight.stream().reduce(0, (a, b) -> a + b) / COMMUNITY_SIZE);
		
		if(value == straight.get(2)) {
			return value;
		}
		
		return findStraightValue(joined.subList(1,joined.size()));
	}
	
	private List<Integer> onlyDuplicates(List<Integer> joined, List<Integer> duplicates) {

		if (joined.isEmpty()) {
			return duplicates;
		}

		int first = joined.get(0);

		if (joined.contains(first) || duplicates.contains(first)) {
			duplicates.add(first);
		}
		return onlyDuplicates(joined.subList(1, joined.size()), duplicates);
	}

	private List<Agent> bestHands(HashMap<Agent, Integer> ranks) {
		ArrayList<Agent> bestPlayers = new ArrayList<>();
		int max = ranks.get(playersInHand.get(0));
		for (Agent player : ranks.keySet()) {
			int comp = ranks.get(player);
			if (comp > max)
				max = comp;
		}
		for (Agent player : ranks.keySet()) {
			if (ranks.get(player) == max) {
				bestPlayers.add(player);
			}
		}
		return bestPlayers;
	}

	private boolean isFlush(Card[] joined, List<SuitEnum> suits) {
		if (suits.isEmpty()) {
			return false;
		}
		Card[] filtered = (Card[]) Arrays.stream(joined).filter(x -> x.getSuit() == suits.get(0))
				.collect(Collectors.toList()).toArray();
		if (filtered.length >= 5) {
			return true;
		}
		return isFlush(joined, suits.subList(1, suits.size()));
	}

	private boolean isStraight(List<Integer> joined, List<Integer> allCardNums) {
		if (allCardNums.size() < COMMUNITY_SIZE) {
			return false;
		}
		if (joined.containsAll(allCardNums.subList(0, COMMUNITY_SIZE))) {
			return true;
		}
		return isStraight(joined, allCardNums.subList(1, allCardNums.size()));
	}

	private boolean isMatchingCard(List<Integer> joined, int match, int required, int count) {
		if (count == 0) {
			return true;
		}
		if (joined.isEmpty()) {
			return false;
		}
		if (joined.get(0) == match) {
			return isMatchingCard(joined.subList(1, joined.size()), joined.get(0), required, count - 1);
		}
		return isMatchingCard(joined.subList(1, joined.size()), joined.get(0), required, required);
	}

	private boolean hasTwoPairs(List<Integer> joined, int match, int required) {
		if (required == 0) {
			return true;
		}
		if (joined.isEmpty()) {
			return false;
		}
		if (joined.get(0) == match) {
			return hasTwoPairs(joined.subList(1, joined.size()), -1, required - 1);
		}

		return hasTwoPairs(joined.subList(1, joined.size()), joined.get(0), required - 1);
	}

	private int computeRank(Card[] joined) {
		List<Integer> sortedCards = CardEnum.sortCards(Arrays.asList(joined));
		if (isFlush(joined, SuitEnum.allSuits())) {
			if (isStraight(sortedCards, CardEnum.allCardEnums())) {
				if (sortedCards.containsAll(CardEnum.allCardEnums().subList(10, 15))) {
					return 10; // Royal Flush
				}
				return 9; // Straight Flush
			}
			return 6; // Flush
		}

		if (isMatchingCard(sortedCards, -1, 2, 2)) {
			if (isMatchingCard(sortedCards, -1, 4, 4)) {
				return 8; // Four of a Kind
			}

			if (isMatchingCard(sortedCards, -1, 3, 3)) {
				if (hasTwoPairs(sortedCards, -1, 2)) {
					return 7; // Full House
				}
				return 4; // Triple
			}

			if (hasTwoPairs(sortedCards, -1, 2)) { // Two Pair
				return 3; // Two Pair
			}
			return 2; // Pair

		}

		if (isStraight(sortedCards, CardEnum.allCardEnums())) {
			return 5; // Straight
		}

		return 1; // High Card

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

}
