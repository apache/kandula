/*
 * Copyright  2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
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
import org.apache.kandula.context.impl.ParticipantContext;
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
			ParticipantContext participantContext,
			MessageContext messageContext) throws AxisFault {
		try {
			ConfigurationContext axis2ConfigurationContext = KandulaConfiguration
					.getInstance().getPariticipantAxis2ConfigurationContext();
			if (axis2ConfigurationContext == null) {
				axis2ConfigurationContext = messageContext
						.getConfigurationContext();
			}
			EndpointReference participantProtocolService = EndpointReferenceFactory
					.getInstance().getParticipantEndpoint(
							participantContext.getID(),participantContext.getRegistrationProtocol());

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
			throw AxisFault.makeFault(e);
		} catch (AbstractKandulaException e) {
			throw AxisFault.makeFault(e);
		}
	}
}
