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
            Scene scene = new Scene(root, 1024, 870);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }

    }

    public static void main(String[] args) {
        //TestReader.getInstance().read("Name,Test1:File,topTabScene.fxml:Steps,Start:Name,Test Box Test:Click,ID:Type,Temp:Check,ID,Data:Steps,End:Steps,Start:Name,Test Box BRO:Click,ID:Type,Temp:Check,ID,Data:Steps,End");
        launch();
    }
}
