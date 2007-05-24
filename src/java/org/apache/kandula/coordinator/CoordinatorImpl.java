/*
 * Created on Dec 23, 2005
 *
 */
package org.apache.kandula.coordinator;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.kandula.utils.SoapFaultSender;
import org.apache.kandula.wscoor.RegisterResponseType;
import org.apache.kandula.wscoor.RegisterType;

/**
 * @author Dasarath Weeratunge
 *  
 */
public abstract class CoordinatorImpl extends Coordinator {

	private String id;

	private CoordinationContext ctx;

	protected CoordinatorImpl(String coordinationType)
			throws MalformedURIException {
		UUIDGen gen = UUIDGenFactory.getUUIDGen();
		id = "uuid:" + gen.nextUUID();
		CoordinationService cs = CoordinationService.getInstance();
		EndpointReference epr = cs.getRegistrationCoordinatorService(this);
		ctx = new CoordinationContext(id, coordinationType, epr);
	}

	public String getID() {
		return id;
	}

	public CoordinationContext getCoordinationContext() {
		return ctx;
	}

	public abstract EndpointReference register(String prot,
			EndpointReference pps) throws InvalidCoordinationProtocolException;

	public synchronized RegisterResponseType registerOperation(
			RegisterType params) throws RemoteException {

		EndpointReference epr = null;
		try {
			epr = register(params.getProtocolIdentifier().toString(),
				new EndpointReference(params.getParticipantProtocolService()));

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