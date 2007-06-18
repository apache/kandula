/*
 * Copyright 2007 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 *  @author Hannes Erven, Georg Hicker
 */

package org.apache.kandula.coordinator.ba.coordinator;

/**
 * This interface defines common methods all BusinessActivity participants share.
 * 
 * In fact, as the BAwPC and BAwCC protocols only differ by one single state and one single message,
 * this interface helps reduce duplicate code. 
 *  
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public interface AbstractCoordParticipantProxy {

	/**
	 * Invoke the "close" operation on the participant.
	 * @param parameters Parameters.
	 * @throws java.rmi.RemoteException A fault from the participant.
	 */
    public void closeOperation(org.apache.kandula.wsba.NotificationType parameters) throws java.rmi.RemoteException;

	/**
	 * Invoke the "cancel" operation on the participant.
	 * @param parameters Parameters.
	 * @throws java.rmi.RemoteException A fault from the participant.
	 */
    public void cancelOperation(org.apache.kandula.wsba.NotificationType parameters) throws java.rmi.RemoteException;
    
	/**
	 * Invoke the "compensate" operation on the participant.
	 * @param parameters Parameters.
	 * @throws java.rmi.RemoteException A fault from the participant.
	 */
    public void compensateOperation(org.apache.kandula.wsba.NotificationType parameters) throws java.rmi.RemoteException;
    
	/**
	 * Invoke the "faulted" operation on the participant.
	 * @param parameters Parameters.
	 * @throws java.rmi.RemoteException A fault from the participant.
	 */
    public void faultedOperation(org.apache.kandula.wsba.NotificationType parameters) throws java.rmi.RemoteException;
    
	/**
	 * Invoke the "exited" operation on the participant.
	 * @param parameters Parameters.
	 * @throws java.rmi.RemoteException A fault from the participant.
	 */
    public void exitedOperation(org.apache.kandula.wsba.NotificationType parameters) throws java.rmi.RemoteException;
    
	/**
	 * Invoke the "getStatus" operation on the participant.
	 * @param parameters Parameters.
	 * @throws java.rmi.RemoteException A fault from the participant.
	 */
    public void getStatusOperation(org.apache.kandula.wsba.NotificationType parameters) throws java.rmi.RemoteException ;
    
	/**
	 * Invoke the "status" operation on the participant.
	 * @param parameters Parameters.
	 * @throws java.rmi.RemoteException A fault from the participant.
	 */
    public void statusOperation(org.apache.kandula.wsba.StatusType parameters) throws java.rmi.RemoteException;
}
