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
package org.apache.ws.transaction.coordinator;

import javax.xml.namespace.QName;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.ReferencePropertiesType;

public class Identifier {
	public static final QName QNAME_IDENTIFIER =
		    new QName("http://schemas.xmlsoap.org/ws/2002/07/utility", "Identifier");	
	
	String id;

	public Identifier(String id) {
		this.id = id;
	}

	public static boolean is(MessageElement e) {
		return e.getQName().equals(QNAME_IDENTIFIER);
	}	

	public Identifier(MessageElement e) {
		if (!is(e))
			throw new IllegalArgumentException(e.getQName().toString());
		id = e.getValue();
	}	

	public ReferencePropertiesType toReferencePropertiesType() {
		try {
			MessageElement[] _referenceProperties = new MessageElement[1];
			_referenceProperties[0] = new MessageElement(QNAME_IDENTIFIER, id);			
			ReferencePropertiesType referenceProperties = new ReferencePropertiesType();
			referenceProperties.set_any(_referenceProperties);
			return referenceProperties;
		}
		catch (Exception  e) {
			throw new RuntimeException(e);
		}
	}

	public int hashCode() {
		return id.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj instanceof Identifier) {
			Identifier _id = (Identifier)obj;
			return id.equals(_id.id);
		}
		return false;
	}
	
	public String toString() {
		return id;
	}
}
