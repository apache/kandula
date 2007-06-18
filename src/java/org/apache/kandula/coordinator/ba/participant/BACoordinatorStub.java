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
package org.apache.kandula.coordinator.ba.participant;

/**
 * The stub interface for all BAcoordinator ports. This interface contains all methods
 * that apply to all registrable participant protocols (currently, ParticipantCompletion
 * and CoordinatorCompletion)
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public interface BACoordinatorStub {

	/**
	 * The Completed operation.
	 * @param parameters Unused.
	 * @throws java.rmi.RemoteException fault
	 */
    public void completedOperation(org.apache.kandula.wsba.NotificationType parameters) throws java.rmi.RemoteException;
    
    /**
     * The Fault operation.
     * @param parameters Unused.
     * @throws java.rmi.RemoteException Fault.
     */
    public void faultOperation(org.apache.kandula.wsba.ExceptionType parameters) throws java.rmi.RemoteException;

    /**
     * The Compensated operation.
     * @param parameters Unused.
     * @throws java.rmi.RemoteException Fault.
     */
    public void compensatedOperation(org.apache.kandula.wsba.NotificationType parameters) throws java.rmi.RemoteException;

    /**
     * The Closed operation.
     * @param parameters Unused.
     * @throws java.rmi.RemoteException Fault.
     */
    public void closedOperation(org.apache.kandula.wsba.NotificationType parameters) throws java.rmi.RemoteException;

    /**
     * The Canceled operation.
     * @param parameters Unused.
     * @throws java.rmi.RemoteException Fault.
     */
    public void canceledOperation(org.apache.kandula.wsba.NotificationType parameters) throws java.rmi.RemoteException;

    /**
     * The Exit operation.
     * @param parameters Unused.
     * @throws java.rmi.RemoteException Fault.
     */
    public void exitOperation(org.apache.kandula.wsba.NotificationType parameters) throws java.rmi.RemoteException;
    
    /**
     * The GetStatus operation.
     * @param parameters Unused.
     * @throws java.rmi.RemoteException Fault.
     */
    public void getStatusOperation(org.apache.kandula.wsba.NotificationType parameters) throws java.rmi.RemoteException;

    /**
     * The Status operation.
     * @param parameters Unused.
     * @throws java.rmi.RemoteException Fault.
     */
    public void statusOperation(org.apache.kandula.wsba.StatusType parameters) throws java.rmi.RemoteException;
}
