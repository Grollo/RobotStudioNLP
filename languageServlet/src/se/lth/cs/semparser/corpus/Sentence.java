package se.lth.cs.semparser.corpus;

import java.util.*;
import java.io.*;
public class Sentence extends ArrayList<Word> {

	private static final long serialVersionUID = 10;
	
	private List<Predicate> predicates;
	public Sentence(String conll2009sentence){
		
		this();
		Word nextWord;
		
		for(String line:conll2009sentence.split("\\n")){
			
			String[] cols=line.split("\\s+");
			//System.out.println("One line" + line + " cols leen " + cols.length);
			if(cols.length > 12 && cols[12].equals("Y")){
				Predicate pred=new Predicate(cols);
				
				addPredicate(pred);
				nextWord=pred;
			} else {
				nextWord=new Word(cols);
			}
			nextWord.setMySentence(this);
			super.add(nextWord);	
		}
		buildDependencyTree();
		buildSemanticTree();
	}
	public Sentence() { 
		Word BOS=new Word();
		super.add(BOS); //Add the root token
		BOS.setMySentence(this);
		predicates=new ArrayList<Predicate>();
	}
	
	public void addPredicate(Predicate pred){
		predicates.add(pred);
	}
	
	public List<Predicate> getPredicates() {
		return predicates;
	}
	
	public void buildDependencyTree(){
		for(int i=1;i<size();++i){
			Word curWord = get(i);
			curWord.setHead(get(curWord.getHeadId()));
		}		
	}
	
	public void buildSemanticTree(){
		for(int i=0;i<predicates.size();++i){
			Predicate pred=predicates.get(i);
			for(int j=1;j<super.size();++j){
				Word curWord = get(j);
				String arg=curWord.getArg(i);
				if(!arg.equals("_"))
					pred.addArgMap(curWord,arg);
			}
		}
		for(Word w:this) //Free this memory as we no longer need this string array
			w.clearArgArray();		
	}
	
	public String toString() {
		String tag;
		StringBuilder ret=new StringBuilder();
		for(int i=1;i<super.size();++i){
			Word w=super.get(i);
			ret.append(i).append("\t").append(w.toString());
			if(!(w instanceof Predicate)) //If its not a predicate add the FILLPRED and PRED cols
				ret.append("\t_\t_");
			for(int j=0;j<predicates.size();++j){
				ret.append("\t");
				Predicate pred=predicates.get(j);
				ret.append((tag=pred.getArgumentTag(w))!=null?tag:"_");
			}
			ret.append("\n");
		}
		return ret.toString().trim();
	}
	
	/*
	 * Functions used when interfacing with Bohnets parser
	 */
	public String[] getFormArray(){
		String[] ret=new String[this.size()];
		ret[0]="<root>";
		for(int i=1;i<this.size();++i)
			ret[i]=this.get(i).Form;
		return ret;
	}
	public String[] getPOSArray(){
		String[] ret=new String[this.size()];
		ret[0]="<root-POS>";
		for(int i=1;i<this.size();++i)
			ret[i]=this.get(i).POS;
		return ret;
	}
	public void setHeadsAndDeprels(int[] heads,String[] deprels){
		for(int i=0;i<heads.length;++i){
			Word w=this.get(i+1);
			w.setHead(this.get(heads[i]));
			w.setDeprel(deprels[i]);
		}
	}
}
