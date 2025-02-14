package io.github.GDXCards.LogicUtilities;

import io.github.GDXCards.GameUtilities.Card;
import io.github.GDXCards.GameUtilities.Deck;
import io.github.GDXCards.GameUtilities.Player;
import io.github.GDXCards.GameUtilities.Stack;
import io.github.GDXCards.Network.HostServerInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostController implements Controller {
    private final Deck deck;
    private final Stack stack;
    private List<Player> players;
    private int currentPlayerIndex;
    private boolean isGameStarted;
    private final HostServerInstance hostServerInstance;

    private Map<String, Integer> otherPlayers;

    public HostController() throws IOException {
        deck = new Deck();
        stack = new Stack();
        players = new ArrayList<>();
        currentPlayerIndex = 0;
        isGameStarted = false;
        hostServerInstance = new HostServerInstance();
        otherPlayers = new HashMap<>();
    }

    public HostController(HostServerInstance hostServerInstance) {
        deck = new Deck();
        stack = new Stack();
        players = new ArrayList<>();
        this.hostServerInstance = hostServerInstance;
        currentPlayerIndex = 0;
        isGameStarted = false;
        otherPlayers = new HashMap<>();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }


    public void startGame() {
        System.out.println("Starting game");
        isGameStarted = true;
        dealCards();
        System.out.println("Current player: " + currentPlayerIndex + ". " + players.get(currentPlayerIndex).getName());
        sendGameUpdate();
    }

    public void dealCards() {
        for (Player player : players) {
            player.addCards(deck.getFromDeck(13));
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public boolean getIsGameStarted() {
        return isGameStarted;
    }

    public Stack getStack() {
        return stack;
    }

    public void checkStack() {
        if (stack.isValid()) {
            players.get(currentPlayerIndex).addCards(stack.getCardsFromStack());
            stack.clearStack();
        } else {
            int previousPlayerIndex = (currentPlayerIndex - 1 + players.size()) % players.size();
            players.get(previousPlayerIndex).addCards(stack.getCardsFromStack());
            stack.clearStack();
        }
        nextPlayer();
    }

    public void addToStack(List <Card> cards, Card.Rank rank) {
        System.out.println(players.get(currentPlayerIndex).getName() + " adds to stack!");
        players.get(currentPlayerIndex).removeCards(cards);
        System.out.println("Current rank is [Controller]: " + rank);
        stack.addToStack(cards, players.get(currentPlayerIndex), rank);
        System.out.println(players.get(currentPlayerIndex).getName() + " now has " + players.get(currentPlayerIndex).getHand().size() + " cards!");
        nextPlayer();
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        System.out.println("Current player: " + currentPlayerIndex + ". " + players.get(currentPlayerIndex).getName());
        sendGameUpdate();
    }

    public void sendGameUpdate() {
        hostServerInstance.sendGameState();
    }

    public Map<String, Integer> getPlayerMap(Player player) {
        Map<String, Integer> map = new HashMap<>();
        for (Player p : players) {
            if (p.getID() != player.getID())
                map.put(p.getName(), p.getHand().size());
        }
        return map;
    }

    public void setOtherPlayers() {
        this.otherPlayers = getPlayerMap(players.get(0));
    }

    @Override
    public Map<String, Integer> getOtherPlayers() {
        setOtherPlayers();
        return otherPlayers;
    }

    public List<Card> getPlayerHand(Player player) {
        List<Card> hand = new ArrayList<>();
        for (Player p : players) {
            if (p.getID() == player.getID())
                hand.addAll(p.getHand());
        }
        return hand;
    }

    @Override
    public boolean isMyTurn() {
        return currentPlayerIndex == 0;
    }

    @Override
    public Player getPlayer() {
        return players.get(0);
    }

    @Override
    public List<Card> getStackCards() {
        return stack.getCardsFromStack();
    }

    @Override
    public Card.Rank getCurrentRank() {
        return stack.getCurrentRank();
    }
}
