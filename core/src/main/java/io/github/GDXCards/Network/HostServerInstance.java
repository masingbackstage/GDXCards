package io.github.GDXCards.Network;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import io.github.GDXCards.LogicUtilities.HostController;
import io.github.GDXCards.GameUtilities.*;
import io.github.GDXCards.Main;
import io.github.GDXCards.Network.Messages.CheckStackMessage;
import io.github.GDXCards.Network.Messages.SendCardsMessage;
import io.github.GDXCards.Network.Messages.UpdateMessage;
import io.github.GDXCards.UIUtilities.HostScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostServerInstance implements ServerInstance {
    private Server server;
    private List<Connection> connections;
    private HostController hostController;
    private Kryo kryo;
    private HostScreen hostScreen;
    private Player playerHost;
    private Main main;
    private Map<Connection, Player> playerMap;

    public HostServerInstance(Main main, String name) throws IOException {
        server = new Server();
        connections = new ArrayList<>();
        kryo = server.getKryo();
        this.main = main;
        playerHost = new Person();
        playerHost.setName("HOST");
        hostController = new HostController(this);
        hostController.addPlayer(playerHost);
        hostScreen = new HostScreen(main.getStage(), hostController);
        playerMap = new HashMap<>();

        main.setScreen(hostScreen);

        registerClasses(kryo);
        addListeners();
        startServer();
    }

    public HostServerInstance() {
    }

    public void startServer() throws IOException {
        server.bind(54555, 54777);
        server.start();
    }

    public void stopServer() {
        server.stop();
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
                Player removedPlayer = playerMap.remove(connection);
                if (removedPlayer != null) {
                    hostController.getPlayers().remove(removedPlayer);
                    System.out.println("Removed player: " + removedPlayer.getName());
                }
                connections.remove(connection);
                System.out.println("Client disconnected: " + connection.getRemoteAddressTCP() + " (Remaining: " + connections.size() + ")");
            }


            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Player clientPlayer) {
                    if (!playerMap.containsKey(connection)) {
                        playerMap.put(connection, clientPlayer);
                        hostController.addPlayer(clientPlayer);
                        System.out.println("Player added: " + clientPlayer.getName() + " from " + connection.getRemoteAddressTCP());
                    } else {
                        System.out.println("Player " + clientPlayer.getName() + " is already mapped to a connection.");
                    }
                }
                if (object instanceof CheckStackMessage) {
                    hostController.checkStack();
                    Gdx.app.postRunnable(hostScreen::updateHandCardActors);
                    Gdx.app.postRunnable(hostScreen::updateOtherPlayers);
                    Gdx.app.postRunnable(hostScreen::updateStackCardActors);
                    Gdx.app.postRunnable(hostScreen::updateRankSelectBox);
                }
                if (object instanceof SendCardsMessage) {
                    hostController.addToStack(((SendCardsMessage) object).getCards(), ((SendCardsMessage) object).getRank());
                    Gdx.app.postRunnable(hostScreen::updateHandCardActors);
                    Gdx.app.postRunnable(hostScreen::updateOtherPlayers);
                    Gdx.app.postRunnable(hostScreen::updateStackCardActors);
                    Gdx.app.postRunnable(hostScreen::updateRankSelectBox);
                }
            }
        });
    }

    public void sendGameState() {
        Gdx.app.postRunnable(hostScreen::updateOtherPlayers);
        if (playerMap == null) {
            System.err.println("Error: playerMap is null!");
            return;
        }
        playerMap.forEach((connection, player) -> {
           if(hostController.getPlayers().get(hostController.getCurrentPlayerIndex()).getID() == player.getID()) {
               connection.sendTCP(new UpdateMessage(hostController.getPlayers().get(hostController.getCurrentPlayerIndex()).getHand(),
                                                    hostController.getStack().getCardsFromStack(),
                                                    hostController.getPlayerMap(player),
                                                    true,
                                                    hostController.getStack().getCurrentRank())
               );
           } else {
               connection.sendTCP(new UpdateMessage(hostController.getPlayerHand(player),
                   hostController.getStack().getCardsFromStack(),
                   hostController.getPlayerMap(player),
                   false,
                   hostController.getStack().getCurrentRank())
               );
           }

        });

    }

}
