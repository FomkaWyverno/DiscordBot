package GUI;

import BackEND.App;
import BackEND.RankDiscordChecker;
import BackEND.StatsChecker;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import javax.security.auth.login.LoginException;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane mPanel;

    @FXML
    private AnchorPane ButtonPanel;

    @FXML
    private JFXToggleButton controlBot;

    @FXML
    private JFXToggleButton ExpButton;

    @FXML
    private MenuButton menuButtonCT;

    @FXML
    private MenuButton menuButtonCH;

    @FXML
    private AnchorPane console;

    @FXML
    private JFXButton saveButton;

    @FXML
    private TextArea outputConsole;

    @FXML
    private TextField inputConsole;

    @FXML
    private AnchorPane upPanel;

    @FXML
    private Text nameBot;

    private static TextArea outputArea;
    private static JFXToggleButton botButton;
    private static MenuButton ctMenu;
    private static MenuButton chMenu;

    public static void sendMessage(String str) {
        outputArea.appendText(str+"\n");
    }

    public static void setBotButton(boolean b) {
        botButton.setDisable(b);
    }

    public static MenuButton getChMenu() {
        return chMenu;
    }

    public static MenuButton getCtMenu() {
        return ctMenu;
    }

    @FXML
    void initialize() {

        menuButtonCT.setVisible(false);
        menuButtonCH.setVisible(false);

        outputArea = outputConsole;

        ctMenu = menuButtonCT;
        chMenu = menuButtonCH;

       /* ByteArray consoleOUT = new ByteArray(outputConsole, "[OUT]"); // обертка для ловли System.out
        ByteArray consoleERR = new ByteArray(outputConsole,"[ERR]"); // обертка для ловли System.err

        PrintStream psERR = new PrintStream(consoleERR);
        PrintStream psOUT = new PrintStream(consoleOUT);

        System.setErr(psERR);
        System.setOut(psOUT);*/

        botButton = controlBot;

        inputConsole.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String text = inputConsole.getText();
                Chat.sendMessage(text);
                inputConsole.setText("");
            }
        });

        controlBot.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (controlBot.isSelected()) {

                    new RunBot().start();

                    menuButtonCH.setVisible(true);
                    menuButtonCT.setVisible(true);
                    ExpButton.setVisible(true);
                    saveButton.setVisible(true);

                } else {
                    RankDiscordChecker.writeList();
                    StatsChecker.writeList();
                    System.out.println("bot-disable");
                    outputConsole.appendText("Бот выключен.\n");
                    App.disableBot();
                    menuButtonCT.setVisible(false);
                    menuButtonCH.setVisible(false);
                    Chat.botEnabled = false;
                }
            }
        });
        ExpButton.setSelected(true);
        ExpButton.setFocusTraversable(false);
        ExpButton.setVisible(false);
        ExpButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (ExpButton.isSelected()) {
                    RankDiscordChecker.setAdd_xp_voice(true);
                } else {
                    RankDiscordChecker.setAdd_xp_voice(false);
                }
            }
        });

        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                RankDiscordChecker.writeList();
                StatsChecker.writeList();
                sendMessage("Выполненно сохранение листа)");
                System.out.println("Выполненно сохранение топа листа)");

            }
        });
    }

    private class RunBot extends Thread {
        @Override
        public void run() {
            try {
                App.runBot();
                outputConsole.appendText("Бот запущен.\n");
                System.out.println("bot-enabled");
                try {
                    Chat.getCategory(ctMenu);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Chat.botEnabled = true;
            } catch (LoginException e) {
                e.printStackTrace();
            }
        }
    }
}
