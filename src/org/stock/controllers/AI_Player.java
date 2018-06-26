package org.stock.controllers;

import java.util.ArrayList;
import java.util.List;

import org.stock.models.AccountTransaction;
import org.stock.models.BankAccount;
import org.stock.models.CompanyTrends;
import org.stock.models.CompanyStocks;
import org.stock.models.Player;
import org.stock.models.PlayerStocks;
import org.stock.models.StockTransactions;
import org.stock.resource.AccountTransactionDAO;
import org.stock.resource.BankAccountDAO;
import org.stock.resource.CompanyStocksDAO;
import org.stock.resource.PlayerDAO;
import org.stock.resource.PlayerStocksDAO;
import org.stock.resource.StockTransactionsDAO;

public class AI_Player {
	private static List<Player> players = new ArrayList<Player>();

	public static List<Player> getPlayers() {
		return players;
	}
	
	public static void newAiPlayer(Player player) {
		players.add(player);
	}

	public static void setPlayers(List<Player> players) {
		AI_Player.players = players;
	}
	
	public static void buy_shares(Player player) {
		List<CompanyTrends> company_trends=MarketController.getCompanyTrends();
		List<CompanyStocks> buyable=new ArrayList<CompanyStocks>();
		List<CompanyStocks> companies=CompanyStocksDAO.getAll();
		int round=MarketController.getCurrent_round()-1;
		for(CompanyTrends company_trend:company_trends) {
        	
        	int remaining=10-round;
        	int future_up=remaining/2;
        	double current_value=company_trend.getRound_values()[round];
        	double future_value=company_trend.getRound_values()[(round+future_up)];
        	double ratio=(future_value/current_value)*100;
        	if(ratio>=110) {
        		for(CompanyStocks company:companies) {
        			if(company.getCompany_Name().equals(company_trend.getCompany_name())) {
        				buyable.add(company);
        			}
        		}
        	}
        }
		List<PlayerStocks> player_stocks=new ArrayList<PlayerStocks>();
		List<PlayerStocks> stocks=PlayerStocksDAO.getAll();
		for(PlayerStocks stock:stocks) {
			if(stock.getPlayer()==player.getPlayer_name()) {
				player_stocks.add(stock);
			}
		}
		for(BankAccount bank_account:BankAccountDAO.getAll()) {
    		if(bank_account.getAccountHolder().equals(player.getPlayer_name())) {
    			BankAccount player_account=bank_account;
    			double balance=player_account.getAccountBalance();
    			double buyable_balance=balance*3/4;
    			for(CompanyStocks company:buyable) {
    				if(buyable_balance>0) {
    					int buyable_shares=(int)(buyable_balance/company.getShare_Vlaue());
    					if(buyable_shares>0) {
    						for(CompanyTrends trend:company_trends) {
            					if(trend.getCompany_name().equals(company.getCompany_Name())) {
            						double current_value=trend.getRound_values()[round];
            						boolean found=false;
            					for(PlayerStocks stock:player_stocks) {
                					if(stock.getCompany().equals(company.getCompany_Name())) {
                						stock.setStock_Count(stock.getStock_Count()+buyable_shares);
                						stock.setStock_Value(stock.getStock_Value()*current_value);
                						PlayerStocksDAO.update(stock);
                        				StockTransactions transaction=new StockTransactions(player.getPlayer_name(),stock.getCompany(),buyable_shares,current_value*buyable_shares,"BUY");
                        				StockTransactionsDAO.save(transaction);
                        				BankAccountDAO.withdraw(bank_account, (current_value*buyable_shares));
                						AccountTransaction account_transaction=new AccountTransaction(bank_account.getAccountHolder(), "WITHDRAW", (current_value*buyable_shares), (bank_account.getAccountBalance()-(current_value*buyable_shares)));
                						AccountTransactionDAO.save(account_transaction);
                						buyable_balance=buyable_balance-(current_value*buyable_shares);
                						player.setStock_value(player.getStock_value()+(current_value*buyable_shares));
                        				PlayerDAO.update(player);
                						found=true;
                					}
                				}
            					if(found==false) {
            						PlayerStocks player_stock=new PlayerStocks(player.getPlayer_name(),company.getCompany_Name(),buyable_shares,current_value*buyable_shares);
            						PlayerStocksDAO.save(player_stock);
            						StockTransactions transaction=new StockTransactions(player.getPlayer_name(),company.getCompany_Name(),buyable_shares,current_value*buyable_shares,"BUY");
                    				StockTransactionsDAO.save(transaction);
                    				BankAccountDAO.withdraw(bank_account, (current_value*buyable_shares));
            						AccountTransaction account_transaction=new AccountTransaction(bank_account.getAccountHolder(), "WITHDRAW", (current_value*buyable_shares), (bank_account.getAccountBalance()-(current_value*buyable_shares)));
            						AccountTransactionDAO.save(account_transaction);
            						buyable_balance=buyable_balance-(current_value*buyable_shares);
            						player.setStock_value(player.getStock_value()+(current_value*buyable_shares));
                    				PlayerDAO.update(player);
            					}
            					}
            				}
    					}
    				}
    			}
    			break;
    		}
    		
    	}
	}
	
	public static void sell_shares(Player player) {
		
		List<CompanyTrends> company_trends=MarketController.getCompanyTrends();
		List<PlayerStocks> stocks=PlayerStocksDAO.getStocks(player.getPlayer_name());
		List<PlayerStocks> player_stocks=new ArrayList<PlayerStocks>();
		for(PlayerStocks stock:stocks) {
			if(stock.getPlayer().equals(player.getPlayer_name())) {
				player_stocks.add(stock);
			}
		}
		for(CompanyTrends company_trend:company_trends) {
        	int round=MarketController.getCurrent_round()-1;
        	int remaining=10-round;
        	int future_up=remaining/2;
        	double current_value=company_trend.getRound_values()[round];
        	double future_value=company_trend.getRound_values()[(round+future_up)];
        	double ratio=(future_value/current_value)*100;
        	if(ratio>=10) {
        		for(PlayerStocks stock:player_stocks) {
        			if(stock.getCompany().equals(company_trend.getCompany_name())) {
        				int shares=stock.getStock_Count();
        				int sellable=shares*3/4;
        				stock.setStock_Count(stock.getStock_Count()-sellable);
        				stock.setStock_Value(stock.getStock_Count()*current_value);
        				PlayerStocksDAO.update(stock);
        				StockTransactions transaction=new StockTransactions(player.getPlayer_name(),stock.getCompany(),sellable,current_value*sellable,"SELL");
        				StockTransactionsDAO.save(transaction);
        				player.setStock_value(player.getStock_value()-(current_value*sellable));
        				PlayerDAO.update(player);
        				for(BankAccount bank_account:BankAccountDAO.getAll()) {
        					if(bank_account.getAccountHolder().equals(player.getPlayer_name())) {
        						BankAccountDAO.deposit(bank_account, (current_value*sellable));
        						AccountTransaction account_transaction=new AccountTransaction(bank_account.getAccountHolder(), "DEPOSIT", (current_value*sellable), (bank_account.getAccountBalance()+(current_value*sellable)));
        						AccountTransactionDAO.save(account_transaction);
        						break;
        					}
        				}
        			}
        		}
        	}
        }
	}
	
	public static void play() {
		for(Player player:players) {
			sell_shares(player);
			buy_shares(player);
		}
	}
}
