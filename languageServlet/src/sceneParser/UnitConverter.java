package sceneParser;

import java.util.HashMap;
import java.util.Map;

public class UnitConverter {

	public final Map<String, Double> converter = new HashMap<>();
	
	public boolean isUnit(String unit){
		return converter.containsKey(unit);
	}
	
	public double convert(double value, String unit) {
		return value * converter.get(unit);
	}
	
	public UnitConverter() {
		//Lengh
			//SI
			converter.put("km", 1000d);
			converter.put("kilometer", 1000d);
			converter.put("m", 1d);
			converter.put("meter", 1d);
			converter.put("dm", 0.1);
			converter.put("decimeter", 0.1);
			converter.put("cm", 0.01);
			converter.put("centimeter", 0.01);
			converter.put("mm", 0.01);
			converter.put("millimeter", 0.01);
			
			//Imperial units
			converter.put("yd", 0.9144);
			converter.put("yard", 0.9144);
			converter.put("ft", 0.3048);
			converter.put("foot", 0.3048);
			converter.put("in", 0.3048);
			converter.put("inch", 0.0254);
		//Weight
			//SI
			converter.put("kg", 1d);
			converter.put("kilogram", 1d);
			converter.put("g", 0.001);
			converter.put("gram", 0.001);
			
			//Imperial Units
			converter.put("st", 6.35029318);
			converter.put("stone", 6.35029318);
			converter.put("lb", 0.45359237);
			converter.put("pound", 0.45359237);
			converter.put("oz", 0.028349523125);
			converter.put("ounce", 0.028349523125);
		//Rotation
			converter.put("rad", 1d);
			converter.put("radian", 1d);
			converter.put("degree", (2*Math.PI) / 360);
			converter.put("turn", 2*Math.PI);
	}
}
