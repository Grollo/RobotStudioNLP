package se.lth.cs.main;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import neo4j.NeoDatabase;

public class ServerMaker implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		NeoDatabase.start("http://localhost:7474");
		System.out.println("Started server correctly! :)");
	}

}