package io.github.GDXCards.Network;

import io.github.GDXCards.GameUtilities.Player;

public class StackCheckMessage {
    private boolean result;
    private Player player;

    public StackCheckMessage() {
        result = false;
        player = null;
    }

    public StackCheckMessage(boolean result, Player player) {
        this.result = result;
        this.player = player;
    }

    public boolean getResult() {
        return result;
    }

    public Player getPlayer() {
        return player;
    }
}
