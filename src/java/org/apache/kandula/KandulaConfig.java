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
 *  @author Dasarath Weeratunge, Hannes Erven, Georg Hicker
 */
package org.apache.kandula;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Dasarath Weeratunge, Hannes Erven, Georg Hicker
 *  
 */
public class KandulaConfig {

	private static final String PROPERTY_FILE = "kandula.properties";

	private static final String LOCAL_SERVICE__PROPERTY = "kandula.localService";

	private static final String PREFERRED_SERVICE__PROPERTY = "kandula.preferredCoordinationService";

	private static KandulaConfig instance = new KandulaConfig();

	private Properties properties = null;

	private KandulaConfig() {
		this.properties = new Properties();
		loadProperties();
	}

	public static KandulaConfig getInstance() {
		return instance;
	}

	private void loadProperties() {
		InputStream in = getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE);

		try {
			this.properties.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public String getContext() {
		return this.properties.getProperty(LOCAL_SERVICE__PROPERTY);
	}

	/**
	 * Return the configured local kandula services endpoint base URL, eg.
	 * http://test1.kandula.apache.org:8280/axis/services/
	 * @return String-URL
	 */
	public String getLocalServicesURL() {
		return this.properties.getProperty(LOCAL_SERVICE__PROPERTY);
	}

	/**
	 * Return the configured preferred kandula coordination services endpoint base URL, eg.
	 * http://my-favorite-coordinator.bar.foo.org:8180/axis/services/
	 * @return String-URL
	 */
	public String getKandulaServicesURL(){
		final String externalURL = this.properties.getProperty(PREFERRED_SERVICE__PROPERTY);

		if (externalURL != null && externalURL.length()>0)
			return externalURL;
		
		return getContext();
	}
}
