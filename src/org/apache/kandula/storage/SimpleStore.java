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

import org.apache.kandula.context.AbstractContext;

import java.util.HashMap;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public class SimpleStore implements Store {

    private HashMap contextMap;

    /* (non-Javadoc)
    * @see org.apache.kandula.storage.Store#putContext(java.lang.String, org.apache.kandula.context.ActivityContext)
    */
    public SimpleStore() {
        contextMap = new HashMap();
    }

    public void put(Object id, Object context) {
        contextMap.put(id, context);
    }

    /* (non-Javadoc)
     * @see org.apache.kandula.storage.Store#getContext(java.lang.String)
     */
    public Object get(Object id) {
        return (AbstractContext) contextMap.get(id);
    }

    /* (non-Javadoc)
     * @see org.apache.kandula.storage.Store#forgetContext(java.lang.String)
     */
    public void forget(Object id) {
        contextMap.remove(id);
    }

}
