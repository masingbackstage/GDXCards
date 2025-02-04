package io.github.GDXCards;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.GDXCards.GameUtilities.Person;
import io.github.GDXCards.GameUtilities.Player;
import io.github.GDXCards.Network.GameInstance;
import io.github.GDXCards.UIUtilities.StartMenuScreen;

import java.util.Random;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    private Stage stage;
    private GameController gameController;
    private Player player;
    private GameInstance gameInstance;

    @Override
    public void create() {
        stage = new Stage(new FitViewport(1920, 1080));
        this.setScreen(new StartMenuScreen(this));
        Gdx.input.setInputProcessor(stage);
        player = new Person(randomName());
        System.out.println("Player: " + player.getName());
        gameController = new GameController(gameInstance);
    }

    public Stage getStage() {
        return stage;
    }

    public GameController getGameController() {
        return gameController;
    }

    public Player getPlayer() {
        return player;
    }

    public void setGameInstance(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
    }

    private String randomName() {
        Random ran = new Random();
        int top = 3;
        char data = ' ';
        String dat = "";

        for (int i=0; i<=top; i++) {
            data = (char)(ran.nextInt(25)+97);
            dat = data + dat;
        }
        return dat;
    }
}
