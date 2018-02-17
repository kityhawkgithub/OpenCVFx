package wxjump;

import org.opencv.core.Core;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class WxRun extends Application 
{

	public static void main(String[] args) 
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		try
		{
			// load the FXML resource
			FXMLLoader loader = new FXMLLoader(getClass().getResource("FirstJFX.fxml"));
			// store the root element so that the controllers can use it
			AnchorPane root = (AnchorPane) loader.load();
			// create and style a scene
			Scene scene = new Scene(root, 940, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			// create the stage with the given title and the previously created
			// scene
			primaryStage.setTitle("JavaFX meets OpenCV");
			primaryStage.setScene(scene);
			// show the GUI
			primaryStage.show();

			// set the proper behavior on closing the application
			//WxController controller = loader.getController();
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					//controller.setClosed();
				}
			}));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}

}
