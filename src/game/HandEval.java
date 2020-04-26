package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import agent.Agent;

public class HandEval {

	
	private int numPlayersInHand;
	private List<Agent> playersInHand;
	private Card[] community;
	
	public HandEval(int numPlayersInHand, List<Agent> playersInHand) {
		
		this.numPlayersInHand = numPlayersInHand;
		this.playersInHand = playersInHand;
	}
	
	public void setCommunity(Card[] community) {
		this.community = community;
	}
	
	public Agent findWinner() {
		if (numPlayersInHand <= 1)
			return playersInHand.get(0);
		Card[] joined = new Card[Dealer.COMMUNITY_SIZE + Dealer.HAND_SIZE];
		for (int i = 0; i < Dealer.COMMUNITY_SIZE; i++) {
			joined[i] = community[i];
		}
		HashMap<Agent, Integer> ranks = new HashMap<>();
		for (Agent player : playersInHand) {
			Card[] hand = player.getHand();
			for (int i = 0; i < Dealer.HAND_SIZE; i++) {
				joined[i + Dealer.COMMUNITY_SIZE] = hand[i];
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
		for (int i = 0; i < Dealer.HAND_SIZE; i++) {
			joined[i + Dealer.COMMUNITY_SIZE] = hand[i];
		}
		int handType = computeRank(joined);

		switch (handType) {
		case 8:
		case 4:
		case 2: {
			int pairedCardsValue;
			for (Agent player : players) {

				hand = player.getHand();
				for (int i = 0; i < Dealer.HAND_SIZE; i++) {
					joined[i + Dealer.COMMUNITY_SIZE] = hand[i];
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
				for (int i = 0; i < Dealer.HAND_SIZE; i++) {
					joined[i + Dealer.COMMUNITY_SIZE] = hand[i];
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
			for (int i = 0; i < Dealer.HAND_SIZE; i++) {
				joined[i + Dealer.COMMUNITY_SIZE] = hand[i];
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
				for (int i = 0; i < Dealer.HAND_SIZE; i++) {
					joined[i + Dealer.COMMUNITY_SIZE] = hand[i];
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
					List<Card> temp = new ArrayList<Card>();
					temp.add(card);
					List<Integer> value = CardEnum.sortCards(temp);
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
		int value = (straight.stream().reduce(0, (a, b) -> a + b) / Dealer.COMMUNITY_SIZE);
		
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
		Object[] filtered = Arrays.stream(joined).filter(x -> x.getSuit() == suits.get(0))
				.collect(Collectors.toList()).toArray();
		if (filtered.length >= 5) {
			return true;
		}
		return isFlush(joined, suits.subList(1, suits.size()));
	}

	private boolean isStraight(List<Integer> joined, List<Integer> allCardNums) {
		if (allCardNums.size() < Dealer.COMMUNITY_SIZE) {
			return false;
		}
		if (joined.containsAll(allCardNums.subList(0, Dealer.COMMUNITY_SIZE))) {
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

	public int computeRank(Card[] joined) {
		List<Integer> sortedCards = CardEnum.sortCards(Arrays.asList(joined));
		if (isFlush(joined, SuitEnum.allSuits())) {
			if (isStraight(sortedCards, CardEnum.allCardEnums())) {
				if (sortedCards.containsAll(CardEnum.allCardEnums().subList(9, 14))) {
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
	
}
