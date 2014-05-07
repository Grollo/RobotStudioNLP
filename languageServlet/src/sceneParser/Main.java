package sceneParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import neo4j.NeoDatabase;
import se.lth.cs.semparser.corpus.Predicate;
import se.lth.cs.semparser.corpus.Sentence;
import se.lth.cs.semparser.corpus.Word;

public class Main {
	private final static Command grammarError = Command.notify("I don't understand your grammar, can you please rephrase that?");
	private final static Command noItemFound = Command.notify("Could not find an item matching the description.");
	private final static Command tooManyItemsFound = Command.notify("Could apply to more than one item, please specify.");
	private final static Command noModelFound = Command.notify("Could not find an model matching the description.");
	private final static Command tooManyModels = Command.notify("Could apply to more than one model, please specify.");
	private final static String noSuchObject = "Couldn't find an object named ";
	private int activeAgentId = -1; // id of last referred to object, -1 if no such thing
	private static NeoDatabase database = NeoDatabase.getDatabase();
	private static int nextId = 0;

	public static ArrayList<Command> interpret(Sentence parsedSentence) {
		ArrayList<Command> commands = new ArrayList<Command>();
		if (incorrectGrammar(parsedSentence)) {
			commands.add(grammarError);
		} else {
			Predicate rootPredicate = getRootPredicate(parsedSentence);
			if(rootPredicate == null){
				commands.add(grammarError);
			} else {
				Map<String, String> verb = database.getVerb(rootPredicate.getLemma());
				String commandType = verb.get("does");
				Command command = null;
				switch(commandType){
				case "create":	command = makeCreate(verb, rootPredicate);
								break;
				case "remove":	command = makeRemove(verb, rootPredicate);
								break;
				case "modify":	command = makeModify(verb, rootPredicate);
								break;
				}
				commands.add(command);
			}
		}
		return commands;
	}

	private static Command makeCreate(Map<String, String> verb, Predicate rootPredicate) {
		Word itemDescription = getArgumentHead(rootPredicate, "A1");
		String[] possibleModels = appropriateModels(itemDescription);
		if(possibleModels.length == 0)
			return noModelFound;
		if(possibleModels.length > 1)
			return tooManyModels;
		int id = getNextId();
		return Command.create(id, possibleModels[0]);
	}

	private static int getNextId() {
		return nextId++;
	}

	private static Command makeRemove(Map<String, String> verb, Predicate rootPredicate) {
		Word itemDescription = getArgumentHead(rootPredicate, "A1");
		Item[] id = identify(itemDescription);
		if(id.length == 0)
			return noItemFound;
		if(id.length > 1)
			return tooManyItemsFound;
		return Command.remove(id[0].id);
	}

	private static Command makeModify(Map<String, String> verb, Predicate rootPredicate) {
		Item item = null;
		Map<String, String> adjective = null;
		String property = null;
		String function = null;
		String value = null;
		Word object = getObject(rootPredicate);
		Boolean creater = shouldCreate(object); 
		if(creater == null){
			return Command.notify("Grammatical error.");
		} else if(creater) {	
			//TODO Create new object
		} else {			
			Item[] items = identify(object);
			if(items.length == 0)
				return noItemFound;
			if(items.length > 1)
				return tooManyItemsFound;
			item = items[0];
		}
		
		property = verb.get("property");
		if(property == null){
			Word adj = getArgumentHead(rootPredicate, "A2");
			adjective = database.getAdjective(adj.getLemma());
			property = adjective.get("property");
			function = adjective.get("function");
			value = adjective.get("value");
		}
		if(value == null){
			if(function == null)
				function = verb.get("function");
			Word adj = getArgumentHead(rootPredicate, "A2");
			adjective = database.getAdjective(adj.getLemma());
			value = adjective.get("value");
		}
		String objectValue = item.get(property);
		if(objectValue == null)
			return Command.notify("Illegal Property on Object.");
		value = applyFunction(function, value, objectValue);

		if(property == null || value == null)
			return Command.notify("Grammatical error.");
		return Command.modify(item.id, property, value);
	}
	
	private static String applyFunction(String function, String value, String sourceValue) throws NumberFormatException {
		if(function == null)
			return value;
		Calculable calc = null;
		try {
			calc = new ExpressionBuilder(function)
					.withVariable("x", Double.valueOf(value))	
					.withVariable("y", Double.valueOf(sourceValue)).build();
		} catch (UnknownFunctionException | UnparsableExpressionException e) {
			e.printStackTrace();
		}
		return Double.toString(calc.calculate());
	}
	
	
	private static Word getObject(Predicate rootPredicate) {
		//Needs to be improved later
		return getArgumentHead(rootPredicate, "A1");
	}
	
	private static Word getArgumentHead(Predicate rootPredicate, String itemToMakeArgument) {
		Set<Word> words = rootPredicate.getArgMap().keySet();
		for(Word word : words){
			if(rootPredicate.getArgMap().get(word).equals(itemToMakeArgument)){
				return word;
			}
		}
		return null;
	}

