package neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import neo4j.Constant.ItemProperties;

import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.entity.RestNode;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

import sceneParser.Item;

public class NeoDatabase implements Database{
	
	private RestCypherQueryEngine engine;
	private RestAPIFacade api;
	
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
	
	public int getIdCount(){
		Map<String, Object> params = new HashMap<String, Object>();
		QueryResult<Map<String, Object>> result = engine.query(
				  "MATCH (i:Item) "
				+ "RETURN i "
				+ "ORDER BY i.id "
				+ "DESC LIMIT 1;", params);
		RestNode node = null;
		for (Map<String, Object> map2 : result) {
			Object o = map2.get("i");
			node = (RestNode) o;
		}
		if(node == null)
			return 0; 
		return 1 + (int) node.getProperty("id");
	}

	public boolean createItem(int id, String model) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$alias", model);
		QueryResult<Map<String, Object>> result = engine.query(
				"MATCH (m:Model)-[:PROTOTYPE]->(p:Prototype)" +
				"WHERE m.alias = {$alias} RETURN p;", params);
		params.clear();
		RestNode node = null;
		for (Map<String, Object> map2 : result) {
			Object o = map2.get("p");
			node = (RestNode) o;
		}
		StringBuilder properties = new StringBuilder();
		int i = 0;
		for (String key : node.getPropertyKeys()) {
			properties.append(", " + key + ": " + "{$prop"+i+"}");
			params.put("$prop"+i , (String) node.getProperty(key));
			i++;
		}
		params.put("$id", id);
		params.put("$alias", model);
		engine.query(
				"MATCH (m:Model) WHERE m.alias = {$alias} " +
				"CREATE (i:Item {id: {$id}" + properties.toString()+ "}) " +
				"CREATE (i)-[:MODEL]->(m);", params);
		engine.query(
				"MATCH (n:Noun), (m:Model), (i:Item) WHERE m.alias = {$alias} " +
				"AND i.id = {$id} AND (n)-[:MEANS]->(m) " +
				"CREATE (n)-[:NAME]->(i);", params);
		return true;
	}
	
	public boolean addName(int itemId, String name){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$id", itemId);
		params.put("$name", name);
		engine.query("MATCH (i:Item) " +
				"WHERE i.id = {$id} CREATE UNIQUE (n:Noun {word:{$name}})-[:NAME]->(i);", params);
		return true;
	}
	
	public boolean removeName(int itemId, String name){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$id", itemId);
		params.put("$name", name);
		engine.query("MATCH (i:Item),(n:Noun) " +
				"WHERE i.id = {$id} AND n.word = {$name} MATCH (n)-[r:NAME]->(i) REMOVE r;", params);
		return true;
	}

	public boolean removeItem(int itemId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$id", itemId);
		engine.query("MATCH (i:Item) WHERE i.id = {$id} " +
				"OPTIONAL MATCH (i)-[r]-() DELETE r,i;", params);
		return true;
	}

	public boolean modifyItem(int itemId, String attribute, String value) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$id", itemId);
		params.put("$value", value);
		if(attribute.equals(ItemProperties.MODEL.toString())){
			engine.query("MATCH (i:Item),(m:Model) WHERE i.id = {$id} " +
					"AND m.alias = {$value} MATCH (i)-[r:MODEL]->()"+ 
					"REMOVE r CREATE (i)-[:MODEL]->(m);", params);
		}else{
			engine.query("MATCH (i:Item) WHERE i.id = {$id} " +
					"SET i."+ attribute +" = {$value};", params);
		}
		return true;
	}
	
	public Item[] getItems(String name) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$name", name);
		QueryResult<Map<String, Object>> result = engine.query("MATCH (n:Noun) WHERE n.word = {$name}" +
				"MATCH (n)-[:NAME]->(i:Item) RETURN i;", params);
		ArrayList<Item> items = new ArrayList<Item>();
		for (Map<String, Object> map2 : result) {
			RestNode node = (RestNode) map2.get("i");
			Item item = new Item((int) node.getProperty("id"));
			items.add(item);
			for (String key : node.getPropertyKeys()) {
				if(!key.equals("id"))
						item.properties.put(key, (String) node.getProperty(key));
			}
			getNames(item);
		}
		return items.toArray(new Item[0]);
	}
	
	public Item getItem(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$id", id);
		QueryResult<Map<String, Object>> result = engine.query("MATCH (i:Item) WHERE i.id = {$id} " +
				"RETURN i;", params);
		Item item = null;
		for (Map<String, Object> map2 : result) {
			RestNode node = (RestNode) map2.get("i");
			item = new Item(id);
			for (String key : node.getPropertyKeys()) {
				if(!key.equals("id"))
						item.properties.put(key, (String) node.getProperty(key));
			}
			getNames(item);
		}
		return item;
	}
	
	private void getNames(Item item) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$id", item.id);
		QueryResult<Map<String, Object>> result = engine.query("MATCH (i:Item) WHERE i.id = {$id}" +
				"MATCH (n)-[:NAME]->(i:Item) RETURN n;", params);
		for (Map<String, Object> map2 : result) {
			RestNode node = (RestNode) map2.get("n");
			item.names.add((String) node.getProperty("word"));
		}
	}

	public String[] getModels(String name) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$name", name);
		QueryResult<Map<String, Object>> result = engine.query("MATCH (n:Noun) WHERE n.word = {$name}" +
				"MATCH (n)-[:MEANS]->(m:Model) RETURN m;", params);
		ArrayList<String> models = new ArrayList<String>();
		for (Map<String, Object> map2 : result) {
			RestNode node = (RestNode) map2.get("m");
			models.add((String) node.getProperty("alias"));
		}
		return models.toArray(new String[0]);
	}

	public Map<String, String> getAdjective(String adjective) {
		Map<String, String> map = new HashMap<String, String>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$word", adjective);
		QueryResult<Map<String, Object>> result = engine.query("match (a:Adjective) where a.word = {$word} return a;", params);
		RestNode node = null;
		for (Map<String, Object> map2 : result) {
			node = (RestNode) map2.get("a");
		}
		if (node != null)  {
			for (String key : node.getPropertyKeys()) {			
				map.put(key, node.getProperty(key).toString());
			}
		}
		return map;
	}

	public Map<String, String> getVerb(String verb) {
		Map<String, String> map = new HashMap<String, String>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$verb", verb);
		QueryResult<Map<String, Object>> result = engine.query("match (v:Verb) where v.word = {$verb} return v;", params);
		RestNode node = null;
		for (Map<String, Object> map2 : result) {
			Object o = map2.get("v");
			node = (RestNode) o;
		}
		for (String key : node.getPropertyKeys()) {			
			map.put(key, node.getProperty(key).toString());
		}
		return map;
	}
	
	public String getProperty(String property){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$property", property);
		QueryResult<Map<String, Object>> result = engine.query(
				"MATCH (p:Property) WHERE p.word = {$property} RETURN p;", params);
		RestNode node = null;
		for (Map<String, Object> map2 : result) {
			Object o = map2.get("p");
			node = (RestNode) o;
		}
		if(node == null){
			return null;
		}
		return (String) node.getProperty("property");
	}

	public boolean connect(String urlPath) {
		// urlPath = "localhost:7474"
		api = new RestAPIFacade(urlPath);
		engine = new RestCypherQueryEngine(api);
		registerShutdownHook(api);
		return true;
	}

	public void disconnect() {
		if (api != null) {
			api.close();
			api = null;
			engine = null;
		}
	}

	private static void registerShutdownHook(final RestAPIFacade api2) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				database.disconnect();
			}
		});
	}
}
