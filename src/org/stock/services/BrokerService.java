package org.stock.services;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.stock.controllers.MarketController;
import org.stock.models.AccountTransaction;
import org.stock.models.BankAccount;
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

@Path("/broker")
public class BrokerService {
	@GET
    @Produces({ MediaType.APPLICATION_JSON })
    public List<CompanyStocks> getStockPrices() {
        List<CompanyStocks> companies = CompanyStocksDAO.getAll();
        return companies;
    }
	
	@GET
    @Path("player/{player}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Player getPlayer(@PathParam("player") String player) {
        return PlayerDAO.get(player);
    }
	@GET
    @Path("bank-account/{player}")
    @Produces({ MediaType.APPLICATION_JSON })
    public BankAccount bankAccount(@PathParam("player") String player) {
        return BankAccountDAO.get(player);
    }
	
	@GET
    @Path("stock-trans/{player}")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<StockTransactions> getTrans(@PathParam("player") String player) {
        return StockTransactionsDAO.getTransactions(player);
    }
	@GET
    @Path("account-trans/{player}")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<AccountTransaction> getAccTrans(@PathParam("player") String player) {
        return AccountTransactionDAO.getTransactions(player);
    }
	@GET
    @Path("player-stocks/{player}")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<PlayerStocks> getPlayerStocks(@PathParam("player") String player) {
        return PlayerStocksDAO.getStocks(player);
    }
	@GET
    @Path("players")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<Player> getPlayers() {
        return PlayerDAO.getAll();
    }
	
	@GET
	@Path("new-player/{player_name}")
    @Produces({ MediaType.APPLICATION_JSON })
    public boolean addplayer(@PathParam("player_name") String player_name) {
		Player player=new Player(player_name);
		boolean not_found=true;
		for(Player plyr:PlayerDAO.getAll()) {
			if(plyr.getPlayer_name().equals(player.getPlayer_name())) {
				not_found=false;
			}
		}
		if(not_found) {
			BankAccount bank_account=new BankAccount(player.getPlayer_name());
			BankAccountDAO.save(bank_account);
			PlayerDAO.save(player);
			MarketController.checkPlayers();
			return not_found;
		}else {
			return not_found;
		}
        
    }
	
	@GET
    @Path("sell/{player}/{company}/{stocks}")
    @Produces({ MediaType.APPLICATION_JSON })
    public boolean sellShares(@PathParam("player") String player,@PathParam("company") String company,@PathParam("stocks") int stocks) {
		List<PlayerStocks> stocks_list=PlayerStocksDAO.getStocks(player);
		Player plyr=PlayerDAO.get(player);
		for(CompanyStocks cmpny:CompanyStocksDAO.getAll()) {
			if(cmpny.getCompany_Name().equals(company)) {
				for(PlayerStocks ps:stocks_list) {
					if(ps.getCompany().equals(cmpny.getCompany_Name())) {
						ps.setStock_Count(ps.getStock_Count()-stocks);
						ps.setStock_Value(ps.getStock_Count()*cmpny.getShare_Vlaue());
        				PlayerStocksDAO.update(ps);
        				StockTransactions transaction=new StockTransactions(plyr.getPlayer_name(),ps.getCompany(),stocks,cmpny.getShare_Vlaue()*stocks,"SELL");
        				StockTransactionsDAO.save(transaction);
        				plyr.setStock_value(plyr.getStock_value()-(cmpny.getShare_Vlaue()*stocks));
        				PlayerDAO.update(plyr);
        				for(BankAccount bank_account:BankAccountDAO.getAll()) {
        					if(bank_account.getAccountHolder().equals(plyr.getPlayer_name())) {
        						BankAccountDAO.deposit(bank_account, (cmpny.getShare_Vlaue()*stocks));
        						AccountTransaction account_transaction=new AccountTransaction(bank_account.getAccountHolder(), "DEPOSIT", (cmpny.getShare_Vlaue()*stocks), (bank_account.getAccountBalance()+(cmpny.getShare_Vlaue()*stocks)));
        						AccountTransactionDAO.save(account_transaction);
        						break;
        					}
        				}
					}
				}
			}
		}
        return true;
    }
	
