package pl.silicon;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

public class Game extends Application {
    private static Font font = Font.getDefault();

    private final Rectangle[][] TILES = new Rectangle[4][4];
    private final int[][] BOARD = new int[4][4];
    private final String[] TILES_BACKGROUND_COLOR = {"#CDC1B4", "#EEE4DA", "#EEE1C9", "#F3B27A", "#F69664", "#F77C5F", "#F75F3B", "#EDD073", "#EDCC62", "#EDC950", "#EDC53F", "#EDC22E"};
    private final String[] TILES_FOREGROUND_COLOR = {"#776E65", "#F9F6F2"};

    private GraphicsContext graphicsContext;

    static {
        try {
            font = Font.loadFont(Objects.requireNonNull(Game.class.getResource("UbuntuMono-Bold.ttf")).toExternalForm(), 40);
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Canvas canvas = new Canvas(450, 450);
        Scene scene = new Scene(root, Color.web("#BBADA0"));

        root.getChildren().add(canvas);
        stage.setScene(scene);
        stage.setTitle("2048");

        graphicsContext = canvas.getGraphicsContext2D();

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.RIGHT) {
                int[][] boardCopy = new int[4][];
                IntStream.range(0, boardCopy.length).forEach(index -> boardCopy[index] = Arrays.copyOf(BOARD[index], BOARD[index].length));

                for (int[] row : BOARD) {
                    operate(row);
                }

                if (!Arrays.deepEquals(BOARD, boardCopy)) {
                    generateNewNumber();
                }

                event.consume();
            }
        });

        IntStream.range(0, 4).forEach(y -> IntStream.range(0, 4).forEach(x -> {
            TILES[y][x] = new Rectangle((x * 100) + ((x + 1) * 10), (y * 100) + ((y + 1) * 10), 100, 100);
            TILES[y][x].setArcWidth(10);
            TILES[y][x].setArcHeight(10);
            TILES[y][x].setFill(Color.TRANSPARENT);

            root.getChildren().add(TILES[y][x]);
        }));

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000D / 60D), event -> drawBoard()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        generateNewNumber();
        generateNewNumber();

        stage.show();
    }

    private void drawBoard() {
        graphicsContext.setFont(font);
        graphicsContext.setTextAlign(TextAlignment.CENTER);
        graphicsContext.setTextBaseline(VPos.CENTER);

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                int value = BOARD[y][x];

                graphicsContext.setFill(Color.web(TILES_BACKGROUND_COLOR[value]));
                graphicsContext.fillRoundRect((x * 100) + ((x + 1) * 10), (y * 100) + ((y + 1) * 10), 100, 100, 10D, 10D);

                if (value != 0) {
                    graphicsContext.setFill(Color.web(TILES_FOREGROUND_COLOR[(value < 3) ? 0 : 1]));
                    graphicsContext.fillText(String.valueOf(1 << BOARD[y][x]), (x * 100) + ((x + 1) * 10) + 50, (y * 100) + ((y + 1) * 10) + 50);
                }
            }
        }
    }

    private void generateNewNumber() {
        int[][] points = new int[16][2];
        byte offset = 0;

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (BOARD[y][x] == 0) {
                    points[offset][0] = y;
                    points[offset++][1] = x;
                }
            }
        }

        if (offset > 0) {
            int random = new Random().nextInt(offset);

            BOARD[points[random][0]][points[random][1]] = ((Math.random() > 0.5D) ? 1 : 2);
        }
    }

    private void operate(int[] row) {
        slide(row);
        combine(row);
        slide(row);
    }

    private void slide(int[] row) {
        int[] match = Arrays.stream(row).filter(cell -> cell != 0).toArray();
        int missing = 4 - match.length;

        for (int i = 0; i < 4; i++) {
            if (i < missing) {
                row[i] = 0;
            } else {
                row[i] = match[i - missing];
            }
        }
    }

    private void combine(int[] row) {
        for (int i = 3; i >= 1; i--) {
            int a = row[i];
            int b = row[i - 1];

            if (a != 0 && a == b) {
                row[i] = a + 1;
                row[i - 1] = 0;
            }
        }
    }
}
