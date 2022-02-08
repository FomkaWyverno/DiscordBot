package BackEND;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;


public class CheckReactionForLeaders extends ListenerAdapter {

    private static String id = null;
    private static String[][] pages = null;
    private static JDA jda = App.getJDA();
    private static GuildMessageReceivedEvent eventMessage = null;
    private static final String left = "U+2b05U+fe0f";
    private static final String right = "U+27a1U+fe0f";
    private static final String close = "U+274c";
    private static int page = 0;
    private static int max_page = 0;
    private static int size = 0;
    private static Color cEmber = new Color(2, 246, 164);

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        try {
            if (!event.getUser().isBot() && id.equals(event.getMessageId())) {

                String reaction = event.getReactionEmote().toString();
                reaction = reaction.substring(3);
                boolean isClose = false;
                EmbedBuilder embed = null;
                switch (reaction) {
                    case left:
                        if (page-1 != -1) {
                            embed = getEmbed(Move.left);
                            eventMessage.getMessage().editMessage(embed.build()).queue();
                        }
                        break;
                    case right:
                        if (page+1 != max_page) {
                            embed = getEmbed(Move.right);
                            eventMessage.getMessage().editMessage(embed.build()).queue();
                        }
                        break;
                    case close:
                        isClose = true;
                        deleteEventMessage();
                }
                if (embed != null) {
                    embed.clear();
                }
                if (!isClose) {
                    event.getReaction().removeReaction(event.getUser()).queue();
                }
            }
        } catch (NullPointerException e) {}

    }

    public static void setId(String id) {
        if (eventMessage != null) { deleteEventMessage(); }
        getInfoPages(RankDiscordChecker.getLeadersList());
        CheckReactionForLeaders.id = id;
    }

    public static void setEventMessage(GuildMessageReceivedEvent event) {
        eventMessage = event;
        eventMessage.getMessage().editMessage(getEmbed(Move.now).build()).queue();
    }
    private static void deleteEventMessage() { // Удаляем ивент который может управлять сообщением.
        if (eventMessage != null) {            // Так-же обнуляем все переменные
            eventMessage.getMessage().delete().queue();
            max_page = 0;
            pages = null;
            eventMessage = null;
        }
    }

    public static EmbedBuilder getEmbed(Move move) {
        switch (move) {
            case now:
                break;
            case left:
                page--;
                break;
            case right:
                page++;
                break;
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Страница " + (page+1) + " из " + max_page + " - Всего участников рейтинга: " + size);
        embedBuilder.setColor(cEmber);
        for (int i = 0; i < pages[page].length;i++) {
            if (pages[page][i] == null) {
                break;
            }
            String str = pages[page][i];
            String name = str.substring(str.indexOf("~place~")+7,str.indexOf("~nameRank~"));
            int place = Integer.parseInt(name)+1;
            name = "#" + place;
            if (place == 1) {
                name = "\uD83E\uDD47" + name;
            } else if (place == 2) {
                name = "\uD83E\uDD48" + name;
            } else if (place == 3) {
                name = "\uD83E\uDD49" + name;
            }

            name += " " + str.substring(str.indexOf("~nickname~")+10,str.indexOf("~level~"));

            String value = "**Звание: **";
            value += str.substring(str.indexOf("~nameRank~")+10,str.indexOf("~nickname~")) + "\n";
            value += "**Уровень: **";
            value += str.substring(str.indexOf("~level~")+7,str.indexOf("~experience~")) + "\n";
            value += "**Опыт: **";
            value += str.substring(str.indexOf("~experience~")+12);


            embedBuilder.addField(name,value,false);
        }

        return embedBuilder;
    }

    private static void getInfoPages(String[] info){
        size = info.length;
        max_page = getPages(size, 10.0);
        pages = new String[max_page][10];

        for (int i = 0, p = 0; i < pages.length; i++) {
            for (int j = 0; j < pages[i].length; j++,p++) {
                if (info.length-1 < p) {
                    break;
                }
                pages[i][j] = info[p];
            }
        }
    }

    public static int getPages(int peoples, double onOnePage){
        double result1 =peoples / onOnePage;
        int rINT = (int)result1;
        double result2 = rINT;
        if (result1 > result2) {
            rINT++;
        }
        return rINT;
    }
}
