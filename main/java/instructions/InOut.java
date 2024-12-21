package main.java.instructions;

public class InOut  {
	public enum TYPES {
		ANY_STRUCT,
		BOOL,
		BYTE,
		DINT,
		DATE_AND_TIME,
		DATE,
		DWORD,
		EVENT,
		INT,
		LINT,
		LREAL,
		LWORD,
		REAL,
		SINT,
		STRING,
		TIME_OF_DAY,
		UDINT,
		ULINT,
		UINT,
		USINT,
		WORD,
		WSTRING;
	}
	public double x, y; // обратить внимание, что эти значения фикшены модом и абсолютными значениями не являются!
	public String comment;
	public String name;
	public String nameType;
	public TYPES[] types;
	public Wire wire;
	protected boolean canBeString = false, canBeInt = false, canBeReal = false, canBeChar = false, canBeBool = false, canBeUint = false;
	
	public Function func;
	
	public InOut(String comment, String name, String type) {
		this.comment = comment;
		this.name = name;
		this.nameType = type;
		
		types = new TYPES[1];
		switch (type) {
		case "ANY": default: // (except event)
			types = new TYPES[22];
			types[0] = TYPES.ANY_STRUCT;
			types[1] = TYPES.BOOL;
			types[2] = TYPES.BYTE;
			types[3] = TYPES.DINT;
			types[4] = TYPES.DATE_AND_TIME;
			types[5] = TYPES.DATE;
			types[6] = TYPES.DWORD;
			types[7] = TYPES.INT;
			types[8] = TYPES.LINT;
			types[9] = TYPES.LREAL;
			types[10] = TYPES.LWORD;
			types[11] = TYPES.REAL;
			types[12] = TYPES.SINT;
			types[13] = TYPES.STRING;
			types[14] = TYPES.TIME_OF_DAY;
			types[15] = TYPES.UDINT;
			types[16] = TYPES.ULINT;
			types[17] = TYPES.UINT;
			types[18] = TYPES.USINT;
			types[19] = TYPES.WORD;
			types[20] = TYPES.WSTRING;
			canBeString = canBeInt = canBeReal = canBeChar = canBeBool = false;
			break;
		case "ANY_NUM": case "ANY_REAL":
			types = new TYPES[10];
			types[0] = TYPES.DINT;
			types[1] = TYPES.INT;
			types[2] = TYPES.LINT;
			types[3] = TYPES.LREAL;
			types[4] = TYPES.REAL;
			types[5] = TYPES.SINT;
			types[6] = TYPES.UDINT;
			types[7] = TYPES.ULINT;
			types[8] = TYPES.UINT;
			types[9] = TYPES.USINT;
			break;
		case "ANY_STRING":
			types = new TYPES[2];
			types[0] = TYPES.STRING;
			types[1] = TYPES.WSTRING;
			break;
		case "ANY_BIT":
			types = new TYPES[5];
			types[0] = TYPES.BYTE;
			types[1] = TYPES.DWORD;
			types[2] = TYPES.LWORD;
			types[3] = TYPES.WORD;
			types[4] = TYPES.WSTRING;
			break;
		case "ANY_INT":
			types = new TYPES[8];
			types[0] = TYPES.DINT;
			types[1] = TYPES.INT;
			types[2] = TYPES.LINT;
			types[3] = TYPES.SINT;
			types[4] = TYPES.UDINT;
			types[5] = TYPES.ULINT;
			types[6] = TYPES.UINT;
			types[7] = TYPES.USINT;
			break;
		case "ANY_STRUCT":
			types[0] = TYPES.ANY_STRUCT;
			break;
		case "BOOL":
			types[0] = TYPES.BOOL;
			canBeBool = true;
			break;
		case "BYTE":
			types[0] = TYPES.BYTE;
			canBeChar = true;
			break;
		case "DINT":
			types[0] = TYPES.DINT;
			canBeInt = true;
			break;
		case "DATE_AND_TIME":
			types[0] = TYPES.DATE_AND_TIME;
			break;
		case "DATE":
			types[0] = TYPES.DATE;
			break;
		case "DWORD":
			types[0] = TYPES.DWORD;
			canBeChar = true;
			break;
		case "EVENT":
			types[0] = TYPES.EVENT;
			break;
		case "INT":
			types[0] = TYPES.INT;
			canBeInt = true;
			break;
		case "LINT":
			types[0] = TYPES.LINT;
			canBeInt = true;
			break;
		case "LREAL":
			types[0] = TYPES.LREAL;
			canBeReal = true;
			break;
		case "LWORD":
			types[0] = TYPES.LWORD;
			canBeChar = true;
			break;
		case "REAL":
			types[0] = TYPES.REAL;
			canBeReal = true;
			break;
		case "SINT":
			types[0] = TYPES.SINT;
			canBeInt = true;
			break;
		case "STRING":
			types[0] = TYPES.STRING;
			canBeString = true;
			break;
		case "TIME_OF_DAY":
			types[0] = TYPES.TIME_OF_DAY;
			break;
		case "UDINT":
			types[0] = TYPES.UDINT;
			canBeUint = true;
			break;
		case "ULINT":
			types[0] = TYPES.ULINT;
			canBeUint = true;
			break;
		case "UINT":
			types[0] = TYPES.UINT;
			canBeUint = true;
			break;
		case "USINT":
			types[0] = TYPES.USINT;
			canBeUint = true;
			break;
		case "WORD":
			types[0] = TYPES.WORD;
			canBeChar = true;
			break;
		case "WSTRING":
			types[0] = TYPES.WSTRING;
			canBeString = true;
			break;
		}
	}
	
	public void setFunc(Function func) {
		this.func = func;
	}
	
	public static boolean canConnect(Out source, In target) {
		for (TYPES s : source.types) {
			for (TYPES t : source.types) {
				if (s == t) return true;
			}
		}
		return false;
	}
	public String getPath() {
		return func.name + "." + name;
		
	}
	
	public static boolean isNumeric(String str) {
		  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
		}
	public void show() {
		System.out.println("Name = " + name + ". DataType = " + nameType + ". Comment = " + comment);
	}
	public void setCoo(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public void rename(String newName) {
		name = newName;
	}
}
