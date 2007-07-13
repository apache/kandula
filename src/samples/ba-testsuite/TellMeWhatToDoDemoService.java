import org.apache.axis.message.addressing.EndpointReferenceType;

/*
 * Created on Dec 27, 2005
 *
 */
/**
 * @author Hannes Erven, Georg Hicker (C) 2006
 *  
 */
public interface BaTestSuiteBAwPCTestParticipantPortType{

    org.apache.kandula.wscoor.RegisterResponseType doTestRegisterOperation(String protocolIdentifier, EndpointReferenceType participantEndpoint);
    java.lang.Boolean doTestCompletedOperation(org.apache.kandula.wsba.NotificationType parameters);
    java.lang.Boolean doTestFaultOperation(org.apache.kandula.wsba.ExceptionType parameters);
    java.lang.Boolean doTestCompensatedOperation(org.apache.kandula.wsba.NotificationType parameters);
    java.lang.Boolean doTestClosedOperation(org.apache.kandula.wsba.NotificationType parameters);
    java.lang.Boolean doTestCanceledOperation(org.apache.kandula.wsba.NotificationType parameters);
    java.lang.Boolean doTestExitOperation(org.apache.kandula.wsba.NotificationType parameters);
    java.lang.Boolean doTestGetStatusOperation(org.apache.kandula.wsba.NotificationType parameters);
    java.lang.Boolean doTestStatusOperation(org.apache.kandula.wsba.StatusType parameters);

    void closeOperation(org.apache.kandula.wsba.NotificationType parameters);
    void cancelOperation(org.apache.kandula.wsba.NotificationType parameters);
    void compensateOperation(org.apache.kandula.wsba.NotificationType parameters);
    void faultedOperation(org.apache.kandula.wsba.NotificationType parameters);
    void exitedOperation(org.apache.kandula.wsba.NotificationType parameters);
    void getStatusOperation(org.apache.kandula.wsba.NotificationType parameters);
    void statusOperation(org.apache.kandula.wsba.StatusType parameters);
}