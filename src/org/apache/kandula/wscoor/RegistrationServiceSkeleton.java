/**
 * RegistrationServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.1.1 Jan 09, 2007 (06:20:51 LKT)
 */
package org.apache.kandula.wscoor;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
import org.apache.kandula.Constants;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.coordinator.Coordinator;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.storage.StorageUtils;
import org.apache.kandula.utility.EndpointReferenceFactory;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.RegisterResponse;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.RegisterResponseType;

/**
 *  RegistrationServiceSkeleton java skeleton for the axisService
 */
public class RegistrationServiceSkeleton {

	public void init(ServiceContext serviceContext)
	{
		
	}
	
	/**
	 * Auto generated method signature
	 
	 * @param param0
	 * @throws AxisFault 
	 
	 */
	public org.oasis_open.docs.ws_tx.wscoor._2006._06.RegisterResponse RegisterOperation(
			org.oasis_open.docs.ws_tx.wscoor._2006._06.Register param0) throws AxisFault

	{

		String protocolIdentifier;
		EndpointReference participantEPR;
		String activityId;
		String participantId=null;
		/*
		 * Extracting data from the received message
		 */
		protocolIdentifier = param0.getRegister().getProtocolIdentifier().toString();
		// Extracting the participant EPR
		participantEPR = EndpointReferenceFactory
				.getEPR(param0.getRegister().getParticipantProtocolService());

		OMElement header = MessageContext.getCurrentMessageContext()
				.getEnvelope().getHeader();
		activityId = header.getFirstChildWithName(
				Constants.TRANSACTION_ID_PARAMETER).getText();
		OMElement participantIDElement = header.getFirstChildWithName(
				Constants.PARTICIPANT_ID_PARAMETER);
		if (participantIDElement != null) {
			participantId = participantIDElement.getText();
		}
		/*
		 * Registering the participant for the activity for the given protocol
		 */
		try {
			Coordinator coordinator = new Coordinator();
			AbstractContext context = (AbstractContext) StorageUtils
					.getContext(activityId);
			if (context == null) {
				throw new IllegalStateException(
						"No Activity Found for this Activity ID");
			}
			EndpointReference epr = coordinator.registerParticipant(context,
					protocolIdentifier, participantEPR,participantId);
			
			RegisterResponseType registerResponseType = new RegisterResponseType();
			registerResponseType.setCoordinatorProtocolService(EndpointReferenceFactory.getEPRTypeFromEPR(epr));
			RegisterResponse registerResponse = new RegisterResponse();
			registerResponse.setRegisterResponse(registerResponseType);
			return registerResponse;
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
