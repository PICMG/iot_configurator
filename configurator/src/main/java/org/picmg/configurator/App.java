//*******************************************************************
//    App.java
//
//    More information on the PICMG IoT data model can be found within
//    the PICMG family of IoT specifications.  For more information,
//    please visit the PICMG web site (www.picmg.org)
//
//    Copyright (C) 2020,  PICMG
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.
//
package org.picmg.configurator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.picmg.test.TestMaker.Test;
import org.picmg.test.TestMaker.TestWriter;

public class App extends Application {	
	
	private static Scene scene;
	private static boolean splash = true;
	/**
	 * isInteger()
	 * A helper that returns true if the string parameter represents
	 * a valid base-10 signed integer
	 * @param num - the string to check
	 * @return - true if the string is a valid integer
	 */
	static boolean isInteger(String num) {
		try {
			Integer.parseInt(num);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * isUnsignedInteger()
	 * A helper that returns true if the string parameter represents
	 * a valid base-10 unsigned integer
	 * @param num - the string to check
	 * @return - true if the string is a valid integer
	 */
	static boolean isUnsignedInteger(String num) {
		try {
			Integer.parseInt(num);
			if (num.charAt(0) != '-') return true;
		} catch (NumberFormatException e) {}
		return false;
	}

	/**
	 * isFloat()
	 * A helper that returns true if the string parameter represents
	 * a valid double-precision floating-point number.
	 * @param num - the string to check
	 * @return - true if the string is a valid integer
	 */
	static boolean isFloat(String num) {
		try {
			Double.parseDouble(num);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}


	@Override
    public void start(Stage stage) {
		Stage temp = new Stage();
		if(splash == true){
			try{
				Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("topTabScene.fxml"));
				Scene scene = new Scene(root, 1024, 768);
				stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("picmg_logo.png")));
				stage.setTitle("PICMG Configurator");
				stage.setScene(scene);
				Parent splashScreen = FXMLLoader.load(getClass().getClassLoader().getResource("splashScreen.fxml"));
				Scene splash = new Scene(splashScreen, 500, 500);
				temp.initStyle(StageStyle.TRANSPARENT);
				temp.setScene(splash);
				temp.show();
			}catch(Exception e){
				e.printStackTrace();
			}
			Thread thread = new Thread(()->{
				try {
					Thread.sleep(3000);
					Platform.runLater(()->{
						temp.close();
						try {
							stage.show();
							stage.setAlwaysOnTop(true);
						} catch (Exception e) {
							System.out.println(e);
						}
					});
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
			thread.start();
		}else{
			Parent root;
			try {
				root = FXMLLoader.load(getClass().getClassLoader().getResource("topTabScene.fxml"));
				Scene scene = new Scene(root, 1024, 768);
				stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("picmg_logo.png")));
				stage.setTitle("PICMG Configurator");
				stage.setScene(scene);
				stage.show();
			} catch (IOException e) {
				System.out.println(e);
			}
		}


	}

	/**
	 * Launch the app
	 * @param args - the command line arguments for the program
	 */
	public static void main(String[] args) {
		Test temp = new Test();
		temp.setName("Testing");
		TestWriter.getInstance().createTest(temp);
		launch();
    }
}
