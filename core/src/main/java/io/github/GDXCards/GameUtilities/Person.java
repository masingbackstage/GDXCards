package io.github.GDXCards.GameUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Person implements Player {
    private String name;
    private final int ID;

    private List<Card> hand;


    public Person(String name) {
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

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void addCards(List<Card> cards) {
        hand.addAll(cards);
        sortHand();
    }

    @Override
    public List<Card> removeCards() {
        List<Card> selectedCards = new ArrayList<>();
        for (Card card : hand) {
            if (card.isClicked()) {
                selectedCards.add(card);
            }
        }
        hand.remove(selectedCards);
        sortHand();
        return selectedCards;
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
        this.hand = hand;
    }
}
