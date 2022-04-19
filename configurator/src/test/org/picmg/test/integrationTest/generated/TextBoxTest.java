package org.picmg.test.integrationTest.generated;
/**
	This is a generated integration test using the testMaker application
*/
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.picmg.test.integrationTest.RobotThread;
import org.picmg.test.integrationTest.RobotUtils;
import java.io.IOException;
public class TextBoxTest extends Application
{
	@Test
	public void robotCalls() {
		Sensorscanbemodified().run();
	}
	@BeforeClass
	public static void setup() {
		launch();
	}

	@Override
	public void start(Stage stage) {
		Parent root;
		try 
		{
			root = FXMLLoader.load(getClass().getClassLoader().getResource("topTabScene.fxml"));
			Scene scene = new Scene(root, 1024, 768);
			stage.setTitle("PICMG Configurator");
			stage.setScene(scene);
			stage.show();
			
		
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}
	public RobotThread Sensorscanbemodified()
	{
		System.out.println("Executing integration test Sensorscanbemodified");
		return new RobotThread()
				.then(1000, ()->RobotUtils.click("#sensorsTab"))
				.then(1000, ()->RobotUtils.click("#stepCheckbox"))
				.then(1000, ()->RobotUtils.check("#stepCheckbox","true"))
				.then(1000, ()->RobotUtils.click("#stepCheckbox"))
				.then(1000, ()->RobotUtils.check("#stepCheckbox","false"))
				.then(1000, ()->RobotUtils.click("#descriptionTextArea"))
				.then(1000, ()->RobotUtils.type("Test"))
				.then(1000, ()->RobotUtils.check("#descriptionTextArea","Test"))
				.wait(8000);
	}
}