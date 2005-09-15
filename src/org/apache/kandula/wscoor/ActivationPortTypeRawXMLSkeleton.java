package org.apache.kandula.wscoor;

import org.apache.axis2.om.OMElement;
import org.apache.kandula.KandulaException;
import org.apache.kandula.context.ActivityContext;
import org.apache.kandula.coordinator.Coordinator;

import javax.xml.namespace.QName;

/**
 * Auto generated java skeleton for the service by the Axis code generator
 */

public class ActivationPortTypeRawXMLSkeleton {

    /**
     * Auto generated method signature
     *
     * @param requestElement
     * @throws KandulaException
     */
    public OMElement CreateCoordinationContextOperation(
            OMElement requestElement)
            throws KandulaException {
        ActivityContext context;
        Coordinator coordinator = new Coordinator();

//          ActivityContext context;
//        Coordinator coordinator = new Coordinator();
//        if (requestDoc.getCreateCoordinationContext().getCurrentContext() != null) {
//            CoordinationContext coorContext = CoordinationContext.Factory.newInstance(requestDoc
//                    .getCreateCoordinationContext().getCurrentContext());
//            context = coordinator.createCoordinationContext(coorContext);
//        }else
//        {
        String coordinationType = requestElement.getFirstChildWithName(new QName("CoordinationType")).getText();
        OMElement expiresElement = requestElement.getFirstChildWithName(new QName("Expires"));
        String expires = null;
        long expiresL = 0;
        if (expiresElement != null) {
            expires = expiresElement.getText();
            if ((expires != null) && (expires.equals(""))) {
                expiresL = Long.parseLong(expires);
            }
        }
        context = coordinator.createCoordinationContext(coordinationType, expiresL);
        return context.getCoordinationContext().toOM();

        // context(coordinationType, expiresL);
        // }
//        CreateCoordinationContextResponseDocument responseDoc = CreateCoordinationContextResponseDocument.Factory
//                .newInstance();
//        CreateCoordinationContextResponseType responseType = CreateCoordinationContextResponseType.Factory
//                .newInstance();
//        responseType.setCoordinationContext((CoordinationContextType)context.getCoordinationContext().getCoordinationContextType());
//        responseDoc.setCreateCoordinationContextResponse(responseType);
        // return new OMElementImpl("Thilina",new OMNamespaceImpl("pre","http://www.thilina.org"));

//        if (requestDoc.getCreateCoordinationContext().getCurrentContext() != null) {
//            CoordinationContext coorContext = CoordinationContext.Factory.newInstance(requestDoc
//                    .getCreateCoordinationContext().getCurrentContext());
//            context = coordinator.createCoordinationContext(coorContext);
//        }else
//        {
//            context = coordinator.createCoordinationContext(requestDoc
//                    .getCreateCoordinationContext().getCoordinationType(), Long
//                    .parseLong(requestDoc.getCreateCoordinationContext()
//                            .getExpires().getId()));
//        }
//        CreateCoordinationContextResponseDocument responseDoc = CreateCoordinationContextResponseDocument.Factory
//                .newInstance();
//        CreateCoordinationContextResponseType responseType = CreateCoordinationContextResponseType.Factory
//                .newInstance();
//        responseType.setCoordinationContext((CoordinationContextType)context.getCoordinationContext().getCoordinationContextType());
//        responseDoc.setCreateCoordinationContextResponse(responseType);
//        return responseDoc;


    }

}