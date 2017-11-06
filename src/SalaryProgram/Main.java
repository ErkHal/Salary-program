package SalaryProgram;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/SalaryProgram/chooseFile.fxml"));

        primaryStage.setTitle("Salary Program");
        primaryStage.setResizable(false);
        Scene fileChoosingScene = new Scene(root, 700, 440);
        fileChoosingScene.getStylesheets().add("SalaryProgram/css/style.css");
        primaryStage.setScene(fileChoosingScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
