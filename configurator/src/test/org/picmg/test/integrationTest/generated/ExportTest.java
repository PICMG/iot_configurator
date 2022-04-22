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
public class Export extends Application
{
	@Test
	public void robotCalls() {
		TestMain().then(()->Test2Main()).run();
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
			Scene scene = new Scene(root, 1024, 870);
			stage.setTitle("PICMG Configurator");
			stage.setScene(scene);
			stage.show();
			
		
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}
	public RobotThread TestMain()
	{
		System.out.println("Executing integration test TestMain");
		return new RobotThread()
				.then(1000, ()->RobotUtils.click("#effectersTab"))
				.then(1000, ()->RobotUtils.click("#auxUnitChoicebox"))
				.then(1000, ()->RobotUtils.check("#auxUnitChoicebox","test"))
				.wait(8000);
	}
	public RobotThread Test2Main()
	{
		System.out.println("Executing integration test Test2Main");
		return new RobotThread()
				.then(1000, ()->RobotUtils.click("#sensorTab"))
				.then(1000, ()->RobotUtils.type("nonsense"))
				.wait(8000);
	}
}