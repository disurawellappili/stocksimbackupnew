package org.stock.resource;

import java.util.ArrayList;
import java.util.List;
import org.stock.models.Sector;

public class SectorDAO {
	private static List<Sector> sector_list=new ArrayList<Sector>();
	
	public static List<Sector> getAll(){
		return sector_list;
	}
	
	public static List<Sector> update(Sector sector){
		return sector_list;
	}
	public static void init() {
		Sector sector=new Sector("Finance");
		sector_list.add(sector);
		sector=new Sector("Technology");
		sector_list.add(sector);
		sector=new Sector("Consumer Service");
		sector_list.add(sector);
		sector=new Sector("Manufacturing");
		sector_list.add(sector);
	}
}
