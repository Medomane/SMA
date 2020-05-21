import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.nd4j.linalg.io.ClassPathResource;

import java.io.File;
import java.io.IOException;


public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage window) throws Exception {

        BorderPane root = new BorderPane();
        HBox topBox = new HBox();
        HBox centerBox = new HBox();
        HBox bottomBox = new HBox();
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(5));
        bottomBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        ProgressBar pb =new ProgressBar();
        pb.setVisible(false);
        bottomBox.getChildren().add(pb);

        Button trainBtn = new Button("Entrainer");
        Button predictionBtn = new Button("Prédiction");
        topBox.getChildren().add(predictionBtn);
        topBox.getChildren().add(trainBtn);
        topBox.setSpacing(10);
        topBox.setAlignment(Pos.CENTER_RIGHT);
        topBox.setPadding(new Insets(5));
        topBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        HBox leftBox = new HBox();
        leftBox.setMinWidth(250);
        centerBox.getChildren().add(leftBox);
        leftBox.setAlignment(Pos.CENTER_RIGHT);

        VBox rightBox = new VBox();
        rightBox.setMinWidth(250);
        rightBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().add(rightBox);
        rightBox.setSpacing(5);
        Label title =new Label("");
        rightBox.getChildren().add(title);
        ImageView image = new ImageView();
        image.setFitWidth(200);
        image.setFitHeight(180);
        rightBox.getChildren().add(image);
        title.setPadding(new Insets(5));
        title.setAlignment(Pos.CENTER);

        GridPane grid = new GridPane();
        TextField sepalLengthField = new TextField();
        grid.add(new Label("SepalLength"), 0, 0);
        grid.add(new Label(":"), 1, 0);
        grid.add(sepalLengthField, 2, 0);

        TextField sepalWidthField = new TextField();
        grid.add(new Label("SepalWidth"), 0, 1);
        grid.add(new Label(":"), 1, 1);
        grid.add(sepalWidthField, 2, 1);

        TextField petalLengthField = new TextField();
        grid.add(new Label("PetalLength"), 0, 2);
        grid.add(new Label(":"), 1, 2);
        grid.add(petalLengthField, 2, 2);

        TextField petalWidthField = new TextField();
        grid.add(new Label("PetalWidth"), 0, 3);
        grid.add(new Label(":"), 1, 3);
        grid.add(petalWidthField, 2, 3);
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        leftBox.getChildren().add(grid);

        final String[] iris = {null};


        trainBtn.setOnAction(click->{
            formTrain f = new formTrain(window);
            f.Init();
        });

        predictionBtn.setOnAction(click->{
            if(Func.isDouble(sepalLengthField.getText()) && Func.isDouble(sepalWidthField.getText()) && Func.isDouble(petalLengthField.getText()) && Func.isDouble(petalWidthField.getText())){
                Task<Void> sleeper = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        pb.setVisible(true);
                        predictionBtn.setDisable(true);
                        trainBtn.setDisable(true);
                        double[] tbl1 = new double[4];
                        tbl1[0]=Double.parseDouble(sepalLengthField.getText());
                        tbl1[1]=Double.parseDouble(sepalWidthField.getText());
                        tbl1[2]=Double.parseDouble(petalLengthField.getText());
                        tbl1[3]=Double.parseDouble(petalWidthField.getText());
                        double[][] tbl2 = new double[1][4];
                        tbl2[0] = tbl1;
                        iris[0] = IrisPrediction.Init(tbl2);
                        System.out.println(iris[0]);
                        return null;
                    }
                };
                sleeper.setOnSucceeded(event -> {
                    pb.setVisible(false);
                    predictionBtn.setDisable(false);
                    trainBtn.setDisable(false);
                    if(!Func.isNull(iris[0])){
                        title.setText(iris[0]);
                        try {
                            File file = new ClassPathResource(iris[0]+".png").getFile();
                            image.setImage(new Image(file.toURI().toString()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                new Thread(sleeper).start();
            }
        });



        root.setTop(topBox);
        root.setCenter(centerBox);
        root.setBottom(bottomBox);
        Scene scene = new Scene(root);
        window.setWidth(520);
        window.setHeight(330);
        window.setTitle("DL4J");
        window.setScene(scene);
        window.show();
    }
}

