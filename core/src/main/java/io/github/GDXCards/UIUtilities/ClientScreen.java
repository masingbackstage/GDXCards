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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import io.github.GDXCards.GameUtilities.Card;
import io.github.GDXCards.GameUtilities.CardActor;
import io.github.GDXCards.LogicUtilities.Controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ClientScreen implements Screen {
    private final Stage stage;
    private final Controller controller;
    private final List<CardActor> handCardActors;
    private final List<CardActor> stackCardActors;

    Skin skin;
    TextButton addToStackButton;
    TextButton checkStackButton;
    SelectBox<String> rankSelectBox;
    Label stackLabel;

    Map<String, Integer> otherPlayers;
    List <Label> otherPlayersLabels;
    List<CardActor> otherPlayersCardActors;

    Card.Rank selectedRank;

    public ClientScreen(Stage stage, Controller controller) {
        this.stage = stage;
        this.controller = controller;
        handCardActors = new ArrayList<>();
        stackCardActors = new ArrayList<>();

        otherPlayers = new HashMap<>();
        otherPlayersCardActors = new ArrayList<>();
        otherPlayersLabels = new ArrayList<>();

        selectedRank = Card.Rank.N2;


        setBackground();
        initializeUI();
    }
    //========================BUTTONS========================//
    private void initializeUI() {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        //Rank Select Box
        rankSelectBox = getStringSelectBox(skin);

        //Add To Stack Button
        addToStackButton = new TextButton("Add to Stack", skin);
        addToStackButton.setSize(300, 100);
        addToStackButton.setPosition(120, 20);

        addToStackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                List <Card> cardsToSend = getClicked();
                if (cardsToSend.isEmpty()) return;
                controller.sendAddToStackMessage(cardsToSend, selectedRank);
            }
        });

        //Check Stack Button
        checkStackButton = new TextButton("Check Stack", skin);
        checkStackButton.setSize(300, 100);
        checkStackButton.setPosition(430, 20);

        checkStackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (getController().getStackCards().isEmpty()) return;
                controller.sendCheckStackMessage();
            }
        });

        getStage().addActor(addToStackButton);
        getStage().addActor(checkStackButton);
        getStage().addActor(rankSelectBox);

        stackLabel = new Label("", skin);
        stackLabel.setPosition(getStage().getWidth() / 2 + 100, getStage().getHeight() / 2 - 13);
        if(getController().getStackCards() == null) {
            stackLabel.setText("0 N2");
        }

        getStage().addActor(stackLabel);


    }

    SelectBox<String> getStringSelectBox(Skin skin) {
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
                for (Card.Rank rank : Card.Rank.values()) {
                    if (rankSelectBox.getSelected().equals(rank.toString())) selectedRank = rank;
                }
                System.out.println("Selected rank [UI]: " + selectedRank);
            }
        });
        return rankSelectBox;
    }

    public void updateRankSelectBox() {
        Array<String> ranks = new Array<>();

        for (Card.Rank rank : Card.Rank.values()) {
            if (rank.ordinal() <= getController().getCurrentRank().ordinal()) {
                ranks.add(rank.name());
            }
        }

        rankSelectBox.setItems(ranks);

        if (ranks.size > 0) {
            rankSelectBox.setSelected(ranks.get(ranks.size - 1));
        }
    }

    void setBackground() {
        Texture backgroundTexture = new Texture(Gdx.files.internal("ui/back_texture.png"));
        Image background = new Image(backgroundTexture);

        background.setSize(stage.getWidth(), stage.getHeight());

        stage.addActor(background);
    }

    private void canUseButtons() {
        if(getController().isMyTurn()) {

            addToStackButton.setDisabled(false);
            addToStackButton.setTouchable(Touchable.enabled);
            checkStackButton.setDisabled(false);
            checkStackButton.setTouchable(Touchable.enabled);
            rankSelectBox.setDisabled(false);
            rankSelectBox.setTouchable(Touchable.enabled);
        }
        else {
            addToStackButton.setDisabled(true);
            addToStackButton.setTouchable(Touchable.disabled);
            checkStackButton.setDisabled(true);
            checkStackButton.setTouchable(Touchable.disabled);
            rankSelectBox.setDisabled(true);
            rankSelectBox.setTouchable(Touchable.disabled);
        }
    }

    //======================CARD ACTORS======================//

    public void updateHandCardActors() {
        List<Card> playerHand = getController().getPlayer().getHand();
        AtomicBoolean changed = new AtomicBoolean(false);
        List<CardActor> removedActors = new ArrayList<>();

        handCardActors.removeIf(actor -> {
            boolean shouldRemove = playerHand.stream().noneMatch(card ->
                actor.getSuit().equals(card.getSuit()) &&
                    actor.getRank().equals(card.getRank())
            );

            if (shouldRemove) {
                removedActors.add(actor);
                changed.set(true);
            }

            return shouldRemove;
        });


        for (Card card : getController().getPlayer().getHand()) {
            boolean exists = handCardActors.stream().anyMatch(actor ->
                actor.getSuit().equals(card.getSuit()) &&
                    actor.getRank().equals(card.getRank())
            );

            if (!exists) {
                CardActor cardActor = new CardActor(card);
                handCardActors.add(cardActor);
                cardActor.setPosition(getStage().getWidth() / 2, -200);
                getStage().addActor(cardActor);
                changed.set(true);
            }
        }

        if (changed.get()) {
            moveCardActorsToStack(removedActors);
            moveCardActorsToHand();
        }


    }

    void moveCardActorsToHand() {
        sortHand();
        float spacing;
        if (handCardActors.size() > 13) {
            spacing = (float) (getStage().getWidth() * 0.8 / handCardActors.size());
        } else {
            spacing = 130;
        }

        float handWidth = spacing * (handCardActors.size() - 1) + 120;
        float startX = (getStage().getWidth() - handWidth) / 2;

        for (int i = 0; i < handCardActors.size(); i++) {
            CardActor cardActor = handCardActors.get(i);

            cardActor.setClicked(false);
            cardActor.toFront();
            float targetX = startX + i * spacing;

            cardActor.addAction(Actions.sequence(
                Actions.rotateTo(0, 0.3f, Interpolation.exp10),
                Actions.moveTo(100, 150, 0.5f, Interpolation.exp10),
                Actions.moveTo(targetX, 150, 0.5f, Interpolation.exp10),
                Actions.run(() -> setFrontWithAnimation(cardActor)),
                Actions.delay(0.5f)
            ));
        }
        updateStackCardActors();
    }

    void moveCardActorsToStack(List<CardActor> removedActors) {
        for (CardActor cardActor : removedActors) {
            cardActor.lowerCard();
            cardActor.addAction(
                Actions.sequence(
                    Actions.parallel(
                        Actions.moveTo(getStage().getWidth() / 2, cardActor.getY(), 0.3f, Interpolation.pow5),
                        Actions.run(() -> setBackWithAnimation(cardActor))
                    ),
                    Actions.moveTo(getStage().getWidth() / 2 - 70, getStage().getHeight() / 2 - 100, 0.5f, Interpolation.exp10),
                    Actions.run(cardActor::remove)
                )
            );
        }
        updateStackCardActors();
    }

    void sortHand() {
        handCardActors.sort((card1, card2) -> {
            int rankComparison = Integer.compare(card1.getRank().ordinal(), card2.getRank().ordinal());
            if (rankComparison == 0) {
                return Integer.compare(card1.getSuit().ordinal(), card2.getSuit().ordinal());
            }
            return rankComparison;
        });
    }

    void setFrontWithAnimation(CardActor cardActor) {
        if(!cardActor.isFaceUp()) {
            cardActor.addAction(Actions.sequence(
                Actions.scaleTo(0, 1, 0.1f, Interpolation.sine),
                Actions.run(cardActor::setFaceUpTrue),
                Actions.scaleTo(1, 1, 0.1f, Interpolation.sine)
            ));
        }
    }

    void setBackWithAnimation(CardActor cardActor) {
        if(cardActor.isFaceUp()) {
            cardActor.addAction(Actions.sequence(
                Actions.scaleTo(0, 1, 0.1f, Interpolation.sine),
                Actions.run(cardActor::setFaceUpFalse),
                Actions.scaleTo(1, 1, 0.1f, Interpolation.sine)
            ));
        }
    }

    List <Card> getClicked() {
        List <Card> clickedCards = new ArrayList<>();
        for (CardActor cardActor : handCardActors) {
            for (int i = 0; i < getController().getPlayer().getHand().size(); i++) {
                if(!cardActor.isClicked()) break;
                if (cardActor.contains(getController().getPlayer().getHand().get(i))) {
                    clickedCards.add(cardActor.getCard());
                    cardActor.lowerCard();
                }
            }
        }
        return clickedCards;
    }

    public void updateOtherPlayers() {
        Map<String, Integer> updatedPlayers = getController().getOtherPlayers();
        AtomicBoolean changed = new AtomicBoolean(false);

        List<CardActor> removedActors = new ArrayList<>();

        for (Label label : otherPlayersLabels) {
            label.remove();
        }
        otherPlayersLabels.clear();

        float[][] positions = {
            {getStage().getWidth() / 2, getStage().getHeight() - 250}, // 1 gracz (środek)
            {150, getStage().getHeight() - 250}, // 2 graczy (lewy)
            {getStage().getWidth() - 150, getStage().getHeight() - 250}, // 2 graczy (prawy)
            {150, getStage().getHeight() - 250}, // 3 graczy (lewy)
            {getStage().getWidth() / 2, getStage().getHeight() - 250}, // 3 graczy (środek)
            {getStage().getWidth() - 150, getStage().getHeight() - 250}  // 3 graczy (prawy)
        };

        int i = 0;
        for (Map.Entry<String, Integer> entry : updatedPlayers.entrySet()) {
            if (i >= 4) break;

            String playerName = entry.getKey();
            int newCardCount = entry.getValue();
            float posX = positions[i][0];
            float posY = positions[i][1];

            Label playerLabel = new Label(playerName + " (" + newCardCount + ")", skin);
            playerLabel.setPosition(posX - 30, posY + 200);
            getStage().addActor(playerLabel);
            otherPlayersLabels.add(playerLabel);

            List<CardActor> playerCards = otherPlayersCardActors.stream()
                .filter(actor -> actor.getPlayerName().equals(playerName))
                .collect(Collectors.toList());

            int currentCardCount = playerCards.size();

            if (newCardCount < currentCardCount) {

                List<CardActor> toRemove = playerCards.subList(newCardCount, currentCardCount);
                for (CardActor card : toRemove) {
                    card.addAction(Actions.sequence(
                        Actions.moveTo(getStage().getWidth() / 2 - 70, getStage().getHeight() / 2 - 100, 0.5f, Interpolation.exp10),
                        Actions.run(card::remove)
                    ));
                    removedActors.add(card);
                }
                otherPlayersCardActors.removeAll(toRemove);

                changed.set(true);
            } else if (newCardCount > currentCardCount) {
                int cardsToAdd = newCardCount - currentCardCount;
                for (int j = 0; j < cardsToAdd; j++) {
                    CardActor cardActor;
                    if (!stackCardActors.isEmpty()) {
                        cardActor = stackCardActors.remove(stackCardActors.size() - 1);
                    } else {
                        cardActor = new CardActor();
                    }
                    cardActor.setPlayerName(playerName);
                    cardActor.toFront();
                    cardActor.setPosition(getStage().getWidth()/2 - 70, getStage().getHeight()/2 - 100);
                    getStage().addActor(cardActor);
                    otherPlayersCardActors.add(cardActor);

                    float targetX = posX + (currentCardCount + j) * 50;
                    cardActor.addAction(Actions.sequence(
                        Actions.moveTo(targetX, posY - 50, 0.3f, Interpolation.exp10),
                        Actions.moveTo(targetX, posY, 0.3f, Interpolation.exp10)
                    ));
                }
            }

            float spacing = 50;
            float startX = posX - (newCardCount - 1) * spacing / 2;

            for (int j = 0; j < newCardCount; j++) {
                if (j >= playerCards.size()) break;

                CardActor cardActor = playerCards.get(j);
                float targetX = startX + j * spacing;

                cardActor.addAction(Actions.sequence(
                    Actions.moveTo(targetX, posY - 50, 0.3f, Interpolation.exp10),
                    Actions.moveTo(targetX, posY, 0.3f, Interpolation.exp10)
                ));
            }


            i++;
        }

        if (changed.get()) {
            moveCardActorsToStack(removedActors);
        }
    }

    public void updateStackCardActors() {
        boolean isStackEmpty = stackCardActors.isEmpty();
        if(!getController().getStackCards().isEmpty()) {
            for (CardActor cardActor : stackCardActors) {
                cardActor.remove();
            }
        }
        stackCardActors.clear();
        for(int i = 0; i < getController().getStackCards().size(); i++) {
            CardActor cardActor = new CardActor();
            cardActor.setPosition(getStage().getWidth() / 2 - 70, getStage().getHeight() / 2 - 100);
            cardActor.toFront();
            stackCardActors.add(cardActor);
            cardActor.setVisible(false);
            getStage().addActor(cardActor);
            if(!isStackEmpty) {
                cardActor.addAction(Actions.sequence(
                    Actions.delay(0.7f),
                    Actions.show()
                ));
            }
            else cardActor.setVisible(true);


        }
        stackLabel.setText(getController().getLastAddedCards() + " "
            + getController().getCurrentRank());
    }

    //=========================OTHER=========================//

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

    Stage getStage() {
        return stage;
    }

    Controller getController() {
        return controller;
    }
}
