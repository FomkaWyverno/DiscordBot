/*
package BackEND;

import net.dv8tion.jda.api.JDA;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Console extends Thread {

    */
/*private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private static JDA jda = App.getJDA();
    private static int chat = 0;
    private static int category = 3;
    private static String console_chat;

    static {
        try {
            console_chat = jda.awaitReady().getCategories().get(category).getTextChannels().get(chat).toString();
            console_chat = console_chat.substring(0,console_chat.indexOf("("));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public Console() {
        start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.print(console_chat + " >_ ");
                String txt = reader.readLine();
                if (txt.length() > 0) {
                    switch (txt) {
                        case "end" :
                            reader.close();
                            return;
                        case "/statusJDA" :
                            System.out.println("<"+jda.getStatus()+">");
                            break;
                        case "/category" :
                            changeCategory();
                            break;
                        case "/chat" :
                            changeChat();
                            break;
                        case "/giveCRYSTAL" :
                            System.out.println("ВЫ ТОЧНО ХОТИТЕ ВЫДАТЬ ВСЕМ ИГРОКАМ РЕЙТИНГА КРИСТАЛЫ ЗА ЗВАНИЕ?\n" +
                                    "НАПИШИТЕ \"ДА\" ЕСЛИ ВЫ УВЕРЕНЫ, ИЛИ \"НЕТ\" ЕСЛИ ВЫ НЕ ХОТЕЛИ ЭТОГО.");
                            String check = reader.readLine();
                            if (check.equals("ДА")) {
                                System.out.println("ВЫ УВЕРЕНЫ?\n" +
                                        "НАПИШИТЕ ЕЩЁ РАЗ \"ДА\"");
                                String check2 = reader.readLine();
                                if (check2.equals("ДА")) {
                                    RankDiscordChecker.giveAllBonus();
                                    System.out.println("Выдано всем кристалы за звание.");
                                    break;
                                }
                            }
                            System.out.println("Кристалы не были выданы игрокам");
                            break;

                        case "/write" :
                            RankDiscordChecker.writeList();
                            System.out.println("Выполненно сохранение топа листа)");
                            break;
                        case "/close" :
                            RankDiscordChecker.writeList();
                            StatsChecker.writeList();
                            System.out.println("Закрытие программы по команде /close");
                            System.exit(0);
                            break;
                        default:
                            jda.awaitReady().getCategories().get(category)
                                    .getTextChannels().get(chat)
                                    .sendMessage(txt)
                                    .queue();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void changeCategory() {
        try {
            int id = jda.awaitReady().getCategories().size();
            for (int i = 0; i < id;i++) {
                String cat = jda.awaitReady().getCategories().get(i).toString();
                cat = cat.substring(0,cat.indexOf("("));
                System.out.println(cat + " id category: \"" + i + "\"");
            }
            id--;
            System.out.println("Write /cancel to cancel.");
            System.out.print("changeCategory >_ ");

            while (true) {
                try {
                    String result = reader.readLine();
                    if (result.equals("/cancel")) {
                        throw new Exception();
                    }
                    int change = Integer.parseInt(result);
                    if (change > id) {
                        throw new NumberFormatException();
                    }
                    category = change;
                    System.out.println("Successfully");
                    System.out.println("You need choose chat!");
                    changeChat();
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please write correctly, number id category or write /cancel for to cancel");
                }
            }

        } catch (Exception e) {
            System.out.println("Cancel.");
            try {
                Thread.sleep(1000);
                System.out.println("Cancel..");
                Thread.sleep(1000);
                System.out.println("Cancel...");
            } catch (InterruptedException interruptedException) {
            }
        }

    }

    private static void changeChat() {
        int id = 0;
        try {
            id = jda.awaitReady().getCategories().get(category).getTextChannels().size();
            for (int i = 0; i < id; i++) {
                String chatix = jda.awaitReady().getCategories().get(category).getTextChannels().get(i).toString();
                chatix = chatix.substring(0,chatix.indexOf("("));
                System.out.println(chatix + " id chat: \""+i+"\"");
            }
            id--;
            System.out.println("Write /cancel to cancel.");
            System.out.print("changeChat >_ ");
            while (true) {
                try {
                    String result = reader.readLine();
                    if (result.equals("/cancel")) {
                        throw new Exception();
                    }
                    int change = Integer.parseInt(result);
                    if (change > id) {
                        throw new NumberFormatException();
                    }
                    chat = change;

                    String tmp = jda.awaitReady().getCategories().get(category).getTextChannels().get(chat).toString();
                    tmp = tmp.substring(0,tmp.indexOf("("));
                    console_chat = tmp;
                    System.out.println("Successfully");
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Please write correctly, number id chat or write /cancel for to cancel");
                }
            }
        } catch (Exception e) {
            chat = id;
            System.out.println("Cancel.");
            try {
                Thread.sleep(1000);
                System.out.println("Cancel..");
                Thread.sleep(1000);
                System.out.println("Cancel...");
            } catch (InterruptedException interruptedException) {
            }
        }
    }*//*

}
*/
