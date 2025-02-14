package io.github.GDXCards.Network;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import io.github.GDXCards.GameUtilities.*;
import io.github.GDXCards.LogicUtilities.ClientController;
import io.github.GDXCards.Main;
import io.github.GDXCards.Network.Messages.CheckStackMessage;
import io.github.GDXCards.Network.Messages.SendCardsMessage;
import io.github.GDXCards.Network.Messages.UpdateMessage;
import io.github.GDXCards.UIUtilities.ClientScreen;

import java.io.IOException;
import java.util.List;

public class ClientServerInstance implements ServerInstance {
    private final Client client;
    private Connection serverConnection;
    private final Kryo kryo;
    private final ClientScreen clientScreen;
    private final Main main;
    private final Player playerClient;
    private final ClientController clientController;

    public ClientServerInstance(Main main, String name, String ipAddress) throws IOException {
        client = new Client();
        kryo = client.getKryo();
        this.main = main;
        playerClient = new Person();
        playerClient.setName(name);
        clientController = new ClientController(this);
        clientController.setPlayer(playerClient);
        clientScreen = new ClientScreen(main.getStage(), clientController);
        main.setScreen(clientScreen);
        registerClasses(kryo);
        addListeners();
        startClient(ipAddress);

    }

    public void startClient(String host) throws IOException {
        client.start();
        client.connect(5000, host, 54555, 54777);
    }

    public void stop() {
        client.stop();
        main.setScreen(main.getStartMenuScreen());
    }

    private void addListeners() {
        client.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                serverConnection = connection;
                serverConnection.sendTCP(playerClient);
            }

            @Override
            public void disconnected(Connection connection) {
                serverConnection = null;
            }

            @Override
            public void received(Connection connection, Object object) {
                if(object instanceof UpdateMessage) {
                    clientController.getPlayer().setHand(((UpdateMessage) object).getCards());
                    clientController.setStack(((UpdateMessage) object).getStack());
                    clientController.setOtherPlayers(((UpdateMessage) object).getPlayersHandSize());
                    clientController.setMyTurn(((UpdateMessage) object).isMyTurn());
                    clientController.setCurrentRank(((UpdateMessage) object).getRank());
                    clientController.setLastAddedCards(((UpdateMessage) object).getLastAddedCards());
                    Gdx.app.postRunnable(clientScreen::updateHandCardActors);
                    Gdx.app.postRunnable(clientScreen::updateOtherPlayers);
                    Gdx.app.postRunnable(clientScreen::updateStackCardActors);
                    Gdx.app.postRunnable(clientScreen::updateRankSelectBox);
                }
            }
        });
    }

    public void sendCards(List<Card> cards, Card.Rank rank) {
        System.out.println("Sending " + cards.size() + " cards");
        serverConnection.sendTCP(new SendCardsMessage(cards, rank));
    }

    public void sendCheckStack() {
        System.out.println("Sending check stack");
        serverConnection.sendTCP(new CheckStackMessage());
    }
}
