package sceneParser;

import java.util.ArrayList;

public class Item {
	int id;
	String model;
	Position position;
	float rotation;
	float scale;
	String colour;
	ArrayList<String> names;
	
	public Item(int id, String model){
		this.id = id;
		this.model = model;
		position = new Position(0, 0, 0);
		rotation = 0;
		scale = 1;
		colour = null;
		names = new ArrayList<String>();
	}
	
	class Position{
		float x;
		float y;
		float z;
		
		public Position(float x, float y, float z){
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

}
