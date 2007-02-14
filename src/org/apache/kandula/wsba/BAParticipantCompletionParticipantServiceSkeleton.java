
    /**
     * BAParticipantCompletionParticipantServiceSkeleton.java
     *
     * This file was auto-generated from WSDL
     * by the Apache Axis2 version: #axisVersion# #today#
     */
    package org.apache.kandula.wsba;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.context.MessageContext;
import org.apache.kandula.Constants;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.faults.AbstractKandulaException;
import org.apache.kandula.participant.ba.BAParticipantTransactionCoordinator;
import org.apache.kandula.storage.Store;
    /**
     *  BAParticipantCompletionParticipantServiceSkeleton java skeleton for the axisService
     */
    public class BAParticipantCompletionParticipantServiceSkeleton{
     
         
        /**
         * Auto generated method signature
         
          * @param param0
         
         */
        public  void FailedOperation
                  (
          org.oasis_open.docs.ws_tx.wsba._2006._06.Failed param0
          )
         
           {
        	//==filed by nis===========================================
        	try{
//        	StorageFactory.getInstance().setConfigurationContext(
//    			MessageContext.getCurrentMessageContext().getServiceContext().getConfigurationContext());
//    		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
//    		String requesterID = header.getFirstChildWithName(
//    				Constants.REQUESTER_ID_PARAMETER).getText();
//    		Store store = StorageFactory.getInstance().getStore();
//    		AbstractContext context = (AbstractContext) store.get(requesterID);
//    		BAParticipantTransactionCoordinator BAtxManager = new BAParticipantTransactionCoordinator();
//    		BAtxManager.Faulted(context,Constants.WS_BA_PC);
        	}catch(Exception e){}
    		//=====================end=========================z
                
        }
     
         
        /**
         * Auto generated method signature
         
          * @param param1
         
         */
        public  void CloseOperation
                  (
          org.oasis_open.docs.ws_tx.wsba._2006._06.Close param1
          )
         
           {
        	try{
        	//==============filled by nis======================
//        	StorageFactory.getInstance().setConfigurationContext(
//   				MessageContext.getCurrentMessageContext().getServiceContext().getConfigurationContext());
//   		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
//   		String requesterID = header.getFirstChildWithName(
//   				Constants.REQUESTER_ID_PARAMETER).getText();
//   		Store store = StorageFactory.getInstance().getStore();
//   		AbstractContext context = (AbstractContext) store.get(requesterID);
//   		BAParticipantTransactionCoordinator BAtxManager = new BAParticipantTransactionCoordinator();
//   		BAtxManager.Close(context,Constants.WS_BA_PC);
           }catch(Exception e){}
   		
   		//=====================end===============================
                //Todo fill this with the necessary business logic
                
        }
     
         
        /**
         * Auto generated method signature
         
          * @param param2
         
         */
        public  void CompensateOperation
                  (
          org.oasis_open.docs.ws_tx.wsba._2006._06.Compensate param2
          )
         
           {
        	try{
        	//  == filled by nis===============================
//        	StorageFactory.getInstance().setConfigurationContext(
//    				MessageContext.getCurrentMessageContext().getServiceContext().getConfigurationContext());
//    		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
//    		String requesterID = header.getFirstChildWithName(
//    				Constants.REQUESTER_ID_PARAMETER).getText();
//    		Store store = StorageFactory.getInstance().getStore();
//    		AbstractContext context = (AbstractContext) store.get(requesterID);
//    		BAParticipantTransactionCoordinator batxManager = new BAParticipantTransactionCoordinator();
//    		 
//    		batxManager.Compensate(context,Constants.WS_BA_PC);//Todo fill this with the necessary business logic
           //=====================end===================================	
        	}catch(Exception e){
        		
        		
        	} }
     
         
        /**
         * Auto generated method signature
         
          * @param param3
         
         */
        public  void CancelOperation( org.oasis_open.docs.ws_tx.wsba._2006._06.Cancel param3)
	    {
//        	StorageFactory.getInstance().setConfigurationContext(
//	 				MessageContext.getCurrentMessageContext().getServiceContext().getConfigurationContext());
//	 		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
//	 		String requesterID = header.getFirstChildWithName(
//	 				Constants.REQUESTER_ID_PARAMETER).getText();
//	 		Store store = StorageFactory.getInstance().getStore();
//	 		AbstractContext context = (AbstractContext) store.get(requesterID);
//	 		//Need a BAParticipantTransactionCoordinator
//	 		BAParticipantTransactionCoordinator baParticipantTxManager = new BAParticipantTransactionCoordinator();
//	 		try {
//	 			baParticipantTxManager.Cancel(context,Constants.WS_BA_PC);
//	 		} catch (AbstractKandulaException e) {
//	 			
//	 		}
 		
        }
     
        /**
         * Auto generated method signature
         
          * @param param4
         
         */
        public  void GetStatusOperation
                  (
          org.oasis_open.docs.ws_tx.wsba._2006._06.GetStatus param4
          )
         
           {
               //Todo fill this with the necessary business logic
                
        }
     
         
        /**
         * Auto generated method signature
         
          * @param param5
         
         */
        public  void NotCompleted
                  (
          org.oasis_open.docs.ws_tx.wsba._2006._06.NotCompleted param5
          )
         
           {
                //Todo fill this with the necessary business logic
                
        }
     
         
        /**
         * Auto generated method signature
         
          * @param param6
         
         */
        public  void StatusOperation
                  (
          org.oasis_open.docs.ws_tx.wsba._2006._06.Status param6
          )
         
           {
                //Todo fill this with the necessary business logic
                
        }
     
         
        /**
         * Auto generated method signature
         
          * @param param7
         
         */
        public  void ExitedOperation
                  (
          org.oasis_open.docs.ws_tx.wsba._2006._06.Exited param7
          )
         
           {
        	try{
        	//== filled by nis===============================
//        	StorageFactory.getInstance().setConfigurationContext(
//    				MessageContext.getCurrentMessageContext().getServiceContext().getConfigurationContext());
//    		OMElement header = MessageContext.getCurrentMessageContext().getEnvelope().getHeader();
//    		String requesterID = header.getFirstChildWithName(
//    				Constants.REQUESTER_ID_PARAMETER).getText();
//    		Store store = StorageFactory.getInstance().getStore();
//    		AbstractContext context = (AbstractContext) store.get(requesterID);
//    		BAParticipantTransactionCoordinator batxManager = new BAParticipantTransactionCoordinator();
//    	//=======================================end=====================
//             batxManager.Exited(context,Constants.WS_BA_PC);   
        	}catch(Exception e){
        		
        		
        	} }
     
    }
    