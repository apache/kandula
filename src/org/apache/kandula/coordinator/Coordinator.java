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
package org.apache.kandula.coordinator;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.KandulaException;
import org.apache.kandula.coordinator.context.ActivityContext;
import org.apache.kandula.typemapping.CoordinationContext;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public interface Coordinator {
    /**
     * @param expires TODO
     * @param CoordinationProtocolType
     *            (eg: ws-AT)
     * @return the Coordination Context created
     * 
     * Initiators can use this to Create new Distributed transactions.This will
     * take in Coordination protocol Type as a parameter and will create an
     * instance of the reapective ActivityContext. This created
     * CoordinationContext will contain an unique activity identifier &
     * Coordinator Registration EPR in addition to the Coordination protocol
     * type. This Coordination Context will be used to convey the information
     * about the transaction between initiator,Participants and coordinator.
     */
    public abstract ActivityContext createCoordinationContext(
            String coordinationType, long expires) throws KandulaException;

    /**
     * @param Coordinationcontext
     * @return the interposed Coordination Context created
     * 
     * Participants decided to use this Coordinator as a interposed
     * sub-coordinator.The newly created CoordinationContext will contain the
     * same ActivityIdentifier & Protocol type. Registration EPR of the earlier
     * CoordinationContext will be replaced by the RegistrationEPR of this
     * Coordinator.
     */
    public abstract ActivityContext createCoordinationContext(
            CoordinationContext coorContext) throws KandulaException;

    /**
     * @param CoordinationSubProtocols
     *            (eg: Completion, 2PC)
     * @param participantEPR
     * @param Activity-id
     * @return Should return the particular Coordinators End Point Reference
     * 
     * This method provides the functional logic for participants to register
     * for a particular protocol in the transaction activity which was created
     * by a initiator. Registration request will be forwarded to repective
     * protocol coordinators.
     */
    public abstract EndpointReference registerParticipant(String protocol,
            String id,EndpointReference participantEPR) throws KandulaException;
}