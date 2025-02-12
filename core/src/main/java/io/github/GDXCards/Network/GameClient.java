package io.github.GDXCards.Network;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import io.github.GDXCards.GameController;
import io.github.GDXCards.GameUtilities.*;
import io.github.GDXCards.Main;
import io.github.GDXCards.UIUtilities.GameScreen;

import java.io.IOException;
import java.util.ArrayList;

public class GameClient implements GameInstance {
    private final Client client;
    private Connection serverConnection;
    private final Main main;
    private GameController gameController;
    private Kryo kryo;
    private GameScreen gameScreen;

    public GameClient(Main main) throws IOException {
        this.main = main;
        client = new Client();
        this.gameController = main.getGameController();
        kryo = client.getKryo();


        registerClasses(kryo);
        addListeners();
        startClient("127.0.0.1");
    }
    @Override
    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public void sendStackCheckResult(boolean result) {
        serverConnection.sendTCP(new StackCheckMessage(result, main.getGameController().getCurrentPlayer()));
    }

    public void startClient(String host) throws IOException {
        client.start();
        client.connect(5000, host, 54555, 54777);
    }

    public void stopClient() {
        client.stop();
    }

    private void addListeners() {
        client.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                serverConnection = connection;
                serverConnection.sendTCP(main.getPlayer());
            }

            @Override
            public void disconnected(Connection connection) {
                serverConnection = null;
            }

            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof StackCheckMessage message) {
                    handleStackCheck(message, gameController, main, gameScreen);
                    System.out.println("Received stack check message!");
                }
                if (object instanceof GameController receivedGameController) {
                    System.out.println("Received updated game state!");

                    main.getGameController().setGameState(
                        receivedGameController.getDeck(),
                        receivedGameController.getStack(),
                        receivedGameController.getPlayers(),
                        receivedGameController.getCurrentPlayerIndex(),
                        receivedGameController.getIsGameStarted()
                    );

                    for (Player gamePlayer : gameController.getPlayers()) {
                        if(gamePlayer.getID() == main.getPlayer().getID()) {
                            main.getPlayer().setHand(gamePlayer.getHand());
                        }
                    }
                    Gdx.app.postRunnable(() -> {
                        if (gameScreen != null) {
                            gameScreen.setPlayer(main.getPlayer());
                            gameScreen.debug();
                            gameScreen.updateGameState();

                        } else {
                            System.err.println("GameScreen is null! Cannot update UI.");
                        }
                    });
                }
            }

        });
    }

    public void sendGameState() {
        if (serverConnection != null) {
            System.out.println("Sending game state");
            serverConnection.sendTCP(gameController.getGameState());
        } else {
            System.err.println("Server connection is null! Cannot send game state.");
        }
    }


}
