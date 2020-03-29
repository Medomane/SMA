package agents;

import java.util.Random;

import containers.AcheteurContainer;
import containers.VendeurContainer;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class VendeurAgent extends GuiAgent{
	public transient VendeurContainer gui ;
	
	@Override
	protected void setup() {
		if(getArguments().length > 0) {
			gui = (VendeurContainer)getArguments()[0];
			gui.setVendeurAgent(this);
		}
		ParallelBehaviour behaviour = new ParallelBehaviour();
		behaviour.addSubBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				ACLMessage aclMessage = receive();
				if(aclMessage != null) {
					gui.logMsg(aclMessage);
					switch (aclMessage.getPerformative()) {
						case ACLMessage.CFP: {
							ACLMessage msg = aclMessage.createReply();
							msg.setContent(String.valueOf(500+new Random().nextInt()));
							msg.setPerformative(ACLMessage.PROPOSE);
							send(msg);
						}
						break;
						case ACLMessage.ACCEPT_PROPOSAL: {
							ACLMessage msg = aclMessage.createReply();
							msg.setPerformative(ACLMessage.AGREE);
							send(msg);
						}
						break;
						default : System.out.println("walo") ;
					}
				}
				else block();
			}
		});
		behaviour.addSubBehaviour(new OneShotBehaviour() {
			
			@Override
			public void action() {
				DFAgentDescription dfd = new DFAgentDescription(); 
				dfd.setName(getAID()); 
				ServiceDescription sd = new ServiceDescription(); 
				sd.setType("transaction"); 
				sd.setName("Vente livre"); 
				dfd.addServices(sd); 
				try {
					DFService.register(myAgent, dfd);
				} catch (FIPAException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		addBehaviour(behaviour);
	}
	
	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onGuiEvent(GuiEvent params) {
		
	}
}
