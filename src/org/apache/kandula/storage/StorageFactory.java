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
package org.apache.kandula.storage;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.kandula.Constants;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class StorageFactory {
    private static StorageFactory instance= new StorageFactory();

    private ConfigurationContext configurationContext=null;

    private Store clientStore =null;

    private StorageFactory()
    {}
    public static StorageFactory getInstance() {
        return instance;
    }

    public Store getStore() {
        if (configurationContext ==null)
            return null;
        Store store  = (Store)configurationContext.getProperty(Constants.KANDULA_STORE);
        if (store == null) {
            store = new SimpleStore();
            configurationContext.setProperty(Constants.KANDULA_STORE,store);
        }
        return store;
    }

    /*
     * TODO: Have to remove this. This is a hack done to get through the interop
     */
    public Store getInitiatorStore()
    {
         if  (clientStore==null)
         {
              clientStore = new SimpleStore();
         }
        return clientStore;
    }

    public void setConfigurationContext(ConfigurationContext configurationContext)
    {
        this.configurationContext = configurationContext;
    }
    /*
     * TODO : Remove this... Parts of the Hack done for Interop
    */
    public ConfigurationContext getConfigurationContext()
    {
       return configurationContext;
    }
}