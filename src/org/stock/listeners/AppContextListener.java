package org.stock.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.stock.controllers.MarketController;

@WebListener
public class AppContextListener implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("System Exitted.");	
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		MarketController.initMarket();
		System.out.println("System Initialized.");
	}
	
}
