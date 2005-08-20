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
package org.apache.kandula.coordinator.context.at;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.kandula.Constants;
import org.apache.kandula.KandulaException;
import org.apache.kandula.coordinator.context.ActivityContext;
import org.apache.kandula.coordinator.context.ActivityContextImpl;
import org.apache.kandula.coordinator.context.Participant;
import org.apache.kandula.typemapping.CoordinationContext;
import org.apache.kandula.typemapping.EndPointReference;
import org.apache.kandula.utility.EndpointReferenceFactory;

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

    private EndPointReference parentEPR;

    public ATActivityContext() {
        super(Constants.WS_AT);
        volatileParticipantsTable = new Hashtable();
        durableParticipantsTable = new Hashtable();
    }

    public ATActivityContext(CoordinationContext context) {
        subOrdinate = true;
        parentEPR = context.getRegistrationService();
        context.setRegistrationService(EndpointReferenceFactory.getInstance()
                .getRegistrationEndpoint());
        volatileParticipantsTable = new Hashtable();
        durableParticipantsTable = new Hashtable();
        setCoordinationContext(context);
    }

    /**
     * @param participantEPR
     * @param protocol
     * @return Coordinator protocol service.
     * @throws KandulaException
     */
    public String addParticipant(String participantEPR, String protocol)
            throws KandulaException {
        if (protocol.equals(Constants.WS_AT_VOLATILE2PC)) {
            return addVolatileParticipant(participantEPR);
        } else if (protocol.equals(Constants.WS_AT_DURABLE2PC)) {
            return addDurableParticipant(participantEPR);
        }
        return "2PC port EPR";
    }

    public String addVolatileParticipant(String participantEPR)
            throws KandulaException {
        if (volatileParticipantsTable.contains(participantEPR))
            throw new KandulaException("wscoor:Already Registered");
        volatileParticipantsTable.put(participantEPR, new Participant(
                participantEPR, Constants.WS_AT_VOLATILE2PC));
        return "2PC port";
    }

    public String addDurableParticipant(String participantEPR)
            throws KandulaException {
        if (durableParticipantsTable.contains(participantEPR))
            throw new KandulaException("wscoor:Already Registered");
        durableParticipantsTable.put(participantEPR, new Participant(
                participantEPR, Constants.WS_AT_DURABLE2PC));
        return "2PC port";
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
        if (preparingParticipantsCount > 0) {
            return true;
        } else {
            return false;
        }
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