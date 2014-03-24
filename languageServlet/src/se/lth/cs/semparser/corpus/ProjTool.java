package se.lth.cs.semparser.corpus;

import java.util.*;
import java.io.*;
public class ProjTool {
	
	public static void Projectivize(Sentence s){
		int i=-1;
		while((i=getShortestNonProjArc(s))!=-1){
			lift(s,i);
		}
	}
	
	public static void deProjectivize(Sentence s){
		Word w=null;

		while((w=s.get(getLiftedArc(s)))!=null){
			String[] labels=w.getDeprel().split("\\|",2);
			//String originalLabel=w.getDeprel().split("\\|",2)[0];
			//String targetLabel=w.getDeprel().split("\\|",2)[1];
			Word targetWord =s.get(findWordWithLabel(s,labels[1], w.getHeadId()));
			if(targetWord!=null){
				w.setHead(targetWord);
				w.setDeprel(labels[0]);
			}else{
				w.setDeprel(labels[0]);
			}
		}
	}
	
	private static int findWordWithLabel(Sentence s, String targetLabel, int word) {  //find word with label targetLabel in subtree with head in word w

		int[] children=getChildren(s, word);
		while(children.length!=0){
			for(int i=0;i<children.length;i++){
				if(s.get(children[i]).getDeprel().equals(targetLabel)){
					return children[i];
				}
			}
			children=getChildren(s, children);
		}
		return -1;
	}
	
	private static int[] getChildren(Sentence s, int[] children){
		int numberofChildren=0;
		for(int i=0;i<children.length;i++){
			numberofChildren+=getChildren(s,children[i]).length;
		}
		int[] ret = new int[numberofChildren];
		int j=0;
		for(int i=0;i<children.length;i++){
			for(int k=0;k<getChildren(s,children[i]).length;k++){
				ret[j]=getChildren(s,children[i])[k];
				j++;
			}
		}
		Arrays.sort(ret);
		return ret;
	}
	
	private static int[] getChildren(Sentence s, int i) {
		
		int numberofChildren=0;
		for(Word w:s){
			if(w.getHeadId()==i)
				numberofChildren++;
		}
		int[] ret = new int[numberofChildren];
		int j=0;
		for(Word w:s){
			if(w.getHeadId()==i){
				ret[j]=s.indexOf(w);
				j++;
			}
		}
		Arrays.sort(ret);
		return ret;
	}

	private static int getLiftedArc(Sentence s){
		for(int i=0;i<s.size();i++){
			String deprel=s.get(i).getDeprel();
			if(deprel.contains("|"))
				return i;
		}
		return -1;
	}
	
	private static void lift(Sentence s, int i) {
		Word syntacticHead=s.get(s.get(i).getHeadId());
		String d=s.get(i).getDeprel();
		String h=syntacticHead.getDeprel();
		String newDeprel=d+"|"+h;
		int newHead=syntacticHead.getHeadId();
		while(!projArc(s,i, newHead) && newHead!=0)
			newHead=s.get(newHead).getHeadId();
		s.get(i).setHead(s.get(newHead));
		s.get(i).setDeprel(newDeprel);
	}

	private static final Comparator<Word> arcLengthComparator= new Comparator<Word>(){
		public int compare(Word w1, Word w2){
			return w1.headID-w2.headID;
		}		
	};
	
	private static int getShortestNonProjArc(Sentence s){
		ArrayList<Word> nonProjArcs = new ArrayList<Word>();
		for(int i=1;i<s.size();i++){
			if(!projArc(s,i))
				nonProjArcs.add(s.get(i));
		}
		if(nonProjArcs.isEmpty())
			return -1;
		
		Collections.sort(nonProjArcs,arcLengthComparator);		
		return s.indexOf(nonProjArcs.get(0));
	}
	
	
	
/*	static class arcLengtComparator implements Comparator<Word>{
		public int compare(Word w1, Word w2){
			return w1.head-w2.head;
		}
	}*/
	
	private static boolean projArc(Sentence s, int i){ //is the arc from word i to its parent projective?
		if(s.get(i).getHeadId()==0)
			return true;
		int start=i<s.get(i).getHeadId()?i:s.get(i).getHeadId();
		int end = i<s.get(i).getHeadId()?s.get(i).getHeadId():i;
		
		if(Math.abs(start-end)<2)
			return true;
		for(int j=start+1;j<end;j++){
			if(s.get(j).getHeadId()<start || s.get(j).getHeadId()>end)
				return false;
		}
		return true;
	}
	
	private static boolean projArc(Sentence s, int child, int parent){ //is the arc from w to index projective?
		if(s.get(child).getHeadId()==0)
			return true;
		int start=child<parent?child:parent;
		int end = child<parent?parent:child;
		
		if(Math.abs(start-end)<2)
			return true;
		for(int i=start+1;i<end;i++){
			if(s.get(i).getHeadId()<start || s.get(i).getHeadId()>end)
				return false;
		}
		return true;
	}
}
