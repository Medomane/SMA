package containers;

import agents.VendeurAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
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

public class VendeurContainer extends Application {
private VendeurAgent vendeurAgent ;
	
	ListView<String> list;
	AgentContainer agentContainer;
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		startContainer();
		list = new ListView<String>();
		
		Label lblAgent = new Label("Nom de l'agent : ");
		TextField agentField = new TextField();
		Button btnAgent = new Button("Deploy");
		list = new ListView<String>();
		btnAgent.setOnAction(click -> {
			if(agentField.getText().trim() != "") {
				try {
					AgentController agentController = agentContainer.createNewAgent(agentField.getText(), "agents.VendeurAgent", new Object[] {this});
					agentController.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		VBox centerBox = new VBox();
		centerBox.setPadding(new Insets(20));
		centerBox.getChildren().add(list);
		
		HBox topBox = new HBox();
		topBox.setStyle("-fx-background-color:lightblue;");
		topBox.setAlignment(Pos.CENTER);
		topBox.setPadding(new Insets(15));
		topBox.setSpacing(10);
		topBox.getChildren().addAll(lblAgent,agentField,btnAgent);

		BorderPane root = new BorderPane();
		root.setCenter(centerBox);
		root.setTop(topBox);
		
		Scene scene = new Scene(root,400,200) ;
		stage.setTitle("Systme multi-agents : Vendeur");
		stage.setScene(scene);
		stage.show();
	}
	
	public void startContainer() throws Exception  {
		Runtime runtime = Runtime.instance();		
		ProfileImpl profileImpl = new ProfileImpl();
		profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		agentContainer = runtime.createAgentContainer(profileImpl);
	}

	public void setVendeurAgent(VendeurAgent vendeurAgent) {
		this.vendeurAgent = vendeurAgent;
	}
	
	public void logMsg(ACLMessage msg) {
		Platform.runLater(() -> {
			list.getItems().add(msg.getContent()+" from "+msg.getSender().getName()+" with performative : "+msg.getPerformative());
		});
	}
}
