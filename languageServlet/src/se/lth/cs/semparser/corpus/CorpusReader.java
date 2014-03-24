package se.lth.cs.semparser.corpus;

import java.io.BufferedReader;
import java.io.*;
import java.util.*;


public class CorpusReader {

	private BufferedReader in;
	private Sentence nextSen;

	public static void main(String[] args) throws IOException{
		String file="/home/anders/workspace/Rosetta/semparser/corpora/train.50p.txt";
		//CorpusReader reader=new CorpusReader(new File("/home/anders/workspace/Rosetta/semparser/corpora/train.50p.txt"));
		//System.out.println(reader.getSentance());
		List<Sentence> sentences=getCorpusCoNLL2009(new File(file));
		System.out.println(sentences.get(0));
	}
	
	public CorpusReader(File filename){
		System.out.println("Opening reader for "+filename+"...");
		try {
			in = new BufferedReader(new FileReader(filename));
			readNextSentance();
		} catch (IOException e) {
			System.out.println("Failed: "+e.toString());
			System.exit(1);
		}
	}
	
	private void readNextSentance() throws IOException{
		String str;
		Sentence sen=null;
		StringBuilder senBuffer=new StringBuilder();
		while ((str = in.readLine()) != null) {
			if(!str.trim().equals("")) {
				senBuffer.append(str).append("\n");
			} else {
				sen=new Sentence(senBuffer.toString());
				break;
			}
		}
		if(sen==null){
			nextSen=null;
			in.close();
		} else {
			nextSen=sen;
		}
	}
	
	public boolean hasNext(){
		return nextSen!=null;
	}
	
	public Sentence getSentance(){
		Sentence ret=nextSen;
		try {
			readNextSentance();
		} catch(IOException e){
			System.out.println("Failed to read from corpus file... exiting.");
			System.exit(1);
		}
		return ret;
	}

	public static List<Sentence> getCorpusCoNLL2009(File trainCorpus) {
		List<Sentence> ret=new ArrayList<Sentence>();
		CorpusReader reader=new CorpusReader(trainCorpus);
		while(reader.hasNext())
			ret.add(reader.getSentance());
		return ret;
	}
}
