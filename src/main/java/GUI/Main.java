package GUI;

import BackEND.App;
import BackEND.RankDiscordChecker;
import BackEND.StatsChecker;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Discord Bot");
        primaryStage.setScene(new Scene(root, 1080, 720));
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(windowEvent -> {
            if (App.isEnable()) {
                RankDiscordChecker.writeList();
                StatsChecker.writeList();
                System.out.println("bot-disable");
                App.disableBot();
            }
            try {
                stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.exit(0);
        });
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}