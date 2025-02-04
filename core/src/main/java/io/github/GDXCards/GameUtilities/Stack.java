package io.github.GDXCards.GameUtilities;

import java.util.ArrayList;
import java.util.List;

public class Stack {
    private List<Card> cards;
    private Card.Rank currentRank;
    private List <Card> topCards;
    private Player lastPlayer;

    public Stack() {
        cards = new ArrayList<>();
        topCards = new ArrayList<>();
        currentRank = Card.Rank.N2;
    }

    public void updateCurrentRank(Card.Rank newRank) {
        currentRank = newRank;
        System.out.println("Current rank is: " + currentRank);
    }

    public void addToStack(List<Card> newCards, Player lastPlayer) {
        cards.addAll(newCards);
        topCards = newCards;
        this.lastPlayer = lastPlayer;
    }

    public List<Card> getCardsFromStack()
    {
        return cards;
    }

    private void clearStack() {
        updateCurrentRank(Card.Rank.N2);
        cards.clear();
    }
}
