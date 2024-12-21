package main.java.instructions;

import java.util.ArrayList;

import main.java.application.WireInList;

public class Wire {
	private static ArrayList<Wire> wires = new ArrayList<>();
	public In target;
	public Out source;
	public WireInList wil;
	public Wire(Out out, In in) {
		out.connect(in);
		in.connect(out);
		target = in;
		source = out;
	}
	public static Wire connect(Out out, In in) {
		for (Wire w : wires) {
			if (w.target.equals(in) && w.source.equals(out) || !InOut.canConnect(out, in)) {
				return null;
			}
		}
		Wire wire = new Wire(out, in);
		wires.add(wire);
		return wire;
	}
	public void disconnect() {
		wires.remove(this);
		source.disconnect(target);
		target.disconnect(source);
		wil.wire = null;
		wil = null;
	}
	public static Wire getConnectedWire(Out out, In in) {
		for (Wire w : wires) {
			if (w.target.equals(in) && w.source.equals(out)) {
				return w;
			}
		}
		return null;
	}
	public static ArrayList<Wire> wires() {
		return wires;
	}
	public static void resetList() {
		wires = new ArrayList<>();
	}
}
