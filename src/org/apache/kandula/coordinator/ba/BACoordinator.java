/*
 * Copyright  2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.kandula.coordinator.ba;


import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.coordinator.Registerable;
import org.apache.kandula.faults.AbstractKandulaException;

public class BACoordinator implements Registerable 
{
	public BACoordinator() 
	{
	}
	
	//Start - Register
	/*public EndpointReference register(AbstractContext context, String protocol,
			EndpointReference participantEPR) throws AbstractKandulaException
	{
		EndpointReference registeredParticipantEPR = null;
		BAActivityContext baContext = (BAActivityContext) context;
		if(baContext.getCoordinationType().equals(Constants.WS_BA_ATOMIC))
		{
			AtomicBACoordinator atomicBACoordinator = new AtomicBACoordinator();
			registeredParticipantEPR = atomicBACoordinator.registerBAAtomicParticipants(baContext,protocol,participantEPR);
		}
		if(baContext.getCoordinationType().equals(Constants.WS_BA_MIXED))
		{
			MixedBACoordinator mixedBACoordinator = new MixedBACoordinator();
			registeredParticipantEPR = mixedBACoordinator.registerBAMixedParticipants(baContext,protocol,participantEPR);
		}
		return registeredParticipantEPR;
	}*/
	//End -Register
	
	public EndpointReference register(AbstractContext context, String protocol,
			EndpointReference participantEPR)throws AbstractKandulaException 
	{
//		BAActivityContext baContext = (BAActivityContext) context;
//		baContext.lock();
//		int coordinatorStatus = baContext.getStatus();
//		if(coordinatorStatus ==(BACoordinatorStatus.STATUS_CLOSING))
//		{
//			baContext.unlock();
//			throw new InvalidStateException ("Coordinator is in closing status ");
//		}else if(coordinatorStatus ==(BACoordinatorStatus.STATUS_COMPENSATING))
//		{
//			baContext.unlock();
//			throw new InvalidStateException ("Coordinator is in compensating status ");
//		}
//		else
//		{
//			EndpointReference epr = baContext.addParticipant(participantEPR,
//					protocol);
//			baContext.unlock();
//			return epr;
//		}
		return null;
	}
	
	/**
	 * Coordinator View of BusinessAgreement with Participant Completion
	 */
	//*********************************************************************************
	/*
	 * Handling Protocol Messages recieved by the Coordinator[sent by participant] </a>
	 */
	
