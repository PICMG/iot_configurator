package org.picmg.test.TestMaker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestMakerGUi extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage temp = new Stage();
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/testMakerGUI.fxml"));
            Scene scene = new Scene(root, 1024, 570);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }

    }

    public static void main(String[] args) {
        launch();
    }
}
