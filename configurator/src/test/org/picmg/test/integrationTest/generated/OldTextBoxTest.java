package org.picmg.test.integrationTest.generated;
/**
	This is a generated integration test using the testMaker application
*/
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.picmg.test.integrationTest.RobotThread;
import org.picmg.test.integrationTest.RobotUtils;
import java.io.IOException;

public class OldTextBoxTest extends Application
{
	public void robotCalls()
	{
		test();
	}
	@Override
	public void start(Stage stage)
	{
		Parent root;
		try 
		{
			root = FXMLLoader.load(getClass().getClassLoader().getResource("topTabScene.fxml"));
			Scene scene = new Scene(root, 1024, 768);
			stage.setTitle("PICMG Configurator");
			stage.setScene(scene);
			stage.show();
			robotCalls();
		
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}

	@BeforeClass
	public static void setup() {
		launch();
	}

	@Test
	public void test()
	{
		System.out.println("Executing integration test test");
		new RobotThread()			.then(400, ()->RobotUtils.click("#effectersTab"))
			.then(400, ()->RobotUtils.click("#stepCheckbox"))
			.then(400, ()->RobotUtils.check("#stepCheckbox","true"))
			.then(400, ()->RobotUtils.click("#stepCheckbox"))
			.then(400, ()->RobotUtils.check("#stepCheckbox","false"))
			.then(400, ()->RobotUtils.click("#descriptionTextArea"))
			.then(400, ()->RobotUtils.type("Test"))
			.then(400, ()->RobotUtils.check("#descriptionTextArea","Test"))
			.wait(5000)
			.run();
		Assert.assertTrue(true);
	}
}