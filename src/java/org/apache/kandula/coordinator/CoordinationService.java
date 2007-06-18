/*
 * Created on Dec 25, 2005
 *
 */
package org.apache.kandula.coordinator;

import java.rmi.RemoteException;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.ReferencePropertiesType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.kandula.KandulaConfig;
import org.apache.kandula.coordinator.at.ATCoordinator;
import org.apache.kandula.coordinator.at.ATCoordinatorImpl;
import org.apache.kandula.coordinator.ba.ProtocolType;
import org.apache.kandula.coordinator.ba.coordinator.BACoordinator;
import org.apache.kandula.coordinator.ba.coordinator.BAwithAtomicOutcomeCoordinator;
import org.apache.kandula.coordinator.ba.coordinator.BAwithMixedOutcomeCoordinator;
import org.apache.kandula.coordinator.ba.coordinator.InvalidOutcomeAssertion;
import org.apache.kandula.coordinator.ba.initiator.AbstractCoordinatorIProxy;
import org.apache.kandula.wscoor.ActivationPortTypeRPC;
import org.apache.kandula.wscoor.CreateCoordinationContextResponseType;
import org.apache.kandula.wscoor.CreateCoordinationContextType;
import org.apache.kandula.wscoor.CreateCoordinationContextTypeCurrentContext;
import org.apache.kandula.wscoor.Expires;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class CoordinationService implements ActivationPortTypeRPC {

	private String contextClient = KandulaConfig.getInstance().getContext();
	private String contextServices = KandulaConfig.getInstance().getKandulaServicesURL();

	private static CoordinationService instance = new CoordinationService();

	public static CoordinationService getInstance() {
		return instance;
	}

	private CoordinationService() {
		// no public constructor needed
	}

	public CoordinationContext createCoordinationContext(

			final String coordinationType,
			final long timeout, 
			final CreateCoordinationContextTypeCurrentContext superContext
	) throws UnsupportedCoordinationTypeException, MalformedURIException,
			InvalidOutcomeAssertion{
		
		// Placeholder for the coordinator to be instantiated
		final Coordinator c;
		
		if (BACoordinator.COORDINATION_TYPE__MIXED.equals(coordinationType)){
			// Create a Business Activity with mixed outcome
			
			c = new BAwithMixedOutcomeCoordinator(superContext);
		}else if (BACoordinator.COORDINATION_TYPE__ATOMIC.equals(coordinationType)){
			// Create a Business Activity with atomic outcome
			c = new BAwithAtomicOutcomeCoordinator(superContext);
			
		}else if (ATCoordinator.COORDINATION_TYPE_ID.equals(coordinationType)){
			// Create an Atomic Transaction 
			
			if (superContext != null){
				throw new UnsupportedOperationException("Kandula does currently not support the creation of sub-ordinate coordination contexts with atomic coordination contexts.");
			}
			c = new ATCoordinatorImpl();
			
		}else{
			// There was an unknown coordination context identifier supplied
			throw new UnsupportedCoordinationTypeException();			
		}

		/* 
		 * Create a new coordination context and register
		 * the coordinator for retrieval on incoming messages.
		 * 
		 */

		CallbackRegistry.getInstance().registerCallback(c, timeout);
		
		return c.getCoordinationContext();
	}

	/**
	 * The Endpoint of the local demo service endpoint
	 * @return
	 */
	public EndpointReference getDemoService() {
		return getEndpointReference(this.contextClient + "TellMeWhatToDoDemoService");
	}
	
	public EndpointReference getActivationCoordinatorService() {
		return getEndpointReference(this.contextServices + "activationCoordinator");
	}

	public EndpointReference getCompletionCoordinatorService(ATCoordinator c) {
		return getEndpointReference(this.contextServices + "completionCoordinator", c);
	}

	public EndpointReference getCoordinatorService(ATCoordinator c,
			String participantRef) {
		EndpointReference epr = getEndpointReference(this.contextServices + "coordinator", c);
		epr.getProperties().add(
			new MessageElement(Coordinator.PARTICIPANT_REF, participantRef));
		return epr;
	}

	public EndpointReference getCompletionInitiatorService(Callback callback,
			long timeout) {
		CallbackRegistry.getInstance().registerCallback(callback, timeout);
		return getEndpointReference(this.contextClient + "completionInitiator", callback);
	}

	public EndpointReference getFaultDispatcherService(Callback callback) {
		return getEndpointReference(this.contextServices + "faultDispatcher", callback);
	}

	public EndpointReference getFaultDispatcherService(Callback callback,
			String participantRef) {
		EndpointReference epr = getEndpointReference(this.contextServices
				+ "faultDispatcher", callback);
		epr.getProperties().add(
			new MessageElement(Coordinator.PARTICIPANT_REF, participantRef));
		return epr;
	}

	/**
	 * Atomic Transaction Particpant Service Endpoint
	 * @param participant
	 * @param timeout
	 * @return
	 */
	public EndpointReference getParticipantService(
			org.apache.kandula.coordinator.at.AbstractParticipant participant, long timeout) {
		CallbackRegistry.getInstance().registerCallback(participant, timeout);
		return getEndpointReference(this.contextClient + "participant", participant);
	}

	/**
	 * Business Activity Participant Service Endpoint
	 * @param participant
	 * @param timeout
	 * @return
	 */
	public EndpointReference getParticipantService(
			final org.apache.kandula.coordinator.ba.participant.AbstractParticipant participant,
			final long timeout
	) {
		CallbackRegistry.getInstance().registerCallback(participant, timeout);
		
		final String contextType;
		if (ProtocolType.PROTOCOL_ID_CC.equals(participant.getProtocolType())){
			contextType="CC";
		}else if (ProtocolType.PROTOCOL_ID_PC.equals(participant.getProtocolType())){
			contextType="PC";
		}else{
			throw new IllegalArgumentException("Sorry, invalid protocol type: "+participant.getProtocolType()+" for getParticipantService");
		}
			return getEndpointReference(this.contextClient + "kandula_BA_"+contextType+"_participant", participant);
	}

	/**
	 * Returns the WS-BA initiator endpoint location.
	 * @param initiator Reference to the coordination context the initiator shall register with
	 * @param timeout
	 * @return A ready-to-use endpoint reference.
	 */
	public EndpointReference getInitiatorService(
			AbstractCoordinatorIProxy initiator, long timeout) {
		CallbackRegistry.getInstance().registerCallback(initiator, timeout);
		return getEndpointReference(this.contextClient + "initiator", initiator);
	}

	private EndpointReference getEndpointReference(String uri) {
		try {
			return new EndpointReference(uri);
		} catch (MalformedURIException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	private EndpointReference getEndpointReference(String uri, Callback callback) {
		EndpointReference epr = getEndpointReference(uri);
		ReferencePropertiesType r = new ReferencePropertiesType();
		r.add(new MessageElement(CallbackRegistry.CALLBACK_REF, callback.getID()));
		
		epr.setProperties(r);
		return epr;
	}

	private EndpointReference getEndpointReference(String uri, String id) {
		EndpointReference epr = getEndpointReference(uri);
		ReferencePropertiesType r = new ReferencePropertiesType();
		r.add(new MessageElement(CallbackRegistry.CALLBACK_REF, id));
		
		epr.setProperties(r);
		return epr;
	}

	public CreateCoordinationContextResponseType createCoordinationContextOperation(
			CreateCoordinationContextType parameters
	) throws RemoteException {
		
		String t = parameters.getCoordinationType().toString();
		Expires ex = parameters.getExpires();
		long timeout = 0;
		
		if (ex != null)
			timeout = ex.get_value().longValue() * 1000;
		CoordinationContext ctx = null;

		try {
			ctx = createCoordinationContext(t, timeout, parameters.getCurrentContext());
		} catch (MalformedURIException e) {
			e.printStackTrace();
			throw new RemoteException(e.toString());
		} catch (UnsupportedCoordinationTypeException e) {
			e.printStackTrace();
			throw new RemoteException(e.toString());
		} catch (InvalidOutcomeAssertion e){
			e.printStackTrace();
			throw new RemoteException(e.toString());			
		}
		CreateCoordinationContextResponseType r = new CreateCoordinationContextResponseType();
		r.setCoordinationContext(ctx);
		
		return r;
	}

	/**
	 * Returns an EndpointReference to the BACoordinator. This EPR can be used
	 * by the initiator to send instructions to the coordinator. Requests are correlated
	 * to the BA context by 
	 * 
	 * @param c The BACoordinator (context) that the initiator endpoint will be used for.
	 * @param initiatorRef The reference code that is used to authenticate the initiator.
	 * @return The finished EndPointReference
	 */
	public EndpointReference getInitiatorCoordinatorService(
			final BACoordinator c, final String initiatorRef
	) {
		
		final String serviceLocation;
		if (BACoordinator.COORDINATION_TYPE__ATOMIC.equals( c.getOutcomeType() )){
			serviceLocation = "kandula_initiatorService_Atomic";
		}else if (BACoordinator.COORDINATION_TYPE__MIXED.equals( c.getOutcomeType()) ){
			serviceLocation = "kandula_initiatorService_Mixed";
		}else{
			throw new IllegalArgumentException("No location known for initiator service and a "+c.getOutcomeType()+" outcome assertion.");
		}
		
		final EndpointReference epr = getEndpointReference(
				this.contextServices + serviceLocation,
				c
		);
		
		epr.getProperties().add(
				new MessageElement(BACoordinator.INITIATOR_REF, initiatorRef)
		);
		
		return epr;
	}

	/**
	 * Returns an EndpointReference to the BACoordinator, with the
	 * participant ref set ready for calling home. ;-)
	 * This EPR can be used by participants to get in touch with the coordinator.
	 * @param c The BACoordinator.
	 * @param participantRef The ref to the participant.
	 * @return The finished EndpointReference.
	 * @throws InvalidCoordinationProtocolException 
	 */
	public EndpointReference getCoordinatorService(
			final BACoordinator c,
			final String participantRef,
			final String participantProtocol
	) throws InvalidCoordinationProtocolException {
		
		final String coordExt;
		if (participantProtocol.equals(ProtocolType.PROTOCOL_ID_CC.getNamespaceURI()+ProtocolType.PROTOCOL_ID_CC.getLocalPart()))
			coordExt="kandula_BA_CC_coordinator";
		else if (participantProtocol.equals(ProtocolType.PROTOCOL_ID_PC.getNamespaceURI()+ProtocolType.PROTOCOL_ID_PC.getLocalPart()))
			coordExt="kandula_BA_PC_coordinator";
		else
			throw new InvalidCoordinationProtocolException();			

		final EndpointReference epr = getEndpointReference(this.contextServices + coordExt, c);
		epr.getProperties().add(
			new MessageElement(Coordinator.PARTICIPANT_REF, participantRef)
		);
		return epr;
	}

	public EndpointReference getRegistrationCoordinatorService(final Coordinator c, final String id) {
		return getEndpointReference(this.contextServices + "registrationCoordinator", id);
	}

}
