package containers;

import agents.AcheteurAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AcheteurContainer extends Application {

	private AcheteurAgent acheteurAgent ;
	
	ListView<String> list;
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		startContainer();

		list = new ListView<String>();
		
		VBox centerBox = new VBox();
		centerBox.setPadding(new Insets(20));
		centerBox.getChildren().add(list);

		BorderPane root = new BorderPane();
		root.setCenter(centerBox);
		
		Scene scene = new Scene(root,350,200) ;
		stage.setTitle("Systme multi-agents : Acheteur");
		stage.setScene(scene);
		stage.show();
	}
	

	public void startContainer() throws Exception  {
		Runtime runtime = Runtime.instance();		
		ProfileImpl profileImpl = new ProfileImpl();
		profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		AgentContainer agentContainer = runtime.createAgentContainer(profileImpl);
		AgentController agentController = agentContainer.createNewAgent("Acheteur", "agents.AcheteurAgent", new Object[] {this});
		agentController.start();
	}

	public void setAcheteurAgent(AcheteurAgent acheteurAgent) {
		this.acheteurAgent = acheteurAgent;
	}
	
	public void logMsg(ACLMessage msg) {
		Platform.runLater(() -> {
			list.getItems().add(msg.getContent()+" from "+msg.getSender().getName()+" with "+msg.getPerformative());
		});
	}
}
