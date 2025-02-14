package io.github.GDXCards.GameUtilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Person implements Player {
    private String name;
    private final int ID;
    private final List<Card> hand;



    public Person(String name, boolean isHost) {
        this.name = name;
        Random rand = new Random();
        this.ID = rand.nextInt();
        hand = new ArrayList<>();
    }

    public Person() {
        name = "skrrrt";
        Random rand = new Random();
        this.ID = rand.nextInt();
        hand = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void addCards(List<Card> cards) {
        hand.addAll(cards);
        sortHand();
    }

    @Override
    public void removeCards(List<Card> cards) {
        Iterator<Card> iterator = hand.iterator();
        while (iterator.hasNext()) {
            Card handCard = iterator.next();
            for (Card card : cards) {
                if (card.getRank().equals(handCard.getRank()) && card.getSuit().equals(handCard.getSuit())) {
                    iterator.remove();
                    break;
                }
            }
        }
        sortHand();
    }


    private void sortHand() {
        hand.sort((card1, card2) -> {
            int rankComparison = Integer.compare(card1.getRank().ordinal(), card2.getRank().ordinal());
            if (rankComparison == 0) {
                return Integer.compare(card1.getSuit().ordinal(), card2.getSuit().ordinal());
            }
            return rankComparison;
        });
    }

    @Override
    public int getID() {
        return ID;
    }

    public List<Card> getHand() {
        return hand;
    }

    @Override
    public void setHand(List<Card> hand) {
        this.hand.clear();
        this.hand.addAll(hand);
    }
}
