/*
 * Created on Jan 3, 2006
 *
 */
package org.apache.kandula.utils;

import java.net.MalformedURLException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.message.addressing.Constants;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.MessageID;
import org.apache.axis.soap.SOAPConstants;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class SoapFaultSender {

	private static MessageID getCurrentMessageID() {
		org.apache.axis.message.addressing.AddressingHeaders headers = (org.apache.axis.message.addressing.AddressingHeaders) MessageContext.getCurrentContext().getProperty(
			Constants.ENV_ADDRESSING_REQUEST_HEADERS);
		return headers.getMessageID();
	}

	public static void invoke(EndpointReference faultTo, String action,
			AxisFault fault) throws AxisFault, MalformedURLException,
			ServiceException {
		if (faultTo == null)
			throw fault;

		AddressingHeaders headers = new AddressingHeaders(faultTo, null);
		headers.setRelatesTo(getCurrentMessageID());
		Service service = new Service();
		service.setAddressingHeaders(headers);
		Call call = (Call) service.createCall();
		call.setTargetEndpointAddress(TCPSnifferHelper.redirect(faultTo.getAddress().toString()));
		call.setSOAPActionURI(action);

		SOAPEnvelope env = new SOAPEnvelope();
		SOAPFault faultElement = new SOAPFault(fault);
		env.addBodyElement(faultElement);
		// FIXME:
		faultElement.setQName(SOAPConstants.SOAP11_CONSTANTS.getFaultQName());
		// FIXME:
		fault.setFaultDetail(null);

		try {
			call.invoke(env);
		} catch (Exception e) {
		}
	}
}