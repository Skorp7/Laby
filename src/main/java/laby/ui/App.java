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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
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
    int thickness;
    int width;
    int height;
    WritableImage outputImg;
    PixelWriter imgWriter;
    ImageHandler ih;
    Canvas canvas;
    ImageView iw;
    boolean done;
    

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
    
    private boolean load(String filepath) {
        done = false;
        sourceImg = new Image("file:"+filepath);
        File dir = new File(filepath);
        if (!dir.exists()) return false;
        reader = sourceImg.getPixelReader();
        width = (int) sourceImg.getWidth();
        height = (int) sourceImg.getHeight();
        outputImg = new WritableImage(width, height);
        imgWriter = outputImg.getPixelWriter();
        ih = new ImageHandler(filepath, info);
        orginalPixs = ih.getImageAsMap();
        drawedPixs = new int[orginalPixs.length][orginalPixs[0].length];
        canvas.setHeight(height);
        canvas.setWidth(width);
        formatArray();
        drawBackground();
        
        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                piirturi.drawImage(outputImg, 0, 0, canvas.getWidth(), canvas.getHeight());
            }
        }.start();
        return true;
    }
    
    @Override
    public void start(Stage stage){
        stage.setTitle("Laby");

        TextField filepath = new TextField("images/default.bmp");
        Button load = new Button("Lataa kuva");
        Button seek = new Button("Etsi reitti");
        Button reset = new Button("Nollaa");
        Slider zoom = new Slider(0.20, 2.0, 0.75);
        Slider thick = new Slider(-1501000,-69536,-856619);
        thick.setSnapToTicks(true);
        thick.setShowTickMarks(true);
        thick.setMinorTickCount(1);
        thick.setMajorTickUnit(1000000);
        Label zoomLabel = new Label("Zoom");
        Label thickLabel = new Label("Seinien vahvuus (- <-> +)");
        info = new Label("---");
        thickness = -856619;
        
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
        
        canvas = new Canvas(width, height);
        canvas.setScaleX(0.75);
        canvas.setScaleY(0.75);
        load(filepath.getText());     
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
            if (color == Color.BLACK) {
                imgWriter.setColor(x-1, y, color);
                imgWriter.setColor(x, y-1, color);
                imgWriter.setColor(x+1, y, color);
                imgWriter.setColor(x, y+1, color);
                imgWriter.setColor(x+1, y+1, color);
                imgWriter.setColor(x-1, y-1, color);
                drawedPixs[y-1][x] = arrayColor(color);
                drawedPixs[y][x-1] = arrayColor(color);
                drawedPixs[y+1][x] = arrayColor(color);
                drawedPixs[y][x+1] = arrayColor(color);
                drawedPixs[y+1][x+1] = arrayColor(color);
                drawedPixs[y-1][x-1] = arrayColor(color);
            }
        });
        
        BorderPane asettelu = new BorderPane();
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        scroll.autosize();
        scroll.setContent(canvas);
        asettelu.setCenter(scroll);
        
        HBox topic = new HBox();
        Label welcome = new Label("Laby Labyrintti");
        welcome.setTextAlignment(TextAlignment.CENTER);
        welcome.setTextFill(Color.GREEN);
        welcome.setAlignment(Pos.CENTER);
        welcome.setFont(Font.font("Monospace", 20));
        welcome.getStyleClass().add("welcometext");
        topic.setPadding(new Insets(40,40,40,40));
        topic.setAlignment(Pos.CENTER);
        topic.getChildren().add(welcome);
        VBox napit = new VBox();
        napit.setSpacing(20);
        napit.setPadding(new Insets(20,20,20,20));
        napit.getChildren().addAll(filepath, load, start, goal, wall,seek, reset, zoomLabel, zoom, thickLabel, thick, info);
        asettelu.setRight(napit);
        asettelu.setTop(topic);

        Scene nakyma = new Scene(asettelu,1400,1000);
        nakyma.getStylesheets().add("laby/ui/App.css");
        stage.setScene(nakyma);
        
        load.setOnAction(event -> {
            scroll.setContent(canvas);
            if (!load(filepath.getText())) {
                info.setText("Lataus epäonnistui.");
            }
        });
        
        reset.setOnAction(event -> {
            scroll.setContent(canvas);
            thick.setValue(-856619);
            load(filepath.getText());
            info.setText("");
        });
        

        seek.setOnAction(event -> {
            ih.doFile(drawedPixs, thickness);
            zoom.setValue(0.75);
            if (info.getText().equals("Ei löydetty alku- ja loppupisteitä")) return;
            Image readyImg = new Image("file:images/output.bmp");
            iw = new ImageView(readyImg);
            iw.setScaleX(0.75);
            iw.setScaleY(0.75);
            scroll.setContent(iw);
            done = true;            
        });

        zoom.setOnMouseDragged((MouseEvent e) -> {
            if (done) {
                iw.setScaleX(zoom.getValue());
                iw.setScaleY(zoom.getValue());
            } else {
                canvas.setScaleX(zoom.getValue());
                canvas.setScaleY(zoom.getValue());
            }
        });
        
        thick.setOnMouseDragged((MouseEvent e) -> {
            thickness = (int) thick.getValue();
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