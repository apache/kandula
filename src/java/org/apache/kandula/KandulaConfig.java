/*
 * Created on Jan 6, 2006
 *
 */
package org.apache.kandula;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Dasarath Weeratunge
 *  
 */
public class KandulaConfig {

	private static final String PROPERTY_FILE = "kandula.properties";

	private static final String CONTEXT_PROPERTY = "kandula.context";

	private static KandulaConfig instance = new KandulaConfig();

	private Properties properties = null;

	private KandulaConfig() {
		properties = new Properties();
		loadProperties();
	}

	public static KandulaConfig getInstance() {
		return instance;
	}

	private void loadProperties() {
		InputStream in = getClass().getClassLoader().getResourceAsStream(
			PROPERTY_FILE);

		try {
			properties.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public String getContext() {
		return properties.getProperty(CONTEXT_PROPERTY);
	}

}