package io.github.GDXCards.GameUtilities;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private ArrayList<Card> deck;

    public Deck() {
        deck = new ArrayList<>();
        for (Card.Rank rank : Card.Rank.values()) {
            for (Card.Suit suit : Card.Suit.values()) {
                deck.add(new Card(rank, suit));
            }
        }
    }

    public Card getRandomFromDeck() {
        if (deck.isEmpty()) {
            return null;
        }
        int index = (int) (Math.random() * deck.size());
        return deck.remove(index);
    }

    public List<Card> getFromDeck(int num) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            cards.add(getRandomFromDeck());
        }
        return cards;
    }

    public List<Card> getDeck() {
        return deck;
    }

}
