package containers;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;

public class SimpleContainer {
	public static void main(String[] args) throws Exception {
		Runtime runtime = Runtime.instance();		
		ProfileImpl profileImpl = new ProfileImpl();
		profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		AgentContainer agentContainer = runtime.createAgentContainer(profileImpl);
		agentContainer.start();
	}
}
