package io.github.GDXCards.Network;

import com.badlogic.gdx.Game;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import io.github.GDXCards.GameController;
import io.github.GDXCards.GameUtilities.*;
import io.github.GDXCards.Main;

import java.io.IOException;
import java.util.ArrayList;

public class GameClient implements GameInstance {
    private final Client client;
    private Connection serverConnection;
    private final Main main;
    private final Player player;
    private GameController gameController;

    public GameClient(Main main) throws IOException {
        this.main = main;
        client = new Client();
        this.gameController = main.getGameController();
        this.player = main.getPlayer();
        registerClasses();
        addListeners();
        startClient("127.0.0.1");
    }

    public void startClient(String host) throws IOException {
        client.start();
        client.connect(5000, host, 54555, 54777);
    }

    public void stopClient() {
        client.stop();
    }

    private void registerClasses() {
        client.getKryo().register(GameServer.class);
        client.getKryo().register(GameClient.class);
        client.getKryo().register(ArrayList.class);
        client.getKryo().register(Card.class);
        client.getKryo().register(Card.Rank.class);
        client.getKryo().register(Card.Suit.class);
        client.getKryo().register(Player.class);
        client.getKryo().register(Person.class);
        client.getKryo().register(Deck.class);
        client.getKryo().register(Stack.class);
        client.getKryo().register(String.class);
        client.getKryo().register(GameController.class);
        client.getKryo().register(com.badlogic.gdx.utils.Array.class);
        client.getKryo().register(java.lang.Object[].class);
    }

    private void addListeners() {
        client.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                serverConnection = connection;
                serverConnection.sendTCP(player);
            }

            @Override
            public void disconnected(Connection connection) {
                serverConnection = null;
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof GameController recievedGameController) {
                    main.getGameController().setGameState(
                            recievedGameController.getDeck(),
                            recievedGameController.getStack(),
                            recievedGameController.getPlayers(),
                            recievedGameController.getCurrentPlayerIndex(),
                            recievedGameController.getIsGameStarted()
                    );
                    for (Player recievedPlayer : recievedGameController.getPlayers()) {
                        if (recievedPlayer.getID() == player.getID()) {
                            player.setHand(recievedPlayer.getHand());
                        }
                    }
                    System.out.println("Received: " + gameController);
                }
            }
        });
    }

    public void sendGameState() {
        serverConnection.sendTCP(gameController.getGameState());
    }
}
