package game;

public class Card {
    private CardEnum card;
    private SuitEnum suit;

    private Card() {}

    public Card(CardEnum card, SuitEnum suit) {
        this.card = card;
        this.suit = suit;
    }

    public void changeCard(CardEnum card, SuitEnum suit) {
        this.card = card;
        this.suit = suit;
    }

    public String getName() {
        return card.getType() + suit.getSuit();
    }

    public CardEnum getCard() { return card; }

    public SuitEnum getSuit() { return suit; }
}
