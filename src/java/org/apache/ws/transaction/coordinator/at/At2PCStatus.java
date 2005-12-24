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
public class At2PCStatus {
	public static final int NONE = 0;

	public static final int ACTIVE = 1;

	public static final int PREPARING_VOLATILE = 2;
	
	public static final int PREPARING_DURABLE = 3;

	public static final int COMMITTING = 5;

	public static final int ABORTING = 6;

	public static final int ENDED = 7;

	private static Field[] flds = At2PCStatus.class.getDeclaredFields();

	public static String getStatusName(int status) {
		String statusName = null;

		try {
			for (int i = 0; i < flds.length; i++) {
				if (flds[i].getInt(null) == status)
					statusName = flds[i].getName();
			}
		} catch (Exception e) {
			statusName = null;
		}
		return statusName;
	}
}