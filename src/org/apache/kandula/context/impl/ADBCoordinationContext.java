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

package org.apache.kandula.context.impl;

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.Constants;
import org.apache.kandula.context.CoordinationContext;
import org.apache.kandula.utility.EndpointReferenceFactory;
import org.oasis_open.docs.ws_tx.wscoor._2006._06.CoordinationContext_type3;

public class ADBCoordinationContext implements CoordinationContext {

	CoordinationContext_type3 context_type3;
	public ADBCoordinationContext(CoordinationContext_type3 context_type3) {
		super();
		this.context_type3 = context_type3;
	}
	public String getActivityID() {
		Map map = this.getRegistrationService().getAllReferenceParameters();
		OMElement element  = (OMElement)map.get(Constants.TRANSACTION_ID_PARAMETER);
		return element.getText();
	}
	public Object getCoordinationContextType() {
		return this;
	}
	public String getCoordinationType() {
		return context_type3.getCoordinationType().getPath();
	}
	public long getExpires() {
		return context_type3.getExpires().getUnsignedInt().longValue();
	}
	public EndpointReference getRegistrationService() {
		return EndpointReferenceFactory
		.getEPR(context_type3.getRegistrationService());
	}
	public void setActivityID(String value) {
		// TODO Auto-generated method stub
		
	}
	public void setCoordinationType(String value) {
		// TODO Auto-generated method stub
		
	}
	public void setExpires(long value) {
		// TODO Auto-generated method stub
		
	}
	public void setRegistrationService(EndpointReference value) {
		// TODO Auto-generated method stub
		
	}
	public OMElement toOM() {
		return context_type3.getOMElement(new QName(Constants.WS_COOR,"CoordinationContext"),
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
	}

}
