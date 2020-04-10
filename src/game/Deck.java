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

    int size() { return this.deck.size(); }

    String draw() {
        Iterator<Card> itrCard = deck.iterator();

        if (!itrCard.hasNext()) {
            throw new IndexOutOfBoundsException("Deck is empty!");
        }

        Card next = itrCard.next();
        String name = next.getName();
        this.deck.remove(next);
        return name;
    }

    void shuffle() {
        Collections.shuffle(this.deck);
    }
}
