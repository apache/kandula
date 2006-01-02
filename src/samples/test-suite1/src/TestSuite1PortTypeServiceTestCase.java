
/**
 * TestSuite1PortTypeServiceTestCase.java
 *  
 */

public class TestSuite1PortTypeServiceTestCase extends junit.framework.TestCase {
/*	public TestSuite1PortTypeServiceTestCase(java.lang.String name) {
		super(name);
	}

	public void testTestSuite1WSDL() throws Exception {
		javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
		java.net.URL url = new java.net.URL(
				new TestSuite1PortTypeServiceLocator().getTestSuite1Address()
						+ "?WSDL");
		javax.xml.rpc.Service service = serviceFactory.createService(url,
			new TestSuite1PortTypeServiceLocator().getServiceName());
		assertTrue(service != null);
	}

	public void test1TestSuite1TestReadonlyCommit() throws Exception {
		TestSuite1SoapBindingStub binding;
		try {
			binding = (TestSuite1SoapBindingStub) new TestSuite1PortTypeServiceLocator().getTestSuite1();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError(
					"JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		binding.testReadonlyCommit();
		// TBD - validate results
	}

	public void test2TestSuite1TestReadonlyRollback() throws Exception {
		TestSuite1SoapBindingStub binding;
		try {
			binding = (TestSuite1SoapBindingStub) new TestSuite1PortTypeServiceLocator().getTestSuite1();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError(
					"JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		binding.testReadonlyRollback();
		// TBD - validate results
	}

	public void test3TestSuite1TestRollback() throws Exception {
		TestSuite1SoapBindingStub binding;
		try {
			binding = (TestSuite1SoapBindingStub) new TestSuite1PortTypeServiceLocator().getTestSuite1();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError(
					"JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		binding.testRollback();
		// TBD - validate results
	}

	public void test4TestSuite1TestPrepareCommit() throws Exception {
		TestSuite1SoapBindingStub binding;
		try {
			binding = (TestSuite1SoapBindingStub) new TestSuite1PortTypeServiceLocator().getTestSuite1();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError(
					"JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		binding.testPrepareCommit();
		// TBD - validate results
	}

	public void test5TestSuite1TestPrepareRollback() throws Exception {
		TestSuite1SoapBindingStub binding;
		try {
			binding = (TestSuite1SoapBindingStub) new TestSuite1PortTypeServiceLocator().getTestSuite1();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError(
					"JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		binding.testPrepareRollback();
		// TBD - validate results
	}
*/
	public void test6TestSuite1TestEarlyCommit() throws Exception {
		TestSuite1SoapBindingStub binding;
		try {
			binding = (TestSuite1SoapBindingStub) new TestSuite1PortTypeServiceLocator().getTestSuite1();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError(
					"JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		binding.testEarlyCommit();
		// TBD - validate results
	}
/*
	public void test7TestSuite1TestEarlyRollback() throws Exception {
		TestSuite1SoapBindingStub binding;
		try {
			binding = (TestSuite1SoapBindingStub) new TestSuite1PortTypeServiceLocator().getTestSuite1();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError(
					"JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		binding.testEarlyRollback();
		// TBD - validate results
	}

	public void test8TestSuite1TestMarkedRollbackCommit() throws Exception {
		TestSuite1SoapBindingStub binding;
		try {
			binding = (TestSuite1SoapBindingStub) new TestSuite1PortTypeServiceLocator().getTestSuite1();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError(
					"JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		binding.testMarkedRollbackCommit();
		// TBD - validate results
	}

	public void test9TestSuite1TestMarkedRollbackRollback() throws Exception {
		TestSuite1SoapBindingStub binding;
		try {
			binding = (TestSuite1SoapBindingStub) new TestSuite1PortTypeServiceLocator().getTestSuite1();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError(
					"JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		binding.testMarkedRollbackRollback();
		// TBD - validate results
	}

	public void test10TestSuite1TestCommitFailure() throws Exception {
		TestSuite1SoapBindingStub binding;
		try {
			binding = (TestSuite1SoapBindingStub) new TestSuite1PortTypeServiceLocator().getTestSuite1();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError(
					"JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		binding.testCommitFailure();
		// TBD - validate results
	}

	public void test11TestSuite1TestRollbackFailure() throws Exception {
		TestSuite1SoapBindingStub binding;
		try {
			binding = (TestSuite1SoapBindingStub) new TestSuite1PortTypeServiceLocator().getTestSuite1();
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError(
					"JAX-RPC ServiceException caught: " + jre);
		}
		assertNotNull("binding is null", binding);

		// Time out after a minute
		binding.setTimeout(60000);

		// Test operation
		binding.testRollbackFailure();
		// TBD - validate results
	}
*/

	public static void main(String[] args){
	}
}