//	1. Start Exit
//	If a exit is recieved the the coordinator removes that participant from the list
//	public void PCExitOperation(AbstractContext context, String enlishmentID)
//	throws AbstractKandulaException 
//	{
//			BAActivityContext baContext = (BAActivityContext) context;
//			baContext.lock();
//			BAParticipantInformation baParticipantInfo = baContext.getParticipant(enlishmentID);
//			switch(baContext.getStatus())
//			{
//			
//			case BACoordinatorStatus.STATUS_ACTIVE:
//				try{		
//					baContext.removeParticipant(enlishmentID);
//					int existingCount = baContext.getParticipantCount();
//					baContext.setParticipantCount(existingCount--);
//					baParticipantInfo.setStatus(Status.BACoordinatorStatus.STATUS_EXITING);
//					baContext.setStatus(BACoordinatorStatus.STATUS_EXITING);
//					baContext.unlock();
//					this.PCExitedOperation(baContext,enlishmentID);
//					}
//				catch(Exception e)
//				{
//					throw new KandulaGeneralException(e);
//				}
//								
//			case BACoordinatorStatus.STATUS_CANCELLING:
//				try{		
//					baContext.removeParticipant(enlishmentID);
//					int existingCount = baContext.getParticipantCount();
//					baContext.setParticipantCount(existingCount--);
//					// Take an iterator
//					baParticipantInfo.setStatus(Status.BACoordinatorStatus.STATUS_EXITING);
//					baContext.setStatus(BACoordinatorStatus.STATUS_EXITING);
//					baContext.unlock();
//					this.PCExitedOperation(baContext,enlishmentID);
//					
//					}
//				catch(Exception e)
//				{
//					throw new KandulaGeneralException(e);
//				}
//				
//			case BACoordinatorStatus.STATUS_ENDED:
//				try{		
//					baContext.unlock();
//					this.PCExitedOperation(baContext,enlishmentID);
//					}
//				catch(Exception e)
//				{
//					throw new KandulaGeneralException(e);
//				}
//								
//				case BACoordinatorStatus.STATUS_COMPLETED:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in completed state");
//				
//				case BACoordinatorStatus.STATUS_CLOSING:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in closing state");
//					
//				case BACoordinatorStatus.STATUS_COMPENSATING:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in compensating state");
//					
//				case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in faulting compensatingstate");
//					
//				case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in faulting active state");	
//					
//				case BACoordinatorStatus.STATUS_EXITING:
//					baContext.unlock();
//			}
//		}
//	//End Exit
//	
//	//2. start Completed
//	public void PCCompletedOperation(BAActivityContext baContext, String enlishmentID)
//	throws AbstractKandulaException 
//	{
//			switch(baContext.getStatus())
//			{			
//			case BACoordinatorStatus.STATUS_COMPLETED:
//				baContext.unlock();
//				
//			case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
//				baContext.unlock();
//				throw new InvalidStateException(
//				"Coordinator is in faulting active state");	
//				
//			case BACoordinatorStatus.STATUS_EXITING:
//				baContext.unlock();
//				throw new InvalidStateException(
//				"Coordinator is in exiting active state");	
//				
//			case BACoordinatorStatus.STATUS_ENDED:
//				baContext.unlock();
//		}
//	}						
//	//EndCompleted
//	
//	// 3. Start - Fault
//	public void PCFaultOperation(AbstractContext context, String enlishmentID)
//	throws AbstractKandulaException 
//	{
//			BAActivityContext baContext = (BAActivityContext) context;
//			baContext.lock();
//			BAParticipantInformation baParticipantInformation= baContext.getParticipant(enlishmentID);
//			switch(baContext.getStatus())
//			{			
//			case BACoordinatorStatus.STATUS_ACTIVE:
//				try{		
//					baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING_ACTIVE);
//					baContext.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING_ACTIVE);
//					baContext.unlock();
//					}
//				catch(Exception e)
//				{
//					throw new KandulaGeneralException(e);
//				}
//				
//			case BACoordinatorStatus.STATUS_CANCELLING:
//				try{		
//					baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING_ACTIVE);
//					baContext.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING_ACTIVE);
//					baContext.unlock();
//					}
//				catch(Exception e)
//				{
//					throw new KandulaGeneralException(e);
//				}
//				
//			case BACoordinatorStatus.STATUS_COMPENSATING:
//				try{		
//					
//					baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING_COMPENSATING);
//					baContext.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING_COMPENSATING);
//					baContext.unlock();
//					}
//				catch(Exception e)
//				{
//					throw new KandulaGeneralException(e);
//				}
//				
//				
//			case BACoordinatorStatus.STATUS_COMPLETED:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in completed state");
//				
//			case BACoordinatorStatus.STATUS_CLOSING:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in closing state");
//				
//			case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
//				baContext.setStatus(BACoordinatorStatus.STATUS_FAULTING);
//				baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING);
//				baContext.unlock();
//				this.PCFaultedOperation(baContext,enlishmentID);
//				
//				
//			case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
//				baContext.setStatus(BACoordinatorStatus.STATUS_FAULTING);
//				baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING);
//				baContext.unlock();
//				this.PCFaultedOperation(baContext,enlishmentID);
//				
//				
//			case BACoordinatorStatus.STATUS_ENDED:
//				baContext.unlock();
//				this.PCFaultedOperation(baContext,enlishmentID);
//				
//			}
//	}
//	//End - Fault
//	
//	//4. Canceled
//	public void PCCanceledOperation(BAActivityContext baContext,String enlishmentID)
//	throws AbstractKandulaException 
//	{
//		switch(baContext.getStatus())
//			{
//			case BACoordinatorStatus.STATUS_ACTIVE:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in Active state");
//				
//				case BACoordinatorStatus.STATUS_COMPLETED:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in completed state");
//					
//				case BACoordinatorStatus.STATUS_CLOSING:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in cLOSING state");
//				
//				case BACoordinatorStatus.STATUS_COMPENSATING:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in compensating state");
//					
//				case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in faulting Compensating state");
//					
//				case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in faulting active state");
//					
//				case BACoordinatorStatus.STATUS_EXITING:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in exiting state");
//				
//				case BACoordinatorStatus.STATUS_ENDED:
//					baContext.unlock();				
//			}
//		}
//	//End Canceled
//	
//	//5.Start -Closed
//	public void PCClosedOperation(BAActivityContext baContext,String enlishmentID)
//	throws AbstractKandulaException 
//	{
//		BAParticipantInformation baPaticipantInformation = baContext.getParticipant(enlishmentID);
//		switch(baContext.getStatus())
//			{
//			case BACoordinatorStatus.STATUS_ACTIVE:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in Active state");
//				
//			case BACoordinatorStatus.STATUS_CANCELLING:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in canceling state");
//				
//				case BACoordinatorStatus.STATUS_COMPLETED:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in completed state");
//					
//				case BACoordinatorStatus.STATUS_CLOSING:
//					baPaticipantInformation.setStatus(BACoordinatorStatus.STATUS_ENDED);
//					baContext.setStatus(BACoordinatorStatus.STATUS_ENDED);
//					baContext.unlock();
//					
//				case BACoordinatorStatus.STATUS_COMPENSATING:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in compensating state");
//					
//				case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in faulting Compensating state");
//					
//				case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in faulting active state");
//					
//				case BACoordinatorStatus.STATUS_EXITING:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in exiting state");
//				
//				case BACoordinatorStatus.STATUS_ENDED:
//					baContext.unlock();	
//			}
//		}
//	//End Closed
//	
//	// Start - Compensated
//	public void PCCompensatedOperation(BAActivityContext baContext,String enlishmentID)
//	throws AbstractKandulaException 
//	{
//		BAParticipantInformation baPaticipantInformation = baContext.getParticipant(enlishmentID);
//			switch(baContext.getStatus())
//			{
//			case BACoordinatorStatus.STATUS_ACTIVE:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in Active state");
//				
//			case BACoordinatorStatus.STATUS_CANCELLING:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in canceling state");
//				
//				case BACoordinatorStatus.STATUS_COMPLETED:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in completed state");
//					
//				case BACoordinatorStatus.STATUS_CLOSING:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in closing state");
//				
//				case BACoordinatorStatus.STATUS_COMPENSATING:
//					baPaticipantInformation.setStatus(BACoordinatorStatus.STATUS_ENDED);
//					baContext.setStatus(BACoordinatorStatus.STATUS_ENDED);
//					baContext.unlock();
//					
//				case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in faulting Compensating state");
//					
//				case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in faulting active state");
//					
//				case BACoordinatorStatus.STATUS_EXITING:
//					baContext.unlock();
//					throw new InvalidStateException(
//							"Coordinator is in exiting state");
//				
//				case BACoordinatorStatus.STATUS_ENDED:
//					baContext.unlock();			
//			}
//		}
//	//End -Compensated
//
///**
// * Handling Protocol Messages sent by the Coordinator[recieved by participant] </a>
// */
//		
////start - Cancel	
//public void PCCancelOperation(AbstractContext context,String enlishmentID)
//throws AbstractKandulaException 
//{
//	BAActivityContext baContext = (BAActivityContext) context;
//	baContext.lock();
//	BAParticipantInformation cancelingParticipant = baContext.getParticipant(enlishmentID);
//	BAParticipantCompletionParticipantServiceStub pcpsStub;
//	switch(baContext.getStatus())
//	{
//	case BACoordinatorStatus.STATUS_ACTIVE:
//		try{
//			pcpsStub = new BAParticipantCompletionParticipantServiceStub(((cancelingParticipant.getEpr()).toString()));
//			Cancel	cancelParam	= new Cancel();
//			pcpsStub.CancelOperation(cancelParam);
//			baContext.setStatus(BACoordinatorStatus.STATUS_CANCELLING_ACTIVE);
//			cancelingParticipant.setStatus(BACoordinatorStatus.STATUS_CANCELLING_ACTIVE);
//			baContext.unlock();
//		}
//		catch(AxisFault e)
//		{
//			throw new KandulaGeneralException(e);
//		}
//		catch(Exception e)
//		{
//			//TODO
//		}
//		
//		case BACoordinatorStatus.STATUS_CANCELLING:
//			try{
//							
//					pcpsStub = new BAParticipantCompletionParticipantServiceStub(((cancelingParticipant.getEpr()).toString()));
//					Cancel	cancelParam	= new Cancel();
//					pcpsStub.CancelOperation(cancelParam);
//					baContext.unlock();
//				}
//			catch(AxisFault e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			catch(Exception e)
//			{
//				//TODO
//			}
//					
//		case BACoordinatorStatus.STATUS_COMPLETED:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in completed state");
//			
//		case BACoordinatorStatus.STATUS_CLOSING:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in closing state");
//					
//		case BACoordinatorStatus.STATUS_COMPENSATING:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in compensated state");
//			
//		case BACoordinatorStatus.STATUS_FAULTING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in faulting state");
//			
//		case BACoordinatorStatus.STATUS_EXITING:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in exiting state");
//	
//		case BACoordinatorStatus.STATUS_ENDED:
//			baContext.unlock();
//			throw new InvalidStateException(
//				"Coordinator is in ended state");					
//	}
//}//End Cancel
//
////Start Colse
//public void PCCloseOperation(BAActivityContext baContext, String enlishmentID)
//throws AbstractKandulaException 
//{
//	BAParticipantInformation baParticipantInformation = baContext.getParticipant(enlishmentID);
//	baContext.lock();
//	BAParticipantCompletionParticipantServiceStub pcpsStub;
//	switch(baContext.getStatus())
//	{
//		case BACoordinatorStatus.STATUS_ACTIVE:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in active state");
//					
//		case BACoordinatorStatus.STATUS_CANCELLING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in cancelling state");
//			
//		case BACoordinatorStatus.STATUS_COMPLETED:
//			try{
//					String participantEndpointReference = baParticipantInformation.getEpr().toString();		
//					pcpsStub = new BAParticipantCompletionParticipantServiceStub(participantEndpointReference);
//					Close	closeParam	= new Close();
//					pcpsStub.CloseOperation(closeParam);
//					baParticipantInformation.setStatus(BACoordinatorStatus.STATUS_CLOSING);
//					baContext.unlock();
//				}
//			catch(AxisFault e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			catch(Exception e)
//			{
//				//TODO
//			}
//			
//		case BACoordinatorStatus.STATUS_CLOSING:
//			try{
//							
//				String participantEndpointReference = baParticipantInformation.getEpr().toString();		
//				pcpsStub = new BAParticipantCompletionParticipantServiceStub(participantEndpointReference);
//				Close	closeParam	= new Close();
//				pcpsStub.CloseOperation(closeParam);
//				baParticipantInformation.setStatus(BACoordinatorStatus.STATUS_CLOSING);
//				baContext.unlock();
//				}
//			catch(AxisFault e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			catch(Exception e)
//			{
//				//TODO
//			}						
//		
//		case BACoordinatorStatus.STATUS_COMPENSATING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in compensating state");
//			
//		case BACoordinatorStatus.STATUS_FAULTING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in faulting state");
//			
//		case BACoordinatorStatus.STATUS_EXITING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in exiting state");
//		
//		case BACoordinatorStatus.STATUS_ENDED:
//			baContext.unlock();
//			throw new InvalidStateException(
//				"Coordinator is in exiting state");
//	}
//}//End - Close
//
////Start - Compensate
//public void PCCompensateOperation(BAActivityContext baContext, String enlishmentID)
//throws AbstractKandulaException 
//{
//	BAParticipantInformation baParticipantInformation = baContext.getParticipant(enlishmentID);
//	baContext.lock();
//	BAParticipantCompletionParticipantServiceStub pcpsStub;
//	switch(baContext.getStatus())
//	{
//		case BACoordinatorStatus.STATUS_ACTIVE:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in active state");
//					
//		case BACoordinatorStatus.STATUS_CANCELLING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in cancelling state");
//			
//		case BACoordinatorStatus.STATUS_COMPLETED:
//			try{
//					String participantEndpointReference = baParticipantInformation.getEpr().toString();		
//					pcpsStub = new BAParticipantCompletionParticipantServiceStub(participantEndpointReference);
//					Compensate	compensateParam	= new Compensate();
//					pcpsStub.CompensateOperation(compensateParam);
//					baParticipantInformation.setStatus(BACoordinatorStatus.STATUS_COMPENSATING);
//					baContext.unlock();
//				}
//			catch(AxisFault e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			catch(Exception e)
//			{
//				//TODO
//			}
//			
//		case BACoordinatorStatus.STATUS_CLOSING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in cancelling state");
//			
//		case BACoordinatorStatus.STATUS_COMPENSATING:
//			try{
//				String participantEndpointReference = baParticipantInformation.getEpr().toString();		
//				pcpsStub = new BAParticipantCompletionParticipantServiceStub(participantEndpointReference);
//				Compensate	compensateParam	= new Compensate();
//				pcpsStub.CompensateOperation(compensateParam);
//				baParticipantInformation.setStatus(BACoordinatorStatus.STATUS_COMPENSATING);
//				baContext.unlock();
//			}
//		catch(AxisFault e)
//		{
//			throw new KandulaGeneralException(e);
//		}
//		catch(Exception e)
//		{
//			//TODO
//		}
//			
//		case BACoordinatorStatus.STATUS_FAULTING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in faulting state");
//			
//		case BACoordinatorStatus.STATUS_EXITING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in exiting state");
//		
//		case BACoordinatorStatus.STATUS_ENDED:
//			baContext.unlock();
//			throw new InvalidStateException(
//				"Coordinator is in exiting state");
//	}
//}//End - Compensate
//
////Start Faulted
//public void PCFaultedOperation(BAActivityContext baContext, String enlishmentID)
//throws AbstractKandulaException 
//{
//	baContext.lock();
//	BAParticipantInformation baParticipantInformation = baContext.getParticipant(enlishmentID);
//	BAParticipantCompletionParticipantServiceStub pcpsStub;
//	switch(baContext.getStatus())
//	{
//		case BACoordinatorStatus.STATUS_ACTIVE:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in active state");
//		
//		case BACoordinatorStatus.STATUS_CANCELLING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in cancelling state");
//			
//		case BACoordinatorStatus.STATUS_COMPLETED:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in completed state");
//		
//		case BACoordinatorStatus.STATUS_CLOSING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in closing state");
//		
//		case BACoordinatorStatus.STATUS_COMPENSATING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in compensating state");
//		
//		case BACoordinatorStatus.STATUS_FAULTING:
//			try{
//				String participantEndpointReference = baParticipantInformation.getEpr().toString();		
//				pcpsStub = new BAParticipantCompletionParticipantServiceStub(participantEndpointReference);
//				Failed	failedParam	= new Failed();
//				pcpsStub.FailedOperation(failedParam);
//				baContext.setStatus(BACoordinatorStatus.STATUS_ENDED);
//				baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_ENDED);
//				baContext.unlock();
//			}
//		catch(AxisFault e)
//		{
//			throw new KandulaGeneralException(e);
//		}
//		catch(Exception e)
//		{
//			//TODO
//		}
//	
//		case BACoordinatorStatus.STATUS_EXITING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in exiting state");
//		
//		case BACoordinatorStatus.STATUS_ENDED:
//			try{
//				String participantEndpointReference = baParticipantInformation.getEpr().toString();		
//				pcpsStub = new BAParticipantCompletionParticipantServiceStub(participantEndpointReference);
//				Failed	failedParam	= new Failed();
//				pcpsStub.FailedOperation(failedParam);
//				baContext.unlock();
//			}
//		catch(AxisFault e)
//		{
//			throw new KandulaGeneralException(e);
//		}
//		catch(Exception e)
//		{
//			//TODO
//		}
//	}	
//}
////End Faulted
//
////Start - Exited 
//public void PCExitedOperation(AbstractContext context, String enlishmentID)
//throws AbstractKandulaException 
//{
//	BAParticipantCompletionParticipantServiceStub pcpsStub;
//	BAActivityContext baContext = (BAActivityContext) context;
//	baContext.lock();
//	BAParticipantInformation exitingParticipant = baContext.getParticipant(enlishmentID);
//	
//	switch(baContext.getStatus())
//	{
//		case BACoordinatorStatus.STATUS_ACTIVE:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in active state");
//			
//		case BACoordinatorStatus.STATUS_CANCELLING:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in canceling state");
//			
//		case BACoordinatorStatus.STATUS_COMPLETED:
//			baContext.unlock();
//			throw new InvalidStateException(
//				"Coordinator is in completed state");					
//					
//		case BACoordinatorStatus.STATUS_CLOSING:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in closing state");	
//					
//		case BACoordinatorStatus.STATUS_COMPENSATING:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in compensating state");
//						
//		case BACoordinatorStatus.STATUS_FAULTING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in faulting state");
//			
//		case BACoordinatorStatus.STATUS_EXITING:
//			try{ 		
//					pcpsStub = new BAParticipantCompletionParticipantServiceStub(((exitingParticipant.getEpr()).toString()));
//					Exited  exitedParam = new Exited();
//					pcpsStub.ExitedOperation(exitedParam);
//					exitingParticipant.setStatus(Status.BACoordinatorStatus.STATUS_ENDED);
//					baContext.setStatus(BACoordinatorStatus.STATUS_ENDED);
//					baContext.unlock();
//				}
//			catch(AxisFault e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			catch(Exception e)
//			{
//				//TODO
//			}			
//		case BACoordinatorStatus.STATUS_ENDED:
//		try{
//			pcpsStub = new BAParticipantCompletionParticipantServiceStub(((exitingParticipant.getEpr()).toString()));
//			Exited  exitedParam = new Exited();
//			pcpsStub.ExitedOperation(exitedParam);
//			baContext.setStatus(Status.BACoordinatorStatus.STATUS_ENDED);
//			baContext.unlock();
//		}
//		catch(AxisFault e)
//		{
//			throw new KandulaGeneralException(e);
//		}
//		catch(Exception e)
//		{
//			//TODO
//		}
//	}
//}
////End Exited
//
////***********************************************************************************
///**
// * Coordinator View of BusinessAgreement with Coordinator Completion
// */
//
///**
// * Handling Protocol Messages recieved by the Coordinator[sent by participant] </a>
// */
////1. Start CCExit
//public void CCExitOperation(AbstractContext context, String enlishmentID)
//throws AbstractKandulaException 
//{
//		BAActivityContext baContext = (BAActivityContext) context;
//		baContext.lock();
//		BAParticipantInformation baParticipantInfo = baContext.getParticipant(enlishmentID);
//		switch(baContext.getStatus())
//		{
//		
//		case BACoordinatorStatus.STATUS_ACTIVE:
//			try{		
//				baContext.removeParticipant(enlishmentID);
//				int existingCount = baContext.getParticipantCount();
//				baContext.setParticipantCount(existingCount--);
//				baParticipantInfo.setStatus(Status.BACoordinatorStatus.STATUS_EXITING);
//				baContext.setStatus(BACoordinatorStatus.STATUS_EXITING);
//				baContext.unlock();
//				this.CCExitedOperation(baContext,enlishmentID);
//				
//				}
//			catch(Exception e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//							
//		case BACoordinatorStatus.STATUS_CANCELLING:
//			try{		
//				baContext.removeParticipant(enlishmentID);
//				int existingCount = baContext.getParticipantCount();
//				baContext.setParticipantCount(existingCount--);
//				baParticipantInfo.setStatus(Status.BACoordinatorStatus.STATUS_EXITING);
//				baContext.setStatus(BACoordinatorStatus.STATUS_EXITING);
//				baContext.unlock();
//				this.CCExitedOperation(baContext,enlishmentID);
//				}
//			catch(Exception e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//				
//		case BACoordinatorStatus.STATUS_CANCELING_COMPLETING:
//			try{		
//				baContext.removeParticipant(enlishmentID);
//				int existingCount = baContext.getParticipantCount();
//				baContext.setParticipantCount(existingCount--);
//				baParticipantInfo.setStatus(Status.BACoordinatorStatus.STATUS_EXITING);
//				baContext.setStatus(BACoordinatorStatus.STATUS_EXITING);
//				baContext.unlock();
//				this.CCExitedOperation(baContext,enlishmentID);
//				
//				}
//			catch(Exception e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			
//			case BACoordinatorStatus.STATUS_COMPLETED:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in completed state");
//			
//			case BACoordinatorStatus.STATUS_CLOSING:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in closing state");
//				
//			case BACoordinatorStatus.STATUS_COMPENSATING:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in compensating state");
//				
//			case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in faulting compensatingstate");
//				
//			case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in faulting active state");	
//				
//			case BACoordinatorStatus.STATUS_EXITING:
//				baContext.unlock();
//				
//			case BACoordinatorStatus.STATUS_ENDED:
//				try{		
//					baContext.unlock();
//					this.CCExitedOperation(baContext,enlishmentID);
//					}
//				catch(Exception e)
//				{
//					throw new KandulaGeneralException(e);
//				}
//		}
//	}
////End CCExit
//
////2. start CCCompleted
//public void CCCompletedOperation(BAActivityContext baContext, String enlishmentID)
//throws AbstractKandulaException 
//{
//	BAParticipantInformation baParticipantInformation= baContext.getParticipant(enlishmentID);
//		switch(baContext.getStatus())
//		{
//		case BACoordinatorStatus.STATUS_ACTIVE:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in active state");	
//			
//		case BACoordinatorStatus.STATUS_CANCELLING_ACTIVE:
//			baContext.setStatus(BACoordinatorStatus.STATUS_CANCELLING);
//			baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_CANCELLING);
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in canceling active state");	
//			
//		case BACoordinatorStatus.STATUS_COMPLETED:
//			baContext.unlock();
//			
//		case BACoordinatorStatus.STATUS_CLOSING:
//			baContext.unlock();
//			CCCloseOperation(baContext,enlishmentID);
//			throw new InvalidStateException(
//			"Coordinator is in faulting active state");	
//			
//		case BACoordinatorStatus.STATUS_COMPENSATING:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in compensating state");
//			
//		case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
//			baContext.setStatus(BACoordinatorStatus.STATUS_FAULTING);
//			baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING);
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in faulting compensating state");	
//			
//		case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
//			baContext.setStatus(BACoordinatorStatus.STATUS_FAULTING);
//			baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING);
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in faulting compensating state");	
//			
//		case BACoordinatorStatus.STATUS_EXITING:
//			baContext.unlock();
//		
//		case BACoordinatorStatus.STATUS_ENDED:
//			baContext.unlock();
//	}
//}						
////End - CCCompleted
//
//// 3. Start - Fault
//public void CCFaultOperation(AbstractContext context, String enlishmentID)
//throws AbstractKandulaException 
//{
//		BAActivityContext baContext = (BAActivityContext) context;
//		baContext.lock();
//		BAParticipantInformation baParticipantInformation= baContext.getParticipant(enlishmentID);
//		switch(baContext.getStatus())
//		{			
//		case BACoordinatorStatus.STATUS_ACTIVE:
//			try{		
//				baContext.setStatus(BACoordinatorStatus.STATUS_FAULTING_ACTIVE);
//				baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING_ACTIVE);
//				baContext.unlock();
//				this.CCFaultedOperation(baContext,enlishmentID);
//				
//				}
//			catch(Exception e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			
//		case BACoordinatorStatus.STATUS_CANCELLING_ACTIVE:
//			try{		
//				baContext.setStatus(BACoordinatorStatus.STATUS_FAULTING_ACTIVE);
//				baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING_ACTIVE);
//				baContext.unlock();
//				this.CCFaultedOperation(baContext,enlishmentID);
//				
//				}
//			catch(Exception e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			
//		case BACoordinatorStatus.STATUS_CANCELING_COMPLETING:
//			try{		
//				baContext.setStatus(BACoordinatorStatus.STATUS_FAULTING_ACTIVE);
//				baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING_ACTIVE);
//				baContext.unlock();
//				this.CCFaultedOperation(baContext,enlishmentID);
//				
//				}
//			catch(Exception e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			
//		case BACoordinatorStatus.STATUS_COMPLETING:
//			try{		
//				baContext.setStatus(BACoordinatorStatus.STATUS_FAULTING_ACTIVE);
//				baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING_ACTIVE);
//				baContext.unlock();
//				this.CCFaultedOperation(baContext,enlishmentID);
//				}
//			catch(Exception e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			
//		case BACoordinatorStatus.STATUS_COMPLETED:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in completed state");
//			
//		case BACoordinatorStatus.STATUS_CLOSING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in closing state");
//			
//		case BACoordinatorStatus.STATUS_COMPENSATING:
//			try{		
//				baContext.setStatus(BACoordinatorStatus.STATUS_FAULTING_COMPENSATING);
//				baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING_COMPENSATING);
//				this.CCFaultedOperation(baContext,enlishmentID);
//				baContext.unlock();
//				}
//			catch(Exception e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//				
//		case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
//			baContext.setStatus(BACoordinatorStatus.STATUS_FAULTING);
//			baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING);
//			baContext.unlock();
//			
//		case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
//			baContext.setStatus(BACoordinatorStatus.STATUS_FAULTING);
//			baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING);
//			baContext.unlock();
//			this.PCFaultedOperation(baContext,enlishmentID);
//			
//		case BACoordinatorStatus.STATUS_ENDED:
//			baContext.unlock();
//			
//		case BACoordinatorStatus.STATUS_EXITING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in exiting state");
//			
//		}
//}
////End - Fault
//
////4. Canceled
//public void CCCanceledOperation(BAActivityContext baContext,String enlishmentID)
//throws AbstractKandulaException 
//{
//	BAParticipantInformation baParticipantInformation = baContext.getParticipant(enlishmentID);
//	switch(baContext.getStatus())
//		{
//		case BACoordinatorStatus.STATUS_ACTIVE:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in Active state");
//			
//		case BACoordinatorStatus.STATUS_COMPLETING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in completing state");
//			
//			case BACoordinatorStatus.STATUS_COMPLETED:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in completed state");
//				
//			case BACoordinatorStatus.STATUS_CLOSING:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in closing state");
//			
//			case BACoordinatorStatus.STATUS_COMPENSATING:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in compensating state");
//				
//			case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
//				baContext.setStatus(BACoordinatorStatus.STATUS_FAULTING);
//				baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING);
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in faulting Compensating state");
//				
//			case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
//				baContext.setStatus(BACoordinatorStatus.STATUS_FAULTING);
//				baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_FAULTING);
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in faulting active state");
//				
//			case BACoordinatorStatus.STATUS_EXITING:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in exiting state");
//			
//			case BACoordinatorStatus.STATUS_ENDED:
//				baContext.unlock();				
//		}
//	}
////End Canceled
//
////5.Start -Closed
//public void CCClosedOperation(BAActivityContext baContext,String enlishmentID)
//throws AbstractKandulaException 
//{
//	BAParticipantInformation baPaticipantInformation = baContext.getParticipant(enlishmentID);
//	switch(baContext.getStatus())
//		{
//		case BACoordinatorStatus.STATUS_ACTIVE:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in Active state");
//				
//		case BACoordinatorStatus.STATUS_CANCELLING_ACTIVE:
//			baPaticipantInformation.setStatus(BACoordinatorStatus.STATUS_CANCELLING);
//			baContext.setStatus(BACoordinatorStatus.STATUS_CANCELLING);
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in canceling active state");
//			
//		case BACoordinatorStatus.STATUS_CANCELING_COMPLETING:
//			baPaticipantInformation.setStatus(BACoordinatorStatus.STATUS_CANCELLING);
//			baContext.setStatus(BACoordinatorStatus.STATUS_CANCELLING);
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in canceling completing state");
//			
//		case BACoordinatorStatus.STATUS_COMPLETING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in completing state");
//			
//			case BACoordinatorStatus.STATUS_COMPLETED:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in completed state");
//				
//			case BACoordinatorStatus.STATUS_CLOSING:
//				baPaticipantInformation.setStatus(BACoordinatorStatus.STATUS_ENDED);
//				baContext.setStatus(BACoordinatorStatus.STATUS_ENDED);
//				baContext.unlock();
//				
//			case BACoordinatorStatus.STATUS_COMPENSATING:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in compensating state");
//				
//			case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
//				baPaticipantInformation.setStatus(BACoordinatorStatus.STATUS_FAULTING);
//				baContext.setStatus(BACoordinatorStatus.STATUS_FAULTING);
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in faulting Compensating state");
//				
//			case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
//				baPaticipantInformation.setStatus(BACoordinatorStatus.STATUS_FAULTING);
//				baContext.setStatus(BACoordinatorStatus.STATUS_FAULTING);
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in faulting active state");
//				
//			case BACoordinatorStatus.STATUS_EXITING:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in exiting state");
//			
//			case BACoordinatorStatus.STATUS_ENDED:
//				baContext.unlock();	
//		}
//	}
////End Closed
//
//// Start - Compensated
//public void CCCompensatedOperation(BAActivityContext baContext,String enlishmentID)
//throws AbstractKandulaException 
//{
//	BAParticipantInformation baPaticipantInformation = baContext.getParticipant(enlishmentID);
//		switch(baContext.getStatus())
//		{
//		case BACoordinatorStatus.STATUS_ACTIVE:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in Active state");
//				
//		case BACoordinatorStatus.STATUS_CANCELLING_ACTIVE:
//			baPaticipantInformation.setStatus(BACoordinatorStatus.STATUS_CANCELLING);
//			baContext.setStatus(BACoordinatorStatus.STATUS_CANCELLING);
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in canceling active state");
//			
//		case BACoordinatorStatus.STATUS_CANCELING_COMPLETING:
//			baPaticipantInformation.setStatus(BACoordinatorStatus.STATUS_CANCELLING);
//			baContext.setStatus(BACoordinatorStatus.STATUS_CANCELLING);
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in canceling completing state");
//			
//		case BACoordinatorStatus.STATUS_COMPLETING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in completing state");
//			
//			case BACoordinatorStatus.STATUS_COMPLETED:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in completed state");
//				
//			case BACoordinatorStatus.STATUS_CLOSING:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in closing state");
//				
//			case BACoordinatorStatus.STATUS_COMPENSATING:
//				baPaticipantInformation.setStatus(BACoordinatorStatus.STATUS_ENDED);
//				baContext.setStatus(BACoordinatorStatus.STATUS_ENDED);
//				baContext.unlock();
//				
//			case BACoordinatorStatus.STATUS_FAULTING_COMPENSATING:
//				baPaticipantInformation.setStatus(BACoordinatorStatus.STATUS_FAULTING);
//				baContext.setStatus(BACoordinatorStatus.STATUS_FAULTING);
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in faulting Compensating state");
//				
//			case BACoordinatorStatus.STATUS_FAULTING_ACTIVE:
//				baPaticipantInformation.setStatus(BACoordinatorStatus.STATUS_FAULTING);
//				baContext.setStatus(BACoordinatorStatus.STATUS_FAULTING);
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in faulting active state");
//				
//			case BACoordinatorStatus.STATUS_EXITING:
//				baContext.unlock();
//				throw new InvalidStateException(
//						"Coordinator is in exiting state");
//			
//			case BACoordinatorStatus.STATUS_ENDED:
//				baContext.unlock();
//		}
//	}
////End -Compensated
//
///**
// * Handling Protocol Messages sent by the Coordinator[sent by participant] </a>
// */
//
//public void CCCancelOperation(AbstractContext context,String enlishmentID)
//throws AbstractKandulaException 
//{
//	BAActivityContext baContext = (BAActivityContext) context;
//	baContext.lock();
//	BAParticipantInformation cancelingParticipant = baContext.getParticipant(enlishmentID);
//	BACoordinatorCompletionParticipantServiceStub ccpsStub;
//	switch(baContext.getStatus())
//	{
//		case BACoordinatorStatus.STATUS_ACTIVE:
//			try{
//				ccpsStub = new BACoordinatorCompletionParticipantServiceStub(((cancelingParticipant.getEpr()).toString()));
//				Cancel	cancelParam	= new Cancel();
//				ccpsStub.CancelOperation(cancelParam);
//				baContext.setStatus(BACoordinatorStatus.STATUS_CANCELLING_ACTIVE);
//				cancelingParticipant.setStatus(BACoordinatorStatus.STATUS_CANCELLING_ACTIVE);
//				baContext.unlock();
//			}
//			catch(AxisFault e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			catch(Exception e)
//			{
//				//TODO
//			}
//		case BACoordinatorStatus.STATUS_CANCELLING:
//			try{
//							
//				ccpsStub = new BACoordinatorCompletionParticipantServiceStub(((cancelingParticipant.getEpr()).toString()));
//					Cancel	cancelParam	= new Cancel();
//					ccpsStub.CancelOperation(cancelParam);
//					baContext.unlock();
//				}
//			catch(AxisFault e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			catch(Exception e)
//			{
//				//TODO
//			}
//				
//		
//		case BACoordinatorStatus.STATUS_COMPLETING:
//			try{
//							
//					ccpsStub = new BACoordinatorCompletionParticipantServiceStub(((cancelingParticipant.getEpr()).toString()));
//					Cancel	cancelParam	= new Cancel();
//					ccpsStub.CancelOperation(cancelParam);
//					baContext.setStatus(BACoordinatorStatus.STATUS_CANCELING_COMPLETING);
//					cancelingParticipant.setStatus(BACoordinatorStatus.STATUS_CANCELING_COMPLETING);
//					baContext.unlock();
//				}
//			catch(AxisFault e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			catch(Exception e)
//			{
//				//TODO
//			}
//		
//		case BACoordinatorStatus.STATUS_COMPLETED:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in completed state");
//			
//		case BACoordinatorStatus.STATUS_CLOSING:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in closing state");
//					
//		case BACoordinatorStatus.STATUS_COMPENSATING:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in compensated state");
//			
//		case BACoordinatorStatus.STATUS_FAULTING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in faulting state");
//			
//		case BACoordinatorStatus.STATUS_EXITING:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in exiting state");
//	
//		case BACoordinatorStatus.STATUS_ENDED:
//			baContext.unlock();
//			throw new InvalidStateException(
//				"Coordinator is in ended state");					
//	}
//}//End Cancel
//
////Start - Complete
//public void CCCompleteOperation(AbstractContext context,String enlishmentID)
//throws AbstractKandulaException 
//{
//	BAActivityContext baContext = (BAActivityContext) context;
//	baContext.lock();
//	BAParticipantInformation cancelingParticipant = baContext.getParticipant(enlishmentID);
//	BACoordinatorCompletionParticipantServiceStub ccpsStub;
//	switch(baContext.getStatus())
//	{
//		case BACoordinatorStatus.STATUS_ACTIVE:
//			try{
//				ccpsStub = new BACoordinatorCompletionParticipantServiceStub(((cancelingParticipant.getEpr()).toString()));
//				Complete	completeParam	= new Complete();
//				ccpsStub.CompleteOperation(completeParam);
//				baContext.setStatus(BACoordinatorStatus.STATUS_COMPLETING);
//				cancelingParticipant.setStatus(BACoordinatorStatus.STATUS_COMPLETING);
//				baContext.unlock();
//			}
//			catch(AxisFault e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			catch(Exception e)
//			{
//				//TODO
//			}
//		case BACoordinatorStatus.STATUS_CANCELLING:
//			baContext.unlock();
//			throw new InvalidStateException(
//				"Coordinator is in cancelling state");	
//		
//		case BACoordinatorStatus.STATUS_COMPLETING:
//			try{
//				ccpsStub = new BACoordinatorCompletionParticipantServiceStub(((cancelingParticipant.getEpr()).toString()));
//				Complete	completeParam	= new Complete();
//				ccpsStub.CompleteOperation(completeParam);
//				baContext.setStatus(BACoordinatorStatus.STATUS_COMPLETING);
//				cancelingParticipant.setStatus(BACoordinatorStatus.STATUS_COMPLETING);
//				baContext.unlock();
//			}
//			catch(AxisFault e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			catch(Exception e)
//			{
//				//TODO
//			}
//		case BACoordinatorStatus.STATUS_COMPLETED:
//			baContext.unlock();
//			throw new InvalidStateException(
//				"Coordinator is in completed state");	
//		
//		case BACoordinatorStatus.STATUS_CLOSING:
//			baContext.unlock();
//			throw new InvalidStateException(
//				"Coordinator is in closing state");	
//		
//		case BACoordinatorStatus.STATUS_COMPENSATING:
//			baContext.unlock();
//			throw new InvalidStateException(
//				"Coordinator is in compensating state");	
//			
//		case BACoordinatorStatus.STATUS_FAULTING:
//			baContext.unlock();
//			throw new InvalidStateException(
//				"Coordinator is in faulting state");
//			
//		case BACoordinatorStatus.STATUS_EXITING:
//			baContext.unlock();
//			throw new InvalidStateException(
//				"Coordinator is in exiting state");
//			
//		case BACoordinatorStatus.STATUS_ENDED:
//			baContext.unlock();
//			throw new InvalidStateException(
//				"Coordinator is in ended state");	
//	}
//}
////End - Complete
//
////Start - Close
//public void CCCloseOperation(BAActivityContext baContext,String enlishmentID)
//throws AbstractKandulaException 
//{
//	BAParticipantInformation baParticipantInformation = baContext.getParticipant(enlishmentID);
//	baContext.lock();
//	BACoordinatorCompletionParticipantServiceStub ccpsStub;
//	switch(baContext.getStatus())
//	{
//		case BACoordinatorStatus.STATUS_ACTIVE:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in active state");
//					
//		case BACoordinatorStatus.STATUS_CANCELLING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in cancelling state");
//			
//		case BACoordinatorStatus.STATUS_COMPLETING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in completing state");
//			
//		case BACoordinatorStatus.STATUS_COMPLETED:
//			try{
//					String participantEndpointReference = baParticipantInformation.getEpr().toString();		
//					ccpsStub = new BACoordinatorCompletionParticipantServiceStub(participantEndpointReference);
//					Close	closeParam	= new Close();
//					ccpsStub.CloseOperation(closeParam);
//					baParticipantInformation.setStatus(BACoordinatorStatus.STATUS_CLOSING);
//					baContext.unlock();
//				}
//			catch(AxisFault e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			catch(Exception e)
//			{
//				//TODO
//			}
//			
//		case BACoordinatorStatus.STATUS_CLOSING:
//			try{
//							
//				String participantEndpointReference = baParticipantInformation.getEpr().toString();		
//				ccpsStub = new BACoordinatorCompletionParticipantServiceStub(participantEndpointReference);
//				Close	closeParam	= new Close();
//				ccpsStub.CloseOperation(closeParam);
//				baParticipantInformation.setStatus(BACoordinatorStatus.STATUS_CLOSING);
//				baContext.unlock();
//				}
//			catch(AxisFault e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			catch(Exception e)
//			{
//				//TODO
//			}						
//		
//		case BACoordinatorStatus.STATUS_COMPENSATING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in compensating state");
//			
//		case BACoordinatorStatus.STATUS_FAULTING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in faulting state");
//			
//		case BACoordinatorStatus.STATUS_EXITING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in exiting state");
//		
//		case BACoordinatorStatus.STATUS_ENDED:
//			baContext.unlock();
//			throw new InvalidStateException(
//				"Coordinator is in exiting state");
//	}
//}
////End - Close
//
////Start - Compensate
//public void CCCompensateOperation(BAActivityContext baContext,String enlishmentID)
//throws AbstractKandulaException 
//{
//	BAParticipantInformation baParticipantInformation = baContext.getParticipant(enlishmentID);
//	baContext.lock();
//	BACoordinatorCompletionParticipantServiceStub ccpsStub;
//	switch(baContext.getStatus())
//	{
//		case BACoordinatorStatus.STATUS_ACTIVE:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in active state");
//					
//		case BACoordinatorStatus.STATUS_CANCELLING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in cancelling state");
//		
//		case BACoordinatorStatus.STATUS_COMPLETING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in completing state");
//			
//		case BACoordinatorStatus.STATUS_COMPLETED:
//			try{
//					String participantEndpointReference = baParticipantInformation.getEpr().toString();		
//					ccpsStub = new BACoordinatorCompletionParticipantServiceStub(participantEndpointReference);
//					Compensate	compensateParam	= new Compensate();
//					ccpsStub.CompensateOperation(compensateParam);
//					baParticipantInformation.setStatus(BACoordinatorStatus.STATUS_COMPENSATING);
//					baContext.unlock();
//				}
//			catch(AxisFault e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			catch(Exception e)
//			{
//				//TODO
//			}
//			
//		case BACoordinatorStatus.STATUS_CLOSING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in cancelling state");
//			
//		case BACoordinatorStatus.STATUS_COMPENSATING:
//			try{
//				String participantEndpointReference = baParticipantInformation.getEpr().toString();		
//				ccpsStub = new BACoordinatorCompletionParticipantServiceStub(participantEndpointReference);
//				Compensate	compensateParam	= new Compensate();
//				ccpsStub.CompensateOperation(compensateParam);
//				baParticipantInformation.setStatus(BACoordinatorStatus.STATUS_COMPENSATING);
//				baContext.unlock();
//			}
//		catch(AxisFault e)
//		{
//			throw new KandulaGeneralException(e);
//		}
//		catch(Exception e)
//		{
//			//TODO
//		}
//			
//		case BACoordinatorStatus.STATUS_FAULTING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in faulting state");
//			
//		case BACoordinatorStatus.STATUS_EXITING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in exiting state");
//		
//		case BACoordinatorStatus.STATUS_ENDED:
//			baContext.unlock();
//			throw new InvalidStateException(
//				"Coordinator is in exiting state");
//	}
//}
////End - compensate
////Start -Failed
//public void CCFaultedOperation(BAActivityContext baContext, String enlishmentID)
//throws AbstractKandulaException 
//{
//	baContext.lock();
//	BAParticipantInformation baParticipantInformation = baContext.getParticipant(enlishmentID);
//	BACoordinatorCompletionParticipantServiceStub ccpsStub;
//	switch(baContext.getStatus())
//	{
//		case BACoordinatorStatus.STATUS_ACTIVE:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in active state");
//		
//		case BACoordinatorStatus.STATUS_CANCELLING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in cancelling state");
//			
//		case BACoordinatorStatus.STATUS_COMPLETING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in completing state");
//			
//		case BACoordinatorStatus.STATUS_COMPLETED:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in completed state");
//		
//		case BACoordinatorStatus.STATUS_CLOSING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in closing state");
//		
//		case BACoordinatorStatus.STATUS_COMPENSATING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in compensating state");
//		
//		case BACoordinatorStatus.STATUS_FAULTING:
//			try{
//				String participantEndpointReference = baParticipantInformation.getEpr().toString();		
//				ccpsStub = new BACoordinatorCompletionParticipantServiceStub(participantEndpointReference);
//				Failed	failedParam	= new Failed();
//				ccpsStub.FailedOperation(failedParam);
//				baContext.setStatus(BACoordinatorStatus.STATUS_ENDED);
//				baParticipantInformation.setStatus(Status.BACoordinatorStatus.STATUS_ENDED);
//				baContext.unlock();
//			}
//		catch(AxisFault e)
//		{
//			throw new KandulaGeneralException(e);
//		}
//		catch(Exception e)
//		{
//			//TODO
//		}
//	
//		case BACoordinatorStatus.STATUS_EXITING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in exiting state");
//		
//		case BACoordinatorStatus.STATUS_ENDED:
//			try{
//				String participantEndpointReference = baParticipantInformation.getEpr().toString();		
//				ccpsStub = new BACoordinatorCompletionParticipantServiceStub(participantEndpointReference);
//				Failed	failedParam	= new Failed();
//				ccpsStub.FailedOperation(failedParam);
//				baContext.unlock();
//			}
//		catch(AxisFault e)
//		{
//			throw new KandulaGeneralException(e);
//		}
//		catch(Exception e)
//		{
//			//TODO
//		}
//	}	
//
//}
////End - Failed
////Start - Exited
//public void CCExitedOperation(AbstractContext context,String enlishmentID)
//throws AbstractKandulaException 
//{
//	BACoordinatorCompletionParticipantServiceStub ccpsStub;
//	BAActivityContext baContext = (BAActivityContext) context;
//	baContext.lock();
//	BAParticipantInformation exitingParticipant = baContext.getParticipant(enlishmentID);
//	
//	switch(baContext.getStatus())
//	{
//		case BACoordinatorStatus.STATUS_ACTIVE:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in active state");
//			
//		case BACoordinatorStatus.STATUS_CANCELLING:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in canceling state");
//			
//		case BACoordinatorStatus.STATUS_COMPLETING:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in canceling state");
//			
//		case BACoordinatorStatus.STATUS_COMPLETED:
//			baContext.unlock();
//			throw new InvalidStateException(
//				"Coordinator is in completed state");					
//					
//		case BACoordinatorStatus.STATUS_CLOSING:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in closing state");	
//					
//		case BACoordinatorStatus.STATUS_COMPENSATING:
//			baContext.unlock();
//			throw new InvalidStateException(
//			"Coordinator is in compensating state");
//						
//		case BACoordinatorStatus.STATUS_FAULTING:
//			baContext.unlock();
//			throw new InvalidStateException(
//					"Coordinator is in faulting state");
//			
//		case BACoordinatorStatus.STATUS_EXITING:
//			try{ 		
//					ccpsStub = new BACoordinatorCompletionParticipantServiceStub(((exitingParticipant.getEpr()).toString()));
//					Exited  exitedParam = new Exited();
//					ccpsStub.ExitedOperation(exitedParam);
//					exitingParticipant.setStatus(Status.BACoordinatorStatus.STATUS_ENDED);
//					baContext.setStatus(BACoordinatorStatus.STATUS_ENDED);
//					baContext.unlock();
//				}
//			catch(AxisFault e)
//			{
//				throw new KandulaGeneralException(e);
//			}
//			catch(Exception e)
//			{
//				//TODO
//			}			
//		case BACoordinatorStatus.STATUS_ENDED:
//		try{
//			ccpsStub = new BACoordinatorCompletionParticipantServiceStub(((exitingParticipant.getEpr()).toString()));
//			Exited  exitedParam = new Exited();
//			ccpsStub.ExitedOperation(exitedParam);
//			baContext.setStatus(Status.BACoordinatorStatus.STATUS_ENDED);
//			baContext.unlock();
//		}
//		catch(AxisFault e)
//		{
//			throw new KandulaGeneralException(e);
//		}
//		catch(Exception e)
//		{
//			//TODO
//		}
//	}

//}
//End - Exited




}