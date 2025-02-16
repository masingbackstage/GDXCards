package io.github.GDXCards.UIUtilities;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.GL20;
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
import io.github.GDXCards.LogicUtilities.Controller;
import io.github.GDXCards.GameUtilities.Card;
import io.github.GDXCards.GameUtilities.CardActor;
import io.github.GDXCards.LogicUtilities.HostController;

import java.util.*;
import java.util.List;

public class HostScreen extends ClientScreen{
    TextButton addToStackButton;
    TextButton checkStackButton;
    SelectBox<String> rankSelectBox;
    TextButton startGameButton;
    List<CardActor> handCardActors;
    List<CardActor> stackCardActors;
    Label stackLabel;
    String winner;
    Window gameOverWindow;

    Map<String, Integer> otherPlayers;
    List <Label> otherPlayersLabels;
    List<CardActor> otherPlayersCardActors;

    Card.Rank selectedRank;

    private boolean isGameStarted;

    public HostScreen(Stage stage, Controller controller) {
        super(stage, controller);
        this.handCardActors = new ArrayList<>();
        this.stackCardActors = new ArrayList<>();
        this.isGameStarted = false;

        otherPlayers = new HashMap<>();
        otherPlayersCardActors = new ArrayList<>();
        otherPlayersLabels = new ArrayList<>();
        winner = "";
        selectedRank = Card.Rank.N2;

        setBackground();
        initializeUI();
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
                List <Card> cardsToSend = getClicked();
                if (cardsToSend.isEmpty()) return;
                getController().addToStack(cardsToSend, selectedRank);
                System.out.println("Stack size:" + getController().getStackCards().size());
                updateHandCardActors();
                updateOtherPlayers();
                updateStackCardActors();
                updateRankSelectBox();
            }
        });

        //Check Stack Button
        checkStackButton = new TextButton("Check Stack", skin);
        checkStackButton.setSize(300, 100);
        checkStackButton.setPosition(430, 20);

        checkStackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (getController().getStack().getCardsFromStack().isEmpty()) return;
                getController().checkStack();
                System.out.println("Stack size:" + getController().getStackCards().size());
                updateHandCardActors();
                updateOtherPlayers();
                updateStackCardActors();
                updateRankSelectBox();
            }
        });


        //Start Game Button

        startGameButton = getStartGameButton(skin);
        getStage().addActor(startGameButton);
        getStage().addActor(addToStackButton);
        getStage().addActor(checkStackButton);
        getStage().addActor(rankSelectBox);

        stackLabel = new Label("", skin);
        stackLabel.setPosition(getStage().getWidth() / 2 + 100, getStage().getHeight() / 2 - 13);
        if(getController().getStackCards() == null) {
            stackLabel.setText("0 N2");
        }

        getStage().addActor(stackLabel);

        gameOverWindow = getGameOverWindow();
        getStage().addActor(gameOverWindow);

    }

    private TextButton getStartGameButton(Skin skin) {
        TextButton startGameButton = new TextButton("Start Game", skin);
        startGameButton.setSize(300, 100);
        startGameButton.setPosition(1050, 20);

        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getController().startGame();
                startGameButton.remove();
                isGameStarted = true;
                updateHandCardActors();
            }
        });
        return startGameButton;
    }

    private void canUseButtons() {
        if(getController().isMyTurn() && isGameStarted) {
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

        if(getController().getPlayers().size() > 1) {
            startGameButton.setDisabled(false);
            startGameButton.setTouchable(Touchable.enabled);
        }
        else {
            startGameButton.setDisabled(true);
            startGameButton.setTouchable(Touchable.disabled);
        }
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

    public void updateGameOver() {
        getController().checkWinCondition();
        winner = getController().getWhoWon();
        if(Objects.equals(winner, "")){
            gameOverWindow.setVisible(false);
            System.out.println("Game not over");
            return;
        }
        System.out.println("Winner is " + winner);
        gameOverWindow.clear();
        gameOverWindow.add(new Label(winner + " won!", skin)).pad(5).row();
        gameOverWindow.setVisible(true);
        gameOverWindow.toFront();

        if(getController().getClass() == HostController.class) {
            TextButton newGameButton = new TextButton("New Game", skin);
            newGameButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    getController().resetGame();
                    gameOverWindow.setVisible(false);
                    updateHandCardActors();
                    updateOtherPlayers();
                    updateStackCardActors();
                    updateRankSelectBox();
                }
            });
            gameOverWindow.add(newGameButton).pad(5).row();
        }

    }

    //======================CARD ACTORS======================//

    public void updateStackCardActors() {
        boolean isStackEmpty = stackCardActors.isEmpty();

        // Usuń wszystkie istniejące CardActor ze stosu
        for (CardActor cardActor : stackCardActors) {
            cardActor.remove();
        }
        stackCardActors.clear();

        // Dodaj nowe CardActor do stosu
        for (int i = 0; i < getController().getStackCards().size(); i++) {
            CardActor cardActor = new CardActor();
            // Ustaw początkową pozycję kart na środku ekranu
            cardActor.setPosition(getStage().getWidth() / 2, getStage().getHeight() / 2);
            cardActor.toFront();
            stackCardActors.add(cardActor); // Dodaj do listy przed animacją
            getStage().addActor(cardActor);

            // Animacja ruchu na stos
            cardActor.addAction(Actions.moveTo(getStage().getWidth() / 2 - 70, getStage().getHeight() / 2 - 100, 0.5f, Interpolation.exp10));

            // Animacja pojawiania się kart
            if (!isStackEmpty) {
                cardActor.addAction(Actions.sequence(
                    Actions.delay(0.7f),
                    Actions.show()
                ));
            } else {
                cardActor.setVisible(true);
            }
        }

        stackLabel.setText(getController().getLastAddedCards() + " " + getController().getCurrentRank());
    }

    //=========================OTHER=========================//

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        canUseButtons();

        getStage().act(Gdx.graphics.getDeltaTime());
        getStage().draw();
    }

    @Override
    public void resize(int width, int height) {
        getStage().getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        getStage().dispose();
    }
}
