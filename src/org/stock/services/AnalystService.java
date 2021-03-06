package org.stock.services;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.stock.controllers.MarketController;
import org.stock.models.CompanyTrends;
import org.stock.models.Recommendation;

@Path("/analyst")
public class AnalystService {
	@GET
	@Path("stocks")
    @Produces({ MediaType.APPLICATION_JSON })
    public List<Recommendation> getRecommendation() {
        List<CompanyTrends> company_trends=MarketController.getCompanyTrends();
        List<Recommendation> recommendations=new ArrayList<Recommendation>();
        for(CompanyTrends company_trend:company_trends) {
        	int round=MarketController.getCurrent_round()-1;
        	int remaining=10-round;
        	int future_up=remaining/2;
        	double current_value=company_trend.getRound_values()[round];
        	double future_value=company_trend.getRound_values()[(round+future_up)];
        	double ratio=(future_value/current_value)*100;
        	if(ratio>=110) {
        		Recommendation recommendation=new Recommendation(company_trend.getCompany_name(), "BUY");
        		recommendations.add(recommendation);
        	}
        	else if(ratio>=10 && ratio<100){
        		Recommendation recommendation=new Recommendation(company_trend.getCompany_name(), "SELL");
        		recommendations.add(recommendation);
        	}
        }
        return recommendations;
    }
	
}
