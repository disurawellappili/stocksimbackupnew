package org.stock.resource;

import java.util.ArrayList;
import java.util.List;

import org.stock.models.AccountTransaction;
public class AccountTransactionDAO {
	private static List<AccountTransaction> account_transactions=new ArrayList<AccountTransaction>();
	public static List<AccountTransaction> getAll(){
		return account_transactions;
	}
	
	
	public static void save(AccountTransaction account_transaction)
	
	{
		account_transactions.add(account_transaction);
	}
	
	public static List<AccountTransaction> getTransactions(String account_holder)
	{
		List<AccountTransaction> transactions = new ArrayList<AccountTransaction>();
		for(AccountTransaction transaction:account_transactions) {
			if(transaction.getAccount_holder().equals(account_holder)) {
				transactions.add(transaction);
			}
		}
		
		
		return transactions;
	}
}



