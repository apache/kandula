/*
 * Created on Dec 23, 2005
 *
 */
package org.apache.kandula.utils;


/**
 * @author Dasarath Weeratunge
 *  
 */
public class TCPSnifferHelper {

	public static String redirect(String url) {
		return url.replaceAll("wsi\\.alphaworks\\.ibm\\.com:8080",
				"localhost:8082");
	}
	
}