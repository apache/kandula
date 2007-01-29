package org.apache.kandula.participant;

import java.io.IOException;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.databinding.types.URI;
import org.apache.kandula.Constants;
import org.apache.kandula.context.impl.ATParticipantContext;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.utility.EndpointReferenceFactory;
import org.apache.kandula.utility.KandulaConfiguration;
import org.apache.kandula.wscoor.RegistrationServiceStub;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.Register;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.RegisterResponse;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.RegisterType;
import org.xmlsoap.schemas.ws._2004._08.addressing.AttributedURI;
import org.xmlsoap.schemas.ws._2004._08.addressing.EndpointReferenceType;
import org.xmlsoap.schemas.ws._2004._08.addressing.ReferenceParametersType;

public class ParticipantUtility {
	public static void registerParticipant(
			ATParticipantContext participantContext,
			MessageContext messageContext) throws AxisFault {
		try {
			ConfigurationContext axis2ConfigurationContext = KandulaConfiguration
					.getInstance().getPariticipantAxis2ConfigurationContext();
			if (axis2ConfigurationContext == null) {
				axis2ConfigurationContext = messageContext
						.getConfigurationContext();
			}
			EndpointReference participantProtocolService = EndpointReferenceFactory
					.getInstance().get2PCParticipantEndpoint(
							participantContext.getID());

			RegistrationServiceStub registrationCoordinator = new RegistrationServiceStub(
					axis2ConfigurationContext, null);
			registrationCoordinator._getServiceClient().setTargetEPR(
					participantContext.getCoordinationContext()
							.getRegistrationService());

			Register register = new Register();
			RegisterType registerType = new RegisterType();
			registerType.setProtocolIdentifier(new URI(participantContext
					.getRegistrationProtocol()));
			EndpointReferenceType endpointReferenceType = new EndpointReferenceType();
			AttributedURI attributedURI = new AttributedURI();
			attributedURI.setAnyURI(new URI(participantProtocolService
					.getAddress()));
			endpointReferenceType.setAddress(attributedURI);
			// setting reference parameters
			ReferenceParametersType referenceParametersType = new ReferenceParametersType();
			Map map = participantProtocolService.getAllReferenceParameters();
			OMElement element = (OMElement) map
					.get(Constants.REQUESTER_ID_PARAMETER);
			referenceParametersType.addExtraElement(element);
			endpointReferenceType
					.setReferenceParameters(referenceParametersType);
			registerType.setParticipantProtocolService(endpointReferenceType);
			register.setRegister(registerType);
			// Actual WS call for registeration
			RegisterResponse registerResponse = registrationCoordinator
					.RegisterOperation(register);
			participantContext.setCoordinationEPR(EndpointReferenceFactory
					.getEPR(registerResponse.getRegisterResponse()
							.getCoordinatorProtocolService()));

		} catch (IOException e) {
			throw new AxisFault(e);
		} catch (AbstractKandulaException e) {
			AxisFault e1 = new AxisFault(e);
			throw e1;
		}
	}
}
