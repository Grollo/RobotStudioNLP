package se.lth.cs.semparser.corpus;
import java.util.*;
import java.io.*;

public class CorpusWriter {
	
	private BufferedWriter out;
	
	public CorpusWriter(File filename) {
		
		System.out.println("Saving corpus to "+filename+"...");
	    try {
	        out = new BufferedWriter(new FileWriter(filename));
	    } catch (IOException e) {
	    	System.out.println("Open writer...\n"+e.toString());
	    	System.exit(1);
	    }
	}
	
	public void write(Sentence s){
        try {
			out.write(s.toString()+"\n\n");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to write sentance.");
			System.exit(1);
		}
	}
	
	public void close(){
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to close writer.");
			System.exit(1);
		}
	}
	
}
