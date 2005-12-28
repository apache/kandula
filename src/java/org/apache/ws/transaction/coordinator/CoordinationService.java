/*
 * Created on Dec 25, 2005
 *
 */
package org.apache.ws.transaction.coordinator;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.PortType;
import org.apache.axis.message.addressing.ReferencePropertiesType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.ws.transaction.coordinator.at.ATCoordinator;
import org.apache.ws.transaction.coordinator.at.ATCoordinatorImpl;
import org.apache.ws.transaction.utility.CallbackRegistry;
import org.apache.ws.transaction.utility.EndpointReferenceFactory;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class CoordinationService {
	public static PortType ACTIVATION_SERVICE = new PortType(
			"http://schemas.xmlsoap.org/ws/2004/10/wscoor",
			"ActivationPortTypeRPC");

	public static PortType COMPLETION_COORDINATOR_SERVICE = new PortType(
			"http://schemas.xmlsoap.org/ws/2004/10/wsat",
			"CompletionCoordinatorPortType");

	public static PortType COORDINATOR_SERVICE = new PortType(
			"http://schemas.xmlsoap.org/ws/2004/10/wsat", "CoordinatorPortType");

	private static CoordinationService instance = new CoordinationService();

	public static PortType REGISTRATION_SERVICE = new PortType(
			"http://schemas.xmlsoap.org/ws/2004/10/wscoor",
			"RegistrationPortTypeRPC");

	public static CoordinationService getInstance() {
		return instance;
	}

	private CoordinationService() {
	}

	public CoordinationContext createCoordinationContext(String coordinationType)
			throws UnsupportedCoordinationTypeException, MalformedURIException {
		if (!ATCoordinator.COORDINATION_TYPE_ID.equals(coordinationType))
			throw new UnsupportedCoordinationTypeException();
		Coordinator c = new ATCoordinatorImpl();
		CallbackRegistry.getInstance().registerCallback(c.getID(), c);
		return c.getCoordinationContext();
	}

	public EndpointReference getActivationService() {
		return EndpointReferenceFactory.getInstance().getEndpointReference(
			ACTIVATION_SERVICE, null);
	}

	public EndpointReference getCompletionCoordinatorService(ATCoordinator c) {
		ReferencePropertiesType r = new ReferencePropertiesType();
		r.add(new MessageElement(CallbackRegistry.COORDINATOR_REF, c.getID()));
		return EndpointReferenceFactory.getInstance().getEndpointReference(
			COMPLETION_COORDINATOR_SERVICE, r);
	}

	public EndpointReference getCoordinatorService(ATCoordinator c, String ref) {
		ReferencePropertiesType r = new ReferencePropertiesType();
		r.add(new MessageElement(CallbackRegistry.COORDINATOR_REF, c.getID()));
		r.add(new MessageElement(ATCoordinator.PARTICIPANT_REF, ref));
		return EndpointReferenceFactory.getInstance().getEndpointReference(
			COORDINATOR_SERVICE, r);
	}

	public EndpointReference getRegistrationService(Coordinator c) {
		ReferencePropertiesType r = new ReferencePropertiesType();
		r.add(new MessageElement(CallbackRegistry.COORDINATOR_REF, c.getID()));
		return EndpointReferenceFactory.getInstance().getEndpointReference(
			REGISTRATION_SERVICE, r);
	}
}