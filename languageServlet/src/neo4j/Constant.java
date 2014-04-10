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
		POSITION_X ("position_x"),
		POSITION_Y ("position_y"),
		POSITION_Z ("position_z"),
		ROTATION_X ("rotation_x"),
		ROTATION_Y ("rotation_y"),
		ROTATION_Z ("rotation_z"),
		SCALE_X ("scale x"),
		SCALE_Y ("scale x"),
		SCALE_Z ("scale x"),
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
