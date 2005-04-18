package org.apache.ws.transaction.participant.j2ee;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.resource.spi.XATerminator;
import javax.rmi.PortableRemoteObject;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

import org.apache.ws.transaction.participant.j2ee.TransactionImporter;
import org.apache.ws.transaction.participant.j2ee.TransactionManagerGlue;
import org.objectweb.jotm.Control;
import org.objectweb.jotm.ControlImpl;
import org.objectweb.jotm.Coordinator;
import org.objectweb.jotm.Current;
import org.objectweb.jotm.InternalTransactionContext;
import org.objectweb.jotm.Resource;
import org.objectweb.jotm.TransactionContext;
import org.objectweb.jotm.TransactionFactory;
import org.objectweb.jotm.TransactionFactoryImpl;
import org.objectweb.jotm.TransactionImpl;


public class JOTM1_4_3TransactionManagerGlueImpl implements TransactionImporter, XATerminator, TransactionManagerGlue {
	Map txList= Collections.synchronizedMap(new HashMap());
	TransactionFactory txFactory= null;
	Current current;

	public JOTM1_4_3TransactionManagerGlueImpl() {
		current= Current.getCurrent();
		if (current == null)
			current= new Current();
		try {
			txFactory= new TransactionFactoryImpl();
			//???
			PortableRemoteObject.unexportObject(txFactory);
		}
		catch (RemoteException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public TransactionImporter getTransactionImporter() {
		return this;
	}

	public XATerminator getXATerminator() {
		return this;
	}

	public TransactionManager getTransactionManager() {
		return current;
	}

	public Transaction importExternalTransaction(Xid xid, int timeout) {
		try {
			Control control= txFactory.create(timeout);
			//???
			PortableRemoteObject.unexportObject(control);
			TransactionContext ctx= new InternalTransactionContext(timeout, (Coordinator)control, xid);
			Transaction tx= new TransactionImpl(ctx);
			txList.put(xid, control);
			return tx;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void commit(Xid xid, boolean onePhase) throws XAException {
		ControlImpl control= (ControlImpl)txList.remove(xid);
		if (control == null)
			throw new XAException();
		try {
			if (onePhase)
				control.commit_one_phase();
			control.commit();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new XAException();
		}
	}

	public void forget(Xid xid) throws XAException {
		txList.remove(xid);
	}

	public int prepare(Xid xid) throws XAException {
		ControlImpl control= (ControlImpl)txList.get(xid);
		if (control == null)
			throw new XAException();
		int ret;
		try {
			ret= control.prepare();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new XAException();
		}
		if (ret == Resource.VOTE_ROLLBACK)
			throw new XAException();
		return ret;
	}

	public Xid[] recover(int arg0) throws XAException {
		return null;
	}

	public void rollback(Xid xid) throws XAException {
		ControlImpl control= (ControlImpl)txList.remove(xid);
		if (control == null)
			throw new XAException();
		try {
			control.rollback();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new XAException();
		}
	}

}
