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
package org.apache.kandula.wscoor;

import javax.xml.namespace.QName;

import org.apache.axis2.clientapi.AsyncResult;
import org.apache.axis2.clientapi.Callback;
import org.apache.axis2.om.OMElement;
import org.apache.kandula.typemapping.CoordinationContext;
import org.apache.kandula.typemapping.SimpleCoordinationContext;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class ActivationPortTypeRawXMLCallback extends Callback {
    
    SimpleCoordinationContext  coordinationContext;
    Exception e;
    
    public void onComplete(AsyncResult result) {
        OMElement response = (OMElement)result.getResponseEnvelope().getFirstChildWithName(new QName("CreateCoordinationContextResponse"));
        OMElement coordinationContextElement = response.getFirstElement();
        coordinationContextElement.build();
        coordinationContextElement.detach();
        coordinationContext = new SimpleCoordinationContext(coordinationContextElement);
    }

    /* (non-Javadoc)
     * @see org.apache.axis2.clientapi.Callback#reportError(java.lang.Exception)
     */
    public void reportError(Exception e) {
        this.e = e;
        this.setComplete(true);
    }
    public CoordinationContext getCoordinationContext() throws Exception
    {
        if (e!=null)
        {
            throw e;
        }
        else if (coordinationContext!=null)
        {
            return coordinationContext;
        }
        return null;
    }

}
