package io.github.GDXCards.Network;

public class StackCheckMessage {
    private boolean result;
    public StackCheckMessage(boolean result) {
        this.result = result;
    }

    public boolean getResult() {
        return result;
    }
}
