package io.github.GDXCards.Network;


import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import io.github.GDXCards.GameController;
import io.github.GDXCards.GameUtilities.*;
import io.github.GDXCards.Main;
import io.github.GDXCards.UIUtilities.GameScreen;


import java.util.ArrayList;

public interface GameInstance {

    default void registerClasses(Kryo kryo) {
        kryo.register(GameServer.class);
        kryo.register(GameClient.class);
        kryo.register(ArrayList.class);
        kryo.register(Card.class);
        kryo.register(Card.Rank.class);
        kryo.register(Card.Suit.class);
        kryo.register(Player.class);
        kryo.register(Person.class);
        kryo.register(Deck.class);
        kryo.register(Stack.class);
        kryo.register(String.class);
        kryo.register(GameController.class);
        kryo.register(Array.class);
        kryo.register(Object[].class);
        kryo.register(StackCheckMessage.class);
    }

    public void sendGameState();

    public void setGameScreen(GameScreen gameScreen);

    public void sendStackCheckResult(boolean result);

    default void handleStackCheck(StackCheckMessage msg, GameController gameController, Main main, GameScreen gameScreen) {
        System.out.println("Handling stack check");
        System.out.println("MSG PLAYER ID: " + msg.getPlayer().getID());
        System.out.println("MY ID: " + main.getPlayer().getID());
        if (!msg.getResult() && msg.getPlayer().getID() == main.getPlayer().getID()) {
            gameScreen.removeCardActorsFromStackToHand();
            System.out.println("I take!");
        } else {
            gameScreen.removeCardActorsFromStackToDeck();
            System.out.println("I do not take!");
        }
        gameScreen.updateGameState();
    }
}
