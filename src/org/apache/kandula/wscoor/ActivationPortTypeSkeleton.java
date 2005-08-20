package org.apache.kandula.wscoor;

import org.apache.kandula.KandulaException;
import org.apache.kandula.coordinator.Coordinator;
import org.apache.kandula.coordinator.CoordinatorImpl;
import org.apache.kandula.coordinator.context.ActivityContext;
import org.apache.kandula.typemapping.CoordinationContext;
import org.xmlsoap.schemas.ws.x2003.x09.wscoor.CoordinationContextType;
import org.xmlsoap.schemas.ws.x2003.x09.wscoor.CreateCoordinationContextResponseDocument;
import org.xmlsoap.schemas.ws.x2003.x09.wscoor.CreateCoordinationContextResponseType;

/**
 *  Auto generated java skeleton for the service by the Axis code generator
 */

public class ActivationPortTypeSkeleton {

    /**
     * Auto generated method signature
     *@param requestDoc
     * @throws KandulaException
     */
    public org.xmlsoap.schemas.ws.x2003.x09.wscoor.CreateCoordinationContextResponseDocument CreateCoordinationContextOperation(
            org.xmlsoap.schemas.ws.x2003.x09.wscoor.CreateCoordinationContextDocument requestDoc)
            throws KandulaException {
        ActivityContext context;
        Coordinator coordinator = new CoordinatorImpl();
        if (requestDoc.getCreateCoordinationContext().getCurrentContext() != null) {
            CoordinationContext coorContext = CoordinationContext.Factory.newInstance(requestDoc
                    .getCreateCoordinationContext().getCurrentContext());
            context = coordinator.createCoordinationContext(coorContext);
        }else
        {
            context = coordinator.createCoordinationContext(requestDoc
                    .getCreateCoordinationContext().getCoordinationType(), Long
                    .parseLong(requestDoc.getCreateCoordinationContext()
                            .getExpires().getId()));
        }
        CreateCoordinationContextResponseDocument responseDoc = CreateCoordinationContextResponseDocument.Factory
                .newInstance();
        CreateCoordinationContextResponseType responseType = CreateCoordinationContextResponseType.Factory
                .newInstance();
        responseType.setCoordinationContext((CoordinationContextType)context.getCoordinationContext().getCoordinationContextType());
        responseDoc.setCreateCoordinationContextResponse(responseType);
        return responseDoc;

    }

}