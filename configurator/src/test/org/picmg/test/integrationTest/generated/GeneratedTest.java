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
public class GeneratedTest extends Application
{
	static boolean hasRun = false;
	@Test
	public void robotCalls() {
		if (hasRun) return;
		hasRun = true;
		RobotThread runner = new RobotThread().wait(340).then(ClickAuxRate()).wait(4550).then(ClickSensorsAndEffecters()).wait(4000);
		runner.run();
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
			robotCalls();
		
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}
	public RobotThread ClickAuxRate()
	{
		return new RobotThread()
				.then(3000, ()-> System.out.println("Executing integration test ClickAuxRate"))
				.then(3000, ()->RobotUtils.click("#sensorTab"))
				.then(1000, ()->RobotUtils.click("#manufacturerTextfield"))
				.wait(8000);
	}
	public RobotThread ClickSensorsAndEffecters()
	{
//		System.out.println("Executing integration test ClickSensorsAndEffecters");
//		return new RobotThread();
		return new RobotThread()
				.then(1000, ()->RobotUtils.click("#effectersTab"))
				.then(1000, ()->RobotUtils.click("#sensorTab"))
				.then(1000, ()->RobotUtils.click("#effectersTab"))
				.then(1000, ()->RobotUtils.click("#sensorTab"))
				.then(1000, ()->RobotUtils.click("#sensorTab"))
				.then(1000, ()->RobotUtils.click("#effectersTab"))
				.wait(8000);
	}
}