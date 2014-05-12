package sceneParser;

import neo4j.Database;

public class Command {
	String[] s;
	
	private Command(String... s){
		this.s = s;
	}

	public static Command create(Database database, int id, String model){
		database.createItem(id, model);
		return new Command("create", Integer.toString(id), model);
	}

	public static Command remove(Database database, Item item){
		database.removeItem(item);
		return new Command("remove", Integer.toString(item.id));
	}

	public static Command modify(Database database, Item item, String attribute, String value){
		database.modifyItem(item, attribute, value);
		return new Command("modify", Integer.toString(item.id), attribute, value);
	}

	public static Command notify(String message){
		return new Command("notify", message);
	}
	
	@Override
	public String toString(){
		String toReturn = s[0];
		for(int i = 1; i < s.length; i++)
			toReturn += " " + s[i];
		return toReturn + "\n";
	}
}