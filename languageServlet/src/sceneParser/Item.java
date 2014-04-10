package sceneParser;

import java.util.ArrayList;
import java.util.List;

public class Item {
	
	public int id;
	public String model;
	public Position position;
	public Position rotation;
	public float scale;
	public String color;
	public List<String> names;
	
	public Item(int id){
		this.id = id;
		this.model = null;
		position = new Position(0, 0, 0);
		rotation = new Position(0, 0, 0);
		scale = 1;
		color = null;
		names = new ArrayList<String>();
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
