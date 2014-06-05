package sceneParser;

import neo4j.Database;

public class Command {
	
	String[] s;
	
	private Command(String... s){
		this.s = s;
		this.s = s;
	}

	public static final Command create(Database database, int id, String model){
		database.createItem(id, model);
		return new Command("create",Integer.toString(id), model);
	}

	public static final Command remove(Database database, int itemId){
		database.removeItem(itemId);
		return new Command("remove", Integer.toString(itemId));
	}

	public static final Command modify(Database database, int itemId, String attribute, String value){
		database.modifyItem(itemId, attribute, value);
		return new Command("modify", Integer.toString(itemId), attribute, value);
	}

	public static final Command notify(String message){
		return new Command("notify", message);
	}
	
	@Override
	public String toString(){
		String toReturn = s[0];
		for(int i = 1; i < s.length; i++)
			toReturn += " " + s[i];
		return toReturn + "; ";
	}
}