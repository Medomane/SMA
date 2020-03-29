package agents;

import java.util.ArrayList;
import java.util.List;

import containers.AcheteurContainer;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AcheteurAgent extends GuiAgent {

	public transient AcheteurContainer gui ;
	AID[] sellerAgents;
	@Override
	protected void setup() {
		if(getArguments().length > 0) {
			gui = (AcheteurContainer)getArguments()[0];
			gui.setAcheteurAgent(this);
		}
		ParallelBehaviour behaviour = new ParallelBehaviour();
		behaviour.addSubBehaviour(new TickerBehaviour(this, 5000) { 
			protected void onTick() {
				// Update the list of seller agents 
				DFAgentDescription template = new DFAgentDescription(); 
				ServiceDescription sd = new ServiceDescription(); 
				sd.setType("transaction");
				sd.setName("Vente livre"); 
				template.addServices(sd); 
				try { 
					DFAgentDescription[] result = DFService.search(myAgent, template); 
					sellerAgents = new AID[result.length]; 
					for (int i = 0; i < result.length; ++i) { 
						sellerAgents[i] = result[i].getName(); 
					} 
				} 
				catch (Exception fe) {  
					fe.printStackTrace();  
				}
			}
		}
		);
		behaviour.addSubBehaviour(new CyclicBehaviour() {
			private List<ACLMessage> listMsg = new ArrayList<ACLMessage>();
			@Override
			public void action() {
				MessageTemplate template= 
						MessageTemplate.or(
							MessageTemplate.MatchPerformative(ACLMessage.REQUEST), 
							MessageTemplate.or(
								MessageTemplate.MatchPerformative(ACLMessage.PROPOSE), 
								MessageTemplate.or(
									MessageTemplate.MatchPerformative(ACLMessage.AGREE),
									MessageTemplate.MatchPerformative(ACLMessage.REFUSE)
								)
							)
						);
				ACLMessage aclMessage = receive(template);
				if(aclMessage != null) {
					switch (aclMessage.getPerformative()) {
						case ACLMessage.REQUEST: {
							ACLMessage msg = new ACLMessage(ACLMessage.CFP);
							msg.setContent(aclMessage.getContent());
							for(AID aid : sellerAgents) msg.addReceiver(aid);
							send(msg);
						} ;
						break;
						case ACLMessage.PROPOSE: {
							listMsg.add(aclMessage);
							if(listMsg.size() == sellerAgents.length) {
								ACLMessage best = listMsg.get(0);
								for(ACLMessage m : listMsg) {
									if(Double.valueOf(m.getContent()) < Double.valueOf(best.getContent())) best = m ;
								}
								ACLMessage replyAccept = best.createReply();
								replyAccept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
								send(replyAccept);
							}
						};
						break;
						case ACLMessage.AGREE: {
							ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
							msg.setContent(aclMessage.getContent());
							msg.addReceiver(new AID("Consumer",AID.ISLOCALNAME));
							send(msg);
						};
						break;
						case ACLMessage.REFUSE:  ;
						break;
					}
					
					
					
					gui.logMsg(aclMessage);
					/*ACLMessage reply = aclMessage.createReply();
					reply.setContent("C'est bon pour le livre "+aclMessage.getContent());
					send(reply);
					
					ACLMessage replySaller = new ACLMessage(ACLMessage.CFP);
					replySaller.setContent(aclMessage.getContent());
					replySaller.addReceiver(new AID("Vendeur",AID.ISLOCALNAME));
					send(replySaller);*/
					
				}
				else block();
			}
		});
		addBehaviour(behaviour);
	}
	
	
	@Override
	public void onGuiEvent(GuiEvent params) {
		
	}

}
