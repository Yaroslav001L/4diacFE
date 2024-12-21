package main.java.instructions;

import java.util.ArrayList;

public class Out extends InOut {
	
public Out(String comment, String name, String type) {
		super(comment, name, type);
	}

public ArrayList<In> targets = new ArrayList<>();
	
	protected void connect(In newTarget) {
		targets.add(newTarget);
	}
	
	protected void disconnect(In target) {
		targets.remove(target);
	}
	@Override
	public void show() {
		System.out.print("Output. ");
		super.show();
		if (!targets.isEmpty()) {
			System.out.print("Targets: ");
			for (In pin : targets) {
				System.out.print(pin.getPath() + " ");
			}
		}
	}
}
