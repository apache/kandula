    package org.apache.kandula.wscoor;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis2.addressing.AnyContentType;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.clientapi.MessageSender;
import org.apache.axis2.om.OMAbstractFactory;
import org.apache.axis2.om.OMElement;
import org.apache.axis2.om.OMNamespace;
import org.apache.kandula.Constants;
import org.apache.kandula.utility.KandulaListener;

    /*
     *  Auto generated java implementation by the Axis code generator
    */

    public class RegistrationCoordinatorPortTypeRawXMLStub extends org.apache.axis2.clientapi.Stub{
        public static final String AXIS2_HOME = ".";
        protected static org.apache.axis2.description.OperationDescription[] _operations;

        static{

           //creating the Service
           _service = new org.apache.axis2.description.ServiceDescription(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/ws/2003/09/wscoor","RegistrationCoordinatorPortType"));

           //creating the operations
           org.apache.axis2.description.OperationDescription __operation;
           _operations = new org.apache.axis2.description.OperationDescription[1];
      
          __operation = new org.apache.axis2.description.OperationDescription();
          __operation.setName(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/ws/2003/09/wscoor", "RegisterOperation"));
          _operations[0]=__operation;
          _service.addOperation(__operation);
          
     
       }

       /**
        * Constructor
        */
        public RegistrationCoordinatorPortTypeRawXMLStub(String axis2Home,String targetEndpoint) throws java.lang.Exception {
			this.toEPR = new org.apache.axis2.addressing.EndpointReference(targetEndpoint);
		    //creating the configuration
           _configurationContext = new org.apache.axis2.context.ConfigurationContextFactory().buildClientConfigurationContext(axis2Home);
           _configurationContext.getAxisConfiguration().addService(_service);
           _serviceContext = _configurationContext.createServiceContext(_service.getName());

	    }


        private org.apache.axis2.soap.SOAPEnvelope createSOAPEnvelope(
                String coordinationType) {
            org.apache.axis2.soap.SOAPEnvelope env = super.createEnvelope();
            org.apache.axis2.soap.SOAPFactory factory = OMAbstractFactory
                    .getSOAP12Factory();
            OMNamespace wsCoor = factory.createOMNamespace(Constants.WS_COOR,
                    "wscoor");
            OMElement request = factory.createOMElement(
                    "CreateCoordinationContext", wsCoor);
            OMElement coorType = factory
                    .createOMElement("CoordinationType", wsCoor);
            coorType.setText(coordinationType);
            request.addChild(coorType);
            env.getBody().addChild(request);
            return env;
        }

        public void RegisterOperation(String coordinationType,
                String id) throws IOException {

            QName serviceName = new QName("RegistrationRequesterPortType");
            QName operationName = new QName(Constants.WS_COOR,
                    "RegisterOperation");
            KandulaListener listener = KandulaListener.getInstance();
            listener.addService(serviceName, operationName,
                    RegistrationRequesterPortTypeRawXMLSkeleton.class.getName());
            listener.start();

            MessageSender messageSender = new MessageSender(_serviceContext);
            org.apache.axis2.context.MessageContext messageContext = getMessageContext();
            EndpointReference replyToEpr = new EndpointReference(listener.getHost()
                    + serviceName.getLocalPart());
            AnyContentType refProperties = new AnyContentType();
            refProperties.addReferenceValue(new QName(
                    "http://ws.apache.org/kandula", id), id);
            replyToEpr.setReferenceProperties(refProperties);
            //  messageSender.
            messageSender.setReplyTo(replyToEpr);
            messageSender.setTo(this.toEPR);
            messageSender.setSoapAction("CreateCoordinationContextOperation");
            //_call.setWsaAction("CreateCoordinationContextOperation");
            org.apache.axis2.soap.SOAPEnvelope env = createSOAPEnvelope(coordinationType);
            messageContext.setEnvelope(env);
            messageSender
                    .setSenderTransport(org.apache.axis2.Constants.TRANSPORT_HTTP);
            messageSender.send(_operations[0], messageContext);

        }

     
      
 
      
    }
    