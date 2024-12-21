package main.java.application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import main.java.instructions.Wire;

public class WireInList extends HBox {
	public Wire wire;
	
	Label l_conn;
	Button b_delete;
	
	public WireInList(Wire wire) {
		this.wire = wire; 
		wire.wil = this;
		l_conn = new Label(wire.source.getPath() + " -> " + wire.target.getPath());
		b_delete = new Button("Delete");
		
		b_delete.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
            	wire.disconnect();
            }
        });
		
		this.getChildren().add(l_conn);
		this.getChildren().add(b_delete);
	}
}
