package laby.ui;
import javax.swing.*; 
import java.awt.image.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.logging.Logger;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Control;
import javafx.scene.layout.HBox;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import laby.logic.ImageHandler;

/**
 *
 * @author sakorpi
 */
public class App extends Application {
    ToggleGroup toolSet;
    int[][] orginalPixs;
    int[][] drawedPixs;
    Label info;
    GraphicsContext piirturi;
    PixelReader reader;
    Image sourceImg;
    int width;
    int height;
    WritableImage outputImg;
    PixelWriter imgWriter;
    ImageHandler ih;
    Canvas canvas;
    

    private Color selectedColor(ToggleGroup group) {
        RadioButton selected = (RadioButton) group.getSelectedToggle();
        if (selected.getId().equals("start")) {
            return Color.BLUE;
        } else if (selected.getId().equals("goal")) {
            return Color.RED;
        } else {
            return Color.BLACK;
        }
    }
    
    private int arrayColor(Color color) {
        if (color == Color.BLUE) {
            return -16776961;
        } else if (color == Color.RED) {
            return -65536;
        } else if (color == Color.BLACK) {
            return -16777216;
        } else {
            return -1;
        }
    }

    private void drawBackground() {
        int y = 0;
        while (y < height) {
            int x = 0;
            while (x < width) {
                Color vari = reader.getColor(x, y);
                imgWriter.setColor(x, y, vari);
                x++;
            }
            y++;
        }
    }
    
    private void load(String filepath) {
        sourceImg = new Image("file:"+filepath);
        reader = sourceImg.getPixelReader();
        width = (int) sourceImg.getWidth();
        height = (int) sourceImg.getHeight();
        outputImg = new WritableImage(width, height);
        imgWriter = outputImg.getPixelWriter();
        ih = new ImageHandler(filepath, info);
        orginalPixs = ih.getImageAsMap();
        drawedPixs = new int[orginalPixs.length][orginalPixs[0].length];
        formatArray();
        drawBackground();
        
        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                piirturi.drawImage(outputImg, 0, 0, canvas.getWidth(), canvas.getHeight());
            }
        }.start();
    }
    
    @Override
    public void start(Stage stage){
        stage.setTitle("Laby");
        
        TextField filepath = new TextField("images/greg4.bmp");
        Button load = new Button("Lataa kuva");
        Button seek = new Button("Etsi reitti");
        Button reset = new Button("Nollaa");
        info = new Label("---");
        
        // Radio buttons for tool choise:
        toolSet = new ToggleGroup();
        RadioButton start = new RadioButton("Lisää lähtö");
        start.setToggleGroup(toolSet);
        start.setId("start");
        start.setSelected(true);
        RadioButton goal = new RadioButton("Lisää maali");
        goal.setToggleGroup(toolSet);
        goal.setId("goal");
        RadioButton wall = new RadioButton("Lisää seinää");
        wall.setToggleGroup(toolSet);
        wall.setId("wall");
        
        load(filepath.getText());

        canvas = new Canvas(width, height);
        piirturi = canvas.getGraphicsContext2D();

        canvas.setOnMouseClicked((MouseEvent e) -> {
            int y = (int) e.getY();
            int x = (int) e.getX();
            Color color = selectedColor(toolSet);
            imgWriter.setColor(x, y, color);
            drawedPixs[y][x] = arrayColor(color);
        });

        canvas.setOnMouseDragged((MouseEvent e) -> {
            int y = (int) e.getY();
            int x = (int) e.getX();
            Color color = selectedColor(toolSet);
            imgWriter.setColor(x, y, color);
            drawedPixs[y][x] = arrayColor(color);
        });

        
        BorderPane asettelu = new BorderPane();
        ScrollPane scroll = new ScrollPane();
        scroll.setMinSize(800, 1000);
        scroll.setScaleX(0.75);
        scroll.setScaleY(0.75);
        scroll.setContent(canvas);
        asettelu.setCenter(scroll);
        
        VBox napit = new VBox();
        napit.setSpacing(20);
        napit.getChildren().addAll(filepath, load, start, goal, wall,seek, reset, info);
        asettelu.setRight(napit);

        Scene nakyma = new Scene(asettelu);
        stage.setScene(nakyma);
        
        load.setOnAction(event -> {
            scroll.setContent(canvas);
            load(filepath.getText());
        });
        
        reset.setOnAction(event -> {
            scroll.setContent(canvas);
            load(filepath.getText());
        });
        
        seek.setOnAction(event -> {
            ih.doFile(drawedPixs);
            if (info.getText().equals("Ei löydetty alku- ja loppupisteitä")) return;
            Image readyImg = new Image("file:images/output.bmp");
            ImageView iw = new ImageView(readyImg);
            scroll.setContent(iw);
        });

        stage.show();
    }
    
    private void formatArray() {
        for (int y = 0; y < orginalPixs.length; y++) {
            for (int x = 0; x < orginalPixs[0].length; x++) {
                drawedPixs[y][x] = orginalPixs[y][x];
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}