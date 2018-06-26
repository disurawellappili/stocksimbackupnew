package org.stock.controllers;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.stock.models.*;
import org.stock.models.CompanyTrends;
import org.stock.resource.CompanyStocksDAO;
import org.stock.resource.PlayerDAO;
import org.stock.resource.PlayerStocksDAO;
public class StopWatch {
	static int time;
    static Timer timer;
    public static void RoundTimer() {
    	int delay = 1000;
        int period = 1000;
        timer = new Timer();
        time = 120;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                setInterval();
            }
        }, delay, period);
    }
    private static int setInterval() {
        if (time == 1) {
            timer.cancel();
            time=0;
            if(MarketController.getCurrent_round()<10) {
            	MarketController.setCurrent_round(MarketController.getCurrent_round()+1);
            	AI_Player.play();
            	List<CompanyTrends> company_trends=MarketController.getCompanyTrends();
            	for(CompanyStocks company:CompanyStocksDAO.getAll()) {
            		for(CompanyTrends trend:company_trends) {
            			if(company.getCompany_Name().equals(trend.getCompany_name())){
            				company.setShare_Vlaue(trend.getRound_values()[MarketController.getCurrent_round()-1]);
            				CompanyStocksDAO.update(company);
            				
            				for(PlayerStocks stock:PlayerStocksDAO.getAll()) {
                    			if(company.getCompany_Name().equals(stock.getCompany())) {
                    				stock.setStock_Value(stock.getStock_Count()*trend.getRound_values()[MarketController.getCurrent_round()-1]);
                    				PlayerStocksDAO.update(stock);
                    			}
                    		}
            			}
            		}
            		
            	}
            	for(Player player:PlayerDAO.getAll()) {
            		List<PlayerStocks> player_stocks=PlayerStocksDAO.getStocks(player.getPlayer_name());
            		double total_value=0;
            		for(PlayerStocks stock:player_stocks) {
            			total_value=total_value+stock.getStock_Value();
            		}
            		player.setStock_value(total_value);
            		PlayerDAO.update(player);
            	}
            	RoundTimer();
            }
            return time;
        }
        else if (time==0){
            return time;
        }
        else{
        return --time;
        }
    }
}
