package sceneParser;

public class Command {
	String[] s;
	
	private Command(String[] s){
		this.s = s;
	}

	public static Command create(int id, String model){
		return new Command(new String[] {"create", Integer.toString(id), model});
	}

	public static Command remove(int id){
		return new Command(new String[] {"remove", Integer.toString(id)});
	}

	public static Command modify(int id, String attribute, String value){
		return new Command(new String[] {"modify", Integer.toString(id), attribute, value});
	}

	public static Command notify(String message){
		return new Command(new String[] {"notify", message});
	}
	
	@Override
	public String toString(){
		String toReturn = s[0];
		for(int i = 1; i < s.length; i++)
			toReturn += " " + s[i];
		return toReturn;
	}
}