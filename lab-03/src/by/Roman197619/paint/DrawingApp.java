package by.Roman197619.paint;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class DrawingApp extends Application {
    private double startY;
    private double startX;
    private final Canvas canvas = new Canvas(1920, 1080);
    private final Canvas canvas_second = new Canvas(1920, 1080);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();
    private final GraphicsContext gc_second = canvas_second.getGraphicsContext2D();


    private enum DrawMode {
        FREE, RECTANGLE, CIRCLE
    }

    private DrawMode drawMode = DrawMode.FREE;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Paint");

        GridPane buttons_and_controls = new GridPane();

        ColorPicker colorPicker = new ColorPicker(Color.BLACK);

        Slider lineWidthSlider = new Slider(1, 20, 10);
        lineWidthSlider.setShowTickMarks(true);
        lineWidthSlider.setShowTickLabels(true);

        ToggleButton freeButton = new ToggleButton("Free");
        ToggleButton rectangleButton = new ToggleButton("Rectangle");
        ToggleButton circleButton = new ToggleButton("Circle");

        freeButton.setOnAction(e -> drawMode = DrawMode.FREE);
        rectangleButton.setOnAction(e -> drawMode = DrawMode.RECTANGLE);
        circleButton.setOnAction(e -> drawMode = DrawMode.CIRCLE);

        canvas.setOnMousePressed(e -> {
            startX = e.getX();
            startY = e.getY();

            gc.beginPath();
            gc.setStroke(colorPicker.getValue());
            gc.setFill(colorPicker.getValue());
            gc.setLineWidth(lineWidthSlider.getValue());

            gc_second.beginPath();
            gc_second.setStroke(colorPicker.getValue());
            gc_second.setFill(colorPicker.getValue());
            gc_second.setLineWidth(lineWidthSlider.getValue());

        });

        canvas.setOnMouseDragged(e -> {
            if (drawMode == DrawMode.FREE) {
                drawLine(e.getX(), e.getY());
            } else if (drawMode == DrawMode.RECTANGLE) {
                gc_second.clearRect(0, 0, 1920, 1080);
                drawRectangle(startX, startY, e.getX(), e.getY(), gc_second);
            } else if (drawMode == DrawMode.CIRCLE) {
                gc_second.clearRect(0, 0, 1920, 1080);
                drawCircle(startX, startY, e.getX(), e.getY(), gc_second);
            }
        });

       canvas.setOnDragDetected(event -> canvas.startFullDrag());

        canvas.setOnMouseDragReleased(e -> {
            if (drawMode == DrawMode.RECTANGLE) {
                drawRectangle(startX, startY, e.getX(), e.getY(), gc);
            } else if (drawMode == DrawMode.CIRCLE) {
                drawCircle(startX, startY, e.getX(), e.getY(), gc);
            }
        });


        HBox controlsBox = new HBox(10, freeButton, rectangleButton, circleButton, colorPicker, lineWidthSlider);

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem saveMenuItem = new MenuItem("Save");
        saveMenuItem.setOnAction(e -> saveImage(canvas));

        MenuItem loadMenuItem = new MenuItem("Load");
        loadMenuItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                loadImage(selectedFile.getAbsolutePath());
            }
        });

        fileMenu.getItems().addAll(saveMenuItem, loadMenuItem);
        menuBar.getMenus().add(fileMenu);

        buttons_and_controls.add(menuBar, 0, 0);
        buttons_and_controls.add(controlsBox, 0, 1);

        Separator sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);

        StackPane stackPane = new StackPane(canvas_second, canvas);

        VBox root = new VBox(menuBar, buttons_and_controls, sep, stackPane);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawLine(double endX, double endY) {
        gc.lineTo(endX, endY);
        gc.stroke();
    }

    private void drawRectangle(double x1, double y1, double x2, double y2, GraphicsContext gc) {
        gc.strokeRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
    }

    private void drawCircle(double x1, double y1, double x2, double y2, GraphicsContext gc) {
        double radius = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        gc.strokeOval(x1 - radius, y1 - radius, radius * 2, radius * 2);
    }

    private void saveImage(Canvas canvas) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                javax.imageio.ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadImage(String path) {
        javafx.scene.image.Image image = new javafx.scene.image.Image("file:" + path);
        gc.drawImage(image, 0, 0);
    }
}
