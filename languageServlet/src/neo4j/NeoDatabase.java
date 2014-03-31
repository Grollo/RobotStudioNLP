package neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.rest.graphdb.RestGraphDatabase;

import sceneParser.Item;

public class NeoDatabase implements Database{
	
	GraphDatabaseService db;
	
	public boolean addVerb(String verb, String cmd){
		Node node = db.createNode(NodeType.Verb);
		node.setProperty("word", verb);
		node.setProperty("does", cmd);
		return true;
	}

	public boolean addArgument(String verb, int argument, String reference) {
		IndexHits<Node> words = db.index().forNodes(NodeType.Verb.toString()).get("word", verb);
		if (words.size() != 1) {
			return false;
		}
		words.getSingle().setProperty("A"+argument, reference);
		return false;
	}
	
	public boolean addAdjective(String adjective, String property){
		Node node = db.createNode(NodeType.Adjective);
		node.setProperty("word", adjective);
		node.setProperty("property", property);
		return true;
	}

	public boolean addModel(String alias, String filename) {
		Node node = db.createNode(NodeType.Model);
		node.setProperty("alias", alias);
		node.setProperty("filename", filename);
		return true;
	}

	public boolean addNoun(String noun) {
		Node node = db.createNode(NodeType.Noun);
		node.setProperty(NodeType.Noun.toString(), noun);
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
		items.getSingle().delete();
		return false;
	}

	public boolean modifyItem(Item item, String attribute, String value) {
		//TODO implement
		return false;
	}
	
	public Item[] getItems(String name) {
		Index<Node> index = db.index().forNodes(NodeType.Noun.toString());
		IndexHits<Node> items = index.query("MATCH {word:'"+name+"'}-[:NAME]->(r)");
		//TODO
		return null;
	}

	public String[] getModels(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAdjective(String Adjective) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getVerb(String verb) {
		// TODO Auto-generated method stub
		return null;
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

	private static void registerShutdownHook(final GraphDatabaseService db) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				db.shutdown();
			}
		});
	}
}
