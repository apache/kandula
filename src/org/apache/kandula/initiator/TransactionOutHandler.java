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

import javax.xml.namespace.QName;

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
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContext_type0;
import org.xmlsoap.schemas.ws._2004._08.addressing.ReferenceParametersType;

public class TransactionOutHandler extends AbstractHandler {

	/**
	 * Field log
	 */
	private static final Log log = LogFactory
			.getLog(TransactionOutHandler.class);

	private static final long serialVersionUID = 4133392345837905499L;

	public InvocationResponse invoke(MessageContext msgContext)
			throws AxisFault {

		String wsaAction = msgContext.getWSAAction();
		if (!(Constants.WS_COOR_CREATE_COORDINATIONCONTEXT.equals(wsaAction))
				&& !(Constants.WS_COOR_REGISTER.equals(wsaAction))
				&& !(Constants.WS_AT_COMMIT.equals(wsaAction))
				&& !(Constants.WS_AT_ROLLBACK.equals(wsaAction))) {
			Object context = null;
			try {
				context = TransactionManager.getTransaction();
			} catch (AbstractKandulaException e) {
				throw AxisFault.makeFault(e);
			}
			if (context == null) {
				context = msgContext
						.getProperty(Constants.Configuration.TRANSACTION_CONTEXT);
			}
			// We let the message to pass through if no transaction is found in
			// the thread or in msgContext
			if (context != null) {
				Object registrationID = msgContext
						.getProperty(Constants.Configuration.PARTICIPANT_IDENTIFIER);
				AbstractContext txContext = (AbstractContext) context;
				SOAPHeader soapHeader = msgContext.getEnvelope().getHeader();
				CoordinationContext coorContext = txContext
						.getCoordinationContext();

				try {
					// ws-ba users can set a identifier for the participants
					if (registrationID != null) {
						CoordinationContext_type0 context_type32 = null;
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

						coorContext.toOM().serialize(byteArrayOutputStream);
						context_type32 = CoordinationContext_type0.Factory
								.parse(StAXUtils
										.createXMLStreamReader(new ByteArrayInputStream(
												byteArrayOutputStream
														.toByteArray())));
						context_type32.setExtraAttributes(null);

						ReferenceParametersType referenceParametersType = context_type32
								.getRegistrationService()
								.getReferenceParameters();
						OMElement omElement = soapHeader.getOMFactory()
								.createOMElement(
										Constants.PARTICIPANT_ID_PARAMETER,
										null);
						omElement.setText((String) registrationID);
						referenceParametersType.addExtraElement(omElement);
						soapHeader.addChild(context_type32.getOMElement(
								new QName(Constants.WS_COOR,
										"CoordinationContext"), soapHeader
										.getOMFactory()));

						log.info("Transaction Context found for message ID"
								+ msgContext.getMessageID()
								+ ". Participant ID :" + registrationID);

					} else {
						soapHeader.addChild(coorContext.toOM());
						log.info("Transaction Context found for message ID"
								+ msgContext.getMessageID());
					}
				} catch (Exception e) {
					throw AxisFault.makeFault(e);
				}

			} else {
				log
						.debug("Transaction Handler Engaged. "
								+ "But no transaction information was found in the thread.");
			}
		}
		return InvocationResponse.CONTINUE;
	}
}
