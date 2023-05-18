package com.example.game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class MenuController {
    private Stage stage;
    @FXML
    TextField nameTextField;

    public void changeSceneMenuToPlay() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-game.fxml"));

        Parent root = loader.load();

        Properties properties = new Properties();
        FileInputStream inputFile = new FileInputStream("src/main/properties/PlayerInfo.properties");
        properties.load(inputFile);

        String name = nameTextField.getText();
        String infoName = properties.getProperty("username");

        if (!name.isEmpty() || Objects.equals(infoName, null)) {
            FileOutputStream file = new FileOutputStream("src/main/properties/PlayerInfo.properties");
            //set properties
            properties.setProperty("username", name);
            properties.setProperty("score", "0");
            //save file
            properties.store(file, "Player info");
            //read and pass file data
            properties.load(inputFile);
            String uName = properties.getProperty("username");
            String uScore = properties.getProperty("score");
            //check userdata and pass them to main game scene
            if (!uName.isEmpty() && !uScore.isEmpty()) {
                MainGameController MainGameController = loader.getController();
                MainGameController.setData(uName, uScore);
            }
            //close files
            inputFile.close();
            file.close();
        } else {
            String uName = properties.getProperty("username");
            String uScore = properties.getProperty("score");

            MainGameController MainGameController = loader.getController();
            MainGameController.setData(uName, uScore);
        }

        Scene scene = new Scene(root);

        MainGameController mainGameController = loader.getController();

        //stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage = StageManager.getInstance().getStage();

        mainGameController.keyListeners(scene);
        mainGameController.setScene(scene);


        stage.setScene(scene);
        scene.getRoot().requestFocus();
        stage.show();

    }

    public void exitGame(ActionEvent e) {
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.close();
    }
}