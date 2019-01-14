package workschedulehandler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class WorkScheduleHandler extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        try {
        Parent root = FXMLLoader.load(getClass().getResource("/workschedulehandler/View.fxml"));
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("Work Time Schedule Handler");
        stage.setResizable(false);
        stage.show();
        } catch(Exception ex) {
            System.out.println("Problem in WorkScheduleHandler/start: " + ex);
        }
         
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
