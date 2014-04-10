package neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import neo4j.Constant.ItemProperties;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.RestGraphDatabase;
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
	
	public boolean addVerb(String verb, String action){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$verb", verb);
		params.put("$action", action);
		engine.query(
				"CREATE (v:Verb {word:{$verb}, does:{$action}});", params);
		return true;
	}

	public boolean addArgument(String verb, String argument, String reference) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$verb", verb);
		params.put("$arg", argument);
		params.put("$value", reference);
		QueryResult<Map<String, Object>> result = engine.query(
				"MATCH (v:Verb) WHERE v.word = {$verb} SET v.{$arg} = {$value};", params);
		return true;
	}
	
	public boolean addAdjective(String adjective, String property, String value){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$adj", adjective);
		params.put("$property", property);
		params.put("$value", value);
		QueryResult<Map<String, Object>> result = engine.query(
				"CREATE (a:Adjective {word:{$adj}, property:{$property}, value:{$value}});", params);
		return true;
	}

	public boolean addModel(String alias, String filename) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$alias", alias);
		params.put("$filename", filename);
		QueryResult<Map<String, Object>> result = engine.query(
				"CREATE (m:Model {alias:{$alias}, filename:{$filename}});", params);
		return true;
	}

	public boolean addNoun(String noun) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$word", noun);
		QueryResult<Map<String, Object>> result = engine.query(
				"CREATE (n:Noun {word:{$word}});", params);
		return true;
	}

	public boolean linkModel(String word, String model) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$word", word);
		params.put("$alias", model);
		QueryResult<Map<String, Object>> result = engine.query(
				"MATCH (n:Noun), (m:Model) WHERE n.word = {$word} AND m.alias = {$alias} " +
				"CREATE (n)-[:MEANS]->(m);", params);
		return true;
	}

	public boolean createItem(Item item) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$id", item.id);
		params.put("$model", item.model);
		params.put("$color", item.color);
		params.put("$pos x", item.position.x);
		params.put("$pos y", item.position.y);
		params.put("$pos z", item.position.z);
		params.put("$rot x", item.rotation.x);
		params.put("$rot y", item.rotation.y);
		params.put("$rot z", item.rotation.z);
		params.put("$scale", item.scale);
		QueryResult<Map<String, Object>> result = engine.query("Create (i:Item {id:{$id}, color:{$color}," +
				"position_x:{$pos x}, position_y:{$pos y}, position_z:{$pos z}, scale:{$scale}," + 
				"rotation_x:{$rot x}, rotation_y:{$rot y}, rotation_z:{$rot z}})" +
				"MATCH (M:Model) WHERE m.alias = {$model} CREATE (i)-[:MODEL]->(m);", params);
		return true;
	}
	
	public boolean addName(int itemId, String name){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$id", itemId);
		params.put("$name", name);
		QueryResult<Map<String, Object>> result = engine.query("MATCH (i:Item),(n:Noun) " +
				"WHERE i.id = {$id} AND n.word = {$name} CREATE (n)-[:NAME]->(i);", params);
		return true;
	}
	
	public boolean removeName(int itemId, String name){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$id", itemId);
		params.put("$name", name);
		QueryResult<Map<String, Object>> result = engine.query("MATCH (i:Item),(n:Noun) " +
				"WHERE i.id = {$id} AND n.word = {$name} MATCH (n)-[r:NAME]->(i) REMOVE r;", params);
		return true;
	}

	public boolean removeItem(Item item) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$id", item.id);
		QueryResult<Map<String, Object>> result = engine.query("MATCH (i:Item) WHERE i.id = {$id} " +
				"OPTIONAL MATCH (n)-[r]-() REMOVE r,i;", params);
		return true;
	}

	public boolean modifyItem(Item item, String attribute, String value) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$id", item.id);
		params.put("$attr", attribute);
		params.put("$value", value);
		if(attribute.equals(ItemProperties.MODEL.toString())){
			QueryResult<Map<String, Object>> result = engine.query("MATCH (i:Item),(m:Model) WHERE i.id = {$id} " +
					"AND m.alias = {$value} MATCH (i)-[r:MODEL]->()"+ 
					"REMOVE r CREATE (i)-[:MODEL]-(m);", params);
		}else{
			QueryResult<Map<String, Object>> result = engine.query("MATCH (i:Item) WHERE i.id = {$id} " +
					"SET i.{$attr} = {$value};", params);
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
			items.add(new Item((int) node.getProperty("id")));
		}
		return items.toArray(new Item[0]);
	}

	public String[] getModels(String name) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$name", name);
		QueryResult<Map<String, Object>> result = engine.query("MATCH (n:Noun) WHERE n.word = {$name}" +
				"MATCH (n)-[:MEANS]->(m:Model) RETURN m;", params);
		ArrayList<String> models = new ArrayList<String>();
		for (Map<String, Object> map2 : result) {
			RestNode node = (RestNode) map2.get("m");
			models.add((String) node.getProperty("filename"));
		}
		return models.toArray(new String[0]);
	}

	public String[] getAdjective(String adjective) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("$word", adjective);
		QueryResult<Map<String, Object>> result = engine.query("match (a:Adjective) where a.word = {$word} return a;", params);
		RestNode node = null;
		for (Map<String, Object> map2 : result) {
			node = (RestNode) map2.get("v");
		}
		String property = (String) node.getProperty("property");
		String value = (String) node.getProperty("value");
		return new String[]{property, value};
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
