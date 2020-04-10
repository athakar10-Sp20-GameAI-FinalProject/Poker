package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Deck {
    private final int newSize = 52;
    private List<Card> deck = new ArrayList<>(newSize);

    public Deck() {
        for (CardEnum c : CardEnum.values()) {
            for (SuitEnum s: SuitEnum.values()) {
                Card newCard = new Card(c, s);
                this.deck.add(newCard);
            }
        }

        if (deck.size() != newSize)
            throw new IndexOutOfBoundsException("Deck does not contain 52 cards.");
    }

    public int size() { return this.deck.size(); }

    public Card draw() {
        Card next = deck.get(0);
        this.deck.remove(0);
        return next;
    }

    public void shuffle() {
        Collections.shuffle(this.deck);
    }
}
