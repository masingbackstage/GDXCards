package io.github.GDXCards.UIUtilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.GDXCards.Main;
import io.github.GDXCards.Network.ClientServerInstance;
import io.github.GDXCards.Network.ServerInstance;
import io.github.GDXCards.Network.HostServerInstance;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class StartMenuScreen implements Screen {
    private final Stage stage;
    private final Main main;
    private String name;
    private String ipAddress;
    private TextField nameInputField;
    private TextField ipInputField;
    private Window nameInputWindow;
    private Label ipLabel;
    private String errorMessage;
    private Skin skin;
    private Table mainTable;
    private final int buttonWidth;

    public StartMenuScreen(Main main) {
        this.main = main;
        this.stage = main.getStage();
        buttonWidth = 350;
        setBackground();
        createMenuUI();
    }

    private void createMenuUI() {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        TextButton serverButton = new TextButton("Start as Server", skin);
        TextButton clientButton = new TextButton("Join as Client", skin);
        TextButton quitButton = new TextButton("Quit", skin);

        serverButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Server button pressed");
                    showNameInputField("server");
                }
            });

        clientButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Client button pressed");
                    showNameInputField("client");
                }
            });

        quitButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Quit button pressed");
                    Gdx.app.exit();
                }
            });

        mainTable.add(serverButton).width(buttonWidth).height(50).pad(10).row();
        mainTable.add(clientButton).width(buttonWidth).height(50).pad(10).row();
        mainTable.add(quitButton).width(buttonWidth).height(50).pad(40);
    }

    private void showNameInputField(String role) {
        if (nameInputWindow != null) {
            nameInputWindow.remove();
        }
        nameInputWindow = new Window("Enter Name", skin);
        nameInputWindow.setSize(500, 400);

        nameInputWindow.setPosition(
            (stage.getWidth() - nameInputWindow.getWidth()) / 2,
            (stage.getHeight() - nameInputWindow.getHeight()) / 2);
        stage.addActor(nameInputWindow);

        nameInputField = new TextField("", skin);
        nameInputField.setMaxLength(20);
        nameInputField.setStyle(new TextField.TextFieldStyle(skin.get(TextField.TextFieldStyle.class)));

        TextButton confirmButton = new TextButton("Confirm", skin);
        confirmButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    name = nameInputField.getText();
                    if (name == null || name.isEmpty()) {
                        setErrorMessage("Please enter a name.");
                        return;
                    }

                    System.out.println("Player name: " + name);
                    if (role.equals("server")) {
                        try {
                            ServerInstance serverInstance = new HostServerInstance(main, name);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (role.equals("client")) {
                        ipAddress = ipInputField.getText();
                        System.out.println("Client connecting to: " + ipAddress);
                        new ClientServerInstance(main, name, ipAddress);
                    }
                    nameInputWindow.remove();
                }
            });

        nameInputWindow.add(new Label("Enter your name:", skin)).pad(5).row();
        nameInputWindow.add(nameInputField).width(buttonWidth).height(50).pad(5).row();

        if (role.equals("client")) {
            ipInputField = new TextField("", skin);
            ipInputField.setMaxLength(15);
            nameInputWindow.add(new Label("Enter Server IP:", skin)).pad(5).row();
            nameInputWindow.add(ipInputField).width(buttonWidth).height(50).pad(5).row();
        }
        if (role.equals("server")) {
            try {
                InetAddress localHost = InetAddress.getLocalHost();
                ipAddress = localHost.getHostAddress();
                System.out.println("Server IP: " + ipAddress);

                if (ipLabel != null) {
                    ipLabel.setText("Server IP: " + ipAddress);
                } else {
                    ipLabel = new Label("Server IP: " + ipAddress, skin);
                    nameInputWindow.add(ipLabel).width(buttonWidth).height(50).pad(5).row();
                }
            } catch (UnknownHostException e) {
                System.err.println("Unable to get IP address: " + e.getMessage());
            }
        }
        nameInputWindow.add(confirmButton).width(buttonWidth).height(50).pad(5);
    }

    void setBackground() {
        Texture backgroundTexture = new Texture(Gdx.files.internal("ui/panorama_3.png"));
        Image background = new Image(backgroundTexture);

        background.setSize(stage.getWidth(), stage.getHeight());

        stage.addActor(background);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        if (errorMessage != null && !errorMessage.isEmpty()) {
            showErrorDialog(errorMessage);
            errorMessage = null;
        }
    }

    private void showErrorDialog(String message) {
        Dialog.WindowStyle windowStyle = skin.get("dialog", Dialog.WindowStyle.class);

        windowStyle.stageBackground = null;

        Dialog dialog =
            new Dialog("Error", skin, "dialog") {
                @Override
                protected void result(Object object) {
                    this.remove();
                }
            };
        dialog.text(message);
        dialog.button("OK", true);
        dialog.key(com.badlogic.gdx.Input.Keys.ENTER, true);
        dialog.show(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
