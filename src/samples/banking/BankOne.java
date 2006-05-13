interface BankOne {

	/**
	 * return the current balance in account 'account'.
	 */
	double getBalance(int account);

	/**
	 * debit 'amount' to account 'account' and return the new balance.
	 */
	double debit(int account, double amount);

	/**
	 * credit 'amount' to account 'account' and return the new balance.
	 */
	double credit(int account, double amount);

}



