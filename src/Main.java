import java.io.IOException;
import java.util.ArrayList;

import org.omg.CORBA.TIMEOUT;


import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Main extends Application {
//	public boolean s = true;
//	@FXML
//	Button btnStart;
//	@FXML
//	Line gLine;
	@FXML
	Button btnSimulate;
	@Override
	public void start(Stage stage) throws Exception {
	
		try {
		//	Parent root = FXMLLoader.load(getClass().getResource("/ui/g.fxml"));
			Parent root = FXMLLoader.load(getClass().getResource("/ui/fxml/Login.fxml"));
			
			stage.setResizable(false);
			stage.getIcons().add(new Image("/ui/resources/logo.png"));
			stage.setTitle("Null Simulator");
			
			Scene scene = new Scene(root);
			root.getStylesheets().add(getClass().getResource("/ui/css/Login.css").toExternalForm());
			scene.getStylesheets().add(getClass().getResource("/ui/css/Login.css").toExternalForm());
			
			
			stage.setScene(scene);
			
			stage.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	public static void main(String[] args) {
		launch(args);

	}

}
