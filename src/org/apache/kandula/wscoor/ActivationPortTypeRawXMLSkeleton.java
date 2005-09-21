package org.apache.kandula.wscoor;

import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.om.OMElement;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.coordinator.Coordinator;
import org.apache.kandula.faults.AbstractKandulaException;

/**
 * Auto generated java skeleton for the service by the Axis code generator
 */

public class ActivationPortTypeRawXMLSkeleton {

    /**
     * Auto generated method signature
     * 
     * @param requestElement
     * @throws AbstractKandulaException
     */
    public OMElement CreateCoordinationContextOperation(OMElement requestElement)
            throws AxisFault {
        AbstractContext context;

        /*
         * Extracting data from the incoming message
         */
        String coordinationType = requestElement.getFirstChildWithName(
                new QName("CoordinationType")).getText();
        OMElement expiresElement = requestElement
                .getFirstChildWithName(new QName("Expires"));
        String expires = null;
        long expiresL = 0;
        if (expiresElement != null) {
            expires = expiresElement.getText();
            if ((expires != null) && (expires.equals(""))) {
                expiresL = Long.parseLong(expires);
            }
        }

        /*
         * Creating the Coordination Context
         */
        try {
            Coordinator coordinator = new Coordinator();
            context = coordinator.createCoordinationContext(coordinationType,
                    expiresL);
            return context.getCoordinationContext().toOM();
        } catch (AbstractKandulaException e) {
            AxisFault fault = new AxisFault(e);
            fault.setFaultCode(e.getFaultCode());
            throw fault;
        }

    }

}