package com.example.game;

import javafx.stage.Stage;

public class StageManager {
    private static StageManager instance = null;
    private Stage stage;

    private StageManager() {
    }

    public static StageManager getInstance() {
        if (instance == null) {
            instance = new StageManager();
        }
        return instance;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
