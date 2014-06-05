package sceneParser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**Parses numbers, both numerical and texted numbers. 
 * Can at moment only handle positive Integers.
 * 
 * @author Hampus Mauritzon (2014)*/
public class NumberParser {
	
	private static Map<String, Double> baseNumbers = new HashMap<>();
	
	/**@param number - A array of texted numerical terms in order
	 * OR a numerical number.*/
	public double parse(String[] number) {
		if(number.length < 2) try	{ 
			return Double.parseDouble(number[0]);
		}catch(NumberFormatException e){
		}
		
		return literalNumber(number);
	}
	
	private double literalNumber(String[] number){
		LinkedList<Container> stack = new LinkedList<>();
		for (String string : number) {
			double c = baseNumbers.get(string);
			int orderC = getOrder(c);
			Container top = stack.peek();
			if(stack.isEmpty() || orderC < top.order){
				stack.push(new Container(c, orderC));
			}else{
				double sum = 0;
				do {
					sum += stack.pop().number;
				}while(!stack.isEmpty() && stack.peek().order < orderC);
				sum *= c;
				stack.push(new Container(sum, orderC));
			}
		}
		
		double sum = 0;
		while(!stack.isEmpty())
			sum += stack.pop().number;
		
		return sum;
	}
	
	/**Handles all doubles bigger than 0.*/
	private static int getOrder(double number){
		return (int) StrictMath.floor(StrictMath.log10(number));
	}
	
	/**Exists to not cause composite numbers to gain increased order.
	 * And to (maybe) decrease computation time.*/
	private static class Container {
		public double number;
		public int order;
		
		public Container(double number, int order){
			this.number = number;
			this.order = order;
		}
	}
	
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
		baseNumbers.put("thirty", 30d);
		baseNumbers.put("forty", 40d);
		baseNumbers.put("fifty", 50d);
		baseNumbers.put("sixty", 60d);
		baseNumbers.put("seventy", 70d);
		baseNumbers.put("eighty", 80d);
		baseNumbers.put("ninety", 90d);
		baseNumbers.put("hundred", 100d);
		baseNumbers.put("thousand", 1000d);
		baseNumbers.put("million", Math.pow(10, 6));
		baseNumbers.put("billion", Math.pow(10, 9));
		baseNumbers.put("trillion", Math.pow(10, 12));
		baseNumbers.put("quadrillion", Math.pow(10, 15));
		baseNumbers.put("quintillion", Math.pow(10, 18));
		baseNumbers.put("sextillion", Math.pow(10, 21));
		baseNumbers.put("septillion", Math.pow(10, 24));
		baseNumbers.put("octillion", Math.pow(10, 27));
		baseNumbers.put("nonillion", Math.pow(10, 30));
		baseNumbers.put("decillion", Math.pow(10, 33));
	}
}
