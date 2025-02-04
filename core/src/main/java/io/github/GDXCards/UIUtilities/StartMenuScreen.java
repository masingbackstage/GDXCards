package io.github.GDXCards.UIUtilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.GDXCards.Main;
import io.github.GDXCards.Network.GameClient;
import io.github.GDXCards.Network.GameInstance;
import io.github.GDXCards.Network.GameServer;

import java.io.IOException;

public class StartMenuScreen implements Screen {
    private final Stage stage;
    private final Main main;


    public StartMenuScreen(Main main) {
        this.main = main;
        this.stage = main.getStage();
        createMenuUI();
    }

    private void createMenuUI() {
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton serverButton = new TextButton("Start as Server", skin);
        serverButton.setSize(300, 100);
        serverButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)  {
                System.out.println("Server button pressed");
                try {
                    GameInstance serverInstance = new GameServer(main);
                    main.setGameInstance(serverInstance);
                    main.getGameController().setGameInstance(serverInstance);
                    main.setScreen(new GameScreen(stage, main.getGameController(), main.getPlayer(), serverInstance));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        TextButton clientButton = new TextButton("Join as Client", skin);
        clientButton.setSize(300, 100);
        clientButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)  {
                System.out.println("Client button pressed");
                try {
                    GameInstance clientInstance = new GameClient(main);
                    main.setGameInstance(clientInstance);
                    main.getGameController().setGameInstance(clientInstance);
                    main.setScreen(new GameScreen(stage, main.getGameController(), main.getPlayer(), clientInstance));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        table.add(serverButton).pad(10).row();
        table.add(clientButton).pad(10);
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
}
