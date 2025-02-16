package io.github.GDXCards.LogicUtilities;

import io.github.GDXCards.GameUtilities.Card;
import io.github.GDXCards.GameUtilities.Player;
import io.github.GDXCards.GameUtilities.Stack;

import java.util.List;
import java.util.Map;

public interface Controller {

    default void addPlayer(Player player) {}

    default void startGame() {}

    default void dealCards() {}

    default List<Player> getPlayers() {
        return List.of();
    }

    default Stack getStack() {
        return null;
    }

    default void checkStack() {}

    default void addToStack(List<Card> cards, Card.Rank rank) {}

    default void nextPlayer() {}

    default Player getPlayer() {
        return null;
    }

    default List<Card> getStackCards() {
        return List.of();
    }

    default Map<String, Integer> getOtherPlayers() {
        return null;
    }

    default void sendAddToStackMessage(List<Card> cards, Card.Rank rank) {}

    default void sendCheckStackMessage() {}

    default boolean isMyTurn() {
        return false;
    }

    default Card.Rank getCurrentRank() {
        return null;
    }

    default void setCurrentRank(Card.Rank newRank) {}

    void setLastAddedCards(int lastAddedCards);

    int getLastAddedCards();

    default void setWhoWon(String whoWon) {

    }

    default String getWhoWon() {
        return "";
    }

    default void checkWinCondition() {}

    default void resetGame() {}
}