	@GET
    @Path("buy/{player}/{company}/{stocks}")
    @Produces({ MediaType.APPLICATION_JSON })
    public boolean buyshares(@PathParam("player") String player,@PathParam("company") String company,@PathParam("stocks") int stocks) {
		List<PlayerStocks> stocks_list=PlayerStocksDAO.getStocks(player);
		Player plyr=PlayerDAO.get(player);
		boolean found=false;
		for(CompanyStocks cmpny:CompanyStocksDAO.getAll()) {
			if(cmpny.getCompany_Name().equals(company)) {
				for(PlayerStocks ps:stocks_list) {
					if(ps.getCompany().equals(cmpny.getCompany_Name())) {
						found=true;
        				for(BankAccount bank_account:BankAccountDAO.getAll()) {
        					if(bank_account.getAccountHolder().equals(plyr.getPlayer_name())) {
        						if(bank_account.getAccountBalance()>=(cmpny.getShare_Vlaue()*stocks)) {
        							ps.setStock_Count(ps.getStock_Count()+stocks);
        							ps.setStock_Value(ps.getStock_Count()*cmpny.getShare_Vlaue());
        	        				PlayerStocksDAO.update(ps);
        	        				StockTransactions transaction=new StockTransactions(plyr.getPlayer_name(),ps.getCompany(),stocks,cmpny.getShare_Vlaue()*stocks,"BUY");
        	        				StockTransactionsDAO.save(transaction);
        	        				plyr.setStock_value(plyr.getStock_value()+(cmpny.getShare_Vlaue()*stocks));
        	        				PlayerDAO.update(plyr);
        	        				cmpny.setNo_of_Stocks(cmpny.getNo_of_Stocks()+stocks);
        	        				CompanyStocksDAO.update(cmpny);
        							AccountTransaction account_transaction=new AccountTransaction(bank_account.getAccountHolder(), "WITHDRAW", (cmpny.getShare_Vlaue()*stocks), (bank_account.getAccountBalance()-(cmpny.getShare_Vlaue()*stocks)));
            						AccountTransactionDAO.save(account_transaction);
            						BankAccountDAO.withdraw(bank_account, (cmpny.getShare_Vlaue()*stocks));
        						}
        						break;
        					}
        				}
					}
				}
				if(found==false) {
					
    				for(BankAccount bank_account:BankAccountDAO.getAll()) {
    					if(bank_account.getAccountHolder().equals(plyr.getPlayer_name())) {
    						if(bank_account.getAccountBalance()>=(cmpny.getShare_Vlaue()*stocks)) {
    							PlayerStocks player_stock=new PlayerStocks(plyr.getPlayer_name(),cmpny.getCompany_Name(),stocks,cmpny.getShare_Vlaue()*stocks);
    							PlayerStocksDAO.save(player_stock);
    							StockTransactions transaction=new StockTransactions(plyr.getPlayer_name(),cmpny.getCompany_Name(),stocks,cmpny.getShare_Vlaue()*stocks,"BUY");
    		    				StockTransactionsDAO.save(transaction);
    		    				plyr.setStock_value(plyr.getStock_value()+(cmpny.getShare_Vlaue()*stocks));
    		    				PlayerDAO.update(plyr);
    		    				CompanyStocksDAO.update(cmpny);
    							AccountTransaction account_transaction=new AccountTransaction(bank_account.getAccountHolder(), "WITHDRAW", (cmpny.getShare_Vlaue()*stocks), (bank_account.getAccountBalance()-(cmpny.getShare_Vlaue()*stocks)));
        						AccountTransactionDAO.save(account_transaction);
        						BankAccountDAO.withdraw(bank_account, (cmpny.getShare_Vlaue()*stocks));
    						}
    						break;
    					}
    				}
    				
				}
			}
		}
        return true;
    }
	
}
