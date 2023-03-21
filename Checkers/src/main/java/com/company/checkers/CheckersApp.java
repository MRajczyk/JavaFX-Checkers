package com.company.checkers;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

public class CheckersApp extends Application {

    public static final int TILE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private Tile[][] board;

    private Group tileGroup;
    private Group pieceGroup;
    private ToMove toMove;
    private Timer timeForMultikill;
    private Boolean killHappened;
    private Boolean thisPC;
    private Timer moveTimer;
    private int seconds;
    private Boolean pvp;
    Text time;
    SceneController sceneController = new SceneController();

    private void startTimer() {
        moveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time.setText(String.valueOf(seconds));
                seconds++;
            }
        }, 0, 1000);
    }

    /**
     *  Klasa tworzy tiles'y na pola(te o parzystej sumie współrzędnych są jasne) oraz pionki - na ciemnych polach, czarne na y<=2 a białe na y>=5
     */
    private Parent createContent() {
        moveTimer = new Timer();
        board = new Tile[WIDTH][HEIGHT];
        tileGroup = new Group();
        pieceGroup = new Group();
        toMove = ToMove.WHITE;
        timeForMultikill = new Timer();
        killHappened = false;
        thisPC = true;
        pvp = true;

        Pane root = new Pane();
        root.setStyle("-fx-background-color: #F3E479;");
        root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        root.getChildren().addAll(tileGroup, pieceGroup);

        for(int y = 0; y < HEIGHT; ++y) {
            for(int x = 0; x < WIDTH; ++x) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;
                tileGroup.getChildren().add(tile);

                Piece piece = null;
                if(y <= 2 && ((x + y) % 2 != 0)){
                    piece = makePiece(PieceType.RED, x, y);
                }
                if(y >= 5 && ((x + y) % 2 != 0)){
                    piece = makePiece(PieceType.WHITE, x, y);
                }

                if(piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }

        VBox vBox = new VBox();
        vBox.setPrefSize(200, 800);
        vBox.setAlignment(Pos.CENTER);
        vBox.setLayoutX(800);
        root.getChildren().add(vBox);

        time = new Text(50, 100, "0");
        time.setStyle("-fx-font-size: 50px;");
        vBox.getChildren().add(time);

        return root;
    }

    private Parent createGameOver(String whoWon) {
        Pane root = new Pane();
        root.setPrefSize(1000, 800);
        root.setStyle("-fx-background-color: #FF8E87;");

        VBox vBox = new VBox();
        vBox.setPrefSize(1000, 800);
        vBox.setAlignment(Pos.CENTER);
        root.getChildren().add(vBox);

        Text text = new Text(500, 400, "GAME OVER, " + whoWon + " won the round!");
        text.setStyle("-fx-font-size: 50px;");
        vBox.getChildren().add(text);

        Button button = new Button();
        button.setText("New Game");
        button.setStyle("-fx-pref-width: 100; -fx-pref-height: 50");
        vBox.getChildren().add(button);

        button.setOnAction(e -> sceneController.changeToHomeScene());

        return root;
    }

    private Parent createHomeScreen() {
        Pane root = new Pane();
        root.setPrefSize(1000, 800);
        root.setStyle("-fx-background-color: #F3E479;");

        VBox vBox = new VBox();
        vBox.setMinSize(1000, 800);
        vBox.setAlignment(Pos.CENTER);
        root.getChildren().add(vBox);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
        vBox.getChildren().add(gridPane);
        for(int i = 0; i < 3; ++i) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(100));
        }

        for(int i = 0; i < 3; ++i) {
            gridPane.getRowConstraints().add(new RowConstraints(30));
        }


        Label textThisPC = new Label("This PC");
        gridPane.add(textThisPC, 0, 0, 1, 1);
        GridPane.setHalignment(textThisPC, HPos.CENTER);

        Text text_Lan = new Text("LAN");
        gridPane.add(text_Lan, 2, 0, 1, 1);
        GridPane.setHalignment(text_Lan, HPos.CENTER);

        ToggleButton button_toggle_conn = new ToggleButton();
        button_toggle_conn.setText("");
        button_toggle_conn.getStyleClass().add("toggle_button");
        gridPane.add(button_toggle_conn, 1, 0, 1, 1);
        GridPane.setHalignment(button_toggle_conn, HPos.CENTER);
        button_toggle_conn.setOnAction(e -> {
            thisPC = !thisPC;
            //debug purpose System.out.println(thisPC);
        });

        Text text_vsHuman = new Text("vs Human");
        gridPane.add(text_vsHuman, 0, 1, 1, 1);
        GridPane.setHalignment(text_vsHuman, HPos.CENTER);

        Text text_vsComputer = new Text("vs Computer");
        gridPane.add(text_vsComputer, 2, 1, 1, 1);
        GridPane.setHalignment(text_vsComputer, HPos.CENTER);

        ToggleButton button_toggle_opp = new ToggleButton();
        button_toggle_opp.setText("");
        button_toggle_opp.getStyleClass().add("toggle_button");
        gridPane.add(button_toggle_opp, 1, 1, 1, 1);
        GridPane.setHalignment(button_toggle_opp, HPos.CENTER);
        button_toggle_opp.setOnAction(e -> {
            pvp = !pvp;
            //debug purpose System.out.println(pvp);
        });

        Button buttonNewGame = new Button();
        buttonNewGame.setText("New Game");
        buttonNewGame.setStyle("-fx-pref-width: 300; -fx-pref-height: 30");
        gridPane.add(buttonNewGame, 0, 3, 3, 1);
        GridPane.setHalignment(buttonNewGame, HPos.CENTER);

        buttonNewGame.setOnAction(e -> {
            sceneController.changeToGame();
            seconds = 0;
            startTimer();
        });

        return root;
    }

    /**
     *  translacja z px na indeksy pól
     */
    private int toBoard(double pixel) {
        //debugSystem.out.println((int)((pixel + TILE_SIZE / 2) / TILE_SIZE));
        return (int)((pixel + TILE_SIZE / 2) / TILE_SIZE);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setResizable(false);
        sceneController.setStage(primaryStage);
        sceneController.setHomeScene(new Scene(createHomeScreen(), 1000, 800));
        sceneController.setGameScene(new Scene(createContent(), 1000, 800));
        primaryStage.setTitle("WARCABKI");
        primaryStage.setScene(sceneController.getHomeScene());
        primaryStage.show();
    }

    private MoveResult tryMove(Piece piece, int newX, int newY) {
        if(killHappened && !piece.getJustKilled())
            return new MoveResult(MoveType.NONE);

        if(board[newX][newY].hasPiece() || (newX + newY) % 2 == 0) {
            return new MoveResult(MoveType.NONE);
        }

        int x0 = toBoard(piece.getOldX());
        int y0 = toBoard(piece.getOldY());

        if(!(piece.getType() == PieceType.WHITE) && toMove == ToMove.WHITE || (piece.getType() == PieceType.WHITE) && !(toMove == ToMove.WHITE)) {
            return new MoveResult(MoveType.NONE);
        }

        //if queen and move by one square its a normal move, already checked if its free in the first line in this class
            // and if a normal pawn check if it gone the right way as its stored in type
        if(!killHappened && piece.isQueen() && Math.abs(newX - x0) == 1 || !killHappened && !piece.isQueen() && Math.abs(newX - x0) == 1 && newY - y0 == piece.getType().moveDir) {
            return new MoveResult(MoveType.NORMAL);
        }
        else if(Math.abs(newX - x0) == 2 && newY - y0 == piece.getType().moveDir * 2 || piece.isQueen() && Math.abs(newX - x0) == 2) {
            int x1 = x0 + (newX - x0) / 2;
            int y1 = y0 + (newY - y0) / 2;

            if(board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
                return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
            }
        }

        return new MoveResult(MoveType.NONE);
    }

    private Piece makePiece(PieceType type, int x, int y) throws IllegalStateException{
        Piece piece = new Piece(type, x, y);

        piece.setOnMouseReleased(e -> {
            int newX = toBoard(piece.getLayoutX());
            int newY = toBoard(piece.getLayoutY());

            MoveResult result = tryMove(piece, newX, newY);
            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());

            switch (result.getType()) {
                case NONE -> piece.abortMove();
                case NORMAL -> {
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    toMove = (toMove == ToMove.WHITE) ? ToMove.RED : ToMove.WHITE;
                    timeForMultikill.cancel();
                    piece.setJustKilled(false);
                    killHappened = false;
                    moveTimer.cancel();
                    moveTimer = new Timer();
                    seconds = 0;
                    time.setText(String.valueOf(seconds));
                    startTimer();
                    if(!isMovePossible()) {
                        sceneController.setGameOver(new Scene(createGameOver(toMove == ToMove.WHITE ? "RED" : "WHITE"), 1000, 800));
                        sceneController.setHomeScene(new Scene(createHomeScreen(), 1000, 800));
                        sceneController.setGameScene(new Scene(createContent(), 1000, 800));
                        sceneController.changeToGameOver();
                    }
                }
                case KILL -> {
                    timeForMultikill.cancel();
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    Piece otherPiece = result.getPiece();
                    board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                    pieceGroup.getChildren().remove(otherPiece);
                    //rozpoczecie taska z czekaniem i pozwolenie na wykonanie multikilla
                    scheduleMultikillTime(piece);
                }
            }
        });
        return piece;
    }

    private void scheduleMultikillTime(Piece piece) {
        if(canKill(piece)) {
            //System.out.println("You can kill something, timer starting. . ."); //debug purpose
            piece.setJustKilled(true);
            killHappened = true;
            timeForMultikill.cancel();
            timeForMultikill = new Timer();
            timeForMultikill.schedule(new TimerTask() {
                final ToMove temp = toMove;
                @Override
                public void run() {
                    System.out.println("Time's up");
                    toMove = (temp == ToMove.WHITE) ? ToMove.RED : ToMove.WHITE;
                    piece.setJustKilled(false);
                    killHappened = false;
                    moveTimer.cancel();
                    moveTimer = new Timer();
                    seconds = 0;
                    time.setText(String.valueOf(seconds));
                    startTimer();
                    if(!isMovePossible()) {
                        sceneController.setGameOver(new Scene(createGameOver(toMove == ToMove.WHITE ? "RED" : "WHITE"), 1000, 800));
                        sceneController.setHomeScene(new Scene(createHomeScreen(), 1000, 800));
                        sceneController.setGameScene(new Scene(createContent(), 1000, 800));
                        sceneController.changeToGameOver();
                    }
                }
            }, 3 * 1000);
        }
        else {
            toMove = (toMove == ToMove.WHITE) ? ToMove.RED : ToMove.WHITE;
            timeForMultikill.cancel();
            piece.setJustKilled(false);
            killHappened = false;
            moveTimer.cancel();
            moveTimer = new Timer();
            seconds = 0;
            time.setText(String.valueOf(seconds));
            startTimer();
            if(!isMovePossible()) {
                sceneController.setGameOver(new Scene(createGameOver(toMove == ToMove.WHITE ? "RED" : "WHITE"), 1000, 800));
                sceneController.setHomeScene(new Scene(createHomeScreen(), 1000, 800));
                sceneController.setGameScene(new Scene(createContent(), 1000, 800));
                sceneController.changeToGameOver();
            }
        }
    }

    private Boolean canKill(Piece piece) {
        if(piece.isQueen()) {
            if(piece.getOldY() / TILE_SIZE >= 0 && piece.getOldY() / TILE_SIZE <= 1) {
                if(piece.getOldX() / TILE_SIZE == 0 || piece.getOldX() / TILE_SIZE == 1) {
                    return board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE + 2)][(int) (piece.getOldY() / TILE_SIZE + 2)].hasPiece();
                }
                else if(piece.getOldX() / TILE_SIZE == 7 || piece.getOldX() / TILE_SIZE == 6) {
                    return board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE - 2)][(int) (piece.getOldY() / TILE_SIZE + 2)].hasPiece();
                }
                else if(piece.getOldX() / TILE_SIZE < 6 && piece.getOldX() / TILE_SIZE > 1) {
                    return board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE + 2)][(int) (piece.getOldY() / TILE_SIZE + 2)].hasPiece()
                            ||
                            board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].hasPiece()
                                    && board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].getPiece().getType() != piece.getType()
                                    && !board[(int) (piece.getOldX() / TILE_SIZE - 2)][(int) (piece.getOldY() / TILE_SIZE + 2)].hasPiece();
                }
                else
                    return false;
            }
            else if(piece.getOldY() / TILE_SIZE >= 2 && piece.getOldY() / TILE_SIZE <= 5) {
                if(piece.getOldX() / TILE_SIZE == 0 || piece.getOldX() / TILE_SIZE == 1) {
                    return board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE + 2)][(int) (piece.getOldY() / TILE_SIZE + 2)].hasPiece()
                            ||
                            board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].hasPiece()
                                    && board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].getPiece().getType() != piece.getType()
                                    && !board[(int) (piece.getOldX() / TILE_SIZE + 2)][(int) (piece.getOldY() / TILE_SIZE - 2)].hasPiece();
                }
                else if(piece.getOldX() / TILE_SIZE == 7 || piece.getOldX() / TILE_SIZE == 6) {
                    return board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE - 2)][(int) (piece.getOldY() / TILE_SIZE - 2)].hasPiece()
                            ||
                            board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].hasPiece()
                                    && board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].getPiece().getType() != piece.getType()
                                    && !board[(int) (piece.getOldX() / TILE_SIZE - 2)][(int) (piece.getOldY() / TILE_SIZE + 2)].hasPiece();
                }
                else if(piece.getOldX() / TILE_SIZE < 6 && piece.getOldX() / TILE_SIZE > 1) {
                    return (board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE + 2)][(int) (piece.getOldY() / TILE_SIZE + 2)].hasPiece())
                            ||
                            (board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].hasPiece()
                                    && board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].getPiece().getType() != piece.getType()
                                    && !board[(int) (piece.getOldX() / TILE_SIZE - 2)][(int) (piece.getOldY() / TILE_SIZE + 2)].hasPiece())
                            ||
                            (board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].hasPiece()
                                    && board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].getPiece().getType() != piece.getType()
                                    && !board[(int) (piece.getOldX() / TILE_SIZE + 2)][(int) (piece.getOldY() / TILE_SIZE - 2)].hasPiece())
                            ||
                            (board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].hasPiece()
                                    && board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].getPiece().getType() != piece.getType()
                                    && !board[(int) (piece.getOldX() / TILE_SIZE - 2)][(int) (piece.getOldY() / TILE_SIZE - 2)].hasPiece());
                }
                else
                    return false;
            }
            else if(piece.getOldY() / TILE_SIZE >= 6 && piece.getOldY() / TILE_SIZE <= 7){
                if(piece.getOldX() / TILE_SIZE == 0 || piece.getOldX() / TILE_SIZE == 1) {
                    return board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE + 2)][(int) (piece.getOldY() / TILE_SIZE - 2)].hasPiece();
                }
                else if(piece.getOldX() / TILE_SIZE == 7 || piece.getOldX() / TILE_SIZE == 6) {
                    return board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE - 2)][(int) (piece.getOldY() / TILE_SIZE - 2)].hasPiece();
                }
                else if(piece.getOldX() / TILE_SIZE < 6 && piece.getOldX() / TILE_SIZE > 1) {
                    return (board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE + 2)][(int) (piece.getOldY() / TILE_SIZE - 2)].hasPiece())
                            ||
                            (board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE - 2)][(int) (piece.getOldY() / TILE_SIZE - 2)].hasPiece());
                }
                else
                    return false;
            }
            else {
                return false;
            }
        }
        else {
            if (piece.getType() == PieceType.WHITE) {
                if (piece.getOldY() / TILE_SIZE < 2)
                    return false;
                if (piece.getOldX() / TILE_SIZE < 2) {
                    return board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE + 2)][(int) (piece.getOldY() / TILE_SIZE - 2)].hasPiece();
                } else if (piece.getOldX() / TILE_SIZE > 5) {
                    return board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE - 2)][(int) (piece.getOldY() / TILE_SIZE - 2)].hasPiece();
                } else {
                    return board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE + 2)][(int) (piece.getOldY() / TILE_SIZE - 2)].hasPiece()
                            ||
                            board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE - 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE - 2)][(int) (piece.getOldY() / TILE_SIZE - 2)].hasPiece();
                }
            } else {
                if (piece.getOldY() / TILE_SIZE > 5)
                    return false;
                if (piece.getOldX() / TILE_SIZE < 2) {
                    return board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE + 2)][(int) (piece.getOldY() / TILE_SIZE + 2)].hasPiece();
                } else if (piece.getOldX() / TILE_SIZE > 5) {
                    return board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE - 2)][(int) (piece.getOldY() / TILE_SIZE + 2)].hasPiece();
                } else {
                    return board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE + 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE + 2)][(int) (piece.getOldY() / TILE_SIZE + 2)].hasPiece()
                            ||
                            board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].hasPiece()
                            && board[(int) (piece.getOldX() / TILE_SIZE - 1)][(int) (piece.getOldY() / TILE_SIZE + 1)].getPiece().getType() != piece.getType()
                            && !board[(int) (piece.getOldX() / TILE_SIZE - 2)][(int) (piece.getOldY() / TILE_SIZE + 2)].hasPiece();
                }
            }
        }
    }

    private Boolean isMovePossible() {
        boolean ret_val = false;
        for (int y = 0; y < HEIGHT; ++y) {
            for (int x = 0; x < HEIGHT; ++x) {
                if (board[x][y].hasPiece()) {
                    Piece tempPiece = board[x][y].getPiece();
                    if(!isPieceMovePossible(tempPiece))
                        continue;
                    ret_val = true;
                }
            }
        }
        return ret_val;
    }

    private Boolean isPieceMovePossible(Piece tempPiece) {
        if(tempPiece.getType() == PieceType.WHITE && toMove == ToMove.WHITE) {
            if(canKill(tempPiece))
                return true;
            else {
                if(tempPiece.isQueen()){
                    if (tempPiece.getOldY() / TILE_SIZE == 0) {
                        if (tempPiece.getOldX() / TILE_SIZE == 0)
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece();
                        else if (tempPiece.getOldX() / TILE_SIZE == 7)
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece();
                        else
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece()
                                    || !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece();
                    }
                    else if(tempPiece.getOldY() / TILE_SIZE == 7) {
                        if (tempPiece.getOldX() / TILE_SIZE == 0)
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece();
                        else if (tempPiece.getOldX() / TILE_SIZE == 7)
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece();
                        else
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece()
                                    || !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece();
                    }
                    else {
                        if(tempPiece.getOldX() / TILE_SIZE == 0)
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece()
                                || !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece();
                        else if(tempPiece.getOldX() / TILE_SIZE == 7)
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece()
                                || !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece();
                        else
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece()
                                || !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece()
                                || !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece()
                                || !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece();
                    }
                }
                else {
                    if(tempPiece.getOldY() / TILE_SIZE == 0)
                        return false;
                    if(tempPiece.getOldX() / TILE_SIZE == 0)
                        return !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece();
                    else if(tempPiece.getOldX() / TILE_SIZE == 7)
                        return !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece();
                    else
                        return !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece()
                            || !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece();
                }
            }
        }
        else if(tempPiece.getType() == PieceType.RED && toMove == ToMove.RED) {
            if(canKill(tempPiece))
                return true;
            else {
                if(tempPiece.isQueen()){
                    if (tempPiece.getOldY() / TILE_SIZE == 0) {
                        if (tempPiece.getOldX() / TILE_SIZE == 0)
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece();
                        else if (tempPiece.getOldX() / TILE_SIZE == 7)
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece();
                        else
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece()
                                    || !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece();
                    }
                    else if(tempPiece.getOldY() / TILE_SIZE == 7) {
                        if (tempPiece.getOldX() / TILE_SIZE == 0)
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece();
                        else if (tempPiece.getOldX() / TILE_SIZE == 7)
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece();
                        else
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece()
                                    || !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece();
                    }
                    else {
                        if(tempPiece.getOldX() / TILE_SIZE == 0)
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece()
                                || !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece();
                        else if(tempPiece.getOldX() / TILE_SIZE == 7)
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece()
                                || !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece();
                        else
                            return !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece()
                                    || !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE - 1)].hasPiece()
                                    || !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece()
                                    || !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece();
                    }
                }
                else {
                    if(tempPiece.getOldY() / TILE_SIZE == 7)
                        return false;
                    if(tempPiece.getOldX() / TILE_SIZE == 0)
                        return !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece();
                    else if(tempPiece.getOldX() / TILE_SIZE == 7)
                        return !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece();
                    else
                        return !board[(int) (tempPiece.getOldX() / TILE_SIZE - 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece()
                            || !board[(int) (tempPiece.getOldX() / TILE_SIZE + 1)][(int) (tempPiece.getOldY() / TILE_SIZE + 1)].hasPiece();
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
