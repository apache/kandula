/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
package org.apache.kandula.utility;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.addressing.AddressingConstants.Final;
import org.apache.axis2.databinding.types.URI;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
import org.apache.kandula.Constants;
import org.xmlsoap.schemas.ws._2004._08.addressing.AttributedURI;
import org.xmlsoap.schemas.ws._2004._08.addressing.EndpointReferenceType;
import org.xmlsoap.schemas.ws._2004._08.addressing.ReferenceParametersType;

public class EndpointReferenceFactory {

	private static EndpointReferenceFactory instance = null;

	private KandulaConfiguration configuration;

	private EndpointReferenceFactory() {

		configuration = KandulaConfiguration.getInstance();

	}

	public static EndpointReferenceFactory getInstance() {
		if (instance == null)
			instance = new EndpointReferenceFactory();
		return instance;
	}

	public EndpointReference getRegistrationEndpoint(String id) {

		EndpointReference epr = new EndpointReference(configuration.getLocationForEPR()
				+ "/axis2/services/RegistrationCoordinator");
		EndpointReferenceFactory.addReferenceProperty(epr, Constants.TRANSACTION_ID_PARAMETER, id);
		return epr;
	}

	public EndpointReference getCompletionEndpoint(String id) {
		EndpointReference epr = new EndpointReference(configuration.getLocationForEPR()
				+ "/axis2/services/CompletionCoordinator");
		EndpointReferenceFactory.addReferenceProperty(epr, Constants.TRANSACTION_ID_PARAMETER, id);

		return epr;
	}

	public EndpointReference get2PCCoordinatorEndpoint(String activityId, String enlistmentId) {
		// Activity ID to find Activity Context , EnlistmentID to find
		// participant in activity
		EndpointReference epr = new EndpointReference(configuration.getLocationForEPR()
				+ "/axis2/services/AtomicTransactionCoordinator");
		EndpointReferenceFactory.addReferenceProperty(epr, Constants.TRANSACTION_ID_PARAMETER,
				activityId);
		EndpointReferenceFactory.addReferenceProperty(epr, Constants.ENLISTMENT_ID_PARAMETER,
				enlistmentId);
		return epr;
	}

	public EndpointReference getParticipantEndpoint(String id, String protocol) {
		EndpointReference epr = null;
		if (protocol.equals(Constants.WS_AT_VOLATILE2PC)
				|| protocol.equals(Constants.WS_AT_DURABLE2PC)) {
			epr = new EndpointReference(configuration.getLocationForEPR()
					+ "/axis2/services/AtomicTransactionParticipant");
			EndpointReferenceFactory
					.addReferenceProperty(epr, Constants.REQUESTER_ID_PARAMETER, id);
		} else if (protocol.equals(Constants.WS_BA_CC)) {
			epr = new EndpointReference(configuration.getLocationForEPR()
					+ "/axis2/services/BACoordinatorCompletionParticipantService");
			EndpointReferenceFactory
					.addReferenceProperty(epr, Constants.REQUESTER_ID_PARAMETER, id);
		} else if (protocol.equals(Constants.WS_BA_CC)) {
			epr = new EndpointReference(configuration.getLocationForEPR()
					+ "/axis2/services/BAParticipantCompletionParticipantService");
			EndpointReferenceFactory
					.addReferenceProperty(epr, Constants.REQUESTER_ID_PARAMETER, id);
		}
		return epr;
	}

