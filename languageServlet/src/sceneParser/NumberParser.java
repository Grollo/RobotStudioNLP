package sceneParser;

import java.util.HashMap;
import java.util.Map;

public class NumberParser {
	
	private static Map<String, Double> baseNumbers = new HashMap<>();
	
	public NumberParser(){
		baseNumbers.put("one", 1d);
		baseNumbers.put("two", 2d);
		baseNumbers.put("three", 3d);
		baseNumbers.put("four", 4d);
		baseNumbers.put("five", 5d);
		baseNumbers.put("six", 6d);
		baseNumbers.put("seven", 7d);
		baseNumbers.put("eight", 8d);
		baseNumbers.put("nine", 9d);
		baseNumbers.put("ten", 10d);
		baseNumbers.put("eleven", 11d);
		baseNumbers.put("twelve", 12d);
		baseNumbers.put("thirteen", 13d);
		baseNumbers.put("fourteen", 14d);
		baseNumbers.put("fifteen", 15d);
		baseNumbers.put("sixteen", 16d);
		baseNumbers.put("seventeen", 17d);
		baseNumbers.put("eighteen", 18d);
		baseNumbers.put("nineteen", 19d);
		baseNumbers.put("twenty", 20d);
	}
	
	public Double parse(String[] number) {
		if(number.length < 2) try	{ 
			return Double.parseDouble(number[0]);
		}catch(NumberFormatException e){
		}
		
		return 0d;
	}
	
	private Double literalNumber(String[] number){
		double sum = 0;
		double d = 0;
		for (String string : number) {
			//TODO
		}
		sum += d;
		return sum;
	}
	
	private class MapEntry {
		public double value;
		public int order;
	}
}
