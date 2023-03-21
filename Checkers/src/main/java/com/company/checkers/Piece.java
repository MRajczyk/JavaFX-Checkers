package com.company.checkers;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import static com.company.checkers.CheckersApp.*;

public class Piece extends StackPane {
    private final PieceType type;

    private double mouseX, mouseY;
    private double oldX = 0, oldY = 0;
    private Boolean justKilled = false;

    private Boolean is_Queen = false;

    public Boolean isQueen() {
        return is_Queen;
    }
    public void promotePawn() {
        this.is_Queen = true;
    }

    public Boolean getJustKilled() {
        return justKilled;
    }
    public void setJustKilled(Boolean justKilled) {
        this.justKilled = justKilled;
    }

    public PieceType getType() {
        return type;
    }

    public double getOldX() {
        return oldX;
    }
    public double getOldY() {
        return oldY;
    }

    public Piece(PieceType type, int x, int y) {
        this.type = type;

        move(x, y);
        
        Ellipse ellipse = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.3125);
        ellipse.setFill(type == PieceType.RED ? Color.valueOf("#c40003") : Color.valueOf("#fff9f4"));
        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(TILE_SIZE * 0.03);
        ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);

        getChildren().add(ellipse);

        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            if(!(e.getSceneX() >= 0 && e.getSceneX() <= WIDTH * TILE_SIZE
                && e.getSceneY() >= 0 && e.getSceneY() <= HEIGHT * TILE_SIZE)) {
                relocate(oldX, oldY);
            }
            else {
                relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);
            }
        });
    }

    public void move(int x, int y) {
        oldX = x * TILE_SIZE;
        oldY = y * TILE_SIZE;
        if(!(x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT)) {
            return;
        }
        relocate(oldX, oldY);
        if(!this.isQueen() && y == 0 && this.getType() == PieceType.WHITE || !this.isQueen() && y == 7 && this.getType() == PieceType.RED) {
            this.promotePawn();
            getChildren().removeAll();

            Ellipse ellipse = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.3125);
            ellipse.setFill(type == PieceType.RED ? Color.valueOf("#690000") : Color.valueOf("#C6C6C6"));
            ellipse.setStroke(Color.BLACK);
            ellipse.setStrokeWidth(TILE_SIZE * 0.03);
            ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
            ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);

            getChildren().addAll(ellipse);
        }
    }

    public void abortMove() {
        relocate(oldX, oldY);
    }
}
