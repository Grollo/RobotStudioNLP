package sceneParser;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.rest.graphdb.RestGraphDatabase;

public class NeoDatabase {

	GraphDatabaseService db;
	
//	/**Adds a new itemType to the database.
//	 * @return <code>true</code> if the itemType was added to the database. Else returns <code>false</code>.*/
//	public boolean addItemType(){
//		//TODO implement
//		return false;
//	}
	
	/**Creates a new item in the database.
	 * @param itemType - the Type of item to be created.
	 * @return <code>true</code> if a item was created. Else returns <code>false</code>.*/
	public boolean createItem(){
		//TODO implement
		return false;
	}
	
	/**Removes an existing item from the database.
	 * @param item - the item to be removed.
	 * @return <code>true</code> if the item was removed. Else returns <code>false</code>.*/
	public boolean removeItem(Item item){
		//TODO implement
		return false;
	}
	
	/**Sets the specified attribute of the specified item to the specified value,
	 * @param item - the item to be modified.
	 * @param attribute - the attribute to be modified.
	 * @param value - the value to be assigned.
	 * @return <code>true</code> if the modification was successful. Else returns <code>false</code>.*/
	public boolean modifyItem(Item item){
		//TODO implement
		return false;
	}
	
	/**Connects to the specified Neo4j-server.
	 * @param urlPath - The URL of the Neo4j-server.
	 * 
	 * @return <code>true</code> if connection was established. Else returns <code>false</code>.*/
	public boolean connect(String urlPath){
		//urlPath = "localhost:7474"
		db = new RestGraphDatabase(urlPath);
		registerShutdownHook(db);
		return true;
	}
	
	
	/**Closes the connection to the Neo4j-database,*/
	public void disconnect(){
		if(db != null){			
			db.shutdown();
			db = null;
		}
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
