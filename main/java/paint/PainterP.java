package main.java.paint;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import main.java.control.MainSceneController;
import main.java.instructions.Function;
import main.java.instructions.In;
import main.java.instructions.InOut;
import main.java.instructions.Wire;

public class PainterP {
	double width;
	double height;
	public Canvas canvas;
	public GraphicsContext gc;
	
	public PainterP(double width, double height) {
		this.width = width;
		this.height = height;
		canvas = new Canvas(width,height);
		gc = canvas.getGraphicsContext2D();
	}
	public static final int FONT_SIZE = 16;
	public static final double PIN_SIZE = 20.0;
	public static final double MOD_TO_FIT = 0.25;
	public static final double SPARE_SPACE = 200;
	public static final double INITAL_SPACE = 1000;
	
	public static double cvt(double value) { // конвертируем значения, чтобы были не такие огромные
		return value * MOD_TO_FIT;
	}
	public void clear() {
		canvas = new Canvas(width, height);
	}
	public void repaint() {
		canvas = new Canvas(width, height);
		gc = canvas.getGraphicsContext2D();
		for (Function func : Function.getFuncList()) {
			func.setPinsCoo();
			paintFunc(func);
		}
		for (Wire wire : Wire.wires()) paintWire(wire);
	}
	public void paintWire(Wire wire){
		double offset = 10; // на сколько выходит линия из пина
		double offsetrightX = wire.source.name.length()*FONT_SIZE*0.8;
		double offsetY = -FONT_SIZE/2;
		
		final double x1 = wire.source.x+offsetrightX, y1 = wire.source.y+offsetY, x2 = wire.target.x, y2 = wire.target.y+offsetY;
		double cx = x1, cy = y1, nx = x1, ny = y1; // текущие и следующие
		// выходим всегда из выхода, идём всегда ко входу
		nx = x1+offset;
		gc.strokeLine(cx, cy, nx, ny);
		cx = nx;
		if (x1+offset > x2-offset) {
			ny = cy+PIN_SIZE/2;
			gc.strokeLine(cx, cy, nx, ny);
			cy = ny;
			
			nx = x2-offset;
			gc.strokeLine(cx, cy, nx, ny);
			cx = nx;
			
			ny = y2;
			gc.strokeLine(cx, cy, nx, ny);
			cy = ny;
			
			nx = x2;
			gc.strokeLine(cx, cy, nx, ny);
		}
		else {
			Random rng = new Random(); // с этим узлы друг на друга будут реже накладываться
			if (cy > y2) ny = rng.nextDouble(y2, cy);
			else if (cy < y2) ny = rng.nextDouble(cy, y2);
			else ny = y2;
			gc.strokeLine(cx, cy, nx, ny);
			cy = ny;
			
			nx = x2-offset;
			ny = y2;
			gc.strokeLine(cx, cy, nx, ny);
			cx = nx;
			cy = ny;
			
			nx = x2;
			gc.strokeLine(cx, cy, nx, ny);
		}
	}
	
	public void paintFunc(Function func) {
		double xfix = 0.8, yfix = 1.15;
		double baseOffset = (PIN_SIZE - FONT_SIZE)/2;
		double funcX, funcY, funcWidth, funcHeight;
		double nameFuncX, nameFuncY;
		double typeFuncX, typeFuncY;
		int levelBlock; // функция будет делится по уровням, количество уровней зависит от числа входных и выходных данных и событий
		int[] pins_ei_eo_di_do = {0, 0, 0, 0}; // количество пинов: eventinput, eventoutput...
		for (InOut io : func.InOutList) {
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
		
		funcX = cvt(func.x);
		funcY = cvt(func.y);
		funcWidth = func.type.length() * FONT_SIZE; // ширина функции зависит от ширины сообщения
		funcHeight = levelBlock * PIN_SIZE;
		
		nameFuncX = funcX + funcWidth/2 - func.name.length()*FONT_SIZE*xfix/2;
		nameFuncY = funcY - FONT_SIZE/2;
		
		typeFuncX = funcX + funcWidth/2 - func.type.length()*FONT_SIZE*xfix/2;
		typeFuncY = funcY + (Math.max(pins_ei_eo_di_do[0], pins_ei_eo_di_do[1])+1)*FONT_SIZE*yfix + baseOffset;

		Font font = Font.font("Arial", FontWeight.NORMAL, FontPosture.REGULAR, FONT_SIZE);
		gc.setFont(font);
		gc.strokeRect(funcX, funcY, funcWidth, funcHeight);
		gc.fillText(func.name, nameFuncX, nameFuncY);
		gc.fillText(func.type, typeFuncX, typeFuncY);
		for (int i = 0; i < func.InOutList.length; i++) {
			gc.fillText(func.InOutList[i].name, func.InOutList[i].x, func.InOutList[i].y);
		}
	}
	
	
	public void showBounds( ScrollPane scrollPane) {

        double hValue = scrollPane.getHvalue();
        double vValue = scrollPane.getVvalue();
        double width = scrollPane.viewportBoundsProperty().get().getWidth();
        double height = scrollPane.viewportBoundsProperty().get().getHeight();

        double x = (scrollPane.getContent().getBoundsInParent().getWidth() - width) * hValue;
        double y = (scrollPane.getContent().getBoundsInParent().getHeight() - height) * vValue;

        //System.out.println( "x=" + x + ", y=" + y + ", width=" + width + ", height=" + height);

        // demo: draw a line of the canvas size and a rectangle of the viewport size => the rectangle must always be in the center
        double size = 80;
        gc.clearRect(x, y, width, height);

        gc.beginPath();
        gc.moveTo(0, 0);
        gc.lineTo(canvas.getWidth(), canvas.getHeight());
        gc.closePath();
        gc.stroke();

        gc.fillRect(x + (width-size) / 2, y + (height-size) / 2, size, size);

    }
	
	public boolean checkBorders() {
		double maxX = 0, maxY = 0;
		for (Function func : Function.getFuncList()) {
			maxX = Math.max(maxX, func.x);
			maxY = Math.max(maxY, func.y);
		}
		if (maxX + SPARE_SPACE > width || maxY + SPARE_SPACE > height) {
			return fitCanvas(maxX + SPARE_SPACE, maxY + SPARE_SPACE);
			
		}
		else if (maxX - SPARE_SPACE < width && width > INITAL_SPACE || maxY - SPARE_SPACE < height && height > INITAL_SPACE) {
			return fitCanvas(Math.max(maxX + SPARE_SPACE, INITAL_SPACE), Math.max(maxY + SPARE_SPACE, INITAL_SPACE));
		}
		return false;
	}
	
	public boolean fitCanvas(double maxX, double maxY) {
		width = maxX;
		height = maxY;
		//MainSceneController.requestToLink(this);
		repaint();
		return true;
	}

	
}
