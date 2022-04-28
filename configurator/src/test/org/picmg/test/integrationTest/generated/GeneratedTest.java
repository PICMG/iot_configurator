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
	static volatile boolean hasRun = false;
	@Test
	public void robotCalls() {
		if (hasRun) return;
		new RobotThread().wait(5000)
				.wait(4000).then(SensorTabTest())
				.wait(4000).then(EffecterTabTest())
				.wait(4000).then(StateSetTabTest())
				.wait(4000).then(DeviceTabTest())
				.then(1333, RobotUtils::close)
				.run();
		hasRun = true;
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
	public RobotThread SensorTabTest()
	{
		System.out.println("Executing integration test SensorTabTest");
		return new RobotThread()
				.then(1000, ()->RobotUtils.click("#sensorTab"))
				.then(1000, ()->RobotUtils.click("#manufacturerTextfield"))
				.then(1000, ()->RobotUtils.type("/clear"))
				.then(1000, ()->RobotUtils.type("/shift"))
				.then(1000, ()->RobotUtils.type("picmg"))
				.then(1000, ()->RobotUtils.type("/unshift"))
				.then(1000, ()->RobotUtils.click("#partNumberTextField"))
				.then(1000, ()->RobotUtils.type("/clear"))
				.then(1000, ()->RobotUtils.type("1"))
				.then(1000, ()->RobotUtils.click("#descriptionTextArea"))
				.then(1000, ()->RobotUtils.type("/clear"))
				.then(1000, ()->RobotUtils.type("Sensor Information"))
				.then(1000, ()->RobotUtils.click("#digitalCheckbox"))
				.then(1000, ()->RobotUtils.click("#maxSampleRateTextfield"))
				.then(1000, ()->RobotUtils.type("/clear"))
				.then(1000, ()->RobotUtils.type("1"))
				.then(1000, ()->RobotUtils.click("#plusAccuracyTextfield"))
				.then(1000, ()->RobotUtils.type("/clear"))
				.then(1000, ()->RobotUtils.type("1"))
				.then(1000, ()->RobotUtils.click("#minusAccuracyTextfield"))
				.then(1000, ()->RobotUtils.type("/clear"))
				.then(1000, ()->RobotUtils.type("1"))
				.then(1000, ()->RobotUtils.click("#outputUnitsTextfield"))
				.then(1000, ()->RobotUtils.type("/clear"))
				.then(1000, ()->RobotUtils.type("Amps"))
				.wait(4000);
	}
	public RobotThread EffecterTabTest()
	{
		System.out.println("Executing integration test EffecterTabTest");
		return new RobotThread()
				.then(1000, ()->RobotUtils.click("#effectersTab"))
				.then(1000, ()->RobotUtils.click("#manufacturerTextfield"))
				.then(1000, ()->RobotUtils.type("/clear"))
				.then(1000, ()->RobotUtils.type("/shift"))
				.then(1000, ()->RobotUtils.type("picmg"))
				.then(1000, ()->RobotUtils.type("/unshift"))
				.then(1000, ()->RobotUtils.click("#partNumberTextField"))
				.then(1000, ()->RobotUtils.type("/clear"))
				.then(1000, ()->RobotUtils.type("1"))
				.then(1000, ()->RobotUtils.click("#descriptionTextArea"))
				.then(1000, ()->RobotUtils.type("/clear"))
				.then(1000, ()->RobotUtils.type("Effecter Information"))
				.then(1000, ()->RobotUtils.click("#digitalCheckbox"))
				.then(1000, ()->RobotUtils.click("#maxSampleRateTextfield"))
				.then(1000, ()->RobotUtils.type("/clear"))
				.then(1000, ()->RobotUtils.type("1"))
				.then(1000, ()->RobotUtils.click("#plusAccuracyTextfield"))
				.then(1000, ()->RobotUtils.type("/clear"))
				.then(1000, ()->RobotUtils.type("1"))
				.then(1000, ()->RobotUtils.click("#minusAccuracyTextfield"))
				.then(1000, ()->RobotUtils.type("/clear"))
				.then(1000, ()->RobotUtils.type("1"))
				.then(1000, ()->RobotUtils.click("#inputUnitsTextfield"))
				.then(1000, ()->RobotUtils.type("/clear"))
				.then(1000, ()->RobotUtils.type("Amps"))
				.wait(4000);
	}
	public RobotThread StateSetTabTest()
	{
		System.out.println("Executing integration test StateSetTabTest");
		return new RobotThread()
				.then(1000, ()->RobotUtils.click("#stateSetsTab"))
				.then(1000, ()->RobotUtils.click("#stateSetVendorNameTextField"))
				.then(1000, ()->RobotUtils.type("/clear"))
				.then(1000, ()->RobotUtils.type("/shift"))
				.then(1000, ()->RobotUtils.type("picmg"))
				.then(1000, ()->RobotUtils.type("/unshift"))
				.then(1000, ()->RobotUtils.click("#stateSetVendorIANATextField"))
				.then(1000, ()->RobotUtils.type("/clear"))
				.then(1000, ()->RobotUtils.type("1"))
				.then(1000, ()->RobotUtils.click("#stateSetIdTextField"))
				.then(1000, ()->RobotUtils.type("/clear"))
				.then(1000, ()->RobotUtils.type("1"))
				.wait(4000);
	}
	public RobotThread DeviceTabTest()
	{
		System.out.println("Executing integration test DeviceTabTest");
		return new RobotThread()
				.then(1000, ()->RobotUtils.click("#deviceTab"))
				.then(1000, ()->RobotUtils.click("#newDeviceMenu"))
				.wait(4000);
	}
}