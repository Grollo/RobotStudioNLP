package sceneParser;

import java.util.ArrayList;
import se.lth.cs.semparser.corpus.Sentence;

public class Main {

	public static ArrayList<String> interpret(Sentence parsedSentence) {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(parsedSentence.toString());
		return commands;
	}
	
}
