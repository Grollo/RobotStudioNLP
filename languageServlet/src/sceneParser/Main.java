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

	private static boolean incorrectGrammar(Sentence parsedSentence) { // TODO: check if root is a verb (probably)
		return false;
	}
	
	private static int[] possibleItems(String noun){ // given an noun, returns list of ids of items the name could be referring to
		return new int[] {}; //TODO
	}
	
	private static int[] namedItemsWithName(String name){ // returns ids of all items that have a certain name
		return new int[] {}; //TODO
	}
	
	private static int[] filterByProperty(int[] ids, String attribute, String value){ // returns items that fit some property
		return new int[] {}; //TODO
	}
	
}
