package io.github.GDXCards.Network;


import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import io.github.GDXCards.LogicUtilities.ClientController;
import io.github.GDXCards.LogicUtilities.Controller;
import io.github.GDXCards.LogicUtilities.HostController;
import io.github.GDXCards.GameUtilities.*;
import io.github.GDXCards.Network.Messages.CheckStackMessage;
import io.github.GDXCards.Network.Messages.SendCardsMessage;
import io.github.GDXCards.Network.Messages.UpdateMessage;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface ServerInstance {

    default void registerClasses(Kryo kryo) {
        kryo.register(HostServerInstance.class);
        kryo.register(ClientServerInstance.class);
        kryo.register(ArrayList.class);
        kryo.register(Map.class);
        kryo.register(HashMap.class);
        kryo.register(Card.class);
        kryo.register(Card.Rank.class);
        kryo.register(Card.Suit.class);
        kryo.register(Player.class);
        kryo.register(Person.class);
        kryo.register(Deck.class);
        kryo.register(Stack.class);
        kryo.register(String.class);
        kryo.register(Controller.class);
        kryo.register(HostController.class);
        kryo.register(ClientController.class);
        kryo.register(Array.class);
        kryo.register(Object[].class);
        kryo.register(UpdateMessage.class);
        kryo.register(CheckStackMessage.class);
        kryo.register(SendCardsMessage.class);

    }

}
