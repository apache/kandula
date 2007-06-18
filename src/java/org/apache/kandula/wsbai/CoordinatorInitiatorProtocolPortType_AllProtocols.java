package org.apache.kandula.wsbai;

import java.rmi.RemoteException;

import org.apache.kandula.wsbai.BAParticipantReferenceType;
import org.apache.kandula.wsbai.BAParticipantType;
import org.apache.kandula.wsbai.GetCoordCtxWCodeReqType;

/**
 * This interface defines all methods in the initiator interface
 * common to mixed and atomic coordination context.
 * 
 * @see org.apache.kandula.wsbai.CoordinatorInitiatorPortType_AtomicOutcome_StubInterface
 * @see org.apache.kandula.wsbai.CoordinatorInitiatorPortType_MixedOutcome_StubInterface
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public interface CoordinatorInitiatorProtocolPortType_AllProtocols {

	/**
	 * Return a new CoordinationContext for the given transation, tagged with
	 * a participant matchode. 
	 * @param withMatchcode The matchcode to register.
	 * @return A fresh coordinationContext object that includes the tagged registration service.
	 * @throws java.rmi.RemoteException
	 */
    public org.apache.kandula.wscoor.CreateCoordinationContextResponseType getCoordinationContextWithMatchcode(GetCoordCtxWCodeReqType withMatchcode) throws java.rmi.RemoteException;
    
    /**
     * List all registered and to-be-registered participants by their matchcodes.
     * @param req The request object.
     * @return An unsorted array auf Business Activity participants, complete with matchcodes and states.
     * @throws java.rmi.RemoteException Exception.
     */
    public org.apache.kandula.wsbai.BAParticipantType[] listParticipants(org.apache.kandula.wsbai.ListParticipantsReqType req) throws java.rmi.RemoteException;

	/**
	 * Tells the coordinator to complete the selected participants. This is valid
	 * both in transaction with the "mixed outcome" and the "atomic outcome" assertions.
	 * 
	 * This method may be called as many times as the initiator wishes. If a 
	 * selected participant is in a state where it is not allowed to complete,
	 * it is silently ignored.
	 * Hence, the initiator must check the return for the participants' current
	 * states.
	 * @param participantsToComplete The matchcodes of the participants to complete.
	 * 
	 * @return A list of the current participants and their states.
	 * @throws RemoteException 
	 */
	public BAParticipantType[] completeParticipants(
			final BAParticipantReferenceType[] participantsToComplete
	) throws RemoteException;

}
