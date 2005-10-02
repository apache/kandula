package org.apache.kandula.wsat.twopc;

import org.apache.axis2.AxisFault;
import org.apache.axis2.om.OMElement;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.coordinator.Coordinator;
import org.apache.kandula.coordinator.at.ATCoordinator;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.participant.Vote;

/**
 * Auto generated java skeleton for the service by the Axis code generator
 */

public class CoordinatorPortTypeRawXMLSkeleton {

    /**
     * @param requestElement
     * @throws AbstractKandulaException
     */
    public OMElement preparedOperation(OMElement requestElement)
            throws AxisFault {
        String activityId = Coordinator.ACTIVITY_ID;
        ATCoordinator coordinator= new ATCoordinator();
        try {
            coordinator.countVote(activityId,Vote.PREPARED,null);
        } catch (AbstractKandulaException e) {
            AxisFault fault = new AxisFault(e);
            fault.setFaultCode(e.getFaultCode());
            throw fault;
        }
        return null;
    }

    /**
     * @param requestElement
     * @throws AbstractKandulaException
     */
    public OMElement abortedOperation(OMElement requestElement)
            throws AxisFault {
        String activityId = Coordinator.ACTIVITY_ID;
        ATCoordinator coordinator= new ATCoordinator();
        try {
            coordinator.countVote(activityId,Vote.ABORT,null);
        } catch (AbstractKandulaException e) {
            AxisFault fault = new AxisFault(e);
            fault.setFaultCode(e.getFaultCode());
            throw fault;
        }
        return null;
    }

    /**
     * @param requestElement
     * @throws AbstractKandulaException
     */
    public OMElement readOnlyOperation(OMElement requestElement)
            throws AxisFault {
        String activityId = Coordinator.ACTIVITY_ID;
        ATCoordinator coordinator= new ATCoordinator();
        try {
            coordinator.countVote(activityId,Vote.READ_ONLY,null);
        } catch (AbstractKandulaException e) {
            AxisFault fault = new AxisFault(e);
            fault.setFaultCode(e.getFaultCode());
            throw fault;
        }
        return null;
    }

    /**
     * @param requestElement
     * @throws AbstractKandulaException
     */
    public OMElement committedOperation(OMElement requestElement)
            throws AxisFault {
        AbstractContext context;
        System.out.println("Visited Commit operation");
        return null;
    }

    /**
     * @param requestElement
     * @throws AbstractKandulaException
     */
    public OMElement replayOperation(OMElement requestElement) throws AxisFault {
        AbstractContext context;
        System.out.println("Visited Commit operation");
        return null;
    }

}