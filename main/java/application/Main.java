package main.java.application;
	
import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import main.java.control.MainSceneController;
import main.java.instructions.Function;
import main.java.instructions.Wire;
import main.java.paint.PainterP;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;

public class Main extends Application {
	public static String saveDirectory = "";
	public static Main main;
	
	public static final String NAME = "4diacFE";
	
	public static ProjParams proj;
	
	VBox root;
	Scene scene;
	MenuBar menu;
	public static MainSceneController control;
	private static Stage primaryStage;
	@Override
	public void start(Stage primaryStage) {
		try {			
			this.primaryStage = primaryStage;
			main = this;
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("/base.fxml"));
			control = (new MainSceneController());
			loader.setController(control);
			root = loader.load();
			scene = new Scene(root);
			
			primaryStage.setTitle(NAME);
			primaryStage.setResizable(false);
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			init(false);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			showError(e);
		}
	}
	public void exit() {
		exit();
	}
	MainSceneController getConttoller() {
		return control;
	}
	
	public void init(boolean createMainFunc) {
		saveDirectory = "";
		Function.resetList();
		Wire.resetList();
		proj = new ProjParams();
		control.linkMap(new PainterP(2000, 2000));
		if (createMainFunc) control.addMainFB();
		// new prog
	}
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public static void main(String[] args) {
		
		launch(args);
	}
	
	private void showError(Exception e) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Exception Dialog");
		alert.setHeaderText("Look, an Exception Dialog. Go to fix bugs.");
		alert.setContentText("Error!Error!Error!Terror!");


		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}
}
