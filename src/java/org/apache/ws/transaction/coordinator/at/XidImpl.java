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
package org.apache.ws.transaction.coordinator.at;

import javax.transaction.xa.Xid;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.addressing.ReferencePropertiesType;
import org.apache.ws.transaction.coordinator.Identifier;



public class XidImpl implements Xid {
	public static final int FORMAT_ID= 0x0101 + 1;
	//	we add 1 to the format ID used in JBoss/XidImpl
	String globalTxId;
	String branchQualifier;

	private UUIDGen uuidGen= UUIDGenFactory.getUUIDGen();

	public static final QName QNAME_XIDIMPL=
		new QName("http://www.cse.mrt.ac.lk/~dasaru/final-project/wsatimpl", "xid");

	static final Name NAME_FORMAT_ID=
		new PrefixedQName("http://www.cse.mrt.ac.lk/~dasaru/final-project/wsatimpl", "formatId", "xidImpl");

	static final Name NAME_GLOBAL_TRANSACTION_ID=
		new PrefixedQName(
			"http://www.cse.mrt.ac.lk/~dasaru/final-project/wsatimpl",
			"globalTransactionId",
			"xidImpl");

	static final Name NAME_BRANCH_QUALIFIER=
		new PrefixedQName(
			"http://www.cse.mrt.ac.lk/~dasaru/final-project/wsatimpl",
			"branchQualifier",
			"xidImpl");

	public XidImpl() {
		globalTxId= createId();
		branchQualifier= "uuid:0";
	}

	private XidImpl(String globalTxId, String branchQualifier) {
		this.globalTxId= globalTxId;
		this.branchQualifier= branchQualifier;
	}

	public static boolean is(MessageElement e) {
		return e.getQName().equals(QNAME_XIDIMPL)
			&& Integer.parseInt(e.getAttributeValue(NAME_FORMAT_ID)) == FORMAT_ID;
	}

	public XidImpl(MessageElement e) {
		if (!is(e))
			throw new IllegalArgumentException();
		globalTxId= e.getAttributeValue(NAME_GLOBAL_TRANSACTION_ID);
		branchQualifier= e.getAttributeValue(NAME_BRANCH_QUALIFIER);
	}

	public XidImpl newBranch() {
		return new XidImpl(globalTxId, createId());
	}

	public ReferencePropertiesType toReferencePropertiesType() {
		try {
			MessageElement[] _referenceProperties= new MessageElement[1];
			_referenceProperties[0]= new MessageElement(QNAME_XIDIMPL);
			_referenceProperties[0].addAttribute(NAME_FORMAT_ID, Integer.toString(FORMAT_ID));
			_referenceProperties[0].addAttribute(NAME_GLOBAL_TRANSACTION_ID, globalTxId);
			if (branchQualifier != null)
				_referenceProperties[0].addAttribute(NAME_BRANCH_QUALIFIER, branchQualifier);
			ReferencePropertiesType referenceProperties= new ReferencePropertiesType();
			referenceProperties.set_any(_referenceProperties);
			return referenceProperties;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public int hashCode() {
		if (branchQualifier != null)
			return globalTxId.hashCode() + branchQualifier.hashCode();
		else
			return globalTxId.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj instanceof XidImpl) {
			XidImpl xid= (XidImpl)obj;
			if (xid.globalTxId.equals(globalTxId))
				if (xid.branchQualifier == null)
					return branchQualifier == null;
				else
					return xid.branchQualifier.equals(branchQualifier);
		}
		return false;
	}

	private String createId() {
		return "uuid:" + uuidGen.nextUUID();
	}

	public String toString() {
		if (branchQualifier == null)
			return "xid://?formatId=" + FORMAT_ID + ",globalTransactionId=" + globalTxId;
		else
			return "xid://?formatId="
				+ FORMAT_ID
				+ ",globalTransactionId="
				+ globalTxId
				+ ",branchQualifier="
				+ branchQualifier;
	}

	public Identifier toActivityId() {
		return new Identifier(toString());
	}

	public byte[] getBranchQualifier() {
		return branchQualifier.getBytes();
	}

	public int getFormatId() {
		return FORMAT_ID;
	}

	public byte[] getGlobalTransactionId() {
		return globalTxId.getBytes();
	}
}
