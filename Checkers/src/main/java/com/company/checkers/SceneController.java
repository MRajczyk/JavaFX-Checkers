package com.company.checkers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.net.URL;

public class SceneController {

    @FXML
    private ToggleButton local_lan_sw;
    @FXML
    private ToggleButton human_cpu;

    private Stage stage;
    private Scene homeScene;
    private Scene gameScene;
    private Scene gameOver;

    public Stage getStage() {
        return stage;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Scene getHomeScene() {
        return homeScene;
    }
    public void setHomeScene(Scene homeScene) {
        this.homeScene = homeScene;
        URL url = this.getClass().getResource("/styles.css");
        if (url == null) {
            System.out.println("Resource not found. Aborting.");
            System.exit(-1);
        }
        String css = url.toExternalForm();
        homeScene.getStylesheets().add(css);
    }

    public Scene getGameScene() {
        return gameScene;
    }
    public void setGameScene(Scene gameScene) {
        this.gameScene = gameScene;
    }

    public Scene getGameOver() {
        return gameOver;
    }
    public void setGameOver(Scene gameOver) {
        this.gameOver = gameOver;
    }

    public void changeToGame() {
        stage.setScene(gameScene);
        stage.show();
    }

    public void changeToHomeScene() {
        stage.setScene(homeScene);
        stage.show();
    }

    public void changeToGameOver() {
        stage.setScene(gameOver);
        stage.show();
    }
}
