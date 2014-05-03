package sceneParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		String itemToMakeArgument = "A1";
		Set<Word> words = rootPredicate.getArgMap().keySet();
		Word itemDescription = null;
		for(Word word : words){
			if(rootPredicate.getArgMap().get(word).equals(itemToMakeArgument))
				itemDescription = word;
		}
		String[] possibleModels = appropriateModels(itemDescription);
		if(possibleModels.length == 0)
			return noModelFound;
		if(possibleModels.length > 1)
			return tooManyModels;
		return Command.create(getNextId(), possibleModels[0]);
	}

	private static int getNextId() {
		return nextId++;
	}

	private static Command makeRemove(Map<String, String> verb, Predicate rootPredicate) {
		String affectedItemArgument = verb.get("item");
		Set<Word> words = rootPredicate.getArgMap().keySet();
		Word itemDescription = null;
		for(Word word : words){
			if(rootPredicate.getArgMap().get(word).equals(affectedItemArgument))
				itemDescription = word;
		}
		int[] id = getIds(itemDescription);
		if(id.length == 0)
			return noItemFound;
		if(id.length > 1)
			return tooManyItemsFound;
		return Command.remove(id[0]);
	}

	private static Command makeModify(Map<String, String> verb, Predicate rootPredicate) {
		// TODO Auto-generated method stub
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
		for(String subnoun : getAllSubnoun(itemDescription)){
			models = intersection(models, database.getModels(subnoun), new String[0]);
		}
		
		return models;
	}
	
	private static List<String> getAllSubnoun(Word itemDescription) {
		ArrayList<String> words = new ArrayList<>();
		for(Word child : itemDescription.getChildren()) {
			if(child.getPOS().equals("NN")) {
				words.add(child.getLemma());
			}
		}
		return words;
	}
	
	/**
	 * get id numbers of objects in the world that fit the description
	 * @param itemDescription
	 * @return
	 */
	private static int[] getIds(Word itemDescription) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param noun
	 * @return list of ids of items the noun could be referring to
	 */
	private static int[] possibleItems(String noun) {
		return new int[] {}; // TODO
	}

	/**
	 * @param name
	 * @return ids of all items that have the name
	 */
	private static int[] namedItemsWithName(String name) {
		return new int[] {}; // TODO
	}

	/**
	 * @param ids
	 * @param attribute
	 * @param value
	 * @return sublist of items that fit the property
	 */
	private static int[] filterByProperty(int[] ids, String attribute, String value) {
		return new int[] {}; // TODO
	}
	
	private static Map<String, String> extractPropertyAdjectives(String item){
		Map<String, String> values = new HashMap<String, String>();
		// TODO: check for adjectives
		return values;
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
