package neo4j;

import java.util.Map;

import org.neo4j.graphdb.RelationshipType;

import sceneParser.Item;

public interface Database {

	public boolean addVerb(String verb, String cmd);
	
	public boolean addArgument(String verb, String argument, String reference);
	
	public boolean addAdjective(String adjective, String property, String value);
	
	/** Adds a new model to the database.
	 * 
	 * @return <code>true</code> if the model was added to the database. Else returns <code>false</code>.
	 */
	public boolean addModel(String alias, String filename);

	/** Adds a new noun to the database.
	 * 
	 * @return <code>true</code> if the noun was added to the database. Else returns <code>false</code>.
	 */
	public boolean addNoun(String noun);
	
	/** Adds a relationship between a word and a model.
	 * 
	 * @param word - the word to be linked.
	 * @param model - the alias of the model to be linked.
	 * @return <code>false</code> if the word or the model isn't uniquely identifiable.
	 * 		Else returns <code>true</code>.
	 */
	public boolean linkModel(String word, String model);
	
	/** Creates a new item in the database.
	 * 
	 * @param itemType - the Type of item to be created.
	 * @return <code>true</code> if a item was created. Else returns <code>false</code>.
	 */
	public boolean createItem(Item item);
	
	/** Removes an existing item from the database.
	 * 
	 * @param item - the item to be removed.
	 * @return <code>true</code> if the item was removed. Else returns <code>false</code>.
	 */
	public boolean removeItem(Item item);
	
	/** Sets the specified attribute of the specified item to the specified
	 *  value,
	 * 
	 * @param item - the item to be modified.
	 * @param attribute - the attribute to be modified.
	 * @param value - the value to be assigned.
	 * @return <code>true</code> if the modification was successful. Else returns <code>false</code>.
	 */
	public boolean modifyItem(Item item, String attribute, String value);
	
	public Item[] getItems(String name);
	
	public String[] getModels(String name);
	
	public String[] getAdjective(String Adjective);
	
	public Map<String, String> getVerb(String verb);
	
	/** Connects to the specified Neo4j-server.
	 * 
	 * @param urlPath - The URL of the Neo4j-server.
	 * 
	 * @return <code>true</code> if connection was established. Else returns <code>false</code>.
	 */
	public boolean connect(String urlPath);
	
	/** Closes the connection to the Neo4j-database, */
	public void disconnect();
}
