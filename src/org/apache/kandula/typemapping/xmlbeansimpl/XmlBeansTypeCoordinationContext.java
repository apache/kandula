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
package org.apache.kandula.typemapping.xmlbeansimpl;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.om.OMElement;
import org.apache.kandula.typemapping.CoordinationContext;
import org.apache.kandula.typemapping.EndPointReference;
import org.xmlsoap.schemas.ws.x2002.x07.utility.AttributedDateTime;
import org.xmlsoap.schemas.ws.x2002.x07.utility.AttributedURI;
import org.xmlsoap.schemas.ws.x2003.x09.wscoor.CoordinationContextType;
import org.xmlsoap.schemas.ws.x2004.x03.addressing.EndpointReferenceType;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class XmlBeansTypeCoordinationContext implements CoordinationContext {
    private CoordinationContextType contextType;
    
    public XmlBeansTypeCoordinationContext() {
        super();
        EndpointReferenceType epr = EndpointReferenceType.Factory.newInstance();
        contextType = CoordinationContextType.Factory.newInstance();
    }
    /**
     * @param contextType
     */
    public XmlBeansTypeCoordinationContext(Object contextType) {
        super();
        this.contextType = (CoordinationContextType)contextType;
    }
 
    public String getActivityID() {
        return contextType.getIdentifier().getId();
    }

    public String getCoordinationType() {
        return contextType.getCoordinationType();
    }

    public EndpointReference getRegistrationService() {
        return null;//contextType.getRegistrationService().
    }

    public long getExpires() {
        return Long.parseLong(contextType.getExpires().getId());
    }

    public void setActivityID(String value) {
        AttributedURI uri = AttributedURI.Factory.newInstance();
        uri.setId(value);
        contextType.setIdentifier(uri);

    }

    public void setCoordinationType(String value) {
        contextType.setCoordinationType(value);

    }

    public void setRegistrationService(EndpointReference value) {
       // contextType.setRegistrationService((EndpointReferenceType)value.getEndPointReferenceType());

    }

    public void setExpires(long value) {
        AttributedDateTime dateTime = AttributedDateTime.Factory.newInstance();
        dateTime.setId(new Long(value).toString());
        contextType.setExpires(dateTime);
    }

    public Object getCoordinationContextType() {
        return contextType;
    }
    /* (non-Javadoc)
     * @see org.apache.kandula.typemapping.CoordinationContext#toOM()
     */
    public OMElement toOM() {
        // TODO Auto-generated method stub
        return null;
    }

}
