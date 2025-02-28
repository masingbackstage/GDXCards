package io.github.GDXCards.GameUtilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CardActor extends Actor {
    Card card;
    private boolean isFaceUp;
    private boolean isClicked;

    private final String texturePath;
    private final String backTexturePath = "Cards/CardBack.png";

    private transient Texture frontTexture;
    private transient Texture backTexture;

    private String playerName;

    public CardActor(Card card) {
        this.card = card;
        this.isFaceUp = false;
        this.isClicked = false;
        this.texturePath = getTexturePath();
        playerName = "";

        loadTextures();

        setSize(frontTexture.getWidth(), frontTexture.getHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isClicked) lowerCard();
                else raiseCard();
            }
        });
    }

    public CardActor() {
        this(new Card());
        loadDummyTextures();
        playerName = "";
    }

    private void loadTextures() {
        if (frontTexture == null) {
            frontTexture = new Texture(Gdx.files.internal(texturePath));
        }
        if (backTexture == null) {
            backTexture = new Texture(Gdx.files.internal(backTexturePath));
        }
    }

    private void loadDummyTextures() {
        frontTexture = new Texture(Gdx.files.internal(backTexturePath));
        backTexture = new Texture(Gdx.files.internal(backTexturePath));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        loadTextures();
        Texture toDraw = isFaceUp ? frontTexture : backTexture;
        if (isFaceUp) {
            batch.draw(toDraw, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(),
                getScaleX(), getScaleY(), getRotation(), 0, 0, 120, 200, false, false);
        } else {
            batch.draw(toDraw, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(),
                getScaleX(), getScaleY(), getRotation(), 0, 0, 140, 200, false, false);
        }

    }

    public String getTexturePath() {
        String rankName = switch (card.getRank()) {
            case A -> "A";
            case J -> "J";
            case Q -> "Q";
            case K -> "K";
            default -> card.getRank().name().substring(1);
        };

        String suitName = switch (card.getSuit()) {
            case Hearts -> "C";
            case Spades -> "N";
            case Diamonds -> "W";
            case Clubs -> "Z";
        };

        return "Cards/" + rankName + "_" + suitName + ".png";
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void raiseCard() {
        if (!isFaceUp) return;
        if (isClicked) return;
        isClicked = true;
        addAction(Actions.moveBy(0, 20, 0.2f, Interpolation.exp5));
    }

    public void lowerCard() {
        if (!isFaceUp) return;
        if (!isClicked) return;
        isClicked = false;
        addAction(Actions.moveBy(0, -20, 0.2f, Interpolation.exp5));
    }

    public boolean isFaceUp() { return isFaceUp; }

    public Card.Rank getRank() { return card.getRank(); }

    public Card.Suit getSuit() { return card.getSuit(); }

    public boolean isClicked() {
        return isClicked;
    }

    public Card getCard() {
        return card;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public boolean contains(Card checkedCard) {
        return checkedCard.getSuit() == card.getSuit() && checkedCard.getRank() == card.getRank();
    }

    public void setFaceUpTrue() {
        isFaceUp = true;
    }

    public void setFaceUpFalse() {
        isFaceUp = false;
    }
}
