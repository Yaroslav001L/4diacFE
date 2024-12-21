package main.java.instructions;

public class MainFunction extends Function { // этим элементом может быть только элемент "START"
	public static final String START = "START";
	public static final String TYPE = "E_RESTART";
	public static final double X = 200.0;
	public static final double Y = 1000.0;
	
	private static MainFunction start = null;

	public MainFunction(InOut[] InOutList, double x, double y, String comment, String name, String type) {
		super(InOutList, x, y, comment, name, type);
	}
	public static void setMainFunc(MainFunction mfunc) {
		start = mfunc;
	}
	public static MainFunction getMainFunc() {
		return start;
	}

}
