package neo4j;

import java.util.HashMap;
import java.util.Map;

import neo4j.Constant.Adjective;
import neo4j.Constant.ItemProperties;
import neo4j.Constant.ItemRelationships;
import neo4j.Constant.Model;
import neo4j.Constant.ModelRelationships;
import neo4j.Constant.NodeType;
import neo4j.Constant.Noun;
import neo4j.Constant.Verb;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.rest.graphdb.RestGraphDatabase;

import sceneParser.Item;

public class NeoDatabase implements Database{
	
	private GraphDatabaseService db;
	
	private static NeoDatabase database;
	
	private NeoDatabase(){
	}
	
	public static void start(String urlPath){
		database = new NeoDatabase();
		database.connect(urlPath);
	}
	
	public static NeoDatabase getDatabase(){
		return database;
	}
	
	public boolean addVerb(String verb, String action){
		Node node = db.createNode(NodeType.Verb);
		node.setProperty(Verb.WORD.toString(), verb);
		node.setProperty(Verb.COMMAND.toString(), action);
		db.index().forNodes(NodeType.Verb.toString()).add(node, Verb.WORD.toString(), verb);
		return true;
	}

	public boolean addArgument(String verb, String argument, String reference) {
		IndexHits<Node> words = db.index().forNodes(NodeType.Verb.toString()).get(Verb.WORD.toString(), verb);
		if (words.size() != 1) {
			return false;
		}
		words.getSingle().setProperty(argument, reference);
		return true;
	}
	
	public boolean addAdjective(String adjective, String property, String value){
		Node node = db.createNode(NodeType.Adjective);
		node.setProperty(Adjective.WORD.toString(), adjective);
		node.setProperty(Adjective.PROPERTY.toString(), property);
		node.setProperty(Adjective.VALUE.toString(), value);
		db.index().forNodes(NodeType.Adjective.toString()).add(node, Adjective.WORD.toString(), adjective);
		return true;
	}

	public boolean addModel(String alias, String filename) {
		Node node = db.createNode(NodeType.Model);
		node.setProperty(Model.ALIAS.toString(), alias);
		node.setProperty(Model.FILE.toString(), filename);
		db.index().forNodes(NodeType.Model.toString()).add(node, Model.ALIAS.toString(), alias);
		return true;
	}

	public boolean addNoun(String noun) {
		Node node = db.createNode(NodeType.Noun);
		node.setProperty(Noun.WORD.toString(), noun);
		db.index().forNodes(NodeType.Noun.toString()).add(node, Noun.WORD.toString(), noun);
		return true;
	}

	public boolean linkModel(String word, String model) {
		IndexHits<Node> words = db.index().forNodes(NodeType.Noun.toString()).get(Noun.WORD.toString(), word);
		IndexHits<Node> models = db.index().forNodes(NodeType.Model.toString()).get(Model.FILE.toString(), model);
		if (words.size() != 1 || models.size() != 1) {
			return false;
		}
		words.getSingle().createRelationshipTo(models.getSingle(),ModelRelationships.MEANS);
		return true;
	}

	public boolean createItem(Item item) {
		Node node = db.createNode();
		Index<Node> modelIndex = db.index().forNodes(NodeType.Model.toString());
		IndexHits<Node> hits = modelIndex.query(Model.ALIAS.toString(), item.model);
		if (hits.size() != 1) {
			return false;
		}
		node.setProperty(ItemProperties.ID.toString(), item.id);
		node.createRelationshipTo(hits.getSingle(), ItemRelationships.MODEL);
		node.setProperty(ItemProperties.POSITION_X.toString(), item.position.x);
		node.setProperty(ItemProperties.POSITION_Y.toString(), item.position.y);
		node.setProperty(ItemProperties.POSITION_Z.toString(), item.position.z);
		node.setProperty(ItemProperties.ROTATION.toString(), item.rotation);
		node.setProperty(ItemProperties.SCALE.toString(), item.scale);
		node.setProperty(ItemProperties.COLOR.toString(), item.color);
		db.index().forNodes(NodeType.Item.toString()).add(node, ItemProperties.ID.toString(), item.id);
		
		for (String name : item.names) {
			IndexHits<Node> names = db.index().forNodes(NodeType.Noun.toString()).get(Noun.WORD.toString(), name);
			if (names.size() != 1) {
				return false;
			}
			names.getSingle().createRelationshipTo(node, ItemRelationships.NAME);
			names.close();
		}
		return true;
	}

	public boolean removeItem(Item item) {
		IndexHits<Node> items = db.index().forNodes(NodeType.Item.toString()).get(ItemProperties.ID.toString(), item.id);
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
		IndexHits<Node> items = db.index().forNodes(NodeType.Item.toString()).get(ItemProperties.ID.toString(), item.id);
		if (items.size() != 1 || items.size() != 1) {
			return false;
		}
		Node node = items.getSingle();
		if(attribute.equals(ItemProperties.MODEL)){
			db.index().forNodes(NodeType.Item.toString()).remove(node);
			for (Relationship r : node.getRelationships(ItemRelationships.MODEL)) {
				r.delete();
			}
			
			Index<Node> modelIndex = db.index().forNodes(NodeType.Model.toString());
			IndexHits<Node> hits = modelIndex.query(Model.ALIAS.toString(), item.model);
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
		int id = (int) node.getProperty(ItemProperties.ID.toString());
		String model = (String) node.getSingleRelationship(ItemRelationships.MODEL, Direction.OUTGOING)
				.getEndNode().getProperty(Model.ALIAS.toString());
		Item item = new Item(id, model);
		return item;
	}

	public String[] getModels(String name) {
		Index<Node> index = db.index().forNodes(NodeType.Noun.toString());
		IndexHits<Node> items = index.query("MATCH ({word:'"+name+"'}-[:MEANS]->(r)) RETURN r;");
		String[] result = new String[items.size()];
		int i = 0;
		for (Node node : items) {
			result[i++] = (String) node.getProperty(Model.ALIAS.toString());
		}
		return result;
	}

	public String[] getAdjective(String adjective) {
		Node node = db.index().forNodes(NodeType.Adjective.toString()).get(Adjective.WORD.toString(), adjective).getSingle();
		return new String[]{(String) node.getProperty(Adjective.PROPERTY.toString()),
				(String) node.getProperty(Adjective.VALUE.toString())};
	}

	public Map<String, String> getVerb(String verb) {
		IndexManager indexM = db.index();
		Index<Node> index = indexM.forNodes(NodeType.Verb.toString());
		IndexHits<Node> hits = index.get(Verb.WORD.toString(), verb);
		Node node = hits.getSingle();
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

	private static void registerShutdownHook(final GraphDatabaseService db) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				db.shutdown();
			}
		});
	}
}
