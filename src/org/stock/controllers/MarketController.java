package org.stock.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.stock.models.*;
import org.stock.resource.CompanyStocksDAO;
import org.stock.resource.PlayerDAO;
import org.stock.resource.SectorDAO;
public class MarketController {
	
	private static int current_round=1;
	private static List<Player> player_list;
	private static int[] market_trends=new int[10];
	private static int[] general_trends=new int[10];
	private static List<Sector> sector_trends=new ArrayList<Sector>();
	private static List<Event> event_list=new ArrayList<Event>();
	private static List<CompanyTrends> company_trends=new ArrayList<CompanyTrends>();
	public static boolean start=false;
	
	public static int[] getGeneral_trends() {
		return general_trends;
	}
	public static void setGeneral_trends(int[] generalTrends) {
		general_trends = generalTrends;
	}
	public static int getCurrent_round() {
		return current_round;
	}
	public static void setCurrent_round(int currentRound) {
		current_round = currentRound;
	}
	public static List<Player> getPlayer_list() {
		return player_list;
	}
	public static void setPlayer_list(List<Player> playerList) {
		player_list = playerList;
	}
	public static int[] getMarket_trends() {
		return market_trends;
	}
	public static void setMarket_trends(int[] marketTrends) {
		market_trends = marketTrends;
	}
	public static List<Sector> getSector_trends() {
		return sector_trends;
	}
	public static void setSector_trends(List<Sector> sectorTrends) {
		sector_trends = sectorTrends;
	}
	public static List<Event> getEvent_list() {
		return event_list;
	}
	public static List<CompanyTrends> getCompanyTrends(){
		return company_trends;
	}
	public void setEvent_list(List<Event> eventList) {
		event_list = eventList;
	}
	
	public static void initMarket() {
		if(SectorDAO.getAll().isEmpty()) {
			SectorDAO.init();
		}
		if(CompanyStocksDAO.getAll().isEmpty()) {
			CompanyStocksDAO.init();
		}
		if(market_trends.length<10) {
			generateMarketTrends();
		}
		if(sector_trends.isEmpty()) {
			generateSectorTrends();
		}
		if(event_list.isEmpty()) {
			generateEventTrends();
		}
		if(general_trends.length<10) {
			generateGeneralTrends();
		}
		if(company_trends.isEmpty()) {
			calRoundStockValues();
		}
//		checkPlayers();
//		getCompanyValues();
	}
	
	
	private static void generateMarketTrends() {
		for(int i=0;i<10;i++){
		       market_trends[i] = -2 + (int) (Math.random() * ((2 - (-2)) + 1));
		}
	}
	
	private static void generateSectorTrends() {
		List<Sector> sectors=SectorDAO.getAll();
		for(Sector sector:sectors) {
			for(int i=0;i<10;i++) {
				Sector trend=new Sector(sector.getSector_name());
				int sec = -3 + (int) (Math.random() * ((3 - (-3)) + 1));
				trend.setSector_Trend(sec);
				sector_trends.add(trend);
			}
		}
	}
	
	private static void generateEventTrends() {
		int end = 0;
		int start=0;
		int duration=0;
		String event_type="";
		while(end<10){
			if(end==0){
				start=2;
			}
			else{
				start=end+1;
			}
			duration = (int )(Math.random() * 8 + 1);
			end=start+duration;
			while(end>10){
				duration = (int )(Math.random() * 8 + 1);
				end=start+duration;               
			}
			//Random Market Component: 
			int range=-6 + (int) (Math.random() * ((5 - (-6)) + 1));
			if(range<=3 && range>=2 )
			    event_type="PROFIT_WARNING";
			if((range<=-3 && range>=-6)&&(range<=-1 && range>=-5) ){
				int chk = (int )(Math.random() * 2 + 1);
			    if(chk==1){
			        event_type="TAKE_OVER ";
			    }
			    if(chk==2){
			        event_type="SCANDAL ";
			    }
			}
			if(range<=-3 && range>=-6){
			    event_type="SCANDAL ";
			}
			if(range<=-1 && range>=-5){
			     event_type="TAKE_OVER ";
			}
			Event event= new Event(event_type,start,end,range);
			event_list.add(event);
		}
	}
	public static void checkPlayers() {
		if(PlayerDAO.getAll().size()<3) {
			while(PlayerDAO.getAll().size()<3) {
				Player player=new Player(getPlayerName());
				PlayerDAO.save(player);
				AI_Player.newAiPlayer(player);
				AI_Player.play();
			}
		}
		if(start==false) {
			start=true;
			StopWatch.RoundTimer();
		}
	}
//	private void getCompanyValues() {
//		
//	}
	private static void generateGeneralTrends() {
		for(int i=0;i<10;i++)
			general_trends[i] = -3 + (int) (Math.random() * ((3 - (-3)) + 1));
		
	}
	
	private static String getPlayerName() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
	
	public static void calRoundStockValues() {
		
		List<CompanyStocks> companies=CompanyStocksDAO.getAll();
		for(CompanyStocks company:companies) {
			double shareValue=0;
			double[] round_values=new double[10];
			for(int i=0;i<10;i++) {
				int round=MarketController.getCurrent_round()-1;
				if(i==0) {
					shareValue=company.getShare_Vlaue();
				}else {
					shareValue=round_values[i-1];
				}
				int sec_trend=0;
				List<Sector> company_sectors=new ArrayList<Sector>();
				for(Sector sector:sector_trends) {
					if(company.getCompany_Sector()==sector.getSector_name()) {
						company_sectors.add(sector);
					}
				}
				if(company_sectors.get(i)!=null) {
					sec_trend=company_sectors.get(i).getSector_Trend();
				}
				int event_trend=0;
				for(Event evnt:event_list) {
					if(i<=evnt.getEnd_turn() && i>=evnt.getStart_turn()) {
						event_trend=evnt.getEvent_value();
					}
				}
				shareValue=shareValue+market_trends[i]+general_trends[i]+sec_trend+event_trend;
				round_values[i]=shareValue;
				if(round==i) {
					company.setShare_Vlaue(round_values[i]);
					CompanyStocksDAO.update(company);
				}
			}
			CompanyTrends company_trend=new CompanyTrends(company.getCompany_Name(), round_values);
			company_trends.add(company_trend);
		}
		
	}
}
