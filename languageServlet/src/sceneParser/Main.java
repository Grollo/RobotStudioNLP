package sceneParser;

import java.util.ArrayList;
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
	private static NeoDatabase database = new NeoDatabase();
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
		String itemToMakeArgument = verb.get("item");
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
		nextId++;
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
	 * given a description of an item, find a model in the database that fits
	 * if no fitting model is found, return null
	 * @param itemDescription
	 * @return
	 */
	private static String[] appropriateModels(Word itemDescription) { //implementing a simple version that only checks one word, expand later
		String name = itemDescription.getLemma();
		return database.getModels(name);
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

}
