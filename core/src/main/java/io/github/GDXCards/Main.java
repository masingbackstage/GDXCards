package io.github.GDXCards;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.GDXCards.UIUtilities.StartMenuScreen;

import java.util.Random;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    private Stage stage;

    @Override
    public void create() {
        stage = new Stage(new FitViewport(1920, 1080));
        this.setScreen(new StartMenuScreen(this));
        Gdx.input.setInputProcessor(stage);

    }

    public Stage getStage() {
        return stage;
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
