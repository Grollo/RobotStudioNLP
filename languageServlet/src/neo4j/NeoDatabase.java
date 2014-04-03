package neo4j;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.rest.graphdb.RestGraphDatabase;

import sceneParser.Item;

public class NeoDatabase implements Database{
	
	public enum ItemRelationships implements RelationshipType {
		MODEL, NAME
	}
	
	public enum ModelRelationships implements RelationshipType {
		MEANS
	}
	
	public enum NodeType implements Label{
		Model, Item, Noun, Verb, Adjective
	}
	
	GraphDatabaseService db;
	
	public boolean addVerb(String verb, String cmd){
		Node node = db.createNode(NodeType.Verb);
		node.setProperty("word", verb);
		node.setProperty("does", cmd);
		db.index().forNodes(NodeType.Verb.toString()).add(node, "word", verb);
		return true;
	}

	public boolean addArgument(String verb, String argument, String reference) {
		IndexHits<Node> words = db.index().forNodes(NodeType.Verb.toString()).get("word", verb);
		if (words.size() != 1) {
			return false;
		}
		words.getSingle().setProperty(argument, reference);
		return true;
	}
	
	public boolean addAdjective(String adjective, String property, String value){
		Node node = db.createNode(NodeType.Adjective);
		node.setProperty("word", adjective);
		node.setProperty("property", property);
		node.setProperty("value", value);
		db.index().forNodes(NodeType.Adjective.toString()).add(node, "word", adjective);
		return true;
	}

	public boolean addModel(String alias, String filename) {
		Node node = db.createNode(NodeType.Model);
		node.setProperty("alias", alias);
		node.setProperty("filename", filename);
		db.index().forNodes(NodeType.Model.toString()).add(node, "alias", alias);
		return true;
	}

	public boolean addNoun(String noun) {
		Node node = db.createNode(NodeType.Noun);
		node.setProperty("word", noun);
		db.index().forNodes(NodeType.Noun.toString()).add(node, "word", noun);
		return true;
	}

	public boolean linkModel(String word, String model) {
		IndexHits<Node> words = db.index().forNodes(NodeType.Noun.toString()).get("model", word);
		IndexHits<Node> models = db.index().forNodes(NodeType.Model.toString()).get("alias", model);
		if (words.size() != 1 || models.size() != 1) {
			return false;
		}
		words.getSingle().createRelationshipTo(models.getSingle(),ModelRelationships.MEANS);
		return true;
	}

	public boolean createItem(Item item) {
		Node node = db.createNode();
		Index<Node> modelIndex = db.index().forNodes(NodeType.Model.toString());
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
		db.index().forNodes(NodeType.Item.toString()).add(node, "id", item.id);
		
		for (String name : item.names) {
			IndexHits<Node> names = db.index().forNodes(NodeType.Noun.toString()).get(NodeType.Noun.toString(), name);
			if (names.size() != 1) {
				return false;
			}
			names.getSingle().createRelationshipTo(node, ItemRelationships.NAME);
			names.close();
		}
		return true;
	}

	public boolean removeItem(Item item) {
		IndexHits<Node> items = db.index().forNodes(NodeType.Item.toString()).get("id", item.id);
		if (items.size() != 1 || items.size() != 1) {
			return false;
		}
		Node node = items.getSingle();
		db.index().forNodes(NodeType.Item.toString()).remove(node);
		for (Relationship r : node.getRelationships()) {
			r.delete();
		}
		node.delete();
		return true;
	}

	public boolean modifyItem(Item item, String attribute, String value) {
		IndexHits<Node> items = db.index().forNodes(NodeType.Item.toString()).get("id", item.id);
		if (items.size() != 1 || items.size() != 1) {
			return false;
		}
		Node node = items.getSingle();
		if(attribute.equals(Item.attributeNames)){
			db.index().forNodes(NodeType.Item.toString()).remove(node);
			for (Relationship r : node.getRelationships(ItemRelationships.MODEL)) {
				r.delete();
			}
			
			Index<Node> modelIndex = db.index().forNodes(NodeType.Model.toString());
			IndexHits<Node> hits = modelIndex.query("alias", item.model);
			if (hits.size() != 1) {
				return false;
			}
			node.createRelationshipTo(hits.getSingle(), ItemRelationships.MODEL);
		}else{
			node.setProperty(attribute, value);
		}
		return true;
	}
	
	public Item[] getItems(String name) {
		Index<Node> index = db.index().forNodes(NodeType.Noun.toString());
		IndexHits<Node> items = index.query("MATCH ({word:'"+name+"'}-[:NAME]->(r)) RETURN r;");
		Item[] result = new Item[items.size()];
		int i = 0;
		for (Node node : items) {
			result[i++] = getItem(node);
		}
		return result;
	}
	
	private Item getItem(Node node){
		int id = (int) node.getProperty("id");
		String model = (String) node.getSingleRelationship(ItemRelationships.MODEL, Direction.OUTGOING)
				.getEndNode().getProperty("alias");
		Item item = new Item(id, model);
		return item;
	}

	public String[] getModels(String name) {
		Index<Node> index = db.index().forNodes(NodeType.Noun.toString());
		IndexHits<Node> items = index.query("MATCH ({word:'"+name+"'}-[:MEANS]->(r)) RETURN r;");
		String[] result = new String[items.size()];
		int i = 0;
		for (Node node : items) {
			result[i++] = (String) node.getProperty("alias");
		}
		return result;
	}

	public String[] getAdjective(String adjective) {
		Node node = db.index().forNodes(NodeType.Adjective.toString()).get("word", adjective).getSingle();
		return new String[]{(String) node.getProperty("property"), (String) node.getProperty("value")};
	}

	public Map<String, String> getVerb(String verb) {
		Node node = db.index().forNodes(NodeType.Verb.toString()).get("word", verb).getSingle();
		Map<String, String> map = new HashMap<String, String>();
		for (String key : node.getPropertyKeys()) {
			map.put(key, (String) node.getProperty(key));
		}
		return map;
	}

	public boolean connect(String urlPath) {
		// urlPath = "localhost:7474"
		db = new RestGraphDatabase(urlPath);
		registerShutdownHook(db);
		return true;
	}

	public void disconnect() {
		if (db != null) {
			db.shutdown();
			db = null;
		}
	}
	
	/*Run to add indexes to database. Only needed when initializing database.*/
	private void init(){
		db.schema().indexFor(NodeType.Adjective).create();
		db.schema().indexFor(NodeType.Verb).create();
		db.schema().indexFor(NodeType.Noun).create();
		db.schema().indexFor(NodeType.Model).create();
		db.schema().indexFor(NodeType.Item).create();
	}

	private static void registerShutdownHook(final GraphDatabaseService db) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				db.shutdown();
			}
		});
	}
}
