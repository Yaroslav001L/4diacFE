package main.java.application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import main.java.instructions.*;

public class PinInList extends VBox{
	public InOut pin = null;
	
	HBox hb_up, hb_down;
	TextField tf_value;
	Label l_nameType, l_name;
	Button b_connDisconn;
	
	public PinInList(InOut pin) {
		this.pin = pin;
		
		hb_up = new HBox();
		hb_down = new HBox();
		
		l_name = new Label(pin.name);
		hb_up.getChildren().add(l_name);
		
		l_nameType = new Label("(" + pin.nameType + ")");
		hb_up.getChildren().add(l_nameType);
		
		b_connDisconn = new Button("Connect");
		hb_down.getChildren().add(b_connDisconn);
		
		b_connDisconn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
            	StateConnect.initConn(pin);
            }
        });
		
		if (pin instanceof In && pin.types[0] != InOut.TYPES.EVENT) {
			tf_value = new TextField(((In)pin).getNonNullValue());
			tf_value.textProperty().addListener((observable, oldValue, newValue) -> {
			    if (!((In)pin).setValue(newValue)) tf_value.setStyle("-fx-border-color: red;");
			    else tf_value.setStyle("-fx-border-color: white;");
			});
			hb_down.getChildren().add(tf_value);
		}
		
		this.getChildren().add(hb_up);
		this.getChildren().add(hb_down);
		
	}
}
