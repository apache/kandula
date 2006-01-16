/*
 * Created on Jan 6, 2006
 *
 */
package org.apache.kandula.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.MessageContext;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.addressing.Constants;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.MessageID;
import org.apache.axis.message.addressing.RelatesTo;
import org.apache.axis.message.addressing.RelationshipTypeValues;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class AddressingHeaders extends
		org.apache.axis.message.addressing.AddressingHeaders {

	public AddressingHeaders(EndpointReference to, EndpointReference from) {
		super(to);
		MessageID id;
		try {
			id = new MessageID(new URI("uuid:"
					+ UUIDGenFactory.getUUIDGen().nextUUID()));
		} catch (MalformedURIException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		setMessageID(id);
		if (from != null)
			setFrom(from);
	}

	public AddressingHeaders(EndpointReference to, EndpointReference from,
			EndpointReference replyTo) {
		this(to, from);
		setReplyTo(replyTo);
	}

	public void setRelatesTo(MessageID id) {
		List l = new ArrayList(1);
		l.add(new RelatesTo(id, RelationshipTypeValues.RESPONSE));
		setRelatesTo(l);
	}

	public static AddressingHeaders getAddressingHeadersOfCurrentMessage() {
		return (AddressingHeaders) MessageContext.getCurrentContext().getProperty(
			Constants.ENV_ADDRESSING_REQUEST_HEADERS);
	}

	public static EndpointReference getReplyToOfCurrentMessage() {
		AddressingHeaders headers = getAddressingHeadersOfCurrentMessage();
		return headers.getReplyTo();
	}

	public static EndpointReference getFaultToOfCurrentMessage() {
		AddressingHeaders headers = getAddressingHeadersOfCurrentMessage();
		return headers.getFaultTo();
	}
}