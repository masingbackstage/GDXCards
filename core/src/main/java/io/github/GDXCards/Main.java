package io.github.GDXCards;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.GDXCards.UIUtilities.StartMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    private Stage stage;
    private StartMenuScreen startMenuScreen;

    @Override
    public void create() {

        stage = new Stage(new FitViewport(1920, 1080));
        startMenuScreen = new StartMenuScreen(this);
        this.setScreen(startMenuScreen);
        Gdx.input.setInputProcessor(stage);

    }

    public Stage getStage() {
        return stage;
    }

    public StartMenuScreen getStartMenuScreen() {
        return startMenuScreen;
    }

}
