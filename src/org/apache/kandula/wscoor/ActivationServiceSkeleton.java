/**
 * ActivationServiceSkeleton.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis2 version: 1.1.1 Jan 09, 2007 (06:20:51
 * LKT)
 */
package org.apache.kandula.wscoor;

import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.databinding.types.URI;
import org.apache.axis2.databinding.types.UnsignedInt;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
import org.apache.kandula.Constants;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.CoordinationContext;
import org.apache.kandula.coordinator.Coordinator;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.storage.StorageUtils;
import org.apache.kandula.utility.EndpointReferenceFactory;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContext_type3;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CreateCoordinationContextResponse;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CreateCoordinationContextResponseType;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CreateCoordinationContextType;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.Expires_type0;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.Identifier_type1;
import org.xmlsoap.schemas.ws._2004._08.addressing.AttributedURI;
import org.xmlsoap.schemas.ws._2004._08.addressing.EndpointReferenceType;
import org.xmlsoap.schemas.ws._2004._08.addressing.ReferenceParametersType;

/**
 * ActivationServiceSkeleton java skeleton for the axisService
 */
public class ActivationServiceSkeleton {

	public void init(ServiceContext serviceContext)
	{
		
	}
	/**
	 * Auto generated method signature
	 * 
	 * @param param0
	 * @throws AxisFault
	 * 
	 */
	public org.oasis_open.docs.ws_tx.wscoor._2006._06.CreateCoordinationContextResponse CreateCoordinationContextOperation(
			org.oasis_open.docs.ws_tx.wscoor._2006._06.CreateCoordinationContext param0)
			throws AxisFault {
		/*
		 * Extracting data from the incoming message
		 */
		CreateCoordinationContextType createCoordinationContext = param0
				.getCreateCoordinationContext();
		String coordinationType = createCoordinationContext.getCoordinationType().toString();
		Expires_type0 expires = createCoordinationContext.getExpires();
		long expiresL = 0;
		if (expires != null) {
			expiresL = expires.getUnsignedInt().longValue();
		}

		/*
		 * Creating the Coordination Context
		 */
		try {
			Coordinator coordinator = new Coordinator();
			AbstractContext context = coordinator.createCoordinationContext(coordinationType,
					expiresL);
			StorageUtils.putContext(context);

			CoordinationContext coordinationContext = context.getCoordinationContext();

			// filling the data bounded coordination context
			CoordinationContext_type3 coordinationContext_type3 = new CoordinationContext_type3();
			coordinationContext_type3.setCoordinationType(new URI(coordinationContext
					.getCoordinationType()));
			Expires_type0 expires_type0 = new Expires_type0();
			expires_type0.setUnsignedInt(new UnsignedInt(coordinationContext.getExpires()));
			coordinationContext_type3.setExpires(expires_type0);

			coordinationContext_type3.setRegistrationService(EndpointReferenceFactory
					.getADBEPRTypeFromEPR(coordinationContext.getRegistrationService()));

			Identifier_type1 identifier_type1 = new Identifier_type1();
			identifier_type1.setAnyURI(new URI("http", "thilina"));
			coordinationContext_type3.setIdentifier(identifier_type1);

			CreateCoordinationContextResponse response = new CreateCoordinationContextResponse();
			CreateCoordinationContextResponseType createCoordinationContextResponseType = new CreateCoordinationContextResponseType();
			createCoordinationContextResponseType.setCoordinationContext(coordinationContext_type3);
			response.setCreateCoordinationContextResponse(createCoordinationContextResponseType);
			return response;
		} catch (AbstractKandulaException e) {
			AxisFault fault = new AxisFault(e);
			fault.setFaultCode(e.getFaultCode());
			throw fault;
		} catch (MalformedURIException e) {
			AxisFault fault = new AxisFault(e);
			throw fault;
		}

	}
}
