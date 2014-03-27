package sceneParser;

import java.util.ArrayList;
import se.lth.cs.semparser.corpus.Sentence;

public class Main {
	private final static Command grammarError = Command.notify("I don't understand your grammar, can you please rephrase that?");
	private int activeAgentId = -1; // id of last referred to object, -1 if no such thing
	
	public static ArrayList<Command> interpret(Sentence parsedSentence) {
		ArrayList<Command> commands = new ArrayList<Command>();if(incorrectGrammar(parsedSentence)){
			commands.add(grammarError);
		} else {
			// TODO
		}
		return commands;
	}

	private static boolean incorrectGrammar(Sentence parsedSentence) { 
		return false;
	}
	
	/**
	 * @param noun
	 * @return list of ids of items the noun could be referring to
	 */
	private static int[] possibleItems(String noun){ 
		return new int[] {}; //TODO
	}
	
	/**
	 * @param name
	 * @return ids of all items that have the name
	 */
	private static int[] namedItemsWithName(String name){
		return new int[] {}; //TODO
	}
	
	/**
	 * @param ids
	 * @param attribute
	 * @param value
	 * @return sublist of items that fit the property
	 */
	private static int[] filterByProperty(int[] ids, String attribute, String value){
		return new int[] {}; //TODO
	}
	
}
