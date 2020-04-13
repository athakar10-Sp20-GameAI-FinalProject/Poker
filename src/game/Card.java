package game;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Card {
	private CardEnum card;
	private SuitEnum suit;

	public Card(CardEnum card, SuitEnum suit) {
		this.card = card;
		this.suit = suit;
	}

	public void changeCard(CardEnum card, SuitEnum suit) {
		this.card = card;
		this.suit = suit;
	}

	public static SuitEnum getSameSuit(Card[] joined, List<SuitEnum> suits) {

		Card[] flushCards = (Card[]) Arrays.stream(joined).filter(x -> x.getSuit() == suits.get(0))
				.collect(Collectors.toList()).toArray();

		if(flushCards.length >= Dealer.COMMUNITY_SIZE) {
			return suits.get(0);
		}
		
		return getSameSuit(joined, suits.subList(1,suits.size()));
	}

	public String getName() {
		return card.getType() + suit.getSuit();
	}

	public CardEnum getCard() {
		return card;
	}

	public SuitEnum getSuit() {
		return suit;
	}
}
