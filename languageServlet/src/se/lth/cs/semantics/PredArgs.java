/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.lth.cs.semantics;

import java.util.List;
import java.util.ArrayList;

import se.lth.cs.semparser.corpus.Predicate;
import se.lth.cs.semparser.corpus.Word;

/**
 *
 * @author pierre
 */
public class PredArgs{

    Predicate predicate;
    Word a1 = null;
    Word a2 = null;
    String a1Out = "";
    
    PredArgs(Predicate predicate, Word a1) {
        this.predicate = predicate;
        this.a1 = a1;
    }

    
    PredArgs(Predicate predicate, Word a1, Word a2) {
        this.predicate = predicate;
        this.a1 = a1;
        this.a2 = a2;
    }

    public String getPredicate() {
        return predicate.getForm();
    }
    
    public String getNaturalPredicate(){
    	int index = predicate.getPred().indexOf('.');
    	String str = predicate.getPred().substring(0,index);
    	return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    	
    }
    
    public String toString(){

    	// change this.
    	String tmp = a2 != null? getA2(): "";
    	String tmp1 = a1 != null? getA1(): "";
    	String out = getPredicate() + "\t" + tmp1 + " "+ tmp;
    	return out;
    	
    }

    public String getType(){
		return "NORMAL";
		
	}
    

    public String getA1() {
        if (a1 == null) {
            return null;
        }
        return a1.getForm();
    }
  

    public String getA2() {
        if (a2 == null) {
            return null;
        }
        // Quick hack to get the noun.
        if (a2.getPOS().equals("IN")) {
            List<Word> listAux = new ArrayList<Word>(a2.getChildren());
            return listAux.get(0).getForm();
        }
        return a2.getForm();
    }
    
    
    
    public String getAllA2(){
    	String str = "";
    	for(Word c: a2.getChildren()){
    		if(!c.getForm().toLowerCase().equals("the")){
    		str += c.getForm() + "_";}
    	}
    	if(!a2.getForm().equals("in")){
    	str += a2.getForm();
    	}
    	return str;
    	
    }
    
    public String getAllA1(){
    	String str = "";
    	for(Word c: a1.getChildren()){
    		if(!c.getForm().toLowerCase().equals("the")){
    		str += c.getForm() + "_";}
    	}
    	str += a1.getForm();
    	return str;
    	
    }
}
