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
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameScreen implements Screen {
    private final Stage stage;
    private GameController gameController;
    private Player player;
    TextButton addToStackButton;
    TextButton checkStackButton;
    SelectBox<String> rankSelectBox;
    TextButton startGameButton;
    List<CardActor> cardActors;
    List<CardActor> handCardActors;
    List<CardActor> stackCardActors;
    private boolean gameStartedChecked;

    public GameScreen(Stage stage, GameController gameController, Player player, GameInstance gameInstance) {
        this.stage = stage;
        this.gameController = gameController;
        this.player = player;
        this.cardActors = new ArrayList<>();
        this.handCardActors = new ArrayList<>();
        this.stackCardActors = new ArrayList<>();
        this.gameStartedChecked = false;


        Texture backgroundTexture = new Texture(Gdx.files.internal("ui/back_texture.png"));
        Image background = new Image(backgroundTexture);

        background.setSize(stage.getWidth(), stage.getHeight());

        stage.addActor(background);
        initializeUI();
        initializeCards();
    }

    //========================BUTTONS========================//
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
                updateGameState();
            }
        });

        //Check Stack Button
        checkStackButton = new TextButton("Check Stack", skin);
        checkStackButton.setSize(300, 100);
        checkStackButton.setPosition(430, 20);

        checkStackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameController.getStack().getCardsFromStack().isEmpty()) return;
                boolean result = gameController.checkStack();
                gameController.nextPlayer();
                if(result) {
                    removeCardActorsFromStackToHand();
                } else {
                    removeCardActorsFromStackToDeck();
                }

                gameController.getGameInstance().sendGameState();

                gameController.getGameInstance().sendStackCheckResult(result);


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

        for (Card.Rank rank : Card.Rank.values()) {
            ranks.add(rank.name());
        }

        SelectBox<String> rankSelectBox = new SelectBox<>(skin);
        rankSelectBox.setItems(ranks);

        rankSelectBox.setSelected("N2");

        rankSelectBox.setSize(300, 100);
        rankSelectBox.setPosition(740, 20);

        rankSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                player.setSelectedRank(rankSelectBox.getSelected());
            }
        });
        return rankSelectBox;
    }

    private void updateRankSelectBox() {
        Array<String> ranks = new Array<>();
        for (Card.Rank rank : Card.Rank.values()) {
            if (rank.ordinal() <= gameController.getStack().getCurrentRank().ordinal()) {
                ranks.add(rank.name());
            }
        }
        rankSelectBox.setItems(ranks);
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

    //======================CARD ACTORS======================//

    //==WORKING 100%==//
    private void initializeCards() {
        for (int i = 0; i < gameController.getDeck().getDeck().size(); i++) {
            Card card = gameController.getDeck().getDeck().get(i);
            CardActor cardActor = new CardActor(card);
            cardActors.add(cardActor);
            cardActor.setPosition(stage.getWidth()/2 - 570 + i * 0.3f, stage.getHeight()/2 - 100 + i * 0.3f);
            stage.addActor(cardActor);
        }
    }

    //==WORKING 100%==//
    private void addCardActorsToHand() {
        Iterator<CardActor> iterator = cardActors.iterator();
        while (iterator.hasNext()) {
            CardActor cardActor = iterator.next();
            for (Card card : player.getHand()) {
                if (cardActor.contains(card)) {
                    handCardActors.add(cardActor);
                    iterator.remove();
                }
            }
        }
    }

    //==SSUUUUUUUUSS==//
    private void moveCardActorsToHand() {
        sortHand();
        float spacing;
        if (handCardActors.size() > 13) {
            spacing = (float) (stage.getWidth() * 0.8 / handCardActors.size());
        } else {
            spacing = 130;
        }

        float handWidth = spacing * (handCardActors.size() - 1) + 120;
        float startX = (stage.getWidth() - handWidth) / 2;

        for (int i = 0; i < handCardActors.size(); i++) {
            CardActor cardActor = handCardActors.get(i);

            cardActor.setClicked(false);
            cardActor.toFront();
            float targetX = startX + i * spacing;

            cardActor.addAction(Actions.sequence(
                Actions.rotateTo(0, 0.1f, Interpolation.sine),
                Actions.moveTo(targetX, 150, 0.1f, Interpolation.sineOut),
                Actions.run(() -> flipWithAnimation(cardActor))
            ));
        }
    }

    //==SSUUUUUUUUSS==//
    private void moveCardActorsToStack() {
        List <CardActor> selectedCardActors = new ArrayList<>();
        Random rand = new Random();

        for (CardActor cardActor : handCardActors) {
            if (cardActor.isClicked()) {
                for (Card card : gameController.getCurrentPlayer().getHand()) {
                    if (cardActor.contains(card)) {
                        selectedCardActors.add(cardActor);
                        cardActor.setClicked(false);
                        card.setClicked(true);
                    }
                }
            }
        }


        handCardActors.removeAll(selectedCardActors);
        stackCardActors.addAll(selectedCardActors);

        for (int i = 0; i < selectedCardActors.size(); i++) {
            if (selectedCardActors.get(i).isFaceUp()) selectedCardActors.get(i).flip();
            float targetX = stage.getWidth() / 2 + 570 + (i) * 0.3f;
            selectedCardActors.get(i).toFront();
            selectedCardActors.get(i).addAction(
                Actions.sequence(
                    Actions.moveTo(stage.getWidth() / 2 - 60, selectedCardActors.get(i).getY(), 0.2f, Interpolation.sine),
                    Actions.parallel(
                        Actions.moveTo(stage.getWidth() / 2, stage.getHeight() / 2, 0.2f, Interpolation.sine),
                        Actions.moveTo(targetX, stage.getHeight() / 2 - 100, 0.7f, Interpolation.sine),
                        Actions.rotateBy(rand.nextFloat(-50, 50), 0.2f, Interpolation.sine)
                    )));
        }
        selectedCardActors.clear();
    }

    //==WORKING ~99%==//
    private void updateStackActors() {
        Random rand = new Random();
        List<CardActor> newStackCardActors = new ArrayList<>();
        for (Card card : gameController.getStack().getCardsFromStack()) {
            boolean found = false;
            for (CardActor stackCardActor : stackCardActors) {
                if (stackCardActor.contains(card)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                CardActor newActor = null;
                for (CardActor cardActor : cardActors) {
                    if (cardActor.contains(card)) {
                        newActor = cardActor;
                        break;
                    }
                }
                if (newActor == null) {
                    newActor = new CardActor(card);
                    stage.addActor(newActor);
                }
                newStackCardActors.add(newActor);
                newActor.toFront();
            }
        }

        stackCardActors.addAll(newStackCardActors);

        for (int i = 0; i < newStackCardActors.size(); i++) {
            float targetX = stage.getWidth() / 2 + 570 + (i) * 0.3f;
            newStackCardActors.get(i).addAction(
                Actions.parallel(
                    Actions.moveTo(targetX, stage.getHeight() / 2 - 100, 0.7f, Interpolation.sine),
                    Actions.rotateBy(rand.nextFloat(-50, 50), 0.2f, Interpolation.sine)
                ));
        }

        System.out.println("Stack size: " + gameController.getStack().getCardsFromStack().size());
        System.out.println("Stack actors size: " + stackCardActors.size());
    }

    //==WORKING ~99%==//
    private void flipWithAnimation(CardActor cardActor) {
        if(!cardActor.isFaceUp()) {
            cardActor.addAction(Actions.sequence(
                Actions.scaleTo(0, 1, 0.1f, Interpolation.sine),
                Actions.run(cardActor::setFaceUpTrue),
                Actions.scaleTo(1, 1, 0.1f, Interpolation.sine)
            ));
        }
    }

    //==WORKING 100%==//
    private void sortHand() {
        handCardActors.sort((card1, card2) -> {
            int rankComparison = Integer.compare(card1.getRank().ordinal(), card2.getRank().ordinal());
            if (rankComparison == 0) {
                return Integer.compare(card1.getSuit().ordinal(), card2.getSuit().ordinal());
            }
            return rankComparison;
        });
    }

    //==WORKING 100%==//
    public void updateGameState() {
        addCardActorsToHand();
        moveCardActorsToHand();
        updateStackActors();
        updateRankSelectBox();
    }

    //==SSUUUUUUUUSS==//
    public void removeCardActorsFromStackToHand() {
        handCardActors.addAll(stackCardActors);
        stackCardActors.clear();
        updateGameState();
    }

    //==WORKING 100%==//
    public void removeCardActorsFromStackToDeck() {
        cardActors.addAll(stackCardActors);
        stackCardActors.clear();
        for (int i = 0; i < cardActors.size(); i++) {
            float targetX = stage.getWidth() / 2 - 570 + i * 0.3f;
            float targetY = stage.getHeight() / 2 - 100 + i * 0.3f;
            cardActors.get(i).addAction(Actions.sequence(
                Actions.parallel(
                    Actions.moveTo(targetX, targetY, 0.5f, Interpolation.sineOut),
                    Actions.rotateTo(0, 0.2f, Interpolation.sine)
                )
            ));
        }

    }

    //=========================OTHER=========================//

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!gameStartedChecked && gameController.getIsGameStarted()) {
            gameStartedChecked = true;
            addCardActorsToHand();
            moveCardActorsToHand();
        }

        canUseButtons();
        stage.act(Gdx.graphics.getDeltaTime());
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

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void debug() {
        System.out.println("------- DEBUG -------");
        System.out.println("Player: " + player.getName());
        System.out.println("Hand: " + player.getHand().size());
        System.out.println("Hand Actors: " + handCardActors.size());
        System.out.println("Card Actors: " + cardActors.size());
        System.out.println("Stack Actors: " + stackCardActors.size());
        System.out.println("Stack size: " + gameController.getStack().getCardsFromStack().size());
        System.out.println("Current stack rank: " + gameController.getStack().getCurrentRank());
        System.out.println("Previous player: " + gameController.getPreviousPlayerIndex());
        System.out.println("Player hand cards: ");
        for (Card c : player.getHand()) {
            System.out.println(" - " + c.getSuit() + " " + c.getRank());
        }

        System.out.println("Hand actors: ");
        for (CardActor ca : handCardActors) {
            System.out.println(" - " + ca.getCard().getSuit() + " " + ca.getCard().getRank());
        }
        System.out.println("----- DEBUG END -----");
    }
}
