
	
    package org.apache.kandula.wsat;

    /**
     *  Auto generated message receiver
     */

    public class CoordinatorPortTypeRPCMessageReceiver extends org.apache.axis2.receivers.AbstractInOutSyncMessageReceiver{
    
		public void invokeBusinessLogic(org.apache.axis2.context.MessageContext msgContext, org.apache.axis2.context.MessageContext newMsgContext)
		throws org.apache.axis2.AxisFault{
    
     try {

            // get the implementation class for the Web Service
            Object obj = getTheImplementationObject(msgContext);
           
            CoordinatorPortTypeRPCSkeleton skel = (CoordinatorPortTypeRPCSkeleton)obj;
            //Out Envelop
             org.apache.axis2.soap.SOAPEnvelope envelope = null;
             //Find the operation that has been set by the Dispatch phase.
            org.apache.axis2.description.OperationDescription op = msgContext.getOperationContext().getAxisOperation();
            if (op == null) {
                throw new org.apache.axis2.AxisFault("Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
            }
            
            String methodName;
            if(op.getName() != null & (methodName = op.getName().getLocalPart()) != null){
            
				
					
					
					if(methodName.equals("ReplayOperation")){
											
				
			org.xmlsoap.schemas.ws.x2003.x09.wsat.ReplayResponseDocument param175 = null;
						
				//doc style
					param175 = skel.ReplayOperation((org.xmlsoap.schemas.ws.x2003.x09.wsat.ReplayDocument)codegen.databinding.ReplayOperationDatabindingSupporter.fromOM((org.apache.axis2.om.OMElement)msgContext.getEnvelope().getBody().getFirstChild().detach(), org.xmlsoap.schemas.ws.x2003.x09.wsat.ReplayDocument.class));
						
					//Create a default envelop
					envelope = getSOAPFactory().getDefaultEnvelope();
					//Create a Omelement of the result if a result exist
					
					envelope.getBody().setFirstChild(codegen.databinding.ReplayOperationDatabindingSupporter.toOM(param175));		
					
						
						
						
					}
			   
					
					
					if(methodName.equals("AbortedOperation")){
											
				
			
				//doc style
					 skel.AbortedOperation((org.xmlsoap.schemas.ws.x2003.x09.wsat.AbortedDocument)codegen.databinding.AbortedOperationDatabindingSupporter.fromOM((org.apache.axis2.om.OMElement)msgContext.getEnvelope().getBody().getFirstChild().detach(), org.xmlsoap.schemas.ws.x2003.x09.wsat.AbortedDocument.class));
						
					//Create a default envelop
					envelope = getSOAPFactory().getDefaultEnvelope();
					//Create a Omelement of the result if a result exist
					
					
						
						
						
					}
			   
					
					
					if(methodName.equals("ReadOnlyOperation")){
											
				
			
				//doc style
					 skel.ReadOnlyOperation((org.xmlsoap.schemas.ws.x2003.x09.wsat.ReadOnlyDocument)codegen.databinding.ReadOnlyOperationDatabindingSupporter.fromOM((org.apache.axis2.om.OMElement)msgContext.getEnvelope().getBody().getFirstChild().detach(), org.xmlsoap.schemas.ws.x2003.x09.wsat.ReadOnlyDocument.class));
						
					//Create a default envelop
					envelope = getSOAPFactory().getDefaultEnvelope();
					//Create a Omelement of the result if a result exist
					
					
						
						
						
					}
			   
			   
			   newMsgContext.setEnvelope(envelope);
            }
           
            

        } catch (Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
     
		 }
	
    }
    