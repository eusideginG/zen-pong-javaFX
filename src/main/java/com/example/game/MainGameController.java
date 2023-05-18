package com.example.game;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;

public class MainGameController implements Initializable {
    // FXML injection
    @FXML
    Rectangle bar;
    @FXML
    Circle ball;
    @FXML
    Pane pane;
    @FXML
    Label gameOve_youWin_label, username, score;
    @FXML
    Button playAgainBtn;
    @FXML
    Separator separator;

    // variables
    int columnOfBricks = 8, rowsOfBricks = 4, brickWidth = 100, brickHeight = 50, points = 0, totalBricks;
    boolean isGameRunning = false, isLeftPressed = false, isRightPressed = false, ballGoingUp = true, hasLost = false, youWin = false, ballGoingRight = false, ballGoingLeft = false;
    double ballSpeed = 5.0, ballMovementX = 1.0, ballMovementY = 1.0, barMovement = 1.0, barAcceleration = 0.0, topBar = 50.0;
    double topBorder, bottomBorder, rightBorder, leftBorder, leftBarBorder, rightBarBorder;
    Stage stage;
    Scene scene;
    // objects
    Random random = new Random();
    Properties properties = new Properties();
    AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long l) {
        try {
            update();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        }
    };
    Bricks[][] brick = new Bricks[rowsOfBricks][columnOfBricks];

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            setInitialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // initialize (reusable)
    public void setInitialize() throws IOException {
        // get stage
        stage = StageManager.getInstance().getStage();
        // add resize listener
        stage.widthProperty().addListener(resizeListener);
        stage.heightProperty().addListener(resizeListener);
        // start animation timer
        animationTimer.start();
        // draw bricks
        drawBricks();
        // set total brick
        totalBricks = rowsOfBricks * columnOfBricks;
        // set score and name
        score.toFront();
        username.toFront();
        // resize stage to update the frame
        stage.setWidth((brickWidth * columnOfBricks) + 0.0001);
        // set separator layout Y
        separator.setLayoutY(topBar - 5);
        // set point
        points = Integer.parseInt(readProperties());
        // align gameOve_youWin_label
        gameOve_youWin_label.setLayoutX((stage.getWidth() - gameOve_youWin_label.getWidth()) / 2);
        gameOve_youWin_label.setLayoutY((stage.getHeight() / 2) - gameOve_youWin_label.getHeight());
        // center the bar
        bar.setX((stage.getWidth() - bar.getWidth()) / 2);
        bar.setY(stage.getHeight() - (bar.getHeight() * 4));
        // center the ball
        ball.setCenterX(stage.getWidth() / 2);
        ball.setCenterY(stage.getHeight() - ((ball.getRadius() * 4) + bar.getHeight()));
    }
    // draw bricks
    public void drawBricks() {
        for (int row = 0; row < rowsOfBricks; ++row) {
            for (int column = 0; column < columnOfBricks; ++column) {
                brick[row][column] = new Bricks(brickWidth, brickHeight);
                brick[row][column].setLayoutX(column * brick[row][column].getWidth());
                brick[row][column].setLayoutY((row * brick[row][column].getHeight()) + topBar);
                brick[row][column].setStroke(Color.BLACK);
                pane.getChildren().addAll(brick[row][column]);
            }
        }
    }
    // delete bricks
    public void deleteBricks() {
        for (int row = 0; row < rowsOfBricks; ++row) {
            for (int column = 0; column < columnOfBricks; ++column) {
                pane.getChildren().removeAll(brick[row][column]);
            }
        }
    }
    // set scene
    public void setScene(Scene s) {
        this.scene = s;
        keyListeners(scene);
    }

    // set scene key listener
    public void keyListeners(Scene scene) {
        // set on key pressed listener
        scene.setOnKeyPressed(press);
        // set on key released listener
        scene.setOnKeyReleased(release);
    }

    // set on key pressed listener
    EventHandler<KeyEvent> press = event -> {
        switch (event.getCode()) {
            case RIGHT -> isRightPressed = true;
            case LEFT -> isLeftPressed = true;
            case SPACE -> isGameRunning = true;
        }
    };
    // set on key released listener
    EventHandler<KeyEvent> release = event -> {
        switch (event.getCode()) {
            case RIGHT -> isRightPressed = false;
            case LEFT -> isLeftPressed = false;
        }
    };

    // center the bar and ball on window resize
    ChangeListener<Number> resizeListener = (observable, oldValue, newValue) -> {
        // center the bar
        bar.setX((stage.getWidth() - bar.getWidth()) / 2);
        bar.setY(stage.getHeight() - (bar.getHeight() * 4));
        // center the ball
        ball.setCenterX(stage.getWidth() / 2);
        ball.setCenterY(stage.getHeight() - ((ball.getRadius() * 4) + bar.getHeight()));
        // center gameOve_youWin_label
        gameOve_youWin_label.setLayoutX((stage.getWidth() - gameOve_youWin_label.getWidth()) / 2);
        gameOve_youWin_label.setLayoutY((stage.getHeight() / 2) - gameOve_youWin_label.getHeight());
        //set bar acceleration
        barAcceleration = stage.getWidth() / 100;
        //set bar length
        bar.setWidth(stage.getWidth() / 6);
        // set and resize borders
        setBorders(stage);
        // resize bricks
        brickWidth = (int) stage.getWidth() / columnOfBricks;
        for (int row = 0; row <= rowsOfBricks - 1; row++) {
            for (int column = 0; column <= columnOfBricks - 1; column++) {
                brick[row][column].setWidth(brickWidth);
                brick[row][column].setLayoutX(column * brick[row][column].getWidth());
            }
        }
        // play again btn resize
        playAgainBtn.setLayoutX((stage.getWidth() - playAgainBtn.getWidth()) / 2);
        playAgainBtn.setLayoutY(stage.getHeight() - (playAgainBtn.getHeight() * 3));
        // change username and score layout
        score.setLayoutX(10);
        score.setLayoutY(0);
        username.setLayoutX((stage.getWidth() - username.getWidth()) - 20);
        username.setLayoutY(0);
        // resize separator
        separator.setPrefWidth(stage.getWidth());
    };

    // set borders
    public void setBorders(Stage stage) {
        // set ball border
        topBorder = ball.getRadius() + topBar;
        bottomBorder = stage.getHeight() - ball.getRadius();
        rightBorder = stage.getWidth() - ball.getRadius();
        leftBorder = ball.getRadius();
        // set bar border
        leftBarBorder = 0;
        rightBarBorder = stage.getWidth() - (bar.getWidth() * 1.15);
    }

    // set user data
    public void setData(String n, String s) {
        username.setText(n);
        score.setText("Score: " + s);
    }

    // update is called every frame
    public void update() throws IOException {
        if (isGameRunning) {
            ballMovement();
            barMovement();
        }
    }

    public void ballMovement() throws IOException {
        // move ball in Y axis
        if (ballGoingUp) ball.setCenterY(ball.getCenterY() - (ballMovementY * ballSpeed));
        else ball.setCenterY(ball.getCenterY() + (ballMovementY * ballSpeed));

        // move ball in X axis
        if (ballGoingLeft) ball.setCenterX(ball.getCenterX() - (ballMovementX * ballSpeed));
        else if (ballGoingRight) ball.setCenterX(ball.getCenterX() + (ballMovementX * ballSpeed));

        // checking Y ball borders
        for (int row = 0; row <= rowsOfBricks - 1; row++) {
            for (int column = 0; column <= columnOfBricks - 1; column++) {
                if (ball.intersects(brick[row][column].getBoundsInParent())) {
                    ballGoingUp = false;
                    brick[row][column].dmgTaken();
                    score.setText("Score: " + (points += 100));
                    brick[row][column].setY(-(brick[row][column].getLayoutY() + (brick[row][column].getHeight() * 2)));
                    totalBricks--;
                }
            }
        }
        // check if no brick left
        if (totalBricks <= 0) {
            youWin = true;
            isGameRunning = false;
            gameOve_youWin_label.setText("YOU WIN");
            gameOve_youWin_label.setLayoutX((stage.getWidth() - gameOve_youWin_label.getWidth()) / 2);
            gameOve_youWin_label.setVisible(true);
            animationTimer.stop();
            if (Integer.parseInt(readProperties()) < points) {
                writeProperties(points);
            }

            playAgainBtn.setVisible(true);
            playAgainBtn.setDisable(false);
        }

        if (ball.getCenterY() <= topBorder) ballGoingUp = false;
        else if (ball.intersects(bar.getBoundsInParent())) {
            ballGoingUp = true;
            // ball X movement on bar touch
            if (isRightPressed) ballGoingRight = true;
            else if (isLeftPressed) ballGoingLeft = true;
            else {
                if (random.nextInt(2) == 0) ballGoingRight = true;
                else ballGoingLeft = true;
            }
        }

        // checking X ball borders
        if (ball.getCenterX() - ball.getRadius() <= leftBorder) {
            ballGoingLeft = false;
            ballGoingRight = true;
        } else if (ball.getCenterX() + ball.getRadius() >= rightBorder) {
            ballGoingLeft = true;
            ballGoingRight = false;
        }

        // check game over
        if (ball.getCenterY() > bottomBorder) {
            hasLost = true;
            isGameRunning = false;
            gameOve_youWin_label.setText("GAME OVER");
            gameOve_youWin_label.setLayoutX((stage.getWidth() - gameOve_youWin_label.getWidth()) / 2);
            gameOve_youWin_label.setVisible(true);
            animationTimer.stop();
            writeProperties(0);

            playAgainBtn.setVisible(true);
            playAgainBtn.setDisable(false);
        }
    }

    public void barMovement() {
        if (isRightPressed) {
            if (!(bar.getX() >= rightBarBorder)) bar.setX(bar.getX() + (barMovement * barAcceleration));
        }
        if (isLeftPressed) {
            if (!(bar.getX() <= leftBarBorder)) bar.setX(bar.getX() - (barMovement * barAcceleration));
        }
    }

    public void playAgain(ActionEvent event) throws IOException {
        // set scene focus so the root pane can listen to events
        ((Node)event.getSource()).getScene().getRoot().requestFocus();
        // reset variables
        hasLost = false;
        gameOve_youWin_label.setVisible(false);
        playAgainBtn.setVisible(false);
        playAgainBtn.setDisable(true);
        score.setText("Score: " + readProperties());
        // delete bricks
        deleteBricks();
        // reinitialize
        setInitialize();
    }

    public String readProperties() throws IOException {
        FileInputStream inputStream = new FileInputStream("src/main/properties/PlayerInfo.properties");
        properties.load(inputStream);
        inputStream.close();
        return properties.getProperty("score");
    }
    public void writeProperties(int s) throws IOException {
        FileOutputStream outputStream = new FileOutputStream("src/main/properties/PlayerInfo.properties");
        properties.setProperty("score", Integer.toString(s));
        properties.store(outputStream, "New HighScore");
    }

}
