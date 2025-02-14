package io.github.GDXCards.Network.Messages;

import io.github.GDXCards.GameUtilities.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateMessage {
    private final List<Card> cards;
    private final List<Card> stack;
    private final Map<String, Integer> playersHandSize;
    private final boolean myTurn;
    private final Card.Rank rank;

    public UpdateMessage(List<Card> cards, List<Card> stack, Map<String, Integer> playerCardCounts, boolean myTurn, Card.Rank rank) {
        this.cards = cards;
        this.stack = stack;
        this.playersHandSize = playerCardCounts;
        this.myTurn = myTurn;
        this.rank = rank;
    }

    public UpdateMessage() {
        this.cards = new ArrayList<>();
        this.stack = new ArrayList<>();
        this.playersHandSize = new HashMap<>();
        this.myTurn = false;
        this.rank = Card.Rank.N2;
    }

    public List<Card> getCards() {
        return cards;
    }

    public List<Card> getStack() {
        return stack;
    }

    public Map<String, Integer> getPlayersHandSize() {
        return playersHandSize;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public Card.Rank getRank() {
        return rank;
    }
}
