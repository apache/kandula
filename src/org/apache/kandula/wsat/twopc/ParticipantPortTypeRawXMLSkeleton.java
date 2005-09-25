package org.apache.kandula.wsat.twopc;

import java.io.IOException;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.om.OMElement;
import org.apache.kandula.Constants;
import org.apache.kandula.context.AbstractContext;
import org.apache.kandula.initiator.TransactionManager;
import org.apache.kandula.storage.StorageFactory;
import org.apache.kandula.context.coordination.CoordinationContext;

/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

/**
 * @author <a href="mailto:thilina@opensource.lk"> Thilina Gunarathne </a>
 */

public class ParticipantPortTypeRawXMLSkeleton {
    private MessageContext msgContext;

    public void init(MessageContext context) {
        this.msgContext = context;
    }

    public OMElement prepareOperation(OMElement requestEle){
      return null;

    }

    public OMElement commitOperation(OMElement requestEle) {
        return null;
    }

    public OMElement rollbackOperation(OMElement requestEle){
        return null;
    }
}