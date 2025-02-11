package io.github.GDXCards.Network;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import io.github.GDXCards.GameController;
import io.github.GDXCards.GameUtilities.*;
import io.github.GDXCards.Main;
import io.github.GDXCards.UIUtilities.GameScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameServer implements GameInstance{
    Server server;
    List<Connection> connections;
    Main main;
    GameController gameController;
    Kryo kryo;
    GameScreen gameScreen;

    public GameServer(Main main) throws IOException {
        this.main = main;
        server = new Server();
        this.gameController = main.getGameController();
        connections = new ArrayList<>();
        kryo = server.getKryo();

        gameController.addPlayer(main.getPlayer());
        registerClasses(kryo);
        addListeners();
        startServer();
    }
    @Override
    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
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
                connections.remove(connection);
                System.out.println("Client disconnected: " + connection.getRemoteAddressTCP() + " (Remaining: " + connections.size() + ")");
            }

            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof StackCheckMessage message) {
                    handleStackCheck(message, gameController, main, gameScreen);
                    System.out.println("Received stack check message!");
                }
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
                    System.out.println("Received GameController: " + gameController);

                    Gdx.app.postRunnable(() -> {
                        if (gameScreen != null) {
                            gameScreen.setPlayer(main.getPlayer());
                            gameScreen.debug();
                            gameScreen.updateGameState();
                        } else {
                            System.err.println("GameScreen is null! Cannot update UI.");
                        }
                    });
                    sendGameState();
                }

            }
        });
    }

    public void sendGameState() {
        System.out.println("Sending game state to clients...");
        server.sendToAllTCP(gameController.getGameState());
    }

    public void sendStackCheckResult(boolean result) {
        server.sendToAllTCP(new StackCheckMessage(result, main.getGameController().getCurrentPlayer()));
    }
}
