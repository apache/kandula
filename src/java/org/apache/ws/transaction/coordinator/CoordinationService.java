/*
 * Created on Dec 25, 2005
 *
 */
package org.apache.ws.transaction.coordinator;

import java.rmi.RemoteException;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.PortType;
import org.apache.axis.message.addressing.ReferencePropertiesType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.ws.transaction.coordinator.at.ATCoordinator;
import org.apache.ws.transaction.coordinator.at.ATCoordinatorImpl;
import org.apache.ws.transaction.utility.CallbackRegistry;
import org.apache.ws.transaction.utility.EndpointReferenceFactory;
import org.apache.ws.transaction.wscoor.ActivationPortTypeRPC;
import org.apache.ws.transaction.wscoor.CreateCoordinationContextResponseType;
import org.apache.ws.transaction.wscoor.CreateCoordinationContextType;
import org.apache.ws.transaction.wscoor.Expires;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class CoordinationService implements ActivationPortTypeRPC {
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

	private CoordinationContext createCoordinationContext(
			String coordinationType, long timeout)
			throws UnsupportedCoordinationTypeException, MalformedURIException {
		if (!ATCoordinator.COORDINATION_TYPE_ID.equals(coordinationType))
			throw new UnsupportedCoordinationTypeException();
		final Coordinator c = new ATCoordinatorImpl();
		CallbackRegistry.getInstance().registerCallback(c.getID(), c, timeout);
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

	public CreateCoordinationContextResponseType createCoordinationContextOperation(
			CreateCoordinationContextType parameters) throws RemoteException {
		String t = parameters.getCoordinationType().toString();
		Expires ex = parameters.getExpires();
		long timeout = 0;
		if (ex != null)
			timeout = ex.get_value().longValue() * 1000;
		CoordinationContext ctx;
		try {
			ctx = createCoordinationContext(t, timeout);
		} catch (MalformedURIException e) {
			e.printStackTrace();
			throw new RemoteException(e.toString());
		} catch (UnsupportedCoordinationTypeException e) {
			e.printStackTrace();
			throw new RemoteException(e.toString());
		}
		CreateCoordinationContextResponseType r = new CreateCoordinationContextResponseType();
		r.setCoordinationContext(ctx);
		return r;
	}
}