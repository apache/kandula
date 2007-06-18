/*
 * Copyright 2007 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 *  @author Hannes Erven, Georg Hicker
 */
package org.apache.kandula.coordinator.ba;

import javax.xml.namespace.QName;

/**
 * This class provides fields and convenience methods for handling protocol types and protocol type QNames.
 * 
 * @author Hannes Erven, Georg Hicker (C) 2006
 *
 */
public class ProtocolType {
	/**
	 * The QName used to identify the BA with Participant Completion protocol. 
	 */
	public final static QName PROTOCOL_ID_PC = QName.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba/}ParticipantCompletion");

	/**
	 * The QName used to identify the BA with Coordinator Completion protocol.
	 */
	public final static QName PROTOCOL_ID_CC = QName.valueOf("{http://schemas.xmlsoap.org/ws/2004/10/wsba/}CoordinatorCompletion");

	private final QName protocol; 
	
	
	/**
	 * Default Constructor. Stores the chosen protocol
	 * @param xprotocol The chosen protocol
	 * @throws IllegalArgumentException if an invalid protocol is passed
	 */
	public ProtocolType(final String xprotocol) throws IllegalArgumentException {
		if (xprotocol.equals(PROTOCOL_ID_CC.toString())
				|| xprotocol.equals(PROTOCOL_ID_CC.getNamespaceURI()+PROTOCOL_ID_CC.getLocalPart())
		) {
			this.protocol = PROTOCOL_ID_CC;
		} else if (xprotocol.equals(PROTOCOL_ID_PC.toString())
				|| xprotocol.equals(PROTOCOL_ID_PC.getNamespaceURI()+PROTOCOL_ID_PC.getLocalPart())
		) {
			this.protocol = PROTOCOL_ID_PC;
		} else {
			throw new IllegalArgumentException(
					"The specified protocol '"+xprotocol+"' is not available!\n"
					+ "Choose one of the following:\n"
					+ "\t" + PROTOCOL_ID_PC.toString() + "\n"
					+ "\t" + PROTOCOL_ID_CC.toString());
		}
	}
	/**
	 * @return Returns the protocol.
	 */
	public QName getProtocol() {
		return this.protocol;
	}

}
