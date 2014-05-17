package sceneParser;

import neo4j.Database;

public abstract class Command {
	
	String[] s;
	
	private Command(String... s){
		this.s = s;
		this.s = s;
	}
	
	public abstract void execute(Database database);
	public abstract boolean isNotify();

	public static final Command create(int id, String model){
		return new CreateCommand(id, model);
	}

	public static final Command remove(Item item){
		return new RemoveCommand(item.id);
	}

	public static final Command modify(Item item, String attribute, String value){
		return new ModifyCommand(item.id, attribute, value);
	}

	public static final Command notify(String message){
		return new NotifyCommand("notify", message);
	}
	
	@Override
	public String toString(){
		String toReturn = s[0];
		for(int i = 1; i < s.length; i++)
			toReturn += " " + s[i];
		return toReturn + "; ";
	}
	
	private static class CreateCommand extends Command {
		int id;
		String model;
		public CreateCommand(int id, String model){
			super("create", Integer.toString(id), model);
			this.id = id;
			this.model = model;
		}
		public void execute(Database database) {
			database.createItem(id, model);
		}
		public boolean isNotify() {
			return false;
		}
	}
	private static class RemoveCommand extends Command {
		int id;
		public RemoveCommand(int id){
			super("remove", Integer.toString(id));
			this.id = id;
		}
		public void execute(Database database) {
			database.removeItem(id);
		}
		public boolean isNotify() {
			return false;
		}
	}
	private static class ModifyCommand extends Command {
		int id;
		String attribute, value;
		public ModifyCommand(int id, String attribute, String value){
			super("modify", Integer.toString(id), attribute, value);
			this.id = id;
			this.attribute = attribute;
			this.value = value;
		}
		public void execute(Database database) {
			database.modifyItem(id, attribute, value);
		}
		public boolean isNotify() {
			return false;
		}
	}
	private static class NotifyCommand extends Command {
		public NotifyCommand(String... s){
			super(s);
		}
		public void execute(Database database) { 
		}
		public boolean isNotify() {
			return true;
		}
	}
}