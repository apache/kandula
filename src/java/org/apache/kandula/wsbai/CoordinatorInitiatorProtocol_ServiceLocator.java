package org.apache.kandula.wsbai;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.axis.AxisFault;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.kandula.coordinator.CoordinationContext;
import org.apache.kandula.coordinator.ba.coordinator.BACoordinator;


/**
 * This is a convenience class for creating business activity initiator protocol#
 * service stubs.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public abstract class CoordinatorInitiatorProtocol_ServiceLocator {
	
	/**
	 * Get an initiator service stub for an "atomic outcome" coordination context.
	 * @param ctx The coordination context.
	 * @param initiatorServiceEPR The initiator service's EPR.
	 * @return A new stub.
	 * @throws AxisFault Fault creating the stub
	 * @throws MalformedURLException The URI contained in the EPR was invalid.
	 */
	public static CoordinatorInitiatorProtocol_AtomicBinding getAtomicStub(
			final CoordinationContext ctx,
			final EndpointReference initiatorServiceEPR
	) throws AxisFault, MalformedURLException{
		if(BACoordinator.COORDINATION_TYPE__ATOMIC.equals(ctx.getCoordinationType())){
			throw new IllegalArgumentException("Sorry, getAtomicStub is only valid with an atomic outcome!");
		}
		
		final CoordinatorInitiatorProtocol_AtomicBinding r = 
			new CoordinatorInitiatorProtocol_AtomicBinding(
					new URL(initiatorServiceEPR.getAddress().toString()),
					initiatorServiceEPR
			);

		return r;
	}

	/**
	 * Get an initiator service stub for a "mixed outcome" coordination context.
	 * @param ctx The coordination context.
	 * @param initiatorServiceEPR The initiator service's EPR.
	 * @return A new stub.
	 * @throws AxisFault Fault creating the stub
	 * @throws MalformedURLException The URI contained in the EPR was invalid.
	 */	
	public static CoordinatorInitiatorProtocol_MixedBinding getMixedStub(
			final CoordinationContext ctx,
			final EndpointReference initiatorServiceEPR
	) throws AxisFault, MalformedURLException{
		if(BACoordinator.COORDINATION_TYPE__MIXED.equals(ctx.getCoordinationType())){
			throw new IllegalArgumentException("Sorry, getMixedStub is only valid with a mixed outcome!");
		}
				
		final CoordinatorInitiatorProtocol_MixedBinding r = 
			new CoordinatorInitiatorProtocol_MixedBinding(
					new URL(initiatorServiceEPR.getAddress().toString()),
					initiatorServiceEPR
			);
		
		return r;
	}

}
