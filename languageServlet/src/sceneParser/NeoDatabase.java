package sceneParser;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.rest.graphdb.RestGraphDatabase;

public class NeoDatabase {

	GraphDatabaseService db;
	
	public boolean addItemType(){
		return false;
	}
	
	public boolean createItem(){
		return false;
	}
	
	public boolean removeItem(){
		return false;
	}
	
	public boolean modifyItem(){
		return false;
	}
	
	/**Connects to the specified Neo4j-server.
	 * @param urlPath - The URL of the Neo4j-server.
	 * 
	 * @return <code>true</code> if connection was established.*/
	public boolean connect(String urlPath){
		//urlPath = "localhost:7474"
		db = new RestGraphDatabase(urlPath);
		registerShutdownHook(db);
		return true;
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
