package io.github.GDXCards.GameUtilities;

import java.util.List;

public interface Player {

    void setName(String name);

    void addCards(List<Card> cards);

    void removeCards(List<Card> cards);

    int getID();

    String getName();

    List<Card> getHand();

    void setHand(List<Card> hand);

}
