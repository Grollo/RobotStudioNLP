package sceneParser;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.rest.graphdb.RestGraphDatabase;

public class NeoDatabase {

	GraphDatabaseService db;
	
	public boolean connect(String Path){
		//Path = "localhost:7474"
		db = new RestGraphDatabase(Path);
		registerShutdownHook(db);
		
		return false;
	}
	
	public void disconnect(){
		db.shutdown();
	}
	
	private static void registerShutdownHook(final GraphDatabaseService db) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				db.shutdown();
			}
		});
	}
}
