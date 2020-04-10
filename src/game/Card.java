package game;

public class Card {
    private CardEnum card;
    private SuitEnum suit;

    private Card() {}

    public Card(CardEnum card, SuitEnum suit) {
        this.card = card;
        this.suit = suit;
    }

    void changeCard(CardEnum card, SuitEnum suit) {
        this.card = card;
        this.suit = suit;
    }

    String getName() {
        return card.getType() + suit.getSuit();
    }

    CardEnum getCard() { return card; }

    SuitEnum getSuit() { return suit; }
}
