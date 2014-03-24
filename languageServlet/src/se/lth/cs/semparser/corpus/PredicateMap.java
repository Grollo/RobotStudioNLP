package se.lth.cs.semparser.corpus;

import java.util.*;
import java.io.*;
/**
 * This class extends HashMap<String,ArrayList<String>> where keys correspond to predicate lemmas,
 * and maps onto a list of labels
 * @author anders
 *
 */

public class PredicateMap extends HashMap<String,ArrayList<String>>{
	private static final long serialVersionUID = 10;
	private HashMap<String,String> filenames; //Maps lemmas -> filename
	
	public PredicateMap(List<Sentence> corpus){
		filenames=new HashMap<String,String>();
		extractPredicates(corpus);
	}
	
	public void setFile(String lemma,String filename){
		filenames.put(lemma,filename);
	}
	public String getFile(String lemma){
		return filenames.get(lemma);
	}
	public Collection<String> getFileNames(){
		return filenames.values();
	}
	
	private void extractPredicates(List<Sentence> corpus){
		int filecounter=0;
		for(Sentence sen:corpus){	
			for(Predicate pred:sen.getPredicates()){
				String lemma=pred.getLemma();
				String sense=pred.getPred();
				if(!super.containsKey(lemma)){
					super.put(lemma,new ArrayList<String>());
					this.setFile(lemma,""+(++filecounter));
				}
				if(!super.get(lemma).contains(sense)){
					super.get(lemma).add(sense);
				}
			}	
		}
	}
	
	public void trim(){
		for(String lemma:this.keySet()){
			if(get(lemma).size()==0){ //All predicates need at least one sense
				throw new Error("Predicate without any sense labels in predicate reference. Aborting");
			} else if(get(lemma).size()==1){ //If the predicate only has one sense, we dont need to train a classifier
				filenames.remove(lemma);
			}
		}
	}
	
	public boolean hasSingleSense(String lemma){
		return get(lemma).size()==1;
	}
}