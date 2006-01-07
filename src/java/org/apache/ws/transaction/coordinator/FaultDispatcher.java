/*
 * Created on Jan 6, 2006
 *
 */
package org.apache.ws.transaction.coordinator;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFault;

import org.apache.axis.MessageContext;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class FaultDispatcher {

	public void process(SOAPEnvelope req, SOAPEnvelope resp)
			throws javax.xml.soap.SOAPException {
		SOAPBody body = req.getBody();
		SOAPFault fault = body.getFault();

		Callback callback = (Callback) CallbackRegistry.getInstance().correlateMessage(
			CallbackRegistry.CALLBACK_REF, false);
		if (callback != null)
			callback.onFault(fault.getFaultCodeAsName());

		MessageContext.getCurrentContext().setResponseMessage(null);
	}

}