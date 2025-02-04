package io.github.GDXCards;

import io.github.GDXCards.GameUtilities.Deck;
import io.github.GDXCards.GameUtilities.Player;
import io.github.GDXCards.GameUtilities.Stack;
import io.github.GDXCards.Network.GameInstance;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private Deck deck;
    private Stack stack;
    private List<Player> players;
    private int currentPlayerIndex;
    private boolean isGameStarted;
    private transient GameInstance gameInstance;

    public GameController() {
        deck = new Deck();
        stack = new Stack();
        players = new ArrayList<>();
        currentPlayerIndex = 0;
        isGameStarted = false;
        gameInstance = new GameInstance() {
            @Override
            public void sendGameState() {

            }
        };
    }

    public GameController(GameInstance gameInstance) {
        deck = new Deck();
        stack = new Stack();
        players = new ArrayList<>();
        this.gameInstance = gameInstance;
        currentPlayerIndex = 0;
        isGameStarted = false;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public GameController getGameState() {
        return this;
    }

    public void startGame() {
        System.out.println("Starting game");
        isGameStarted = true;
        dealCards();
        System.out.println("Current player: " + currentPlayerIndex + ". " + players.get(currentPlayerIndex).getName());
        gameInstance.sendGameState();
    }

    public void dealCards() {
        for (Player player : players) {
            player.addCards(deck.getFromDeck(13));
        }
    }

    public Player getServerPlayer() {
        if (!players.isEmpty())
            return players.get(0);
        return null;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public boolean getIsGameStarted() {
        return isGameStarted;
    }

    public Deck getDeck() {
        return deck;
    }

    public Stack getStack() {
        return stack;
    }

    public void checkStack() {
    }

    public void addToStack() {
        System.out.println(players.get(currentPlayerIndex).getName() + " adds to stack!");
        stack.addToStack(players.get(currentPlayerIndex).removeCards(), players.get(currentPlayerIndex));
        nextPlayer();
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        System.out.println("Current player: " + currentPlayerIndex + ". " + players.get(currentPlayerIndex).getName());
        gameInstance.sendGameState();
    }

    public void setGameInstance(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setGameState(Deck deck, Stack stack, List<Player> players, int currentPlayerIndex, boolean isGameStarted) {
        this.deck = deck;
        this.stack = stack;
        this.players = players;
        this.currentPlayerIndex = currentPlayerIndex;
        this.isGameStarted = isGameStarted;
    }
}
