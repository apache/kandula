/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *  
 */
package org.apache.kandula.initiator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kandula.Constants;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.CoordinationContext;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.faults.KandulaGeneralException;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContext_type3;
import org.xmlsoap.schemas.ws._2004._08.addressing.ReferenceParametersType;

public class TransactionOutHandler extends AbstractHandler {

	/**
	 * Field log
	 */
	private static final Log log = LogFactory.getLog(TransactionOutHandler.class);

	private static ThreadLocal threadInfo = new ThreadLocal();

	private static final long serialVersionUID = 4133392345837905499L;

	public InvocationResponse invoke(MessageContext msgContext) throws AxisFault {

		InitiatorContext initiatorTransaction;
		String wsaAction = msgContext.getWSAAction();
		if ((wsaAction != Constants.WS_COOR_CREATE_COORDINATIONCONTEXT)
				&& (wsaAction != Constants.WS_COOR_REGISTER)
				&& (wsaAction != Constants.WS_AT_COMMIT) && (wsaAction != Constants.WS_AT_ROLLBACK)) {
			Object context = null;
			try {
				context = TransactionManager.getTransaction();
			} catch (AbstractKandulaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (context == null) {
				context = msgContext.getProperty(Constants.Configuration.TRANSACTION_CONTEXT);
			}
			// We let the message to pass through if no transaction is found in
			// the thread or in msgContext
			if (context != null) {
				Object registrationID = msgContext
						.getProperty(Constants.Configuration.PARTICIPANT_IDENTIFIER);
				AbstractContext txContext = (AbstractContext) context;
				SOAPHeader soapHeader = msgContext.getEnvelope().getHeader();
				CoordinationContext coorContext = txContext.getCoordinationContext();
	
				//ws-ba users can set a identifier for the participants
				if (registrationID != null) {
					CoordinationContext_type3 context_type32 = null;
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					try {
						coorContext.toOM().serialize(byteArrayOutputStream);
						context_type32 = CoordinationContext_type3.Factory.parse(StAXUtils
								.createXMLStreamReader(new ByteArrayInputStream(
										byteArrayOutputStream.toByteArray())));
						context_type32.setExtraAttributes(null);
					} catch (Exception e) {
						throw new AxisFault(e);
					}
					ReferenceParametersType referenceParametersType = context_type32
							.getRegistrationService().getReferenceParameters();
					OMElement omElement = soapHeader.getOMFactory().createOMElement(
							Constants.PARTICIPANT_ID_PARAMETER, null);
					omElement.setText((String) registrationID);
					referenceParametersType.addExtraElement(omElement);
					soapHeader.addChild(context_type32.getOMElement(new QName(Constants.WS_COOR,
							"CoordinationContext"), soapHeader.getOMFactory()));
				} else {
					soapHeader.addChild(coorContext.toOM());
				}

			} else {
				log.debug("Transaction Handler Engaged. "
						+ "But no transaction information was found in the thread.");
			}
		}
		return InvocationResponse.CONTINUE;
	}

	private static void addParticipantIdentifier(OMElement coorContext, String participantID) {
		// Opps.. OMSourcedElementImpl.build() is broken
		coorContext.getFirstOMChild();
		OMElement registrationEPRElement = coorContext.getFirstChildWithName(new QName(
				"RegistrationService", Constants.WS_COOR));
		OMElement refParameters = registrationEPRElement.getFirstChildWithName(new QName(
				"ReferenceParameters", "http://schemas.xmlsoap.org/ws/2004/08/addressing"));
		OMElement omElement = registrationEPRElement.getOMFactory().createOMElement(
				Constants.PARTICIPANT_ID_PARAMETER, null);
		omElement.setText(participantID);
		refParameters.addChild(omElement);
	}
}
