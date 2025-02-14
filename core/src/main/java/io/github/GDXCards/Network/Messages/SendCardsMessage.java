package io.github.GDXCards.Network.Messages;

import io.github.GDXCards.GameUtilities.Card;

import java.util.ArrayList;
import java.util.List;

public class SendCardsMessage {
    List<Card> cards;
    Card.Rank rank;

    public SendCardsMessage(List<Card> cards, Card.Rank rank) {
        this.cards = cards;
        this.rank = rank;
    }

    public SendCardsMessage() {
        cards = new ArrayList<>();
    }

    public List<Card> getCards() {
        return cards;
    }

    public Card.Rank getRank() {
        return rank;
    }
}
