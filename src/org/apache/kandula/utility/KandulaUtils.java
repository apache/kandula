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
package org.apache.kandula.utility;

import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.AddressingConstants.Final;
import org.apache.axis2.addressing.AnyContentType;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMNamespace;
import org.apache.axis2.soap.SOAPFactory;

import javax.xml.namespace.QName;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class KandulaUtils {
    
    public static void endpointToOM(EndpointReference epr, OMElement parentEPR, SOAPFactory factory) {
        OMNamespace wsAddressing = factory.createOMNamespace(
                AddressingConstants.Submission.WSA_NAMESPACE,
                AddressingConstants.WSA_DEFAULT_PRFIX);
        OMElement addressElement = factory.createOMElement("Address",
                wsAddressing);
        addressElement.setText(epr.getAddress());
        parentEPR.addChild(addressElement);
        AnyContentType referenceValues = epr.getReferenceProperties();
        if (referenceValues != null) {
            OMElement refPropertyElement = factory.createOMElement(
                    "ReferenceProperties", wsAddressing);
            parentEPR.addChild(refPropertyElement);
            Iterator iterator = referenceValues.getKeys();
            while (iterator.hasNext()) {
                QName key = (QName) iterator.next();
                String value = referenceValues.getReferenceValue(key);
                OMElement omElement = factory.createOMElement(key,
                        refPropertyElement);
                refPropertyElement.addChild(omElement);
                if (Final.WSA_NAMESPACE.equals(wsAddressing)) {
                    omElement.addAttribute(
                            Final.WSA_IS_REFERENCE_PARAMETER_ATTRIBUTE,
                            Final.WSA_TYPE_ATTRIBUTE_VALUE, wsAddressing);
                }
                omElement.setText(value);
            }
        }
    }
    
    public static EndpointReference endpointFromOM(OMElement eprElement)
    {
        EndpointReference epr;
        epr = new EndpointReference(eprElement
                .getFirstChildWithName(new QName("Address")).getText());
        AnyContentType referenceProperties = new AnyContentType();
        OMElement referencePropertiesElement = eprElement
                .getFirstChildWithName(new QName("ReferenceProperties"));
        Iterator propertyIter = referencePropertiesElement.getChildElements();
        while (propertyIter.hasNext()) {
            OMElement element = (OMElement) propertyIter.next();
            referenceProperties.addReferenceValue(element.getQName(), element
                    .getText());
        }
        epr.setReferenceProperties(referenceProperties);
        return epr;
    }

    /**
     * MD5 a random string with localhost/date etc will return 128 bits
     * construct a string of 18 characters from those bits.
     *
     * @return string
     */
    public static String getRandomStringOf18Characters() {
        Random myRand = new Random();
        long rand = myRand.nextLong();
        String sid;
        try {
            sid = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            sid = Thread.currentThread().getName();
        }
        long time = System.currentTimeMillis();
        StringBuffer sb = new StringBuffer();
        sb.append(sid);
        sb.append(":");
        sb.append(Long.toString(time));
        sb.append(":");
        sb.append(Long.toString(rand));
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            //System.out.println("Error: " + e);
            //todo heve to be properly handle
        }
        md5.update(sb.toString().getBytes());
        byte[] array = md5.digest();
        StringBuffer sb2 = new StringBuffer();
        for (int j = 0; j < array.length; ++j) {
            int b = array[j] & 0xFF;
            sb2.append(Integer.toHexString(b));
        }
        int begin = myRand.nextInt();
        if (begin < 0) begin = begin * -1;
        begin = begin % 8;
        return new String(sb2.toString().substring(begin, begin + 18)).toUpperCase();
    }
}
