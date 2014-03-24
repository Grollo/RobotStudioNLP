package se.lth.cs.semparser.corpus;
import java.util.*;
import java.io.*;

import se.lth.cs.semantics.*;

public class Predicate extends Word {
	private static final long serialVersionUID = 1L;
	
	private HashMap<Word,String> argmap;
	private String sense; //This is PredLemmaSense in CoNLL2008
	boolean isPred = true;

	/**
	 * Used to replace an old word with a predicate (updates dependencies). 
	 * Used to make a predicate from a word during predicate identification.
	 * @param w The Word
	 */
	public Predicate(Word w){
		super(w);
		argmap=new HashMap<Word,String>();
	}
	/**
	 * Only use this constructor if you manually add the other attributes later on (i.e. in constructor Word(String CoNLL2009String))
	 * @param sense the sense label of the predicate
	 */
	public Predicate(String[] CoNLL2009Columns){
		super(CoNLL2009Columns);
		this.sense=CoNLL2009Columns[13];
		argmap=new HashMap<Word,String>();
	}
	
	public HashMap<Word, String> getArgMap() {
		return argmap;
	}
	public void setArgMap(HashMap<Word, String> argmap) {
		this.argmap = argmap;
	}
	public void addArgMap(Word w,String label){
		argmap.put(w,label);
	}
	public String getPred() {
		return sense;
	}
	public void setPred(String sense) {
		this.sense = sense;
	}
	public String getAttr(WordData attr){
		if(attr==WordData.Pred)
			return sense;
		else
			return super.getAttr(attr);
	}
	
	
	
	public boolean isNeg(){
		for(Word w: argmap.keySet()){
			if(getArgumentTag(w).equals("AM-NEG")) return true;
		}
		return false;
		
	}
	public String getArgumentTag(Word w) {
		return argmap.get(w);
	}
	public String toString(){
		return super.toString()+"\tY\t"+sense;
	}
}
