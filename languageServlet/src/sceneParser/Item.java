package sceneParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Item {
	
	public int id;
	public String model;
	public Position position;
	public Position rotation;
	public float scale;
	public String color;
	public List<String> names;
	
	public Map<String, String> properties;
	
	public Item(int id){
		this.id = id;
		names = new ArrayList<String>();
		properties = new HashMap<String, String>();
	}
	
	public String get(String property){
		return properties.get(property);
	}
	
	public class Position{
		public float x;
		public float y;
		public float z;
		
		public Position(float x, float y, float z){
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

}
