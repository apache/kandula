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
package org.apache.kandula.context.at;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.Constants;
import org.apache.kandula.KandulaException;
import org.apache.kandula.Status;
import org.apache.kandula.context.ActivityContext;
import org.apache.kandula.context.ActivityContextImpl;
import org.apache.kandula.context.Participant;
import org.apache.kandula.context.coordination.CoordinationContext;
import org.apache.kandula.utility.EndpointReferenceFactory;
import org.apache.kandula.utility.KandulaUtils;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class ATActivityContext extends ActivityContextImpl implements
        ActivityContext {

    private int preparingParticipantsCount = 0;

    private boolean subOrdinate = false;

    private Hashtable volatileParticipantsTable;

    private Hashtable durableParticipantsTable;

    private boolean subVolatileRegistered = false;

    private boolean subDurableRegistered = false;

    private EndpointReference parentEPR;

    public static String REQUESTER_ID = "requesterID";

    public static String ACTIVATION_EPR = "activationEPR";


    /**
     * Used when creating new activities
     */
    public ATActivityContext() {
        super(Constants.WS_AT);
        this.setStatus(Status.CoordinatorStatus.STATUS_ACTIVE);
        volatileParticipantsTable = new Hashtable();
        durableParticipantsTable = new Hashtable();
    }

    /**
     * @param context To be used when coordinator is used as a sub ordinate to an another  cooordinator
     */
    public ATActivityContext(CoordinationContext context) {
        subOrdinate = true;
        parentEPR = context.getRegistrationService();
//        context.setRegistrationService(EndpointReferenceFactory.getInstance()
//                .getRegistrationEndpoint());
        this.setStatus(Status.CoordinatorStatus.STATUS_ACTIVE);
        volatileParticipantsTable = new Hashtable();
        durableParticipantsTable = new Hashtable();
        setCoordinationContext(context);
    }

    /**
     * @param id To be used when using as the requester
     */
    public ATActivityContext(EndpointReference activationEPR) {
        super();
        this.setProperty(REQUESTER_ID, KandulaUtils.getRandomStringOf18Characters());
        this.setProperty(ACTIVATION_EPR, activationEPR);
    }

    /**
     * @param participantEPR
     * @param protocol
     * @return Coordinator protocol service.
     * @throws KandulaException
     */
    public EndpointReference addParticipant(EndpointReference participantEPR, String protocol)
            throws KandulaException {
        if (Constants.WS_AT_VOLATILE2PC.equals(protocol)) {
            addVolatileParticipant(participantEPR);
            return EndpointReferenceFactory.getInstance().get2PCEndpoint(this.activityID);
        } else if (Constants.WS_AT_DURABLE2PC.equals(protocol)) {
            addDurableParticipant(participantEPR);
            return EndpointReferenceFactory.getInstance().get2PCEndpoint(this.activityID);
        } else if (Constants.WS_AT_COMPLETION.equals(protocol)) {
            //TODO keep track of requesters
            return EndpointReferenceFactory.getInstance().getCompletionEndpoint(this.activityID);
        } else {
            throw new KandulaException("UnSupported Protocol");
        }
    }

    public void addVolatileParticipant(EndpointReference participantEPR)
            throws KandulaException {
        if (volatileParticipantsTable.contains(participantEPR))
            throw new KandulaException("wscoor:Already Registered");
        volatileParticipantsTable.put(participantEPR, new Participant(
                participantEPR, Constants.WS_AT_VOLATILE2PC));

    }

    public void addDurableParticipant(EndpointReference participantEPR)
            throws KandulaException {
        if (durableParticipantsTable.contains(participantEPR))
            throw new KandulaException("wscoor:Already Registered");
        durableParticipantsTable.put(participantEPR, new Participant(
                participantEPR, Constants.WS_AT_DURABLE2PC));

    }

    public Iterator getRegisteredParticipants(String protocol) {
        if (protocol.equals(Constants.WS_AT_VOLATILE2PC)) {
            return volatileParticipantsTable.values().iterator();
        } else if (protocol.equals(Constants.WS_AT_DURABLE2PC)) {
            return durableParticipantsTable.values().iterator();
        }
        return null;
    }

    public Iterator getAllParticipants() {
        LinkedList list = new LinkedList(volatileParticipantsTable.values());
        list.addAll(durableParticipantsTable.values());
        return list.iterator();
    }

    public void countPreparing() {
        preparingParticipantsCount++;

    }

    public boolean hasMorePreparing() {
        return (preparingParticipantsCount > 0);
    }

    public boolean getSubVolatileRegistered() {

        return subVolatileRegistered;
    }

    public boolean getSubDurableRegistered() {
        return subDurableRegistered;
    }

    public void setSubVolatileRegistered(boolean value) {
        subVolatileRegistered = value;
    }

    public void setSubDurableRegistered(boolean value) {
        subDurableRegistered = value;
    }
    //    public void prepared(Participant participant)
    //    {
    //        if (participant.getStatus()==Status.ParticipantStatus.STATUS_ABORTED)
    //        {
    //            //throw new
    //        }
    //    }

}