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
import org.apache.kandula.coordinator.at.AbstractParticipant;
import org.apache.kandula.wscoor.ActivationPortTypeRPC;
import org.apache.kandula.wscoor.CreateCoordinationContextResponseType;
import org.apache.kandula.wscoor.CreateCoordinationContextType;
import org.apache.kandula.wscoor.Expires;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class CoordinationService implements ActivationPortTypeRPC {

	private String context = KandulaConfig.getInstance().getContext();

	private static CoordinationService instance = new CoordinationService();

	public static CoordinationService getInstance() {
		return instance;
	}

	private CoordinationService() {
	}

	public CoordinationContext createCoordinationContext(
			String coordinationType, long timeout)
			throws UnsupportedCoordinationTypeException, MalformedURIException {
		if (!ATCoordinator.COORDINATION_TYPE_ID.equals(coordinationType))
			throw new UnsupportedCoordinationTypeException();
		Coordinator c = new ATCoordinatorImpl();
		CallbackRegistry.getInstance().registerCallback(c, timeout);
		return c.getCoordinationContext();
	}

	public EndpointReference getActivationCoordinatorService() {
		return getEndpointReference(context + "activationCoordinator");
	}

	public EndpointReference getCompletionCoordinatorService(ATCoordinator c) {
		return getEndpointReference(context + "completionCoordinator", c);
	}

	public EndpointReference getCoordinatorService(ATCoordinator c,
			String participantRef) {
		EndpointReference epr = getEndpointReference(context + "coordinator", c);
		epr.getProperties().add(
			new MessageElement(ATCoordinator.PARTICIPANT_REF, participantRef));
		return epr;
	}

	public EndpointReference getRegistrationCoordinatorService(Coordinator c) {
		return getEndpointReference(context + "registrationCoordinator", c);
	}

	public EndpointReference getCompletionInitiatorService(Callback callback,
			long timeout) {
		CallbackRegistry.getInstance().registerCallback(callback, timeout);
		return getEndpointReference(context + "completionInitiator", callback);
	}

	public EndpointReference getFaultDispatcherService(Callback callback) {
		return getEndpointReference(context + "faultDispatcher", callback);
	}

	public EndpointReference getFaultDispatcherService(Coordinator callback,
			String participantRef) {
		EndpointReference epr = getEndpointReference(context
				+ "faultDispatcher", callback);
		epr.getProperties().add(
			new MessageElement(ATCoordinator.PARTICIPANT_REF, participantRef));
		return epr;
	}

	public EndpointReference getParticipantService(
			AbstractParticipant participant, long timeout) {
		CallbackRegistry.getInstance().registerCallback(participant, timeout);
		return getEndpointReference(context + "participant", participant);
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
		r.add(new MessageElement(CallbackRegistry.CALLBACK_REF,
				callback.getID()));
		epr.setProperties(r);
		return epr;
	}

	public CreateCoordinationContextResponseType createCoordinationContextOperation(
			CreateCoordinationContextType parameters) throws RemoteException {
		String t = parameters.getCoordinationType().toString();
		Expires ex = parameters.getExpires();
		long timeout = 0;
		if (ex != null)
			timeout = ex.get_value().longValue() * 1000;
		CoordinationContext ctx = null;
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