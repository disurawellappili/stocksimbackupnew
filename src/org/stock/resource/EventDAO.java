package org.stock.resource;

import java.util.ArrayList;

import java.util.List;

import org.stock.models.Event;

public class EventDAO
{
	private static List<Event> event_list=new ArrayList<Event>();
	public static List<Event> getAll(){
		return event_list;
	}
	public static void save(Event event) {
		event_list.add(event);
	}
}
