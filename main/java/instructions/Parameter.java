package main.java.instructions;

public class Parameter {
	public In pin;
	public String value;
	
	public Parameter(In pin, String value) {
		this.pin = pin;
		this.value = value;
	}
	
	public String getName() {
		return pin.name;
	}
	public String getValue() {
		return value;
	}
}
