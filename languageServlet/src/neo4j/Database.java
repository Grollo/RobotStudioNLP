package neo4j;

import java.util.List;
import java.util.Map;

import sceneParser.Item;

public interface Database {

	/** Adds a verb to the database.
	 * 
	 * @param verb - the verb to be added.
	 * @param action - the action connected to this verb.
	 * @return <code>true</code> if the verb was added to the database. Else returns <code>false</code>.
	 */
	public boolean addVerb(String verb, String action);
	
	/**Adds a argument to a verb.
	 * 
	 * @param verb - the verb itself.
	 * @param argument
	 * @param reference - reference to be mapped to argument.
	 * @return <code>true</code> if the database change was successful. Else returns <code>false</code>.
	 */
	public boolean addArgument(String verb, String argument, String reference);
	
	/** Adds a adjective to the database.
	 * 
	 * @param adjective - the adjective to be added.
	 * @param property - the property of this adjective.
	 * @param value - the value of this adjective.
	 * @return <code>true</code> if the adjective was added to the database. Else returns <code>false</code>.
	 */
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
	 * @param id - the id of the item.
	 * @param model - the Type of item to be created.
	 * @return <code>true</code> if a item was created. Else returns <code>false</code>.
	 */
	public boolean createItem(int id, String model);
	
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
	
	/** Gets items from the database.
	 * 
	 * @param name - a reference to a item.
	 * @return a Array of all items the name referred to.
	 */
	public Item[] getItems(String name);
	
	/** Gets models from the database.
	 * 
	 * @param name - a reference to a model.
	 * @return a Array of all models the name referred to.
	 */
	public String[] getModels(String name);
	
	/** Gets a Adjective from the database.
	 * 
	 * @param adjective - the adjective itself.
	 * @return A map where is the property is mapped the value. If no adjective
	 * 		can be found, a empty map will be returned.
	 */
	public Map<String, String> getAdjective(String adjective);
	
	/**Gets a verb from the database.
	 * @param verb - the verb itself.
	 * @return a map of all arguments mapped to its corresponding reference.*/
	public Map<String, String> getVerb(String verb);
	
	/** Connects to the specified Neo4j-server.
	 * 
	 * @param urlPath - The URL of the Neo4j-server.
	 * @return <code>true</code> if connection was established. Else returns <code>false</code>.
	 * /
	public boolean connect(String urlPath);
	
	/** Closes the connection to the Neo4j-database, */
	public void disconnect();
}
