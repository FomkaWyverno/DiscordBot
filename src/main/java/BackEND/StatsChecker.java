package BackEND;

import GUI.Controller;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class StatsChecker extends ListenerAdapter {

    private static JDA jda = App.getJDA();
    private static final String file = "Stats/stats.txt";
    private static final String time = "[time]";
    private static final String message = "[message]";
    private static AutoSave autoSave;

    private static HashMap<String/*ID Discords*/,StatsAccount> statsAccounts = new HashMap<>();

    static { // Статическая загрузка загрузка базы данных и включение Авто-Сохранения
        try {
            BufferedReader reader = new BufferedReader( // Создаем обьект Буфферед Ридер
                    new InputStreamReader( // Кладем в него ИнпутСтримРидер
                            new FileInputStream( // ИнпутСтримРидер читает с ФайлИнпутСтрима
                                    new File( // ФайлИнтпутСтрим узнает путь в File
                                            Objects.requireNonNull(StatsChecker.class. // Обращаемся в классу
                                                    getClassLoader(). // Берем у класса КлассЛоадер
                                                    getResource(file)).toURI()))));  // Узнаем путь к ресурсу в переменной file
                                                                                     // Получаем URL обьект.
                                                                                     // С URL делаем URI, что бы обьект File
                                                                                     // смог прочитать правильно директорию.
            User[] members = checkOnlineVoice();
            while (reader.ready()) {
                String str = reader.readLine();
                String id = str.substring(0, str.indexOf(time)).trim();
                String timeJoin = str.substring(str.indexOf(time)+time.length(),str.indexOf(message)).trim();
                String messages = str.substring(str.indexOf(message) + message.length()).trim();
                StatsAccount account = new StatsAccount(Long.parseLong(timeJoin),
                        Integer.parseInt(messages));


                statsAccounts.put(id,account);
            }
            Date startTime = new Date();
            for (int i = 0; i < members.length; i++) {
                searchAccount(members[i].getId(),startTime.getTime());
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        }
        autoSave = new AutoSave();
    }

    private static User[] checkOnlineVoice() throws InterruptedException {
        ArrayList<User> members = new ArrayList<>();

        int size_category = jda.awaitReady().getGuilds().get(0)
                .getCategories().size();

        for (int i = 0; i < size_category; i++) {
            int size_voice = jda.awaitReady()
                    .getGuilds().get(0)
                    .getCategories().get(i)
                    .getVoiceChannels().size();
            for (int j = 0; j < size_voice; j++) {
                int size_members = jda.awaitReady()
                        .getGuilds().get(0)
                        .getCategories().get(i)
                        .getVoiceChannels().get(j)
                        .getMembers().size();
                for (int e = 0; e < size_members; e++) {
                    members.add(jda.awaitReady()
                    .getGuilds().get(0)
                    .getCategories().get(i)
                    .getVoiceChannels().get(j)
                    .getMembers().get(e).getUser());
                }
            }
        }



        return members.toArray(new User[0]);
    }

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        Date date = new Date();
        long timeJ = date.getTime();
        String id_member = event.getMember().getId();
        searchAccount(id_member,timeJ);
    }


    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        String id_member = event.getMember().getId();
        Date date = new Date();
        long timeL = date.getTime();
        sumOnePersonTime(id_member,timeL);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        try {
            if (!Objects.requireNonNull(event.getMember()).getUser().isBot()) {
                String memberId = event.getMember().getId();
                addMessage(memberId);
            }
        } catch (NullPointerException e) {
            String memberId;
            try { memberId = event.getMember().getId(); } catch (NullPointerException j) {
                //skip
                System.out.println("event.getMember выдал Null на сообщение: " +
                        event.getMessage().getContentRaw());
                return;
            }
            addMessage(memberId);
        }

    }

    public static void writeList(){ // Запись файла
        Date date = new Date();
        sumAllTime(date.getTime());
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    new File(Objects.requireNonNull(StatsChecker.class.getClassLoader().getResource(file)).toURI())
            )));
            /*for (int i = 0; i < statsAccounts.size(); i++) {


                     // ЭТОТ АЛГОРИТМ БЫЛ ДЛЯ ИСПОЛЬЗОВАНИЕ ARRAYLIST, А НЕ HASHMAP<ID,StatsAccount>


                String accountId = statsAccounts.get(i).getId();
                String accountTime = String.valueOf(statsAccounts.get(i).getTime());
                if (accountId.length()<20) {
                    accountId = RankDiscordChecker.addSpace(accountId,20);
                }
                if (accountTime.length()<20) {
                    accountTime = RankDiscordChecker.addSpace(accountTime,20);
                }

                writer.write(accountId+time+accountTime);
            }*/

            for (Map.Entry<String,StatsAccount> key: statsAccounts.entrySet()) {
                String accountId = key.getKey();
                String accountValue = String.valueOf(key.getValue());

                if (accountId.length()<20) {
                    accountId = RankDiscordChecker.addSpace(accountId,20);
                }
                writer.write(accountId+accountValue+"\n");
            }
            writer.close();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static int getMessagesUser(String id) throws NullPointerException {
        return statsAccounts.get(id).getMessages();
    }

    public static long getTimeUser(String id) throws NullPointerException {

        long time = statsAccounts.get(id).getTime();

        return time;
    }


    private static void addMessage(String id) {
        try {
            statsAccounts.get(id).addMessage();
        } catch (NullPointerException e) {
            statsAccounts.put(id,new StatsAccount(0,0));
            statsAccounts.get(id).addMessage();
        }

    }

    private static void sumOnePersonTime(String id, long nowTime) { // Добавить время мемберу который вышел из войс канала
        /*for (int i = 0; i < statsAccounts.size();i++) {
            StatsAccount account = statsAccounts.get(i);
            String accountId = account.getId();                // МЕТОД ДО HashMap
            if (accountId.equals(id)) {
                account.addMillisecond(nowTime - account.getJoin_time());
                account.setJoin_time(0);
            }

        }*/


        StatsAccount account = statsAccounts.get(id);
        if (account.getJoin_time() != 0) {
            account.addMillisecond(nowTime - account.getJoin_time());
            account.setJoin_time(0);
        }
    }

    private static void sumAllTime(long nowTime) { // Добавляет всем аккаунтам время.
        /*for (int i = 0; i < statsAccounts.size(); i++) {
            StatsAccount account = statsAccounts.get(i);
            long time = account.getJoin_time();
            if (time != 0) {
                account.addMillisecond(nowTime - account.getJoin_time());
                account.setJoin_time(0);
            }
        }*/

        for (Map.Entry<String,StatsAccount> pair: statsAccounts.entrySet()) {
            StatsAccount account = pair.getValue();
            long time = account.getJoin_time();
            if (time != 0) {
                account.addMillisecond(nowTime - account.getJoin_time());
                account.setJoin_time(0);
            }
        }

    }

    private static void searchAccount(String id,long time) { // Ищет и ставит стартовую точку при заходе в войсе
                                                             // Если не было аккаунта в базе добавляет новый аккаунт.
       try {
           statsAccounts.get(id).setJoin_time(time);
       } catch (NullPointerException e) {
           statsAccounts.put(id,new StatsAccount());
           statsAccounts.get(id).setJoin_time(time);
       }

    }


    private static class StatsAccount { // Подкласс который хранит данные о людях.

        long millisecond;
        int messages;


        long join_time;

        private StatsAccount() { }

        private StatsAccount(long millisecond,int messages) {
            this.millisecond = millisecond;
            this.messages = messages;
        }

        private long getJoin_time(){
            return join_time;
        }

        private long getTime() {
            return millisecond;
        }

        private int getMessages(){
            return messages;
        }

        private void setJoin_time(long time) {
            this.join_time = time;
        }

        private void addMillisecond(long millisecond) {
            this.millisecond +=  millisecond;
        }

        private void addMessage() {
            messages++;
        }

        @Override
        public String toString() {
            String allTime = String.valueOf(millisecond);
            String allMessage = String.valueOf(messages);
            if (allTime.length()<20) {
                allTime = RankDiscordChecker.addSpace(allTime,20);
            }
            if (allMessage.length()<20) {
                allMessage = RankDiscordChecker.addSpace(allMessage,20);
            }

            return StatsChecker.time+allTime+StatsChecker.message+allMessage;
        }
    }

    private static class AutoSave extends Thread {
        private AutoSave() {
            start();
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(900000);
                    Controller.sendMessage("[StatsChecker] - Начало сохранения!");
                    writeList();
                    User[] users = checkOnlineVoice();
                    Date autoSaveDate = new Date();
                    for (int i = 0; i < users.length; i++) {
                        searchAccount(users[i].getId(),autoSaveDate.getTime());
                    }
                    Controller.sendMessage("[StatsChecker] - Сохранение выполнено.");
                }
            } catch (InterruptedException e) {
                RankDiscordChecker.writeList();
                StatsChecker.writeList();
                Controller.sendMessage("[StatsChecker] - Аварийное закрытие программы по причине ошибки в Авто-Сохранении.");
                Controller.setBotButton(false);
                System.exit(0);
            }
        }
    }
}

