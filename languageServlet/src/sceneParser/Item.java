package sceneParser;

import java.util.ArrayList;
import java.util.List;

public class Item {
	public int id;
	public String model;
	public Position position;
	public float rotation;
	public float scale;
	public String color;
	public List<String> names;
	
	public Item(int id, String model){
		this.id = id;
		this.model = model;
		position = new Position(0, 0, 0);
		rotation = 0;
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
