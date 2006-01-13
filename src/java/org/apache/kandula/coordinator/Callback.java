/*
 * Created on Dec 30, 2005
 *
 */
package org.apache.kandula.coordinator;

import javax.xml.soap.Name;

import org.apache.axis.message.addressing.EndpointReference;




/**
 * @author Dasarath Weeratunge
 *  
 */
public interface Callback {
	
	String getID();
	
	void onFault(Name code);

	void timeout() throws TimedOutException;
	
	EndpointReference getEndpointReference();

}