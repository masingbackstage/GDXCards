package io.github.GDXCards.GameUtilities;

import java.util.ArrayList;
import java.util.List;

public class Stack {
    private final List<Card> cards;
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

    public Card.Rank getCurrentRank() {
        return currentRank;
    }

    public void addToStack(List<Card> newCards, Player lastPlayer, Card.Rank newRank) {
        cards.addAll(newCards);
        topCards = newCards;
        this.lastPlayer = lastPlayer;
        currentRank = newRank;
        System.out.println("Cards added to the stack: " + topCards.size());
        System.out.println("Current rank is [Stack]: " + currentRank);
    }

    public List<Card> getCardsFromStack()
    {
        return cards;
    }

    public void clearStack() {
        updateCurrentRank(Card.Rank.N2);
        cards.clear();
    }

    public boolean isValid() {
        for (Card card : topCards) {
            if (card.getRank() != currentRank) {
                System.out.println("----------------------Player was cheating----------------------");
                return false;
            }
        }
        System.out.println("--------------------Player was telling truth-------------------");
        return true;
    }
}
