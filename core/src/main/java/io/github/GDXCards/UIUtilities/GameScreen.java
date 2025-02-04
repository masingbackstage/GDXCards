package io.github.GDXCards.UIUtilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import io.github.GDXCards.GameController;
import io.github.GDXCards.GameUtilities.Card;
import io.github.GDXCards.GameUtilities.CardActor;
import io.github.GDXCards.GameUtilities.Player;
import io.github.GDXCards.Network.GameInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreen implements Screen {
    private final Stage stage;
    private final GameController gameController;
    private final Player player;
    TextButton addToStackButton;
    TextButton checkStackButton;
    SelectBox<String> rankSelectBox;
    TextButton startGameButton;
    GameInstance gameInstance;
    List<CardActor> cardActors;
    List<CardActor> myCardActors;

    public GameScreen(Stage stage, GameController gameController, Player player, GameInstance gameInstance) {
        this.stage = stage;
        this.gameController = gameController;
        this.player = player;
        this.gameInstance = gameInstance;
        cardActors = new ArrayList<>();
        myCardActors = new ArrayList<>();


        Texture backgroundTexture = new Texture(Gdx.files.internal("ui/back_texture.png"));
        Image background = new Image(backgroundTexture);

        background.setSize(stage.getWidth(), stage.getHeight());

        stage.addActor(background);
        initializeUI();
        initializeCards();
    }

    private void initializeCards() {
        for (Card card : gameController.getDeck().getDeck()) {
            cardActors.add(new CardActor(card));
        }
        for (int i = 0; i < cardActors.size(); i++) {
            cardActors.get(i).setPosition(300 + i * 0.3f, 700 + i * 0.3f);
            stage.addActor(cardActors.get(i));
        }
    }

    private void initializeUI() {
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        //Rank Select Box
        rankSelectBox = getStringSelectBox(skin);

        //Add To Stack Button
        addToStackButton = new TextButton("Add to Stack", skin);
        addToStackButton.setSize(300, 100);
        addToStackButton.setPosition(120, 20);

        addToStackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                moveCardActorsToStack();
                gameController.addToStack();
                System.out.println(player.getHand().size());
                moveCardActors();
            }
        });

        //Check Stack Button
        checkStackButton = new TextButton("Check Stack", skin);
        checkStackButton.setSize(300, 100);
        checkStackButton.setPosition(430, 20);

        checkStackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameController.checkStack();
                moveCardActors();
            }
        });


        //Start Game Button
        if(gameController.getServerPlayer() == player){
            startGameButton = getStartGameButton(skin);
            stage.addActor(startGameButton);
        }

        stage.addActor(addToStackButton);
        stage.addActor(checkStackButton);
        stage.addActor(rankSelectBox);

    }

    private SelectBox<String> getStringSelectBox(Skin skin) {
        Array<String> ranks = new Array<>();
        SelectBox<String> rankSelectBox = new SelectBox<>(skin);
        rankSelectBox.setItems(ranks);
        rankSelectBox.setSize(300, 100);
        rankSelectBox.setPosition(740, 20);

        rankSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            }
        });
        return rankSelectBox;
    }

    private TextButton getStartGameButton(Skin skin) {
        TextButton startGameButton = new TextButton("Start Game", skin);
        startGameButton.setSize(300, 100);
        startGameButton.setPosition(1050, 20);

        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameController.startGame();
                startGameButton.remove();
                findCardActors();
                moveCardActors();
            }
        });
        return startGameButton;
    }

    private void canUseButtons() {
        if(gameController.getIsGameStarted()) {
            if (gameController.getCurrentPlayer().getID() != player.getID()) {
                //System.out.println("Cant use buttons");
                addToStackButton.setDisabled(true);
                addToStackButton.setTouchable(Touchable.disabled);
                checkStackButton.setDisabled(true);
                checkStackButton.setTouchable(Touchable.disabled);
            } else {
                //System.out.println("Can use buttons");
                addToStackButton.setDisabled(false);
                addToStackButton.setTouchable(Touchable.enabled);
                checkStackButton.setDisabled(false);
                checkStackButton.setTouchable(Touchable.enabled);
            }
            rankSelectBox.setDisabled(false);
            rankSelectBox.setTouchable(Touchable.enabled);
        }
        else {
            //System.out.println("Cant use buttons, game not started");
            addToStackButton.setDisabled(true);
            addToStackButton.setTouchable(Touchable.disabled);
            checkStackButton.setDisabled(true);
            checkStackButton.setTouchable(Touchable.disabled);
            rankSelectBox.setDisabled(true);
            rankSelectBox.setTouchable(Touchable.disabled);
        }

        if(gameController.getServerPlayer() == player){
            if(gameController.getPlayers().size() > 1) {
                startGameButton.setDisabled(false);
                startGameButton.setTouchable(Touchable.enabled);
            }
            else {
                startGameButton.setDisabled(true);
                startGameButton.setTouchable(Touchable.disabled);
            }
        }
    }

    private void findCardActors() {
        myCardActors.clear();
        List<Card> myHand = player.getHand();

        for (Card card : myHand) {
            for (CardActor cardActor : cardActors) {
                if (cardActor.getRank() == card.getRank() && cardActor.getSuit() == card.getSuit()) {
                    myCardActors.add(cardActor);
                    card.setClicked(cardActor.isClicked());
                }
            }
        }
        cardActors.removeAll(myCardActors);
    }

    private void moveCardActors() {
        float spacing;
        if (myCardActors.size() > 13) {
            spacing = (float) (stage.getWidth() * 0.8 / myCardActors.size());
        } else {
            spacing = 130;
        }

        float handWidth = spacing * (myCardActors.size() - 1) + 120;
        float startX = (stage.getWidth() - handWidth) / 2;

        for (int i = 0; i < myCardActors.size(); i++) {
            CardActor cardActor = myCardActors.get(i);

            if (!stage.getActors().contains(cardActor, true)) {
                stage.addActor(cardActor);
            }

            float targetX = startX + i * spacing;

            cardActor.addAction(Actions.sequence(
                Actions.rotateTo(0, 0.3f, Interpolation.sine),
                Actions.moveTo(targetX, 150, 0.5f, Interpolation.sineOut),
                Actions.run(() -> flipWithAnimation(cardActor))
            ));
        }
    }

    private void moveCardActorsToStack() {
        for (CardActor cardActor : myCardActors) {
            if (cardActor.isClicked()) {
                Random rand = new Random();

                if (cardActor.isFaceUp()) cardActor.flip();
                if (!stage.getActors().contains(cardActor, true)) {
                    stage.addActor(cardActor);
                }
                float targetX = 700 + 0.3f;
                cardActor.addAction(
                    Actions.sequence(
                        Actions.moveTo(stage.getWidth()/2 - 60, cardActor.getY(), 0.2f, Interpolation.sine),
                        Actions.parallel(
                            Actions.moveTo(stage.getWidth()/2, stage.getHeight()/2, 0.2f, Interpolation.sine),
                            Actions.moveTo(targetX, 700, 0.7f, Interpolation.sine),
                            Actions.rotateBy(rand.nextFloat(-50, 50), 0.2f, Interpolation.sine)
                        )
                    )
                );
            }
        }
    }

    private void flipWithAnimation(CardActor cardActor) {
        if(!cardActor.isFaceUp()) {
            cardActor.addAction(Actions.sequence(
                Actions.scaleTo(0, 1, 0.2f, Interpolation.sine),
                Actions.run(cardActor::flip),
                Actions.scaleTo(1, 1, 0.2f, Interpolation.sine)
            ));
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        findCardActors();
        canUseButtons();
        moveCardActors();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
