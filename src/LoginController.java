import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class LoginController {
			
	@FXML
	Button select;
	 Stage stage;
	
	public void loginServer() throws Exception{
		Controller.isServer = true;
			stage = new Stage();
		 
			
			//Pane root = loader.load(getClass().getResource("/ui/g.fxml").openStream());
			FXMLLoader myLoader = new FXMLLoader(getClass().getResource("/ui/fxml/Workspace.fxml"));
			
			Controller userController = (Controller) myLoader.getController();
			
			Pane myPane = (Pane) myLoader.load();            
		    Scene scene = new Scene(myPane);
		    stage.setTitle("Null Simulator");
		    
		    scene.getStylesheets().add(getClass().getResource("/ui/css/Workspace.css").toExternalForm());
		    stage.getIcons().add(new Image("/ui/resources/logo.png"));
		   
		    stage.setResizable(false);
		    stage.setScene(scene);
		    
		    stage.show();
		    
		    
		    /*
		     * !- Down where .stopServer() is called must be changed.
		     * !- In Controller class "stopServer" method is visibile because it's static
		     * !- That is not safe. Also that method has it's object sm " ServerMain that
		     * !- ..also is required to be changed to static, which again is not safe
		     * !- But right now this way I'm able to call stop server from THIS class.
		     */
		    
		    
		    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		          public void handle(WindowEvent we) {
		              System.out.println("Server is closing");
		              try {
		            	  Controller.stopSimulator();
					} catch (Exception e) {
						System.out.println("Server side: stopSimulator failed");
					}
		              try{
		            	  
		            	  Controller.stopServer();
		            	  
		            	  System.out.println("Simulation threads destroyed");
		              }catch(Exception e){
		            	  System.out.println("Server is closing yet it was never run");
		              }
		          }
		      });        
	}
	
	public void loginClient()throws Exception{
		Controller.isServer = false;
		stage = new Stage();
		FXMLLoader myLoader = new FXMLLoader(getClass().getResource("/ui/fxml/Workspace.fxml"));
		Controller userController = (Controller) myLoader.getController();
		
		Pane myPane = (Pane) myLoader.load();            
	    Scene scene = new Scene(myPane);
	    stage.setTitle("Null Simulator");
	    
	    scene.getStylesheets().add(getClass().getResource("/ui/css/Workspace.css").toExternalForm());
	    stage.getIcons().add(new Image("/ui/resources/logo.png"));
	   
	    stage.setScene(scene);
	    stage.setResizable(false);
	    stage.show();
	    
	    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	              System.out.println("Transfer is closing");
	              try {
	            	  Controller.stopTransfer();
				} catch (Exception e) {
					System.out.println("Client: Stopping Transfer Failed!");
				}
	              try{
	            	 
	            	  Controller.stopSimulator();
	            	  System.out.println("Simulation threads destroyed");
	              }catch(Exception e){
	            	  System.out.println("StopThreads won't work!");
	              }
	          }
	      });    
	  
	    
	}

}
