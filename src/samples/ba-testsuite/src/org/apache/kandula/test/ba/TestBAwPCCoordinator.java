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
 *  @author Hannes Erven, Georg Hicker
 */
package org.apache.kandula.test.ba;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.kandula.coordinator.ActivationStub;
import org.apache.kandula.coordinator.CoordinationContext;
import org.apache.kandula.coordinator.CoordinationService;
import org.apache.kandula.coordinator.ba.ProtocolType;
import org.apache.kandula.coordinator.ba.coordinator.BACoordinator;
import org.apache.kandula.wsba.ExceptionType;
import org.apache.kandula.wsba.NotificationType;
import org.apache.kandula.wscoor.RegisterType;

import junit.framework.TestCase;

public class TestBAwPCCoordinator extends TestCase {

	TellMeWhatToDoDemoServiceSoapBindingStub binding;

	/**
	 * The endpoint of the activation service to use.
	 */
	public final EndpointReference activationServiceEPR = 
		CoordinationService.getInstance().getActivationCoordinatorService();
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestBAwPCCoordinator.class);
	}

	public TestBAwPCCoordinator(String arg0) {
		super(arg0);
	}

	/**
	 * 
	 */
	protected void setUp() throws Exception {
		assertNotNull(this.activationServiceEPR);
		try {
			this.binding = (TellMeWhatToDoDemoServiceSoapBindingStub) new TellMeWhatToDoDemoServiceSLocator().getTellMeWhatToDoDemoServicePort();

			// Time out after a minute
			this.binding.setTimeout(60000);
		} catch (javax.xml.rpc.ServiceException jre) {
			if (jre.getLinkedCause() != null)
				jre.getLinkedCause().printStackTrace();
			throw new junit.framework.AssertionFailedError(
					"JAX-RPC ServiceException caught: " + jre);
		}
	}

	/**
	 * 
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * Create a new coordination context with Atomic Outcome coordination type.
	 * @throws Exception
	 * @see #createAndTestCoordinationContext(String)
	 */
	public void testCreateCoordinationContext_Atomic() throws Exception{
		createAndTestCoordinationContext(BACoordinator.COORDINATION_TYPE__ATOMIC);
	}
	/**
	 * Create a new coordination context with Mixed Outcome coordination type.
	 * @throws Exception
	 * @see #createAndTestCoordinationContext(String)
	 */
	public void testCreateCoordinationContext_Mixed() throws Exception{
		createAndTestCoordinationContext(BACoordinator.COORDINATION_TYPE__MIXED);
	}
	
	/**
	 * Creates a new Coordination Context with the given coordination type. This method makes a call to the
	 * WS-Coordination Activation Services specified in the kandula configuration.
	 * 
	 * Attention: this method is test case supporting and should not be called by non-testcase classes!
	 * 
	 * @param coordinationType The Coordination Type.
	 * @return The Coordination Context object. It is guaranteed that it is non-null and features the
	 *          requested coordination type.
	 * @throws Exception If an error occurs.
	 */
	protected CoordinationContext createAndTestCoordinationContext(final String coordinationType) throws Exception {
		final CoordinationContext ctx;
		ctx = new ActivationStub(this.activationServiceEPR).createCoordinationContext(coordinationType);

		assertNotNull(ctx);
		assertEquals(ctx.getCoordinationType().toString(), coordinationType);
		
		return ctx;
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testRegisterOperation_CC() throws Exception {
		assertNotNull("binding is null", this.binding);

		// Test operation
		final CoordinationContext ctx = createAndTestCoordinationContext(BACoordinator.COORDINATION_TYPE__ATOMIC);

		final TestRegisterResponseType rrt = registerParticipant(ctx, ProtocolType.PROTOCOL_ID_CC);
		System.out.println("testregister: part#"+rrt.getTestParticipantReference()+", protocol service is at "+rrt.getTestRegisterResponse().getCoordinatorProtocolService());
		// TBD - validate results
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testRegisterOperation_PC() throws Exception {
		assertNotNull("binding is null", this.binding);

		// Test operation
		final CoordinationContext ctx = createAndTestCoordinationContext(BACoordinator.COORDINATION_TYPE__ATOMIC);

		final TestRegisterResponseType rrt = registerParticipant(ctx, ProtocolType.PROTOCOL_ID_PC);
		System.out.println("testregister: part#"+rrt.getTestParticipantReference()+", protocol service is at "+rrt.getTestRegisterResponse().getCoordinatorProtocolService());
		// TBD - validate results
	}

	/**
	 * Register a new participant for a certain protocol
	 * @param ctx The coordination context
	 * @param proto
	 * @return The register response
	 * @throws MalformedURIException 
	 * @throws RemoteException 
	 */
	protected TestRegisterResponseType registerParticipant(
			final CoordinationContext ctx, 
			final QName proto
	) throws MalformedURIException, RemoteException{
		final RegisterType regType = new RegisterType();
		regType.setProtocolIdentifier(new URI(proto.getNamespaceURI()+proto.getLocalPart()));
		regType.setParticipantProtocolService(ctx.getRegistrationService());

		final TestRegisterResponseType rrt = this.binding.doTestRegisterOperation(regType);

		assertNotNull(rrt);
		assertNotNull(rrt.getTestParticipantReference());
		assertNotNull(rrt.getTestRegisterResponse());

		return rrt;
	}
	
	/**
	 * Test exit on 
	 * @throws Exception
	 */
	public void testExitOperation_CC() throws Exception {
		assertNotNull("binding is null", this.binding);

		// Test operation
		final CoordinationContext ctx = createAndTestCoordinationContext(BACoordinator.COORDINATION_TYPE__ATOMIC);
		
		final TestRegisterResponseType rrt = registerParticipant(ctx, ProtocolType.PROTOCOL_ID_CC);
		
		System.out.println("testExit: part#"+rrt.getTestParticipantReference()+", protocol service is at "+rrt.getTestRegisterResponse().getCoordinatorProtocolService());
		// Now: try exiting
		final TestExitRequestType tert = new TestExitRequestType(new NotificationType(), rrt.getTestParticipantReference());
		final boolean r = this.binding.doTestExitOperation(tert);
		
		assertTrue("Result was false", r);
	}

	/**
	 * Test exit on 
	 * @throws Exception
	 */
	public void testExitOperation_SendExitTwice_CC() throws Exception {
		assertNotNull("binding is null", this.binding);

		// Test operation
		final CoordinationContext ctx = createAndTestCoordinationContext(BACoordinator.COORDINATION_TYPE__ATOMIC);
		
		final TestRegisterResponseType rrt = registerParticipant(ctx, ProtocolType.PROTOCOL_ID_CC);
		
		System.out.println("testExit: part#"+rrt.getTestParticipantReference()+", protocol service is at "+rrt.getTestRegisterResponse().getCoordinatorProtocolService());
		// Now: try exiting
		final TestExitRequestType tert = new TestExitRequestType(new NotificationType(), rrt.getTestParticipantReference());

		{
			final boolean r = this.binding.doTestExitOperation(tert);
			assertTrue("First Result was false", r);
		}
		{
			final boolean r = this.binding.doTestExitOperation(tert);
			assertTrue("Second Result was false", r);
		}
	}

	/**
	 * Test exit on 
	 * @throws Exception
	 */
	public void testExitOperation_SendExitTwice_PC() throws Exception {
		assertNotNull("binding is null", this.binding);

		// Test operation
		final CoordinationContext ctx = createAndTestCoordinationContext(BACoordinator.COORDINATION_TYPE__ATOMIC);
		
		final TestRegisterResponseType rrt = registerParticipant(ctx, ProtocolType.PROTOCOL_ID_PC);
		
		System.out.println("testExit: part#"+rrt.getTestParticipantReference()+", protocol service is at "+rrt.getTestRegisterResponse().getCoordinatorProtocolService());
		// Now: try exiting
		final TestExitRequestType tert = new TestExitRequestType(new NotificationType(), rrt.getTestParticipantReference());

		{
			final boolean r = this.binding.doTestExitOperation(tert);
			assertTrue("First Result was false", r);
		}
		{
			final boolean r = this.binding.doTestExitOperation(tert);
			assertTrue("Second Result was false", r);
		}
	}

	/**
	 * Test exit on 
	 * @throws Exception
	 */
	public void testExitOperation_PC() throws Exception {
		assertNotNull("binding is null", this.binding);

		// Test operation
		final CoordinationContext ctx = createAndTestCoordinationContext(BACoordinator.COORDINATION_TYPE__ATOMIC);
		
		final TestRegisterResponseType rrt = registerParticipant(ctx, ProtocolType.PROTOCOL_ID_PC);
		
		System.out.println("testExit: part#"+rrt.getTestParticipantReference()+", protocol service is at "+rrt.getTestRegisterResponse().getCoordinatorProtocolService());
		// Now: try exiting
		final TestExitRequestType tert = new TestExitRequestType(new NotificationType(), rrt.getTestParticipantReference());
		final boolean r = this.binding.doTestExitOperation(tert);
		
		assertTrue("Result was false", r);
	}

	/**
	 * Test exit on 
	 * @throws Exception
	 */
	public void testFaultOperation_PC() throws Exception {
		assertNotNull("binding is null", this.binding);

		// Test operation
		final CoordinationContext ctx = createAndTestCoordinationContext(BACoordinator.COORDINATION_TYPE__ATOMIC);
		
		final TestRegisterResponseType rrt = registerParticipant(ctx, ProtocolType.PROTOCOL_ID_PC);
		
		System.out.println("testExit: part#"+rrt.getTestParticipantReference()+", protocol service is at "+rrt.getTestRegisterResponse().getCoordinatorProtocolService());
		// Now: try exiting
		final TestFaultRequestType tfrt = new TestFaultRequestType(new ExceptionType("Kandula_TEST", null), rrt.getTestParticipantReference());
		final boolean r = this.binding.doTestFaultOperation(tfrt);
		
		assertTrue("Result was false", r);
	}

	/**
	 * Test exit on 
	 * @throws Exception
	 */
	public void testFaultOperation_CC() throws Exception {
		assertNotNull("binding is null", this.binding);

		// Test operation
		final CoordinationContext ctx = createAndTestCoordinationContext(BACoordinator.COORDINATION_TYPE__ATOMIC);
		
		final TestRegisterResponseType rrt = registerParticipant(ctx, ProtocolType.PROTOCOL_ID_CC);
		
		System.out.println("testExit: part#"+rrt.getTestParticipantReference()+", protocol service is at "+rrt.getTestRegisterResponse().getCoordinatorProtocolService());
		// Now: try exiting
		final TestFaultRequestType tfrt = new TestFaultRequestType(new ExceptionType("Kandula_TEST", null), rrt.getTestParticipantReference());
		final boolean r = this.binding.doTestFaultOperation(tfrt);
		
		assertTrue("Result was false", r);
	}

}
