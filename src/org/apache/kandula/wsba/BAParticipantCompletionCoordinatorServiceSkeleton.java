package org.apache.kandula.wsba;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kandula.Constants;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.context.impl.BAActivityContext;
import org.apache.kandula.coordinator.ba.BACoordinator;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.storage.StorageUtils;

/**
 * BAParticipantCompletionCoordinatorServiceSkeleton java skeleton for the
 * axisService
 */
public class BAParticipantCompletionCoordinatorServiceSkeleton {

	private static final Log log = LogFactory
			.getLog(BAParticipantCompletionCoordinatorServiceSkeleton.class);

	public void CompletedOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Completed param3)
			throws AxisFault

	{
		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
		String activityId = header.getFirstChildWithName(Constants.TRANSACTION_ID_PARAMETER)
				.getText();
		String enlistmentId = header.getFirstChildWithName(Constants.ENLISTMENT_ID_PARAMETER)
				.getText();
		try {
			BAActivityContext baContext = (BAActivityContext) StorageUtils.getContext(activityId);
			BACoordinator atomicBACoordinator = new BACoordinator();
			atomicBACoordinator.completedOperation(baContext, enlistmentId);
		} catch (AbstractKandulaException e) {
			log.fatal("WS_BA : message " + MessageContext.getCurrentMessageContext().getMessageID()
					+ " : CompletedOperation :" + e);
			throw AxisFault.makeFault(e);
		}
	}

	public void ClosedOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Closed param1)
			throws AxisFault

	{
		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
		String activityId = header.getFirstChildWithName(Constants.TRANSACTION_ID_PARAMETER)
				.getText();
		String enlistmentId = header.getFirstChildWithName(Constants.ENLISTMENT_ID_PARAMETER)
				.getText();
		try {
			AbstractContext context = StorageUtils.getContext(activityId);
			if (context != null) {
				BAActivityContext baContext = (BAActivityContext) context;
				BACoordinator atomicBACoordinator = new BACoordinator();
				atomicBACoordinator.closedOperation(baContext, enlistmentId);
			}
		} catch (AbstractKandulaException e) {
			log.fatal("WS_BA : message " + MessageContext.getCurrentMessageContext().getMessageID()
					+ " : ClosedOperation :" + e);
			throw AxisFault.makeFault(e);
		}
	}

	public void CanceledOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Canceled param8) throws AxisFault

	{
		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
		String activityId = header.getFirstChildWithName(Constants.TRANSACTION_ID_PARAMETER)
				.getText();
		String enlistmentId = header.getFirstChildWithName(Constants.ENLISTMENT_ID_PARAMETER)
				.getText();
		try {
			AbstractContext context = StorageUtils.getContext(activityId);
			if (context != null) {
				BAActivityContext baContext = (BAActivityContext) context;
				BACoordinator atomicBACoordinator = new BACoordinator();
				atomicBACoordinator.canceledOperation(baContext, enlistmentId);
			}
		} catch (AbstractKandulaException e) {
			log.fatal("WS_BA : message " + MessageContext.getCurrentMessageContext().getMessageID()
					+ " : CanceledOperation :" + e);
			throw AxisFault.makeFault(e);
		}

	}
	
	public void CompensatedOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Compensated param0)

	{
/*		try {
			OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
			String requesterID = header.getFirstChildWithName(Constants.REQUESTER_ID_PARAMETER)
					.getText();
			AbstractContext contextx = (AbstractContext) StorageUtils.getContext(requesterID);
			BAParticipantTransactionSentCoordinator BAStxManager = new BAParticipantTransactionSentCoordinator();
			BAStxManager.Fault(contextx, Constants.WS_BA_PC);
		} catch (Exception e) {
		}
		// =====================end=========================z //Todo fill this
		// with the necessary business logic
*/
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param param2
	 * 
	 */
	public void ExitOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Exit param2)

	{
		// Todo fill this with the necessary business logic
		try {
			OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
			String requesterID = header.getFirstChildWithName(Constants.REQUESTER_ID_PARAMETER)
					.getText();
			AbstractContext context = (AbstractContext) StorageUtils.getContext(requesterID);
			// BAParticipantTransactionSentCoordinator BAStxManager = new
			// BAParticipantTransactionSentCoordinator();
			// BAStxManager.exit(context);
		} catch (Exception e) {
		}
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param param4
	 * 
	 */
	public void CannotComplete(org.oasis_open.docs.ws_tx.wsba._2006._06.CannotComplete param4)

	{
		// Todo fill this with the necessary business logic

	}

	/**
	 * Auto generated method signature
	 * 
	 * @param param5
	 * 
	 */
	public void GetStatusOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.GetStatus param5)

	{
		// Todo fill this with the necessary business logic

	}

	/**
	 * Auto generated method signature
	 * 
	 * @param param6
	 * 
	 */
	public void FailOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Fail param6)

	{
		// Todo fill this with the necessary business logic

	}

	/**
	 * Auto generated method signature
	 * 
	 * @param param7
	 * 
	 */
	public void StatusOperation(org.oasis_open.docs.ws_tx.wsba._2006._06.Status param7)

	{
		// Todo fill this with the necessary business logic

	}
}
