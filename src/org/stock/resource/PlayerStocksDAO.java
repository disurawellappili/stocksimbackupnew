package org.stock.resource;
import java.util.ArrayList;
import java.util.List;

import org.stock.models.PlayerStocks;
public class PlayerStocksDAO {
	private static List<PlayerStocks> player_stocks = new ArrayList<PlayerStocks>();
	public static List<PlayerStocks> getAll(){
		return player_stocks;
	}
	
	
	public static void save(PlayerStocks player_stock) {
		player_stocks.add(player_stock);	
	}
	
	public static List<PlayerStocks> update(PlayerStocks player_stock) {
		for(PlayerStocks stock:player_stocks)
		{
			if((stock.getPlayer().equals(player_stock.getPlayer())) && (stock.getCompany().equals(player_stock.getCompany())))
			{
				stock.setStock_Count(player_stock.getStock_Count());
				stock.setStock_Value(player_stock.getStock_Value());
			}
		}
		
		
		return player_stocks;
	}
	
	
	public static List<PlayerStocks> getStocks(String player_name){
		List<PlayerStocks> playerStocks = new ArrayList<PlayerStocks>();
		for(PlayerStocks stock:player_stocks) {
			if(stock.getPlayer().equals(player_name)) {
				playerStocks.add(stock);
			}
		}
		
		
		return playerStocks;
	}
}
