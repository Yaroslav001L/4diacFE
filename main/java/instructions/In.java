package main.java.instructions;

import java.util.ArrayList;

public class In extends InOut{
	public Parameter value = null;
	
	public In(String comment, String name, String type) {
		super(comment, name, type);
	}

	public ArrayList<Out> sources = new ArrayList<>();
	
	protected void connect(Out newSource) {
		sources.add(newSource);
	}
	
	protected void disconnect(Out source) {
		sources.remove(source);
	}
	
	public boolean setValue(String value) {
		// byte, word - это char
		boolean canSet = false;
		if (value.startsWith("'") && value.endsWith("'") && canBeString) canSet = true;
		else if (canBeReal && isNumeric(value)) canSet = true;
		else if (canBeInt && isNumeric(value) && !value.contains(".")) canSet = true;
		else if ((canBeUint || canBeChar) && isNumeric(value) && !value.contains("-")) canSet = true;
		else if (canBeBool && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false") || value.equals("1") || value.equals("0"))) {
			value.toLowerCase();
			canSet = true;
		}
		if (!canSet) return false;
		this.value = new Parameter(this, value);
		return true;
	}
	
	public String getNonNullValue() {
		if (value == null) return "";
		return getValue();
	}
	public String getValue() {
		if (value == null) return null;
		return value.getValue();
	}
	@Override
	public void show() {
		System.out.print("Input. ");
		if (value != null) System.out.print("Value = " + getNonNullValue() + ". ");
		super.show();
	}
}
