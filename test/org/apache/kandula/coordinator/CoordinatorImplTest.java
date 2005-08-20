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

import org.apache.kandula.Constants;
import org.apache.kandula.KandulaException;
import org.apache.kandula.Status;
import org.apache.kandula.coordinator.context.ActivityContext;
import org.apache.kandula.coordinator.context.at.ATActivityContext;
import org.apache.kandula.typemapping.CoordinationContext;
import org.xmlsoap.schemas.ws.x2003.x09.wscoor.CoordinationContextType;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class CoordinatorImplTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Class under test for ActivityContext createCoordinationContext(String, long)
     */
    public void testCreateCoordinationContextStringlong() throws KandulaException {
        CoordinatorImpl coordinator =  new CoordinatorImpl();
        ActivityContext context = coordinator.createCoordinationContext(Constants.WS_AT,3000);
        assertTrue(ATActivityContext.class.isInstance(context));
        assertNotNull(context.getCoordinationContext());
        assertTrue(context.getStatus()==Status.CoordinatorStatus.STATUS_ACTIVE);
    }

    /*
     * Class under test for ActivityContext createCoordinationContext(CoordinationContext)
     */
    public void testCreateCoordinationContextCoordinationContext() throws KandulaException {
        CoordinatorImpl coordinator =  new CoordinatorImpl();
        CoordinationContext coorContext =  CoordinationContext.Factory.newInstance();
        coorContext.setCoordinationType(Constants.WS_AT);
        coorContext.setExpires(3000);
        coorContext.setActivityID("uuid:29919219jdk02102021");
       // coorContext.setRegistrationService()
        ActivityContext context = coordinator.createCoordinationContext(coorContext);
        assertTrue(ATActivityContext.class.isInstance(context));
        assertNotNull(context.getCoordinationContext());
        assertTrue(context.getStatus()==Status.CoordinatorStatus.STATUS_ACTIVE);
        assertEquals(context.getCoordinationContext().getCoordinationType(),coorContext.getCoordinationType());
        assertEquals(context.getCoordinationContext().getActivityID(),coorContext.getActivityID());
    }

    public void testRegisterParticipant() {
        //TODO Implement registerParticipant().
    }

}
