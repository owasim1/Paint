package sprint;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToolBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class Sprint extends Application {

    WhiteCanvas whiteCanvas;
    
    File file;
    
    double x;
    double y;
    
    double radius;
    
    double y2;
    double x2;
    
    double xSelectInitial;
    double ySelectInitial;
    
    double xSelectFinal;
    double ySelectFinal;
    
    double xDiff;
    double yDiff;
    
    Image snapshotSelect;
    
    double xLine;
    double yLine;
    
    int i = 0;

    @Override
    public void start(Stage primaryStage) {

        //Creates new canvas and assigns it the the variable canvas. 
        whiteCanvas = new WhiteCanvas(450, 400);
        
        VBox root = new VBox();
        Scene scene = new Scene(root, 675, 550);
        ColorPicker cp = new ColorPicker();
        ComboBox<String> selectBox = new ComboBox();
        TextField textBox = new TextField();

        selectBox.setPromptText("Edit Tools");
        selectBox.getItems().addAll("Dropper", "Text-Box", "Eraser", "Select", "Cut-Drag", "Line", "Pencil", "Circle", "Rectangle");
        /*Created a new MenuBar object, where Menu objects are going to
        be added. */
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        //Created a New Menu object.
        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        //Created new MenuItem objects, named "Open", "Save As", and "Exit"
        MenuItem openMenuItem = new MenuItem("Open");
        MenuItem saveMenuItem = new MenuItem("Save");
        MenuItem saveAsMenuItem = new MenuItem("Save As");
        MenuItem exitMenuItem = new MenuItem("Exit");

        MenuItem undoMenuItem = new MenuItem("Undo");
        MenuItem redoMenuItem = new MenuItem("Redo");

        Stack<Image> undoStack = new Stack();
        Stack<Image> shapeVisibleDragStack = new Stack();
        Stack<Image> redoStack = new Stack();

        //The exitMenuItem is being set to exit the program.
        //exitMenuItem.setOnAction(actionEvent -> Platform.exit());
        exitMenuItem.setOnAction((ActionEvent event) -> {
            Stage smartQuit = new Stage();
            StackPane exitBox = new StackPane();
            Scene quitScene = new Scene(exitBox, 300, 150);
            smartQuit.setTitle("Paint");

            Label quitLabel = new Label("Do you want to save changes?");
            Button yes = new Button("Yes");
            Button no = new Button("No");

            yes.setMaxHeight(30);
            yes.setMaxWidth(80);
            yes.setTranslateX(-50);
            yes.setTranslateY(25);

            no.setMaxHeight(30);
            no.setMaxWidth(80);
            no.setTranslateX(50);
            no.setTranslateY(25);

            quitLabel.setTranslateY(-30);
            exitBox.getChildren().addAll(yes, no, quitLabel);
            smartQuit.setScene(quitScene);
            smartQuit.show();

            yes.setOnAction((ActionEvent event1) -> {
                saveAsOption(primaryStage);
                Platform.exit();
            });
            no.setOnAction((ActionEvent actionEvent) -> {
                Platform.exit();
            });

        });

        /*Created an action for the openMenuItem. It gives you a file chooser
        to open the file, and then displays the choosen file on the imageView. */
        openMenuItem.setOnAction((ActionEvent event) -> {
            //Created a FileChooser object.
            snapshot(whiteCanvas.canvas, undoStack);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooserExtensions(fileChooser);
            file = fileChooser.showOpenDialog(primaryStage);
            
            Image image = new Image(file.toURI().toString());
            whiteCanvas.canvas.setWidth(image.getWidth());
            whiteCanvas.canvas.setHeight(image.getHeight());
            
            //imageView being set to the chosen image.
            whiteCanvas.gc.drawImage(image, 0, 0, image.getWidth(), image.getHeight());
        });
        saveAsMenuItem.setOnAction((ActionEvent event) -> {
            saveAsOption(primaryStage);
        });

        Spinner<Integer> intSpinner = new Spinner<>(0, 100, 0, 1);

        //This functions lets you draw a line on the canvas.
        selectBox.setOnAction((ActionEvent event) -> {
            if ("Dropper".equals(selectBox.getValue())) {
                //Gets the x and y coordinates of the first mouse-click and starts the line
                whiteCanvas.canvas.setOnMousePressed((MouseEvent e) -> {

                });

                whiteCanvas.canvas.setOnMouseDragged((MouseEvent e) -> {

                });

                whiteCanvas.canvas.setOnMouseReleased((MouseEvent e) -> {
                    int xInitial = (int) e.getX();
                    int yInitial = (int) e.getY();
                    WritableImage im = whiteCanvas.canvas.snapshot(null, null);
                    Color colorSelect = im.getPixelReader().getColor(xInitial, yInitial);
                    whiteCanvas.gc.setFill(colorSelect);
                    whiteCanvas.gc.setStroke(colorSelect);
                    cp.setValue(colorSelect);

                });
            }

            if ("Text-Box".equals(selectBox.getValue())) {
                //Gets the x and y coordinates of the first mouse-click and starts the line
                whiteCanvas.canvas.setOnMousePressed((MouseEvent e) -> {
                    snapshot(whiteCanvas.canvas, undoStack);
                    shapeVisibleDragStack.clear();
                    xSelectInitial = e.getX();
                    ySelectInitial = e.getY();
                });

                whiteCanvas.canvas.setOnMouseDragged((MouseEvent e) -> {
                    undo(whiteCanvas.canvas, whiteCanvas.gc, shapeVisibleDragStack, redoStack);
                    snapshot(whiteCanvas.canvas, shapeVisibleDragStack);
                    double xFinal = e.getX();
                    if (xFinal < xSelectInitial) {
                        double xTemp = xSelectInitial;
                        xSelectInitial = xFinal;
                        xFinal = xTemp;
                    }
                    xDiff = xFinal - xSelectInitial;
                    String xText = textBox.getText();
                    whiteCanvas.gc.strokeText(xText, xSelectInitial, ySelectInitial, 0);

                });

                whiteCanvas.canvas.setOnMouseReleased((MouseEvent e) -> {

                });
            }
            if ("Eraser".equals(selectBox.getValue())) {
                whiteCanvas.canvas.setOnMousePressed((MouseEvent e) -> {
                    whiteCanvas.gc.setStroke(Color.WHITE);
                    whiteCanvas.gc.beginPath();
                });
                whiteCanvas.canvas.setOnMouseDragged((MouseEvent e) -> {
                    whiteCanvas.gc.setLineWidth(intSpinner.getValue());
                    whiteCanvas.gc.lineTo(e.getX(), e.getY());
                    whiteCanvas.gc.stroke();
                    whiteCanvas.gc.setLineWidth(intSpinner.getValue());
                });

                whiteCanvas.canvas.setOnMouseReleased((MouseEvent e) -> {
                    whiteCanvas.gc.closePath();
                });
            }
            if ("Select".equals(selectBox.getValue())) {
                //Gets the x and y coordinates of the first mouse-click and starts the line
                whiteCanvas.canvas.setOnMousePressed((MouseEvent e) -> {
                    xSelectInitial = e.getX();
                    ySelectInitial = e.getY();
                });

                whiteCanvas.canvas.setOnMouseDragged((MouseEvent e) -> {
                    xSelectFinal = e.getX();
                    ySelectFinal = e.getY();
                });

                whiteCanvas.canvas.setOnMouseReleased((MouseEvent e) -> {
                    xDiff = Math.abs(xSelectFinal - xSelectInitial);
                    yDiff = Math.abs(ySelectFinal - ySelectInitial);
                });
            }
            if ("Cut-Drag".equals(selectBox.getValue())) {
                snapshotSelect = whiteCanvas.canvas.snapshot(null, null);

                whiteCanvas.canvas.setOnMousePressed((MouseEvent e) -> {

                });

                whiteCanvas.canvas.setOnMouseDragged((MouseEvent e) -> {
                    whiteCanvas.gc.setFill(Color.WHITE);
                    whiteCanvas.gc.fillRect(xSelectInitial, ySelectInitial, xDiff, yDiff);
                    undo(whiteCanvas.canvas, whiteCanvas.gc, shapeVisibleDragStack, redoStack);
                    snapshot(whiteCanvas.canvas, shapeVisibleDragStack);
                    whiteCanvas.gc.drawImage(snapshotSelect, xSelectInitial, ySelectInitial, xDiff, yDiff, e.getX(), e.getY(), xDiff, yDiff);
                });

                whiteCanvas.canvas.setOnMouseReleased((MouseEvent e) -> {
                    whiteCanvas.gc.setFill(Color.WHITE);
                    whiteCanvas.gc.fillRect(xSelectInitial, ySelectInitial, xDiff, yDiff);
                    undo(whiteCanvas.canvas, whiteCanvas.gc, shapeVisibleDragStack, redoStack);
                    snapshot(whiteCanvas.canvas, shapeVisibleDragStack);
                    whiteCanvas.gc.drawImage(snapshotSelect, xSelectInitial, ySelectInitial, xDiff, yDiff, e.getX(), e.getY(), xDiff, yDiff);
                    shapeVisibleDragStack.clear();
                });
            }
            if ("Line".equals(selectBox.getValue())) {
                //Gets the x and y coordinates of the first mouse-click and starts the line
                whiteCanvas.canvas.setOnMousePressed((MouseEvent e) -> {
                    xLine = e.getX();
                    yLine = e.getY();
                    whiteCanvas.gc.lineTo(xLine, yLine);
                    snapshot(whiteCanvas.canvas, undoStack);
                });

                whiteCanvas.canvas.setOnMouseDragged((MouseEvent e) -> {
                    undo(whiteCanvas.canvas, whiteCanvas.gc, shapeVisibleDragStack, redoStack);
                    snapshot(whiteCanvas.canvas, shapeVisibleDragStack);
                    whiteCanvas.gc.beginPath();
                    whiteCanvas.gc.lineTo(xLine, yLine);
                    whiteCanvas.gc.getStroke();
                    whiteCanvas.gc.setStroke(cp.getValue());
                    whiteCanvas.gc.lineTo(e.getX(), e.getY());
                    whiteCanvas.gc.stroke();

                    whiteCanvas.gc.setLineWidth(intSpinner.getValue());
                    whiteCanvas.gc.closePath();
                });

                whiteCanvas.canvas.setOnMouseReleased((MouseEvent e) -> {
                    whiteCanvas.gc.lineTo(e.getX(), e.getY());
                    whiteCanvas.gc.stroke();

                    whiteCanvas.gc.setLineWidth(intSpinner.getValue());

                    snapshot(whiteCanvas.canvas, shapeVisibleDragStack);
                });
            }
            if ("Circle".equals(selectBox.getValue())) {
                whiteCanvas.canvas.setOnMousePressed((MouseEvent e) -> {
                    x = e.getX();
                    y = e.getY();
                    snapshot(whiteCanvas.canvas, undoStack);
                });
                whiteCanvas.canvas.setOnMouseDragged((MouseEvent e) -> {
                    undo(whiteCanvas.canvas, whiteCanvas.gc, shapeVisibleDragStack, redoStack);
                    snapshot(whiteCanvas.canvas, shapeVisibleDragStack);
                    radius = Math.sqrt((x - e.getX()) * (x - e.getX()) + (y - e.getY()) * (y - e.getY()));
                    whiteCanvas.gc.setStroke(cp.getValue());
                    whiteCanvas.gc.setLineWidth(intSpinner.getValue());
                    whiteCanvas.gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);

                });

                whiteCanvas.canvas.setOnMouseReleased((MouseEvent e) -> {
                    radius = Math.sqrt((x - e.getX()) * (x - e.getX()) + (y - e.getY()) * (y - e.getY()));
                    whiteCanvas.gc.setStroke(cp.getValue());
                    whiteCanvas.gc.setLineWidth(intSpinner.getValue());
                    whiteCanvas.gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
                    shapeVisibleDragStack.clear();
                });
            }
            if ("Pencil".equals(selectBox.getValue())) {

                whiteCanvas.canvas.setOnMousePressed((MouseEvent e) -> {
                    whiteCanvas.gc.beginPath();
                    whiteCanvas.gc.lineTo(e.getX(), e.getY());
                    whiteCanvas.gc.setLineWidth(intSpinner.getValue());
                    snapshot(whiteCanvas.canvas, undoStack);
                });
                whiteCanvas.canvas.setOnMouseDragged((MouseEvent e) -> {
                    whiteCanvas.gc.stroke();
                    whiteCanvas.gc.setStroke(cp.getValue());
                    whiteCanvas.gc.lineTo(e.getX(), e.getY());
                    whiteCanvas.gc.setLineWidth(intSpinner.getValue());

                });
                whiteCanvas.canvas.setOnMouseReleased((MouseEvent e) -> {
                    whiteCanvas.gc.lineTo(e.getX(), e.getY());
                    whiteCanvas.gc.stroke();
                    whiteCanvas.gc.setLineWidth(intSpinner.getValue());
                    whiteCanvas.gc.closePath();
                });

            }
            if ("Rectangle".equals(selectBox.getValue())) {
                whiteCanvas.canvas.setOnMousePressed((MouseEvent e) -> {
                    x = e.getX();
                    y = e.getY();
                    snapshot(whiteCanvas.canvas, undoStack);
                });
                whiteCanvas.canvas.setOnMouseDragged((MouseEvent e) -> {
                    undo(whiteCanvas.canvas, whiteCanvas.gc, shapeVisibleDragStack, redoStack);
                    snapshot(whiteCanvas.canvas, shapeVisibleDragStack);
                    x2 = e.getX();
                    y2 = e.getY();
                    whiteCanvas.gc.setStroke(cp.getValue());
                    whiteCanvas.gc.setLineWidth(intSpinner.getValue());
                    whiteCanvas.gc.strokeLine(x, y, x, y2);
                    whiteCanvas.gc.strokeLine(x, y, x2, y);
                    whiteCanvas.gc.strokeLine(x2, y, x2, y2);
                    whiteCanvas.gc.strokeLine(x, y2, x2, y2);
                });

                whiteCanvas.canvas.setOnMouseReleased((MouseEvent e) -> {
                    x2 = e.getX();
                    y2 = e.getY();
                    whiteCanvas.gc.setStroke(cp.getValue());
                    whiteCanvas.gc.setLineWidth(intSpinner.getValue());
                    whiteCanvas.gc.strokeLine(x, y, x, y2);
                    whiteCanvas.gc.strokeLine(x, y, x2, y);
                    whiteCanvas.gc.strokeLine(x2, y, x2, y2);
                    whiteCanvas.gc.strokeLine(x, y2, x2, y2);
                    shapeVisibleDragStack.clear();

                });
            }

        });

        undoMenuItem.setOnAction((ActionEvent event) -> {
            undo(whiteCanvas.canvas, whiteCanvas.gc, undoStack, redoStack);
        });

        redoMenuItem.setOnAction((ActionEvent event) -> {
            redo(whiteCanvas.canvas, whiteCanvas.gc, undoStack, redoStack);
        });

        //Created a save button action.
        saveMenuItem.setOnAction((ActionEvent event) -> {
            saveOption();
        });

        /*Adds the menuItems to the fileMenu created before. Also adds a
        seperator. */
        fileMenu.getItems().addAll(openMenuItem, saveMenuItem, saveAsMenuItem,
                new SeparatorMenuItem(), exitMenuItem);

        editMenu.getItems().addAll(undoMenuItem, redoMenuItem);

        //Adds the fileMenu to the the menu bar.
        menuBar.getMenus().addAll(fileMenu, editMenu);

        //Creates a new ToolBar object.
        ToolBar toolBar = new ToolBar(cp, selectBox, intSpinner, textBox);

        /*Adds the menuBar and the toolBar to the frame. menuBar at top, and
          toolBar at the bottom. */
        root.getChildren().addAll(menuBar, toolBar, whiteCanvas.canvas);
        //root.getChildren().add(canvas);

        primaryStage.setTitle("Image Loader");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void fileChooserExtensions(FileChooser fileChooser) {
        // Created extension filters.
        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG", "*.png");
        FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("JPG", "*.jpg");
        FileChooser.ExtensionFilter bmpFilter = new FileChooser.ExtensionFilter("BMP", "*.bmp");
        FileChooser.ExtensionFilter allFilesFilter = new FileChooser.ExtensionFilter("All Files", "*.*");
        // Added the file extensions to the file chooser
        fileChooser.getExtensionFilters().addAll(jpgFilter, pngFilter, bmpFilter, allFilesFilter);
    }

    public void saveAsOption(Stage primaryStage) {
        WritableImage im = whiteCanvas.canvas.snapshot(new SnapshotParameters(), null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooserExtensions(fileChooser);
        file = fileChooser.showSaveDialog(primaryStage);

        BufferedImage bImage = SwingFXUtils.fromFXImage(im, null);
        try {
            ImageIO.write(bImage, "png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveOption() {
        //Creates a writable image (image that's supposed to be saved)
        WritableImage im = whiteCanvas.canvas.snapshot(null, null);

        /*Assigns the "opened" image file to the outputFile to overwrite
            image file. */
        File outputFile = file;
        BufferedImage bImage = SwingFXUtils.fromFXImage(im, null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void undo(Canvas canvas, GraphicsContext gc, Stack<Image> undoStack, Stack<Image> redoStack) {
        Image image = canvas.snapshot(new SnapshotParameters(), null);
        redoStack.push(image);
        if (!undoStack.empty()) {
            gc.drawImage(undoStack.pop(), 0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        }
    }

    public static void redo(Canvas canvas, GraphicsContext gc, Stack<Image> undoStack, Stack<Image> redoStack) {
        Image image = canvas.snapshot(new SnapshotParameters(), null);
        undoStack.push(image);
        if (!redoStack.empty()) {
            gc.drawImage(redoStack.pop(), 0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        }
    }

    public void snapshot(Canvas canvas, Stack<Image> stack) {
        Image image = canvas.snapshot(new SnapshotParameters(), null);
        stack.push(image);
    }

}
