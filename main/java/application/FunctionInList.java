package main.java.application;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.java.instructions.Function;
import main.java.instructions.InOut;

public class FunctionInList extends TitledPane{
	public Function func;
	
	VBox info;
	HBox hb_up, hb_down;
	TextField tf_nameFunc, tf_x, tf_y;
	Button b_delete;
	
	public FunctionInList(Function func) {
		this.func = func;
		func.fil = this;
		
		info = new VBox();
		hb_up = new HBox();
		hb_down = new HBox();
		tf_nameFunc = new TextField(func.name);
		tf_x = new TextField(((Double)func.x).toString());
		tf_y = new TextField(((Double)func.y).toString());
		b_delete = new Button("Delete");
		
		tf_nameFunc.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!func.rename(newValue)) tf_nameFunc.setText(oldValue);
			else resetName();
			Main.control.repaintMap();
		});
		tf_x.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!func.changeX(newValue)) tf_x.setText(oldValue);
			Main.control.repaintMap();
		});
		tf_y.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!func.changeY(newValue)) tf_y.setText(oldValue);
			Main.control.repaintMap();
		});
		
		hb_up.getChildren().add(tf_nameFunc);
		hb_up.getChildren().add(b_delete);
		hb_down.getChildren().add(tf_x);
		hb_down.getChildren().add(tf_y);
		info.getChildren().add(hb_up);
		info.getChildren().add(hb_down);
		
		this.setText(func.name + " (" + func.type + ")");
		for (InOut io : func.InOutList) info.getChildren().add(new PinInList(io));
		this.setContent(info);
		this.setExpanded(false);
		
		b_delete.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
            	func.deleteFunc();
            	Main.control.updateInterface();
            	Main.control.repaintMap();
            }
        });
	}
	private void resetName() {
		this.setText(func.name + " (" + func.type + ")");
		
	}
	public boolean checkSource() {
		if (func == null) return false;
		return true;
	}
}
