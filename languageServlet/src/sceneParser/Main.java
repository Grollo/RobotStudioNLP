package sceneParser;

import java.util.ArrayList;
import se.lth.cs.semparser.corpus.Sentence;

public class Main {

	public static ArrayList<Command> interpret(Sentence parsedSentence) {
		ArrayList<Command> commands = new ArrayList<Command>();
		
		if(parsedSentence.contains("Undo")){
			//TODO
		}
		
		return commands;
	}
	
}
