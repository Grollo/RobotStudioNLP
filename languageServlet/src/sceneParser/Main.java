package sceneParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	private final static Command illegalProperty = Command.notify("Illegal Property on Object.");
	private final static String noSuchObject = "Couldn't find an object named ";
	private static int activeAgentId = -1; // id of last referred to object, -1 if no such thing
	private static NeoDatabase database = NeoDatabase.getDatabase();
	private static final UnitConverter unitConverter = new UnitConverter();
	private static final NumberParser numberParser = new NumberParser();
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
				switch(commandType){
				case "create":	commands.addAll(makeCreate(verb, rootPredicate));
								break;
				case "remove":	commands.addAll(makeRemove(verb, rootPredicate));
								break;
				case "modify":	commands.addAll(makeModify(verb, rootPredicate));
								break;
				}
			}
		}
		boolean shouldExecute = true;
		for (Command command : commands) {
			if(command.isNotify()){
				shouldExecute = false;
				break;
			}
		}
		if(shouldExecute) for (Command command : commands) {
			command.execute(database);
		}
		return commands;
	}

	private static ArrayList<Command> makeCreate(Map<String, String> verb, Predicate rootPredicate) {
		ArrayList<Command> commands = new ArrayList<Command>();
		Word itemDescription = getArgumentHead(rootPredicate, "A1");
		String[] possibleModels = appropriateModels(itemDescription);
		if(possibleModels.length == 0){
			commands.add(noModelFound);
			return commands;
		}
		if(possibleModels.length > 1){
			commands.add(tooManyModels);
			return commands;
		}
		int times = 1;
		if(hasSubtag(itemDescription, "CD")){
			ArrayList<Word> list = new ArrayList<>();
			getAllSubtags(list, itemDescription, "CD");
			Collections.sort(list);
			String[] nbrs = new String[list.size()];
			int i = 0;
			for (Word word: list) {
				nbrs[i] = word.getLemma();
				i++;
			}
			times = (int) numberParser.parse(nbrs);
		}
		int id = -1;
		for (int i = 0; i < times; i++) {
			id = getNextId();			
			commands.add(Command.create(id, possibleModels[0]));
		}
		if(times > 1)
			activeAgentId = id;
		return commands;
	}

	private static int getNextId() {
		return nextId++;
	}

	private static ArrayList<Command> makeRemove(Map<String, String> verb, Predicate rootPredicate) {
		ArrayList<Command> commands = new ArrayList<Command>();
		Word itemDescription = getArgumentHead(rootPredicate, "A1");
		int det = analyzeDeterminer(itemDescription);
		Item[] items = identify(itemDescription);
		if(det == APPLY_ALL){
			for (Item item : items) {
				commands.add(Command.remove(item));
			}
		}else {			
			if(items.length == 0){
				commands.add(noItemFound);
				return commands;
			}
			if(items.length > 1){
				commands.add(tooManyItemsFound);
				return commands;
			}
			commands.add(Command.remove(items[0]));
		}
		activeAgentId = -1;
		return commands;
	}

	private static final String[] POSITIONS = new String[]{"position_x", "position_y", "position_z"};
	
	private static ArrayList<Command> makeModify(Map<String, String> verb, Predicate rootPredicate) {
		ArrayList<Command> commands = new ArrayList<Command>();
		Item[] items = null;
		Item[] referenceObjects = null;
		Map<String, String> adjective = null;
		String property = null;
		String function = null;
		String value = null;
		Word object = getObject(rootPredicate);
		
		int creater = analyzeDeterminer(object);
		switch (creater) {
			case CREATE_NO_TAG: {
				commands.add(grammarError);
				return commands;
			} case CREATE_TRUE: {
				String[] possibleModels = appropriateModels(object);
				if(possibleModels.length == 0){
					commands.add(noModelFound);
					return commands;
				}
				if(possibleModels.length > 1){
					commands.add(tooManyModels);
					return commands;
				}
				int id = getNextId();
				commands.add(Command.create(id, possibleModels[0]));
				activeAgentId = id;
				items = new Item[]{database.getItem(id)};
				break;
			} case CREATE_FALSE: {
				Item[] itemps = identify(object);
				if(itemps.length == 0){
					commands.add(noItemFound);
					return commands;
				}
				if(itemps.length > 1){
					commands.add(tooManyItemsFound);
					return commands;
				}
				items = itemps;
				break;
			} case APPLY_ALL: {
				items = identify(object);
				break;
			}
		}
		
		property = verb.get("property");
		if(property == null) {
			Word word = getArgumentHead(rootPredicate, "A1");
			if(word.isPred()) {
				property = database.getProperty(word.getLemma());
			}
		}
		if(property == null){
			Word adj = getAdjective(rootPredicate);
			if(adj != null){				
				adjective = database.getAdjective(adj.getLemma());
				property = adjective.get("property");
				function = adjective.get("function");
				value = adjective.get("value");
			}
		}
		if(property.equals("position")){
			//TODO
			Word adj = getAdjective(rootPredicate);
			if(adj != null){				
				adjective = database.getAdjective(adj.getLemma());
				property = adjective.get("property");
				function = adjective.get("function");
				value = adjective.get("value");

				if(value == null){
					Word w = adj;
					do {
						w = getWord(w.getChildren().iterator().next());
					}while(!isObject(w));
					Item[] refs = identify(w);
					if(refs.length == 0){
						commands.add(noItemFound);
						return commands;
					}
					if(refs.length > 1){
						commands.add(tooManyItemsFound);
						return commands;
					}
					Item ref = refs[0];
					referenceObjects = new Item[]{ref};
					
					for (String posProperty : POSITIONS) {
						if(posProperty.equals(property)){
							value = "1";
						}else{							
							commands.add(Command.modify(items[0], posProperty, ref.get(posProperty)));
						}
					}
				}
			}
			
			
		}
		if(value == null){
			if(function == null)
				function = verb.get("function");
			Word adj = getAdjective(rootPredicate);
			if(adj != null){				
				if(property.equals("name")){
					value = adj.getLemma();
				}else{				
					adjective = database.getAdjective(adj.getLemma());
					if(function == null)
						function = adjective.get("function");
					value = adjective.get("value");
				}
			}
		}
		
		for (Item item : items) {
			String objectValue = item.get(property);
			if(objectValue == null && !property.equals("name") && !property.equals("model")){
				commands.add(illegalProperty);
				return commands;
			}
			
			if(property == null || value == null){
				commands.add(grammarError);
				return commands;
			}	
		}
		//Exceptions are not allowed to be thrown between this comment and return
		for (Item item : items) {
			if(property.equals("name")){
				database.addName(item.id, value);
			}else {			
				String objectValue = item.get(property);
				String referenceValue = "0";
				if (referenceObjects != null){					
					referenceValue = referenceObjects[0].get(property);
				}
				value = applyFunction(function, value, objectValue, referenceValue);
				commands.add(Command.modify(item, property, value));
			}
		}
		
		activeAgentId = items[0].id;
		return commands;
	}
	
	private static String applyFunction(String function, String value, String sourceValue, String targetValue) throws NumberFormatException {
		if(function == null)
			return value;
		Calculable calc = null;
		try {
			calc = new ExpressionBuilder(function)
					.withVariable("x", Double.valueOf(value))	
					.withVariable("y", Double.valueOf(sourceValue))
					.withVariable("z", Double.valueOf(targetValue)).build();
		} catch (UnknownFunctionException | UnparsableExpressionException e) {
			e.printStackTrace();
		}
		return Double.toString(calc.calculate());
	}
	
	
	private static Word getObject(Predicate rootPredicate) {
		Word word = getArgumentHead(rootPredicate, "A1");
		while(word.isPred()){
			Predicate predicate = (Predicate) word;
			word = getArgumentHead(predicate, "A1");
		}
		while(!isObject(word) && word.getChildren().size() == 1){
			word = word.getChildren().toArray(new Word[0])[0];
		}
		return word;
	}
	
	private static boolean isObject(Word word) {
		switch(word.getPOS()){
			case "NN":
			case "NNS":
			case "NNP":
			case "NNPS":
				return true;
			default:
				return false;
		}
	}
	
	private static Word getAdjective(Predicate rootPredicate) {
		Word word = getArgumentHead(rootPredicate, "A2");
		if(word == null){
			//TODO
		}
		word = getWord(word);
		return word;
	}
	
	private static Word getWord(Word word){
		if(word == null)
			return null;
		if (word.getChildren().size() == 1) {
			switch(word.getLemma()){
			case "in":
			case "of":
			case "to":
				return getWord(word.getChildren().iterator().next());
			}
		}
		return word;
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
		for(String subnoun : getSubtags(itemDescription, "NNP")){
			models = intersection(models, database.getModels(subnoun), new String[0]);
		}
		
		return models;
	}
	

	private static boolean hasSubtag(Word word, String tag){
		return getSubtags(word, tag).size() > 0;
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
	
	public static final int CREATE_NO_TAG = 0;
	public static final int CREATE_TRUE = 1;
	public static final int CREATE_FALSE = 2;
	public static final int APPLY_ALL = 3;
	
	private static int analyzeDeterminer(Word item){
		switch(item.getLemma()) {
			case "all":
			case "everything":
				return APPLY_ALL;
		}
		List<String> dts = getSubtags(item, "DT");
		if(dts.size() > 1)
			return CREATE_NO_TAG;
		else if (dts.size() < 1)
			return CREATE_FALSE;
		switch(dts.get(0)){
			case "a":
			case "an":
			case "another":
				return CREATE_TRUE;
			case "every":
			case "all":
				return APPLY_ALL;
			case "the":
			case "this":
			default:
				return CREATE_FALSE;
		}
	}
	
	private static Item[] identify(Word item) {
		if(item.getLemma().equals("it")) {
			return new Item[]{database.getItem(activeAgentId)};
		}
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
	 * @return list of items the noun could be referring to
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
	private static void getAllSubtags(List<Word> values, Word word, String tag) {
		if(word.getPOS().equals(tag)) {
			values.add(word);
		}
		for(Word child : word.getChildren()) {
			getAllSubtags(values, child, tag);
		}
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
