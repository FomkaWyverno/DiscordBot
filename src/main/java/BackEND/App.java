package BackEND;

import BackEND.MusicCORE.MusicPlaylist;
import BackEND.MusicCORE.SpectatorChats;
import BackEND.MusicCORE.SpectatorServerPlaylist;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.*;

public class App {
    private final static String TOKEN;
    public static JDA jda;
    private static boolean enable = false;

    static {
        BufferedReader reader = null;
        String t_token = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("token.txt")));

            t_token = reader.readLine();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert t_token != null;

        TOKEN = t_token;
        System.out.println();
    }

    public static void main(String[] args) throws LoginException {
        JDABuilder jdaBuilder = JDABuilder.createDefault(TOKEN);
        jdaBuilder.setBulkDeleteSplittingEnabled(false);
        jdaBuilder.setCompression(Compression.NONE);
        jdaBuilder.setStatus(OnlineStatus.DO_NOT_DISTURB);

        jdaBuilder.setActivity(Activity.watching("Pornhub.com"));
        //jdaBuilder.setActivity(Activity.streaming("вместе с Фомкой", "https://www.twitch.tv/fomka_wyverno"));

        jda = jdaBuilder.build();
        /*new Window();*/

        jda.addEventListener(new Commands()); // Команды;
        jda.addEventListener(new CheckReactionForLeaders());
        jda.addEventListener(new StatsChecker());
        jda.addEventListener(new SpectatorServerPlaylist());
       /* Console console = new Console(); */// Пишем через консоль в чат.
        RankDiscordChecker rankDiscordChecker = new RankDiscordChecker(); // Поток начисления опыта за войс и чат

    }

    public static void runBot() throws LoginException { // Метод для кнопки в GUI Включения бота
        JDABuilder jdaBuilder = JDABuilder.createDefault(TOKEN);
        jdaBuilder.setBulkDeleteSplittingEnabled(false);
        jdaBuilder.setCompression(Compression.NONE);
        jdaBuilder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        jdaBuilder.setActivity(Activity.playing("You != Logic"));

        jda = jdaBuilder.build();
        /*jda.getPresence().setActivity(Activity.streaming("вместе с Фомкой", "https://www.twitch.tv/fomka_wyverno"));*/


        jda.addEventListener(new Commands()); // Команды
        jda.addEventListener(new ConsoleAllChats()); // Все чаты в консоле.
        jda.addEventListener(new CheckReactionForLeaders());
        jda.addEventListener(new StatsChecker());
        jda.addEventListener(new CheckerMember());
        jda.addEventListener(new MusicPlaylist());
        jda.addEventListener(new SpectatorServerPlaylist());
        jda.addEventListener(new SpectatorChats()); // Наблюдатель за чатом для Музыки
        /*Console console = new Console();*/ // Пишем через консоль в чат.
        RankDiscordChecker rankDiscordChecker = new RankDiscordChecker(); // Поток начисления опыта за войс и чат
        enable = true;

    }

    public static void disableBot() {
        jda.shutdownNow();
        enable = false;
    }

    public static JDA getJDA() {
        return jda;
    }

    public static boolean isEnable() {
        return enable;
    }
}
