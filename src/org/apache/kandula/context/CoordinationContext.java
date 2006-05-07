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
package org.apache.kandula.context;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.kandula.context.impl.SimpleCoordinationContext;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public interface CoordinationContext {
	public abstract String getActivityID();

	public abstract String getCoordinationType();

	public abstract EndpointReference getRegistrationService();

	public abstract long getExpires();

	public abstract void setActivityID(String value);

	public abstract void setCoordinationType(String value);

	public abstract void setRegistrationService(EndpointReference value);

	public abstract void setExpires(long value);

	public abstract OMElement toOM();

	public abstract Object getCoordinationContextType();

	public static final class Factory {
		public static CoordinationContext newContext(String activityId,
				String coordinationType, EndpointReference epr) {
			return new SimpleCoordinationContext(activityId, coordinationType,
					epr);
		}

		public static CoordinationContext newContext(OMElement contextElement) {
			return new SimpleCoordinationContext(contextElement);
		}

		//        public static CoordinationContext newInstance(Object contextType) {
		//            return new XmlBeansTypeCoordinationContext(contextType);
		//        }

		private Factory() {
		} // No instance of this class allowed
	}
}