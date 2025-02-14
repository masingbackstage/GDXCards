package io.github.GDXCards.LogicUtilities;

import io.github.GDXCards.GameUtilities.Card;
import io.github.GDXCards.GameUtilities.Player;
import io.github.GDXCards.Network.ClientServerInstance;

import java.util.List;
import java.util.Map;

public class ClientController implements Controller {
    private final ClientServerInstance clientServerInstance;
    private Player player;
    private Map<String, Integer> otherPlayers;
    private List<Card> stack;
    private boolean myTurn;
    private Card.Rank currentRank;

    public ClientController(ClientServerInstance clientServerInstance) {
        this.clientServerInstance = clientServerInstance;
        myTurn = false;
        currentRank = Card.Rank.N2;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public List<Card> getStackCards() {
        return stack;
    }

    public void setStack(List<Card> stack) {
        this.stack = stack;
    }

    public void setOtherPlayers(Map<String, Integer> otherPlayers) {
        this.otherPlayers = otherPlayers;
    }

    @Override
    public Map<String, Integer> getOtherPlayers() {
        return otherPlayers;
    }

    @Override
    public void sendAddToStackMessage(List<Card> cards, Card.Rank rank) {
        clientServerInstance.sendCards(cards, rank);
    }

    @Override
    public void sendCheckStackMessage() {
        clientServerInstance.sendCheckStack();
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    @Override
    public boolean isMyTurn() {
        return myTurn;
    }

    @Override
    public Card.Rank getCurrentRank() {
        return currentRank;
    }

    @Override
    public void setCurrentRank(Card.Rank newRank) {
        currentRank = newRank;
    }
}
