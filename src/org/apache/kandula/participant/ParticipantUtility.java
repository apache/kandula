package org.apache.kandula.participant;

import java.io.IOException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.kandula.context.impl.ATParticipantContext;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.utility.EndpointReferenceFactory;
import org.apache.kandula.utility.KandulaConfiguration;
import org.apache.kandula.wscoor.RegistrationCoordinatorPortTypeRawXMLStub;

public class ParticipantUtility {
	public static void registerParticipant(
			ATParticipantContext participantContext) throws AxisFault {
		try {
			ConfigurationContext axis2ConfigurationContext = KandulaConfiguration
					.getInstance().getPariticipantAxis2ConfigurationContext();
			RegistrationCoordinatorPortTypeRawXMLStub stub = new RegistrationCoordinatorPortTypeRawXMLStub(
					axis2ConfigurationContext, participantContext
							.getCoordinationContext().getRegistrationService());
			EndpointReference participantProtocolService = EndpointReferenceFactory
					.getInstance().get2PCParticipantEndpoint(
							participantContext.getID());
			stub.registerOperation(
					participantContext.getRegistrationProtocol(),
					participantContext.getID(), participantProtocolService,
					false);
		} catch (IOException e) {
			throw new AxisFault(e);
		} catch (AbstractKandulaException e) {
			AxisFault e1 = new AxisFault(e);
			throw e1;
		}
	}
}
