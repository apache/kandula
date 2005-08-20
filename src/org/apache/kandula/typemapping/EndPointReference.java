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
package org.apache.kandula.typemapping;

import org.apache.kandula.typemapping.xmlbeansimpl.XmlBeansTypeEndPointReference;

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */
public interface EndPointReference {

    public abstract Object getEndPointReferenceType();
    
    public abstract String getPortTypeLocalPart();

    public static final class Factory {

        public static EndPointReference newInstance() {
            return new XmlBeansTypeEndPointReference();
        }

        public static EndPointReference newInstance(Object contextType) {
            return new XmlBeansTypeEndPointReference(contextType);
        }

        private Factory() {
        } // No instance of this class allowed
    }
}

