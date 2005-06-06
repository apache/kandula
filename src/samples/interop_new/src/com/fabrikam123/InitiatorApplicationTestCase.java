/*
 * Created on May 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.fabrikam123;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.ws.transaction.participant.standalone.TransactionManager;

/**
 * @author Thilina Gunarathne
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InitiatorApplicationTestCase extends TestCase {
	/*public void testCompletionCommit() throws Exception {
		ParticipantPortType participantApp = new ParticipantServiceLocator().getParticipantPortType(new URL("http://localhost:8080/axis/services/InteropServiceNew"));
		participantApp.completionCommit();		
	}
	public void testCompletionRollback() throws Exception {
	    ParticipantPortType participantApp = new ParticipantServiceLocator().getParticipantPortType(new URL("http://localhost:8081/axis/services/InteropServiceNew"));
		participantApp.completionRollback();		
	}*/
	public void testCommit() throws Exception {
	    
		TransactionManager tm= TransactionManager.getInstance();
		ParticipantPortType participantApp = new ParticipantServiceLocator().getParticipantPortType(new URL("http://localhost:8081/axis/services/InteropServiceNew"));
		tm.begin();
		participantApp.commit();
		tm.commit();
	}
	public void testRollback() throws Exception {
	    
		TransactionManager tm= TransactionManager.getInstance();
		ParticipantPortType participantApp = new ParticipantServiceLocator().getParticipantPortType(new URL("http://localhost:8081/axis/services/InteropServiceNew"));
		tm.begin();
		participantApp.rollback();
		tm.rollback();
	}
}
