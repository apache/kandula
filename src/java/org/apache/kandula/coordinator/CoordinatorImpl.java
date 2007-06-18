/*
 * Created on Dec 23, 2005
 *
 */
package org.apache.kandula.coordinator;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.kandula.utils.SoapFaultSender;
import org.apache.kandula.wscoor.RegisterResponseType;
import org.apache.kandula.wscoor.RegisterType;

/**
 * An abstract implementation of the Coordinator interface.
 * 
 * @author Dasarath Weeratunge, Hannes Erven, Georg Hicker
 *  
 */
public abstract class CoordinatorImpl extends Coordinator {

	/**
	 * 
	 * This header element is set to a value the initiator chose for the
	 * participant before forwarding the registration service endpoint.
	 */
	public final static QName INITIATOR_MATCHCODE = new QName(
			"http://ws.apache.org/kandula/ws-ba--extension", "matchcode"
	);
	
	/**
	 * T
	 */
	private CoordinationContext ctx;

	protected CoordinatorImpl(String coordinationType)
			throws MalformedURIException 
	{
		UUIDGen gen = UUIDGenFactory.getUUIDGen();
		final String id = "uuid:" + gen.nextUUID();
		
		CoordinationService cs = CoordinationService.getInstance();
		EndpointReference epr = cs.getRegistrationCoordinatorService(this, id);

		this.ctx = new CoordinationContext(id, coordinationType, epr);
	}

	public String getID() {
		final String id = this.ctx.getIdentifier().get_value().toString();
		return id;
	}

	public CoordinationContext getCoordinationContext() {
		return this.ctx;
	}

	/**
	 * Welcome a new participant in a transaction context.
	 * @param prot The protocol the participant wishes to register for
	 * @param pps The participant's protocol service endpoint
	 * @param referenceCode The reference code the initiator provided to the participant
	 * @return The coordinator's endpoint reference for this participant
	 * @throws InvalidCoordinationProtocolException
	 */
	public abstract EndpointReference register(
			final String prot,
			final EndpointReference pps,
			final String referenceCode
	) throws InvalidCoordinationProtocolException;

	
	/**
	 * Add a participant matchcode to the default registration service.
	 * ATTENTION: at this point there are no checks!
	 * @param participantMATCHCODE The matchcode
	 * @return The service.
	 */
	protected CoordinationContext getCoordinationContext(final String participantMATCHCODE) {
		final CoordinationContext ctx = this.getCoordinationContext();
		
		// Duplicate the coordination context!
		final CoordinationContext r = 
			new CoordinationContext(
					ctx.getIdentifier(), 
					ctx.getCoordinationType(), 
					new EndpointReference(ctx.getRegistrationService())
			);

		final MessageElement participantIDMessageElement = 
			new MessageElement(INITIATOR_MATCHCODE, participantMATCHCODE);
		r.getRegistrationService().getProperties().add(participantIDMessageElement);

		return r;
	}

	public synchronized RegisterResponseType registerOperation(
			RegisterType params) throws RemoteException {

		EndpointReference epr = null;
		try {
			epr = register(
				params.getProtocolIdentifier().toString(),
				new EndpointReference(params.getParticipantProtocolService()),
				CallbackRegistry.getRef(INITIATOR_MATCHCODE)
			);
		} catch (InvalidCoordinationProtocolException e) {
			throw INVALID_PROTOCOL_SOAP_FAULT();

		} catch (IllegalStateException e) {
			throw INVALID_STATE_SOAP_FAULT();


		} catch (IllegalArgumentException e) {
			throw INVALID_PARAMETERS_SOAP_FAULT();

		}

		RegisterResponseType r = new RegisterResponseType();
		r.setCoordinatorProtocolService(epr);
		return r;
	}

	protected void trigger(EndpointReference faultTo, AxisFault fault)
			throws AxisFault {
		try {
			SoapFaultSender.invoke(faultTo,
				fault.getFaultCode().getNamespaceURI() + "/fault", fault);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		} catch (ServiceException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
