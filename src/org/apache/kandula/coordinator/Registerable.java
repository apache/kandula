package org.apache.kandula.coordinator;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.context.ActivityContext;
import org.apache.kandula.KandulaException;

/**
 * Created by IntelliJ IDEA.
 * User: Thilina
 * Date: Sep 13, 2005
 * Time: 8:17:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Registerable {
    public EndpointReference register(ActivityContext context, String protocol,
                                      EndpointReference participantEPR) throws KandulaException;
}
