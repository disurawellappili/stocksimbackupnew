package org.stock.services;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.stock.controllers.AI_Player;
import org.stock.controllers.MarketController;
import org.stock.models.BankAccount;
import org.stock.models.CompanyStocks;
import org.stock.models.CompanyTrends;
import org.stock.models.Player;
import org.stock.models.PlayerStocks;
import org.stock.resource.BankAccountDAO;
import org.stock.resource.CompanyStocksDAO;
import org.stock.resource.PlayerDAO;
import org.stock.resource.PlayerStocksDAO;

@Path("/market")
public class MarketService {
	
	@GET
    @Path("init")
    @Produces({ MediaType.APPLICATION_JSON })
    public String initMarket() {
		try {
        MarketController.initMarket();
        return "Success";
		}catch(Exception e) {
        	return e.getMessage();
        }
    }
	
	@GET
    @Path("players")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<Player> getPlayers() {
        return PlayerDAO.getAll();
    }
	
	@GET
    @Path("companies")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<CompanyStocks> getCompanies() {
        return CompanyStocksDAO.getAll();
    }
	
	@GET
    @Path("round")
    @Produces({ MediaType.APPLICATION_JSON })
    public int getRound() {
        return MarketController.getCurrent_round();
    }
	
	@GET
    @Path("new-round")
    @Produces({ MediaType.APPLICATION_JSON })
    public int newRound() {
        MarketController.setCurrent_round(MarketController.getCurrent_round()+1);
        return MarketController.getCurrent_round();
    }
	
	@GET
    @Path("player-stock/{player_name}")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<PlayerStocks> getPlayerStocks(@PathParam("player_name") String player_name) {
        return PlayerStocksDAO.getStocks(player_name);
    }
	
	@GET
    @Path("bank-accounts")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<BankAccount> getAccounts() {
        return BankAccountDAO.getAll();
    }
	
	@GET
    @Path("ai-players")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<Player> getAIs() {
        return AI_Player.getPlayers();
    }
	@GET
    @Path("company-trends")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<CompanyTrends> getCompanyTrends() {
        return MarketController.getCompanyTrends();
    }
	@GET
    @Path("update_round/{player_name}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Player updateRound(@PathParam("player_name") String player_name) {
		Player p=new Player();
		for(Player plyr:PlayerDAO.getAll()) {
			if(plyr.getPlayer_name().equals(player_name)) {
				p=plyr;
				int cur_round=p.getCurrent_round();
				if(cur_round<10) {
					p.setCurrent_round(p.getCurrent_round()+1);
					PlayerDAO.update(p);
				}
				
			}
		}
        return p;
    }
}
