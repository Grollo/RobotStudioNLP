package neo4j;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

public class Constant {
	public static enum ItemRelationships implements RelationshipType { MODEL, NAME }
	
	public static enum ModelRelationships implements RelationshipType { MEANS }
	
	public enum NodeType implements Label{ Model, Item, Noun, Verb, Adjective }
	
	public static enum Model {
		ALIAS ("alias"), 
		FILE ("filename");
		public final String string; 
		
		private Model(String string){
			this.string = string;
		}
		
		public String toString(){
			return string;
		}
	};
	
	public static enum ItemProperties {
		ID ("id"),
		MODEL ("model"),
		POSITION_X ("position x"),
		POSITION_Y ("position y"),
		POSITION_Z ("position z"),
		ROTATION ("rotation"),
		SCALE ("scale"),
		COLOR ("color");
		public final String string;
		
		private ItemProperties(String string){
			this.string = string;
		}
		
		public String toString(){
			return string;
		}
	}
	
	public static enum Adjective {
		WORD ("word"),
		PROPERTY ("property"),
		VALUE ("value");
		public final String string;
		
		private Adjective(String string){
			this.string = string;
		}
		
		public String toString(){
			return string;
		}
	}
	
	public static enum Verb {
		WORD ("word"),
		COMMAND ("does");
		public final String string;
		
		private Verb(String string){
			this.string = string;
		}
		
		public String toString(){
			return string;
		}
	}
	
	public static enum Noun {
		WORD ("word");
		public final String string;
		
		private Noun(String string){
			this.string = string;
		}
		
		public String toString(){
			return string;
		}
	}
}
