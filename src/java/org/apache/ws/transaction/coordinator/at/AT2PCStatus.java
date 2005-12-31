/*
 * Created on Dec 23, 2005
 *
 */
package org.apache.ws.transaction.coordinator.at;

import java.lang.reflect.Field;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class AT2PCStatus {
	public static final int NONE = 0;

	public static final int ACTIVE = 1;

	public static final int PREPARING_VOLATILE = 2;

	public static final int PREPARING_DURABLE = 3;

	public static final int PREPARING = 4;

	public static final int PREPARED = 5;

	public static final int COMMITTING = 6;

	public static final int ABORTING = 7;

	private static Field[] flds = AT2PCStatus.class.getDeclaredFields();

	public static String getStatusName(int status) {
		try {
			for (int i = 0; i < flds.length; i++) {				
				if (flds[i].getInt(null) == status)
					return flds[i].getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException();
	}
}