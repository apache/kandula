/*
 * Created on Dec 25, 2005
 *
 */
package org.apache.ws.transaction.coordinator;

import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.PortType;
import org.apache.axis.message.addressing.ReferencePropertiesType;
import org.apache.ws.transaction.utility.CallbackRegistry;
import org.apache.ws.transaction.utility.EndpointReferenceFactory;
import org.apache.ws.transaction.wsat.CompletionInitiatorPortType;
import org.apache.ws.transaction.wsat.ParticipantPortType;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class ParticipantService {
	public static PortType COMPLETION_INITIATOR_SERVICE = new PortType(
			"http://schemas.xmlsoap.org/ws/2004/10/wsat",
			"CompletionInitiatorPortType");

	public static PortType PARTICIPANT_SERVICE = new PortType(
			"http://schemas.xmlsoap.org/ws/2004/10/wsat", "ParticipantPortType");

	private static ParticipantService instance = new ParticipantService();

	private ParticipantService() {
	}

	public static ParticipantService getInstance() {
		return instance;
	}

	public EndpointReference getCompletionInitiatorService(
			CompletionInitiatorPortType callback) {
		String urn = "uuid" + UUIDGenFactory.getUUIDGen().nextUUID();
		CallbackRegistry.getInstance().registerCallback(urn, callback);
		ReferencePropertiesType r = new ReferencePropertiesType();
		r.add(new MessageElement(CallbackRegistry.CALLBACK_REF, urn));
		return EndpointReferenceFactory.getInstance().getEndpointReference(
			COMPLETION_INITIATOR_SERVICE, r);
	}

	public EndpointReference getParticipantService(ParticipantPortType callback) {
		String urn = "uuid" + UUIDGenFactory.getUUIDGen().nextUUID();
		CallbackRegistry.getInstance().registerCallback(urn, callback);
		ReferencePropertiesType r = new ReferencePropertiesType();
		r.add(new MessageElement(CallbackRegistry.CALLBACK_REF, urn));
		return EndpointReferenceFactory.getInstance().getEndpointReference(
			PARTICIPANT_SERVICE, r);
	}

	public void forget(Object callback) {
		CallbackRegistry.getInstance().remove(callback);
	}

}