package main.java.application;

import main.java.instructions.In;
import main.java.instructions.InOut;
import main.java.instructions.Out;

public class StateConnect {
	public static In in = null;
	public static Out out = null;
	
	public static void initConn(InOut io) {
		if (io instanceof In) {
			if (in == null) {
				in = (In)io;
			}
			else {
				in = null;
			}
		}
		else {
			if (out == null) {
				out = (Out)io;
			}
			else {
				out = null;
		}
	}
		
		if (in != null && out != null) {
			Main.control.addConn(out, in);
			in = null;
			out = null;
		}
		updateLabel();
	}
	public static void updateLabel() {
		String s = "";
		if (out != null) s += out.getPath();
		else s += " ...";
		s += " -> ";
		if (in != null) s += in.getPath(); 
		else s += "... ";
		if (in == null && out == null) s = "State";
		Main.control.state.setText(s);
	}
}
