import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class formTrain {
    private Stage owner;
    public formTrain(Stage owner){
        this.owner = owner;
    }
    public void Init(){
        final Stage[] dialog = {new Stage()};
        dialog[0] = new Stage();

        BorderPane root = new BorderPane();

        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(5));
        bottom.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        ProgressBar pb =new ProgressBar();
        pb.setVisible(false);
        bottom.getChildren().add(pb);

        HBox dialogHBox = new HBox();
        dialogHBox.setAlignment(Pos.CENTER);
        dialogHBox.getChildren().add(new Label("nombre de fois :"));
        TextField times = new TextField();
        dialogHBox.getChildren().add(times);
        Button timesBtn = new Button("Valider");

        timesBtn.setOnAction(click->{
            if(Func.isLong(times.getText())){
                Task<Void> sleeper = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        pb.setVisible(true);
                        timesBtn.setDisable(true);
                        iris_app.Init(Integer.parseInt(times.getText()));
                        return null;
                    }
                };
                sleeper.setOnSucceeded(event -> {
                    pb.setVisible(false);
                    timesBtn.setDisable(false);
                });
                new Thread(sleeper).start();
            }
            else times.requestFocus();
        });

        dialogHBox.getChildren().add(timesBtn);
        dialogHBox.setPadding(new Insets(30));
        dialogHBox.setSpacing(10);

        root.setCenter(dialogHBox);
        root.setBottom(bottom);
        Scene dialogScene = new Scene(root, 400, 100);
        dialog[0].initModality(Modality.APPLICATION_MODAL);
        dialog[0].initOwner(owner);
        dialog[0].setTitle("Entrainement");
        dialog[0].resizableProperty().setValue(Boolean.FALSE);
        dialog[0].setScene(dialogScene);
        dialog[0].show();
    }
}