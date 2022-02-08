package GUI;

import BackEND.App;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import net.dv8tion.jda.api.JDA;

import java.util.ArrayList;

public class Chat {
    private static final JDA jda = App.getJDA();
    private static final MenuButton categoryMenuButton = Controller.getCtMenu();
    private static final MenuButton chatMenuButton = Controller.getChMenu();
    private static boolean canWrite;
    protected static boolean botEnabled;
    private static int ctId;
    private static int chId;
    private static ArrayList<MenuItem> chatItems = new ArrayList<>();


    protected static void getCategory(MenuButton menu) throws InterruptedException {
        int id = jda.awaitReady().getCategories().size();
        for (int i = 0; i < id;i++) {
            String cat = jda.awaitReady().getCategories().get(i).getName();
            MenuItem menuItem = new MenuItem();
            menuItem.setText(cat);
            int finalI = i;
            menuItem.setOnAction(actionEvent -> {
                categoryMenuButton.setText(cat);
                ctId = finalI;
                chatMenuButton.setText("Chats");
                canWrite = false;
                try {
                    changeChat();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            menu.getItems().add(menuItem);
        }
    }
    protected static void changeChat() throws InterruptedException {
        int id = jda.awaitReady().getCategories().get(ctId).getTextChannels().size();
        chatMenuButton.getItems().removeAll(chatItems);
        chatItems = new ArrayList<>();
        for (int i = 0; i < id; i++) {
            String ch = jda.awaitReady().getCategories()
                    .get(ctId).getTextChannels()
                    .get(i).getName();
            MenuItem menuItem = new MenuItem();
            menuItem.setText(ch);
            int finalI = i;
            menuItem.setOnAction(actionEvent -> {
                chatMenuButton.setText(ch);
                chId = finalI;
                canWrite = true;
            });
            chatItems.add(menuItem);
        }
        chatMenuButton.getItems().addAll(chatItems);
    }

    protected static void sendMessage(String str){
        if (canWrite && botEnabled) {
            try {
                if (str.length() > 0) {
                    if (str.charAt(0) != ' ') {
                        Controller.sendMessage("Wyverno-Bot: "+str+"\n");
                    }
                }
                jda.awaitReady().getCategories()
                        .get(ctId).getTextChannels()
                        .get(chId).sendMessage(str)
                        .queue();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (botEnabled) {
            Controller.sendMessage("Choose chat");
        } else {
            Controller.sendMessage("Enable bot");
        }
    }
}
