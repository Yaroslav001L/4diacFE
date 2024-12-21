package main.java.instructions;

import java.util.ArrayList;

import main.java.application.FunctionInList;
import main.java.control.MainSceneController;
import main.java.paint.PainterP;

public class Function{ 
	public String comment;
	public String name;
	public String type;
	public FunctionInList fil;
	private static ArrayList<Function> funcList = new ArrayList<>();
	
	public InOut[] InOutList;
	public double x, y;
	
	public Function(InOut[] InOutList, double x, double y, String comment, String name, String type){
		this.x = x;
		this.y = y;
		this.InOutList = InOutList;
		for (InOut pin : InOutList) {
			pin.setFunc(this);
		}
		this.comment = comment;
		this.name = name;
		this.type = type;
	}
	public static Function create(InOut[] InOutList, double x, double y, String comment, String name, String type) {
		if (existSimilarName(name)) return null;
		Function func;
		if (name.equals(MainFunction.START)) {
		func = new MainFunction(InOutList, x, y, comment, name, type);
		MainFunction.setMainFunc((MainFunction)func);
		}
		func = new Function(InOutList, x, y, comment, name, type);
		funcList.add(func);
		func.setPinsCoo();
		return func;
	}
	public void deleteFunc() {
		this.disconnectAllWires();
		funcList.remove(this);
		fil.func = null;
	}
	public void disconnectAllWires() {
		for (int i = 0; i < Wire.wires().size(); i++) {
			for (InOut io : InOutList) {
				if (Wire.wires().get(i).source.hashCode() == io.hashCode() || Wire.wires().get(i).target.hashCode () == io.hashCode()) {
					Wire.wires().get(i).disconnect();
					i--;
					break;
				}
			}
			
		}
	}
	
	public static void resetList() {
		funcList = new ArrayList<>();
	}
	
	public static Function searchFunc(String name) {
		for (Function func : funcList) {
			if (func.name.equals(name)) return func;
		}
		return null;
	}
	public static InOut searchPin(String path) {
		String[] names = path.split("\\.");
		return searchPin(searchFunc(names[0]), names[1]);
	}
	public static InOut searchPin(Function func, String namePin) {
		for (InOut pin : func.InOutList) {
			if (pin.name.equals(namePin)) return pin;
		}
		return null;
	}
	public boolean setParam(String namePin, String val) {
		In pin = (In)searchPin(this, namePin);
		if (pin == null) return false;
		return pin.setValue(val);
	}
	public static void showAll() {
		for (Function func : funcList) {
			func.show();
		}
	}
	public void show() {
		System.out.println("Name = " + name + ". Type = " + type + ". Comment = " + comment + ". PinList: ");
		for (InOut pin : InOutList) {
			pin.show();
		}
		System.out.println("");
	}
	public static ArrayList<Function> getFuncList(){
		return funcList;
	}
	public static Function getFuncFromList(int id){
		return funcList.get(id);
	}
	public boolean inOutRename(InOut io, String newName) {
		for (InOut p : InOutList) {
			if (p.name.equals(newName)) return false;
		}
		io.rename(newName);
		return true;
	}
	public boolean changeX(String value) {
		if (!InOut.isNumeric(value)) return false;
		x = Double.parseDouble(value);
		return true;
	}
	public boolean changeY(String value) {
		if (!InOut.isNumeric(value)) return false;
		y = Double.parseDouble(value);
		return true;
	}
	public boolean rename(String value) {
		if (existSimilarName(value)) return false;
		name = value;
		return true;
	}
	public static boolean existSimilarName(String name) {
		for (Function func : funcList) {
			if (func.name.equals(name)) return true;
		}
		return false;
	}
	public void setPinsCoo() {
		double xfix = 0.8, yfix = 1.15;
		double baseOffset = (PainterP.PIN_SIZE - PainterP.FONT_SIZE)/2;
		double funcX, funcY, funcWidth, funcHeight;
		double x = 0, y = 0;
		int levelBlock; // функция будет делится по уровням, количество уровней зависит от числа входных и выходных данных и событий
		int[] pins_ei_eo_di_do = {0, 0, 0, 0}; // количество пинов: eventinput, eventoutput...
		
		for (InOut io : InOutList) {
			if (io instanceof In) {
				if (io.types.length > 1 || io.types[0] != InOut.TYPES.EVENT) pins_ei_eo_di_do[2]++;
				else pins_ei_eo_di_do[0]++;
			}
			else {
				if (io.types.length > 1 || io.types[0] != InOut.TYPES.EVENT) pins_ei_eo_di_do[3]++;
				else pins_ei_eo_di_do[1]++;
			}
		}
		levelBlock = Math.max(pins_ei_eo_di_do[0], pins_ei_eo_di_do[1]) + 1 + Math.max(pins_ei_eo_di_do[2], pins_ei_eo_di_do[3]); // верхние пины, название функции, нижние пины
		pins_ei_eo_di_do = new int[4];
		funcX = PainterP.cvt(this.x);
		funcY = PainterP.cvt(this.y);
		funcWidth = this.type.length() * PainterP.FONT_SIZE; // ширина функции зависит от ширины сообщения
		funcHeight = levelBlock * PainterP.PIN_SIZE;
		
		InOut[] io = InOutList;
		for (int i = 0; i < InOutList.length; i++) {
			if (io[i] instanceof In) {
				if (io[i].types.length > 1 || io[i].types[0] != InOut.TYPES.EVENT) {
					x = baseOffset + funcX;
					y = -pins_ei_eo_di_do[2]*PainterP.PIN_SIZE*yfix - baseOffset + funcY + funcHeight;
					pins_ei_eo_di_do[2]++;
				}
				else {
					x = baseOffset + funcX;
					y = funcY + (pins_ei_eo_di_do[0] + 1)*PainterP.PIN_SIZE/yfix + baseOffset;
					pins_ei_eo_di_do[0]++;
				}
			}
			else {
				if (io[i].types.length > 1 || io[i].types[0] != InOut.TYPES.EVENT) {
					x = funcWidth - io[i].name.length() * PainterP.FONT_SIZE * xfix - baseOffset + funcX;
					y = -pins_ei_eo_di_do[3]*PainterP.PIN_SIZE*yfix - baseOffset + funcY + funcHeight;
					pins_ei_eo_di_do[3]++;
				}
				else {
					x = funcWidth - io[i].name.length() * PainterP.FONT_SIZE * xfix - baseOffset + funcX;
					y = funcY + (pins_ei_eo_di_do[1] + 1)*PainterP.PIN_SIZE/yfix + baseOffset;
					pins_ei_eo_di_do[1]++;
				}
			}
		io[i].setCoo(x, y);
		} 
	}
}
