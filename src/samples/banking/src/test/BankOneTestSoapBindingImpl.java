/**
 * BankOneTestSoapBindingImpl.java
 * 
 */

package test;

import java.rmi.RemoteException;

import org.apache.kandula.coordinator.at.TransactionImpl;
import org.apache.kandula.coordinator.at.TransactionManagerImpl;

import bankone.BankOne;
import bankone.BankOneServiceLocator;

public class BankOneTestSoapBindingImpl implements BankOneTest {

	TransactionManagerImpl tm = TransactionManagerImpl.getInstance();

	private void tx1(BankOne bank) throws RemoteException {
		System.out.println("[BankOneTestSoapBindingImpl] tx1");
		tm.begin();
		bank.credit(0, 100.0);
		bank.credit(1, 200.0);
		tm.commit();
		getBalances(bank);
	}
	
	private void tx2(BankOne bank) throws RemoteException {
		System.out.println("[BankOneTestSoapBindingImpl] tx2");
		tm.begin();
		bank.credit(0, 50.0);
		bank.debit(1, 50.0);
		tm.commit();
		getBalances(bank);
	}
	
	private void tx3(BankOne bank) throws RemoteException {
		System.out.println("[BankOneTestSoapBindingImpl] tx3");
		tm.begin();
		bank.credit(0, 35.0);
		bank.debit(1, 35.0);
		getBalances(bank);
		tm.rollback();
		getBalances(bank);		
	}
	
	private void tx4(BankOne bank) throws RemoteException {
		System.out.println("[BankOneTestSoapBindingImpl] tx4");
		
		tm.begin();
		bank.debit(0, 20.0);
		bank.credit(1, 20.0);		
		getBalances(bank);
		TransactionImpl txa = tm.suspend();
		
		tm.begin();
		bank.debit(0, 70.0);
		bank.credit(1, 70.0);		
		getBalances(bank);
		TransactionImpl txb = tm.suspend();
		
		try {
			txa.commit();
		}
		catch(Exception e) {
			System.out.println("[BankOneTestSoapBindingImpl] could not commit txa");
		}
		
		try {
			txb.commit();
		}
		catch(Exception e) {
			System.out.println("[BankOneTestSoapBindingImpl] could not commit txb");
		}
		getBalances(bank);		
	}
	
	
	private void getBalances(BankOne bank) throws RemoteException {
		double balance0 = bank.getBalance(0);
		double balance1 = bank.getBalance(1);
		System.out.println("[BankOneTestSoapBindingImpl] balance0= " + balance0
				+ " balance1= " + balance1);
	}

	public void test1() throws RemoteException {
		try {
			BankOneServiceLocator locator = new BankOneServiceLocator();
			BankOne bank = locator.getBankOne();
			tx1(bank);
			tx2(bank);
			tx4(bank);
			tx3(bank);
		} catch (Exception e) {
			throw new RemoteException(e.getMessage());
		}
	}

}