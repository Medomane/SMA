package containers;

import agents.ConsumerAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConsumerContainer extends Application {
	
	protected ConsumerAgent consumerAgent;
	ListView<String> list;
	public static void main(String[] args) throws Exception {
		launch(args);
	}
	
	public void startContainer() throws Exception  {
		Runtime runtime = Runtime.instance();		
		ProfileImpl profileImpl = new ProfileImpl();
		profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		AgentContainer agentContainer = runtime.createAgentContainer(profileImpl);
		AgentController agentController = agentContainer.createNewAgent("Consumer", "agents.ConsumerAgent", new Object[] {this});
		agentController.start();
	}

	@Override
	public void start(Stage stage) throws Exception {
		startContainer();

		Label lblLvr = new Label("Livre : ");
		TextField livreField = new TextField();
		Button btnLvr = new Button("Ajouter");
		list = new ListView<String>();
		btnLvr.setOnAction(click -> {
			if(livreField.getText().trim() != "") {
				
				//list.getItems().add(livreField.getText());
				var event = new GuiEvent(this, 1);
				event.addParameter(livreField.getText());
				consumerAgent.onGuiEvent(event);
				
			}
		});
		
		VBox centerBox = new VBox();
		centerBox.setPadding(new Insets(20));
		centerBox.getChildren().add(list);
		
		HBox topBox = new HBox();
		topBox.setStyle("-fx-background-color:lightblue;");
		topBox.setAlignment(Pos.CENTER);
		topBox.setPadding(new Insets(15));
		topBox.setSpacing(20);
		topBox.getChildren().addAll(lblLvr,livreField,btnLvr);
		
		BorderPane root = new BorderPane();
		root.setTop(topBox);
		root.setCenter(centerBox);
		
		Scene scene = new Scene(root,350,200) ;
		stage.setTitle("Systme multi-agents : Consumer");
		stage.setScene(scene);
		stage.show();
	}

	
	public void setConsumerAgent(ConsumerAgent consumerAgent) {
		this.consumerAgent = consumerAgent;
	}
	
	public void logMsg(ACLMessage msg) {
		Platform.runLater(() -> {
			list.getItems().add(msg.getContent()+" from "+msg.getSender().getName()+" with "+msg.getPerformative());
		});
	}
	
}