	/**
	 * MD5 a random string with localhost/date etc will return 128 bits construct a string of 18
	 * characters from those bits.
	 * 
	 * @return string
	 */
	public static String getRandomStringOf18Characters() {
		Random myRand = new Random();
		long rand = myRand.nextLong();
		String sid;
		try {
			sid = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			sid = Thread.currentThread().getName();
		}
		long time = System.currentTimeMillis();
		StringBuffer sb = new StringBuffer();
		sb.append(sid);
		sb.append(":");
		sb.append(Long.toString(time));
		sb.append(":");
		sb.append(Long.toString(rand));
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// System.out.println("Error: " + e);
			// todo heve to be properly handle
		}
		md5.update(sb.toString().getBytes());
		byte[] array = md5.digest();
		StringBuffer sb2 = new StringBuffer();
		for (int j = 0; j < array.length; ++j) {
			int b = array[j] & 0xFF;
			sb2.append(Integer.toHexString(b));
		}
		int begin = myRand.nextInt();
		if (begin < 0)
			begin = begin * -1;
		begin = begin % 8;
		return new String(sb2.toString().substring(begin, begin + 18)).toUpperCase();
	}

	public static void addReferenceProperty(EndpointReference epr, QName key, String value) {
		// We'll have to live with reference parameters for the moment
		// Since Axis2 Addressing does not support ref properties well
		HashMap refProperties;
		if ((refProperties = (HashMap) epr.getAllReferenceParameters()) == null) {
			refProperties = new HashMap();
		}
		OMLinkedListImplFactory factory = new OMLinkedListImplFactory();
		OMElement omElement = factory.createOMElement(key, null);
		omElement.setText(value);
		refProperties.put(key, omElement);
		epr.setReferenceParameters(refProperties);
	}

	public static EndpointReference endpointFromOM(OMElement eprElement) {
		EndpointReference epr;

		epr = new EndpointReference(eprElement.getFirstChildWithName(
				new QName(AddressingConstants.Submission.WSA_NAMESPACE,
						AddressingConstants.EPR_ADDRESS)).getText());
		HashMap referenceProperties = new HashMap();
		OMElement referencePropertiesElement = eprElement.getFirstChildWithName(new QName(
				"ReferenceParameters"));
		if (referencePropertiesElement != null) {
			Iterator propertyIter = referencePropertiesElement.getChildElements();
			while (propertyIter.hasNext()) {
				OMElement element = (OMElement) propertyIter.next();

				// TODO do we need to detach the OMElement
				referenceProperties.put(element.getQName(), element.cloneOMElement());
			}
		}

		epr.setReferenceParameters(referenceProperties);
		return epr;
	}

	public static void endpointToOM(EndpointReference epr, OMElement parentElement, SOAPFactory factory) {
		OMNamespace wsAddressing = factory.createOMNamespace(
				AddressingConstants.Submission.WSA_NAMESPACE,
				AddressingConstants.WSA_DEFAULT_PREFIX);
		OMElement addressElement = factory.createOMElement("Address", wsAddressing);
		addressElement.setText(epr.getAddress());
		parentElement.addChild(addressElement);
		Map referenceValues = epr.getAllReferenceParameters();
		if (referenceValues != null) {
			OMElement refPropertyElement = factory.createOMElement("ReferenceParameters",
					wsAddressing);
			parentElement.addChild(refPropertyElement);
			Iterator iterator = referenceValues.keySet().iterator();
			while (iterator.hasNext()) {
				QName key = (QName) iterator.next();
				OMElement omElement = (OMElement) referenceValues.get(key);
				refPropertyElement.addChild(omElement);
				if (Final.WSA_NAMESPACE.equals(wsAddressing)) {
					omElement.addAttribute(Final.WSA_IS_REFERENCE_PARAMETER_ATTRIBUTE,
							Final.WSA_TYPE_ATTRIBUTE_VALUE, wsAddressing);
				}
			}
		}
	}

	public static OMElement endpointAddressToOM(EndpointReference epr) {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMNamespace wsAddressing = factory.createOMNamespace(
				AddressingConstants.Submission.WSA_NAMESPACE,
				AddressingConstants.WSA_DEFAULT_PREFIX);
		OMElement addressElement = factory.createOMElement("Address", wsAddressing);
		addressElement.setText(epr.getAddress());

		return addressElement;
	}

	public EndpointReference getAtomicOutcomePCCoordinatorEndpoint(String activityId,
			String enlistmentId) {
		// Activity ID to find Activity Context , EnlistmentID to find
		// participant in activity
		EndpointReference epr = new EndpointReference(configuration.getLocationForEPR()
				+ "/axis2/services/BAParticipantCompletionCoordinatorService");
		EndpointReferenceFactory.addReferenceProperty(epr, Constants.TRANSACTION_ID_PARAMETER, activityId);
		EndpointReferenceFactory.addReferenceProperty(epr, Constants.ENLISTMENT_ID_PARAMETER,
				enlistmentId);
		return epr;
	}

	public EndpointReference getMixedOutcomePCCoordinatorEndpoint(String activityId,
			String enlistmentId) {
		// Activity ID to find Activity Context , EnlistmentID to find
		// participant in activity
		EndpointReference epr = new EndpointReference(configuration.getLocationForEPR()
				+ "/axis2/services/BAParticipantCompletionCoordinatorService");
		EndpointReferenceFactory.addReferenceProperty(epr, Constants.BA_ID_PARAMETER, activityId);
		EndpointReferenceFactory.addReferenceProperty(epr, Constants.ENLISTMENT_ID_PARAMETER,
				enlistmentId);
		return epr;
	}
	
	public EndpointReference getAtomicOutcomeCCCoordinatorEndpoint(String activityId,
			String enlistmentId) {
		// Activity ID to find Activity Context , EnlistmentID to find
		// participant in activity
		EndpointReference epr = new EndpointReference(configuration.getLocationForEPR()
				+ "/axis2/services/BACoordinatorCompletionCoordinatorService");
		EndpointReferenceFactory.addReferenceProperty(epr, Constants.TRANSACTION_ID_PARAMETER, activityId);
		EndpointReferenceFactory.addReferenceProperty(epr, Constants.ENLISTMENT_ID_PARAMETER,
				enlistmentId);
		return epr;
	}

	public EndpointReference getMixedOutcomeCCCoordinatorEndpoint(String activityId,
			String enlistmentId) {
		// Activity ID to find Activity Context , EnlistmentID to find
		// participant in activity
		EndpointReference epr = new EndpointReference(configuration.getLocationForEPR()
				+ "/axis2/services/BACoordinatorCompletionCoordinatorService");
		EndpointReferenceFactory.addReferenceProperty(epr, Constants.BA_ID_PARAMETER, activityId);
		EndpointReferenceFactory.addReferenceProperty(epr, Constants.ENLISTMENT_ID_PARAMETER,
				enlistmentId);
		return epr;
	}

	/**
	 * Util method to convert ADB generated EPR to a org.apache.axis2.addressing.EndpointReference 
	 * @param endpointReferenceType
	 * @return
	 */
	public static EndpointReference getEPR(EndpointReferenceType endpointReferenceType) {
		EndpointReference endpointReference = new EndpointReference(endpointReferenceType
				.getAddress().getAnyURI().toString());
		ReferenceParametersType parametersType = endpointReferenceType.getReferenceParameters();
		if (parametersType != null) {
			OMElement[] elements = parametersType.getExtraElement();
			for (int i = 0; i < elements.length; i++) {
				endpointReference.addReferenceParameter(elements[i].getQName(), elements[i]
						.getText());
			}
		}
		return endpointReference;
	}

	/**
	 * Util method to convert org.apache.axis2.addressing.EndpointReference to a ADB generated EPR
	 * @param endpointReference
	 * @return
	 * @throws MalformedURIException
	 */
	public static EndpointReferenceType getEPRTypeFromEPR(EndpointReference endpointReference)
			throws MalformedURIException {
		EndpointReferenceType endpointReferenceType = new EndpointReferenceType();
		AttributedURI attributedURI = new AttributedURI();
		attributedURI.setAnyURI(new URI(endpointReference.getAddress()));
		endpointReferenceType.setAddress(attributedURI);
		// setting reference parameters

		Map map = endpointReference.getAllReferenceParameters();
		if (map != null) {
			ReferenceParametersType referenceParametersType = new ReferenceParametersType();
			Iterator iterator = map.values().iterator();
			while (iterator.hasNext()) {
				OMElement element = (OMElement) iterator.next();
				referenceParametersType.addExtraElement(element);
			}
			endpointReferenceType.setReferenceParameters(referenceParametersType);
		}
		return endpointReferenceType;
	}

}
