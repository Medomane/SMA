package agents;

import containers.ConsumerContainer;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class ConsumerAgent extends GuiAgent {
	
	private transient ConsumerContainer gui ;
	
	@Override
	protected void setup() {
		//first method to execute
		if(getArguments().length > 0) {
			gui = (ConsumerContainer)getArguments()[0];
			gui.setConsumerAgent(this);
		}
		ParallelBehaviour behaviour = new ParallelBehaviour();
		behaviour.addSubBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				ACLMessage aclMessage = receive();
				if(aclMessage != null) {
					switch (aclMessage.getPerformative()) {
					case ACLMessage.CONFIRM: {
						gui.logMsg(aclMessage);
					}
					break ;
					default:
						System.out.println("consumer walo");
					}
				}
				else block();
			}
		});
		addBehaviour(behaviour);
	}
	
	/*@Override
	protected void beforeMove() {
		System.out.println("\nAvant migration\n");
	}

	@Override
	protected void afterMove() {
		System.out.println("\nAprès migration\n");
	}
	
	@Override
	protected void takeDown() {
		System.out.println("\nI am going to die\n");
	}*/

	@Override
	public void onGuiEvent(GuiEvent params) {
		if(params.getType() == 1) {
			var livre = (String)params.getParameter(0);
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setContent(livre);
			message.addReceiver(new AID("Acheteur",AID.ISLOCALNAME));
			send(message);
		}
	}

}