	private static Predicate getRootPredicate(Sentence s){
		if(s.getPredicates().isEmpty())
			return null;
		Word w = s.get(0);
		while(!w.isBOS())
			w = w.getHead();
		ArrayList<Word> bfs = new ArrayList<Word>();
		bfs.add(w);
		while(!bfs.isEmpty()){
			w = bfs.get(0);
			bfs.remove(0);
			if(w.isPred())
				return (Predicate) w;
			for(Word word : w.getChildren()){
				bfs.add(word);
			}
		}
		return null;
	}

	private static boolean incorrectGrammar(Sentence parsedSentence) {
		return false; // TODO
	}

	/**
	 * Given a description of an item, finds models in the database that fits the description.
	 * if no fitting model is found, returns null.
	 * @param itemDescription
	 * @return 
	 */
	private static String[] appropriateModels(Word itemDescription) { //implementing a simple version that only checks one word, expand later
		String name = itemDescription.getLemma();
		String[] models = database.getModels(name);
		for(String subnoun : getSubtags(itemDescription, "NN")){
			models = intersection(models, database.getModels(subnoun), new String[0]);
		}
		
		return models;
	}
	
	/**@return All words with matching <code>tag</code> from one level beneath <code>item</code>.*/
	private static List<String> getSubtags(Word item, String tag) {
		ArrayList<String> words = new ArrayList<>();
		for(Word child : item.getChildren()) {
			if(child.getPOS().equals(tag)) {
				words.add(child.getLemma());
			}
		}
		return words;
	}
	
	private static Boolean shouldCreate(Word item){
		List<String> dts = getSubtags(item, "DT");
		if(dts.size() > 1)
			return null;
		switch(dts.get(0)){
			case "a":
			case "an":
				return true;
			case "the":
			default:
				return false;
		}
	}
	
	private static Item[] identify(Word item) {
		Item[] items = possibleItems(item.getLemma());
		Map<String, String> adjectives = extractAdjectives(item);
		List<String> names = extractNames(item);
		for (String name : names) {
			filterByName(items, name);
		}
		for (Entry<String, String> entry : adjectives.entrySet()) {
			filterByProperty(items, entry.getKey(), entry.getValue());
		}
		return items;
	}

	/**
	 * @param noun
	 * @return list of ids of items the noun could be referring to
	 */
	private static Item[] possibleItems(String noun) {
		Item[] items = database.getItems(noun);
		return items;
	}

	/**
	 * @param ids
	 * @param attribute
	 * @param value
	 * @return sublist of items that fit the property
	 */
	private static Item[] filterByProperty(Item[] items, String property, String value) {
		List<Item> filtered = new ArrayList<Item>();
		for (Item item : filtered) {
			if(item.get(property).equals(value)){
				filtered.add(item);
			}
		}
		return filtered.toArray(new Item[0]);
	}
	
	private static Item[] filterByName(Item[] items, String name) {
		List<Item> filtered = new ArrayList<Item>();
		for (Item item : filtered) {
			if(item.names.contains(name)){
				filtered.add(item);
			}
		}
		return filtered.toArray(new Item[0]);
	}
	
	private static Map<String, String> extractAdjectives(Word item){
		Map<String, String> values = new HashMap<String, String>();
		getAllAdjectives(values, item);
		return values;
	}
	
	private static List<String> extractNames(Word item){
		List<String> values = new ArrayList<String>();
		getAllNames(values, item);
		return values;
	}
	
	/**@return All words with matching <code>tag</code> from all levels beneath <code>item</code>.*/
	private static void getAllNames(List<String> values, Word word) {
		if(word.getPOS().equals("NNP")) {
			values.add(word.getLemma());
		}
		for(Word child : word.getChildren()) {
			getAllNames(values, child);
		}
	}

	/**@return All words with matching <code>tag</code> from all levels beneath <code>item</code>.*/
	private static void getAllAdjectives(Map<String, String> values, Word word) {
		if(word.getPOS().equals("JJ")) {
			Map<String, String> map = database.getAdjective(word.getLemma());
			values.put(map.get("property"), map.get("value"));
		}
		for(Word child : word.getChildren()) {
			getAllAdjectives(values, child);
		}
	}
	
	/**@param t - Should be a empty Array of the same type as <code>a</code> and <code>b</code>.*/
	private static<T extends Comparable<T>> T[] intersection(T[] a, T[] b, T[] t){
		List<T> result = new ArrayList<T>();
		Arrays.sort(a);
		Arrays.sort(b);
		
		int i = 0;
		int j = 0;
		while(i < a.length && j < b.length){
			int c = a[i].compareTo(b[j]);
			if(c < 0){
				i++;
			}else if(c > 0){
				j++;
			}else if(a[i].equals(b[j])) {
				result.add(a[i]);
				i++;
				j++;
			}else{
				i++;
				j++;
			}
		}
		
		return result.toArray(t);
	}

}
