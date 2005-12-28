/*
 * Created on Dec 27, 2005
 *
 */
/**
 * @author Dasarath Weeratunge
 *  
 */
public interface TestSuite1PortType {

	/*
	 * no resources involved
	 */
	void testReadonlyCommit();
	
	void testReadonlyRollback();
	
	/*
	 * involves just one resource, which answers XA_OK on prepare
	 */
	void testRollback();

	void testPrepareCommit();
	
	/*
	 * involves two resources, one which answers XA_OK and another
	 * which throws an XAException on prepare
	 */
	void testPrepareRollback();

	/*
	 * attempt to commit the local (j2ee) tx
	 */
	void testEarlyCommit();
	
	/*
	 * attemp to rollback the local (j2ee) tx
	 */
	
	void testEarlyRollback();
	
	/*
	 * local (j2ee) tx is marked rollback
	 */
	void testMarkedRollbackCommit();
	
	void testMarkedRollbackRollback();
	
	/*
	 * involves one resource that throws an XAException on commit/rollback
	 */
	
	void testCommitFailure();
	
	void testRollbackFailure();
	
	
	
	
	void justReturnOperation();	
	
	void enlistXA_OKOnPrepareResourceOperation();
	
	void enlistXAExceptionOnPrepareResourceOperation();
	
	void markTransactionForRollbackOperation();
	
	void commitTransactionOperation();
	
	void rollbackTransactionOperation();
	
	void enlistXAExceptionOnCommitRollbackResourceOperation();
	
}