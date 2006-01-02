/*
 * Created on Dec 30, 2005
 *
 */
package org.apache.ws.transaction.utility;

import org.apache.ws.transaction.coordinator.TimedOutException;

/**
 * @author Dasarath Weeratunge
 *  
 */
public interface Callback {

	void timeout() throws TimedOutException;

}