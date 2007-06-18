package org.apache.kandula.wsbai;

import java.net.URL;

import org.apache.axis.AxisFault;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.kandula.utils.AddressingHeaders;
import org.apache.kandula.utils.Service;

import org.apache.kandula.wsbai.CoordinatorInitiator_Mixed_BindingStub;



/**
 * Binding for the initiator service's stub, suitable for business activities
 * with mixed outcome.
 * (For use with @see org.apache.kandula.coordinator.ba.initiator.CoordinatorIProxy )
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class CoordinatorInitiatorProtocol_MixedBinding
extends CoordinatorInitiator_Mixed_BindingStub
implements CoordinatorInitiatorProtocolPortType_Mixed
{

	/**
	 * The appropriate addressing headers.
	 */
	final AddressingHeaders headers;

	/**
	 * Default constructor, calls super().
	 * 
	 * @param endpointURL Endpoint's URL 
	 * @param epr  EPR
	 * @throws AxisFault Throws axis fault.
	 */
	public CoordinatorInitiatorProtocol_MixedBinding(
			final URL endpointURL, 
			final EndpointReference epr
	) throws AxisFault {
		super(endpointURL, new Service());

		this.headers = new AddressingHeaders(epr, null);
		((org.apache.kandula.utils.Service) this.service).setAddressingHeaders(this.headers);
	}

	/**
	 * Fetch the appropriate AddressingHeaders
	 * @return The AddressingHeaders.
	 */
	public AddressingHeaders getAddressingHeaders(){
		return this.headers;
	}
}
