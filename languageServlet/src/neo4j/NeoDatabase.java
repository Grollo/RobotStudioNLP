package neo4j;

import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.rest.graphdb.RestGraphDatabase;

import sceneParser.Item;
import sceneParser.Item.Position;

public class NeoDatabase {
	
	GraphDatabaseService db;

	/** Adds a new model to the database.
	 * 
	 * @return <code>true</code> if the model was added to the database. Else returns <code>false</code>.
	 */
	public boolean addModel(String alias, String filename) {
		Node node = db.createNode(NodeType.Model);
		node.setProperty("alias", alias);
		node.setProperty("filename", filename);
		return true;
	}

	/** Adds a new word to the database.
	 * 
	 * @return <code>true</code> if the word was added to the database. Else returns <code>false</code>.
	 */
	public boolean addWord(String word) {
		Node node = db.createNode(NodeType.Word);
		node.setProperty("word", word);
		return true;
	}

	/** Adds a relationship between a word and a model.
	 * 
	 * @param word - the word to be linked.
	 * @param model - the alias of the model to be linked.
	 * @return <code>false</code> if the word or the model isn't uniquely identifiable.
	 * 		Else returns <code>true</code>.
	 */
	public boolean linkModel(String word, String model) {
		IndexHits<Node> words = db.index().forNodes(NodeType.Word.toString()).get("word", word);
		IndexHits<Node> models = db.index().forNodes(NodeType.Model.toString()).get("alias", model);
		if (words.size() != 1 || models.size() != 1) {
			return false;
		}
		words.getSingle().createRelationshipTo(models.getSingle(),ModelRelationships.MEANS);
		return true;
	}

	/** Creates a new item in the database.
	 * 
	 * @param itemType - the Type of item to be created.
	 * @return <code>true</code> if a item was created. Else returns <code>false</code>.
	 */
	public boolean createItem(Item item) {
		Node node = db.createNode();
		Index<Node> modelIndex = db.index().forNodes("model");
		IndexHits<Node> hits = modelIndex.query("alias", item.model);
		if (hits.size() != 1) {
			return false;
		}
		node.setProperty("id", item.id);
		node.createRelationshipTo(hits.getSingle(), ItemRelationships.MODEL);
		node.setProperty("position x", item.position.x);
		node.setProperty("position y", item.position.y);
		node.setProperty("position z", item.position.z);
		node.setProperty("rotation", item.rotation);
		node.setProperty("scale", item.scale);
		node.setProperty("color", item.color);
		
		for (String name : item.names) {
			IndexHits<Node> names = db.index().forNodes(NodeType.Word.toString()).get("word", name);
			if (names.size() != 1) {
				return false;
			}
			names.getSingle().createRelationshipTo(node, ItemRelationships.NAME);
		}
		return true;
	}

	/** Removes an existing item from the database.
	 * 
	 * @param item - the item to be removed.
	 * @return <code>true</code> if the item was removed. Else returns <code>false</code>.
	 */
	public boolean removeItem(Item item) {
		IndexHits<Node> items = db.index().forNodes(NodeType.Item.toString()).get("id", item.id);
		if (items.size() != 1 || items.size() != 1) {
			return false;
		}
		items.getSingle().delete();
		return false;
	}

	/** Sets the specified attribute of the specified item to the specified
	 *  value,
	 * 
	 * @param item - the item to be modified.
	 * @param attribute - the attribute to be modified.
	 * @param value - the value to be assigned.
	 * @return <code>true</code> if the modification was successful. Else returns <code>false</code>.
	 */
	public boolean modifyItem(Item item) {
		
		//TODO implement
		
		return false;
	}

	/** Connects to the specified Neo4j-server.
	 * 
	 * @param urlPath - The URL of the Neo4j-server.
	 * 
	 * @return <code>true</code> if connection was established. Else returns <code>false</code>.
	 */
	public boolean connect(String urlPath) {
		// urlPath = "localhost:7474"
		db = new RestGraphDatabase(urlPath);
		registerShutdownHook(db);
		return true;
	}

	/** Closes the connection to the Neo4j-database, */
	public void disconnect() {
		if (db != null) {
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
