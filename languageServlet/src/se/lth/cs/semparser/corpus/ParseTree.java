package se.lth.cs.semparser.corpus;
import java.util.*;
import java.io.*;

public class ParseTree implements Serializable, Cloneable{
	
	private static final long serialVersionUID = 1L;
	private HashMap<Word,Word> arcs;
	private HashMap<Word,String> deprels;
	public ParseTree(){
		arcs = new HashMap<Word,Word>(); //maps dependent(key) to its head(value)
		deprels=new HashMap<Word,String>();
	}
	
	@SuppressWarnings("unchecked")
	public Object clone(){
    	try {
    		ParseTree newParsTree=(ParseTree) super.clone();
    		newParsTree.arcs=(HashMap<Word,Word>)arcs.clone();
    		newParsTree.deprels=(HashMap<Word,String>)deprels.clone();
    		return newParsTree;
    	}catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
    }
	
	public void printDeprels(){
		String ret="";
		for (Word w:deprels.keySet()){
			ret+=w.getForm()+" "+getDeprel(w)+"\n";
		}
		System.out.println(ret);
	}
	
	public Set<Word> keySet(){
		return arcs.keySet();
	}
	
	public void mkArc(Word w1, Word w2){ // makes an arc from w1 (head) to w2 (dependent)
		mkArc(w1,w2,"");
	}
	public void mkArc(Word w1, Word w2,String deprel){ // makes an arc from w1 (head) to w2 (dependent) whit deprel deprel
		arcs.put(w2, w1);
		deprels.put(w2, deprel);
	}
	
	public boolean hasArc(Word w1, Word w2){ // is there an arc from w1 (head) to w2 (dependent)
		boolean hasArc = false;
		if (arcs.get(w2)==w1){
			hasArc=true;
		}
		return hasArc;
	}
	/*
	public Word getRightmostChild(Word w){
		Word ret=w;
		for(Word w2:arcs.keySet()){
			if(hasArc(w,w2)){
				if(w.getMySentance().getIndexof(w2)>w.getMySentance().getIndexof(ret)){
					ret=w2;
				}
			}
		}
		if(ret==w)
			return null;
		return ret;
	}*/
	
	
	public Word getRightmostChild(Word w){
		Sentence s=w.mySentence;
		Word ret=w;
		for(Word w2:arcs.keySet()){
			if(hasArc(w,w2) && s.indexOf(w2)>s.indexOf(ret)){
				ret=w2;
			}
		}
		if(ret==w)
			return null;
		return ret;
	}
	
	public Word getLeftmostChild(Word w){
		Sentence s=w.mySentence;
		Word ret=w;
		for(Word w2:arcs.keySet()){
			if(hasArc(w,w2)){
				if(s.indexOf(w2)<s.indexOf(ret)){
					ret=w2;
				}
			}
		}
		if(ret==w)
			ret=null;
		return ret;
	}
	/*
	public Word getLeftmostChild(Word w){
		Word ret=w;
		for(Word w2:arcs.keySet()){
			if(hasArc(w,w2)){
				if(w.getMySentance().getIndexof(w2)<w.getMySentance().getIndexof(ret)){
					ret=w2;
				}
			}
		}
		if(ret==w)
			return null;
		return ret;
	}*/
	
	public boolean gotHead(Word w){ //is there an arc that connects w whit its head
		boolean gotHead=false;
		if (arcs.get(w)!=null){
			gotHead=true;
		}
		return gotHead;
	}
	public Word getHead(Word w){
		return arcs.get(w);
	}
	public String getDeprel(Word w){
		return deprels.get(w);
	}
	public HashMap<Word,Word> getMap(){
		return arcs;
	}
	
	public void printArcs(){
		for (Word w:arcs.keySet()){
			System.out.println("Tree Arc: " + w.getForm()+" <-- "+arcs.get(w).getForm());
		}
	}
	
}
