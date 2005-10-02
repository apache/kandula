package org.apache.kandula.wsat.completion;

import org.apache.axis2.AxisFault;
import org.apache.axis2.om.OMElement;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.coordinator.Coordinator;
import org.apache.kandula.coordinator.at.ATCoordinator;
import org.apache.kandula.faults.AbstractKandulaException;

/**
 * Auto generated java skeleton for the service by the Axis code generator
 */

public class CompletionCoordinatorPortTypeRawXMLSkeleton {

    /**
     * @param requestElement
     * @throws AxisFault
     */
    public OMElement commitOperation(OMElement requestElement) throws AxisFault {
        AbstractContext context;
        String activityId;
        System.out.println("Visited Commit operation");
        activityId = Coordinator.ACTIVITY_ID;

        // TODO do we need to check the incoming message
        try {
            ATCoordinator coordinator = new ATCoordinator();
            coordinator.commitOperation(activityId);
        } catch (AbstractKandulaException e) {
            AxisFault fault = new AxisFault(e);
            fault.setFaultCode(e.getFaultCode());
            throw fault;
        }
        return null;
    }

    public OMElement rollbackOperation(OMElement requestElement)
            throws AxisFault {
        AbstractContext context;
        String activityId;
        System.out.println("Visited rollback operation");
        activityId = Coordinator.ACTIVITY_ID;
        try {
            ATCoordinator coordinator = new ATCoordinator();
            coordinator.commitOperation(activityId);
        } catch (AbstractKandulaException e) {
            AxisFault fault = new AxisFault(e);
            fault.setFaultCode(e.getFaultCode());
            throw fault;
        }
        return null;
    }

}