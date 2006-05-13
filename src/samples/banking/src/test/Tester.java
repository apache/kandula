/*
 * Created on May 10, 2006
 *
 */
package test;

/**
 * @author Dasarath Weeratunge
 *
 */
public class Tester {

	public static void main(String[] args) throws Exception {
		BankOneTest test = new BankOneTestServiceLocator().getBankOneTest();
		test.test1();
	}
}
