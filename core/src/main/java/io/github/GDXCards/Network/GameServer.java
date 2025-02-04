package io.github.GDXCards.Network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import io.github.GDXCards.GameController;
import io.github.GDXCards.GameUtilities.*;
import io.github.GDXCards.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameServer implements GameInstance{
    Server server;
    List<Connection> connections;
    Main main;
    GameController gameController;
    Player player;

    public GameServer(Main main) throws IOException {
        this.main = main;
        server = new Server();
        this.gameController = main.getGameController();
        this.player = main.getPlayer();
        connections = new ArrayList<>();

        gameController.addPlayer(player);
        registerClasses();
        addListeners();
        startServer();
    }

    public void startServer() throws IOException {
        server.bind(54555, 54777);
        server.start();
    }

    public void stopServer() {
        server.stop();
    }


    private void registerClasses() {
        server.getKryo().register(GameServer.class);
        server.getKryo().register(GameClient.class);
        server.getKryo().register(ArrayList.class);
        server.getKryo().register(Card.class);
        server.getKryo().register(Card.Rank.class);
        server.getKryo().register(Card.Suit.class);
        server.getKryo().register(Player.class);
        server.getKryo().register(Person.class);
        server.getKryo().register(Deck.class);
        server.getKryo().register(Stack.class);
        server.getKryo().register(String.class);
        server.getKryo().register(GameController.class);
        server.getKryo().register(com.badlogic.gdx.utils.Array.class);
        server.getKryo().register(java.lang.Object[].class);
    }

    private void addListeners() {
        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                System.out.println("Adding: " + connection);
                connections.add(connection);
                System.out.println("Added: " + connection);
                System.out.println("Client connected: " + connection.getRemoteAddressTCP() + " (Total: " + connections.size() + ")");

            }

            @Override
            public void disconnected(Connection connection) {
                connections.remove(connection);
                System.out.println("Client disconnected: " + connection.getRemoteAddressTCP() + " (Remaining: " + connections.size() + ")");
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Player ClientPlayer) {
                    gameController.addPlayer(ClientPlayer);
                    System.out.println("Player added: " + ClientPlayer.getName());
                    sendGameState();
                }
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
                    sendGameState();
                    System.out.println("Received GameController: " + gameController);
                }
            }
        });
    }

    public void sendGameState() {
        for (Connection connection : connections) {
            System.out.println("Sending GameState: " + connection);
            connection.sendTCP(gameController.getGameState());
        }
    }
}
