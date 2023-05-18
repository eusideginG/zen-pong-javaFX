package com.example.game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Bricks extends Rectangle {
    private int hitPoints = 1;

    public Bricks(double width, double height) {
        this.setWidth(width);
        this.setHeight(height);
        this.setFill(Color.web("#EBA344"));
        this.setArcWidth(10);
        this.setArcHeight(10);
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
        if (this.hitPoints == 0) this.setVisible(false);
    }

    public void dmgTaken() {
        setHitPoints(getHitPoints() - 1);
    }

}
