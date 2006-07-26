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
package interop;

import java.util.Map;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.kandula.Constants;
import org.apache.kandula.Status;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.CoordinationContext;
import org.apache.kandula.context.impl.ATParticipantContext;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.participant.KandulaResource;
import org.apache.kandula.participant.ParticipantUtility;
import org.apache.kandula.storage.StorageFactory;
import org.apache.kandula.storage.Store;
import org.apache.kandula.wsat.twopc.CoordinatorPortTypeRawXMLStub;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class testService {

	private MessageContext msgcts;

	public testService() {

	}

	public void setOperationContext(OperationContext oc) throws AxisFault {
		msgcts = oc.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
	}

	public OMElement Commit(OMElement element) {
		return getResponseElement();
	}

	public OMElement Rollback(OMElement element) {
		return getResponseElement();
	}

	public OMElement Phase2Rollback(OMElement element) throws AxisFault {

		String reqID = (String) msgcts
				.getProperty(AbstractContext.REQUESTER_ID);
		Store store = StorageFactory.getInstance().getStore();
		ATParticipantContext participantContext = (ATParticipantContext) store
				.get(reqID);
		CoordinationContext coordinationContext = participantContext
				.getCoordinationContext();

		ATParticipantContext participantContext2 = new ATParticipantContext();
		participantContext2.setCoordinationContext(coordinationContext);
		KandulaResource resource1 = new Phase2RollbackTestVolatileResource();
		participantContext2.setResource(resource1);
		store.put(participantContext2.getID(), participantContext2);
		ParticipantUtility.registerParticipant(participantContext2);

		ATParticipantContext participantContext1 = new ATParticipantContext();
		participantContext1.setCoordinationContext(coordinationContext);
		KandulaResource resource = new RollbackTestResource();
		participantContext1.setResource(resource);
		store.put(participantContext1.getID(), participantContext1);
		ParticipantUtility.registerParticipant(participantContext1);

		return getResponseElement();
	}

	public OMElement Readonly(OMElement element) throws AxisFault {

		String reqID = (String) msgcts
				.getProperty(AbstractContext.REQUESTER_ID);
		Store store = StorageFactory.getInstance().getStore();
		ATParticipantContext participantContext = (ATParticipantContext) store
				.get(reqID);
		CoordinationContext coordinationContext = participantContext
				.getCoordinationContext();

		ATParticipantContext participantContext2 = new ATParticipantContext();
		participantContext2.setCoordinationContext(coordinationContext);
		KandulaResource resource1 = new DurableReadOnlyResource();
		participantContext2.setResource(resource1);
		store.put(participantContext2.getID(), participantContext2);
		ParticipantUtility.registerParticipant(participantContext2);

		ATParticipantContext participantContext1 = new ATParticipantContext();
		participantContext1.setCoordinationContext(coordinationContext);
		KandulaResource resource = new CommitTestResource();
		participantContext1.setResource(resource);
		store.put(participantContext1.getID(), participantContext1);
		ParticipantUtility.registerParticipant(participantContext1);

		return getResponseElement();
	}

	public OMElement VolatileAndDurable(OMElement element) throws AxisFault {

		String reqID = (String) msgcts
				.getProperty(AbstractContext.REQUESTER_ID);
		final Store store = StorageFactory.getInstance().getStore();
		ATParticipantContext participantContext = (ATParticipantContext) store
				.get(reqID);
		final CoordinationContext coordinationContext = participantContext
				.getCoordinationContext();

		final ATParticipantContext participantContext2 = new ATParticipantContext();
		participantContext2.setCoordinationContext(coordinationContext);
		KandulaResource resource1 = new VolatileAndDurableTestVolatileResource();
		participantContext2.setResource(resource1);
		store.put(participantContext2.getID(), participantContext2);
		ParticipantUtility.registerParticipant(participantContext2);
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					ATParticipantContext participantContext1 = new ATParticipantContext();
					participantContext1
							.setCoordinationContext(coordinationContext);
					KandulaResource resource = new CommitTestResource();
					participantContext1.setResource(resource);
					store.put(participantContext1.getID(), participantContext1);

					Map referenceParametersmap = participantContext2
							.getCoordinationEPR().getAllReferenceParameters();
					String id = ((OMElement) referenceParametersmap
							.get(Constants.TRANSACTION_ID_PARAMETER)).getText();
					AbstractContext transaction = (AbstractContext) store
							.get(id);
					while (!(transaction.getStatus() == Status.CoordinatorStatus.STATUS_PREPARING_VOLATILE)) {
					}
					ParticipantUtility.registerParticipant(participantContext1);

				} catch (AxisFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		thread.start();
		return getResponseElement();
	}

	public OMElement EarlyAborted(OMElement element) throws AxisFault {

		String reqID = (String) msgcts
				.getProperty(AbstractContext.REQUESTER_ID);
		Store store = StorageFactory.getInstance().getStore();
		ATParticipantContext participantContext = (ATParticipantContext) store
				.get(reqID);
		CoordinationContext coordinationContext = participantContext
				.getCoordinationContext();

		ATParticipantContext participantContext2 = new ATParticipantContext();
		participantContext2.setCoordinationContext(coordinationContext);
		KandulaResource resource1 = new CommitTestResource();
		participantContext2.setResource(resource1);
		store.put(participantContext2.getID(), participantContext2);
		ParticipantUtility.registerParticipant(participantContext2);

		ATParticipantContext participantContext1 = new ATParticipantContext();
		participantContext1.setCoordinationContext(coordinationContext);
		KandulaResource resource = new VolatileReadOnlyResource();
		participantContext1.setResource(resource);
		store.put(participantContext1.getID(), participantContext1);
		ParticipantUtility.registerParticipant(participantContext1);

		CoordinatorPortTypeRawXMLStub stub;
		try {
			stub = new CoordinatorPortTypeRawXMLStub(participantContext1
					.getCoordinationEPR());
			stub.abortedOperation();
		} catch (AbstractKandulaException e) {
			throw new AxisFault(e);
		}

		return getResponseElement();
	}
	private OMElement getResponseElement()
	{
		SOAPFactory factory = OMAbstractFactory.getSOAP12Factory();
		OMNamespace namespace = factory.createOMNamespace("http://fabrikam123.com",null);
		OMElement testType = factory.createOMElement("Response",namespace);
		return testType;
	}
}