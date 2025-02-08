package io.github.GDXCards.GameUtilities;

import java.util.List;

public interface Player {



    public void addCards(List<Card> cards);

    public List<Card> removeCards();

    public int getID();

    public String getName();

    public List<Card> getHand();

    public void setHand(List<Card> hand);

    public Card.Rank getSelectedRank();

    public void setSelectedRank(String name);
}
