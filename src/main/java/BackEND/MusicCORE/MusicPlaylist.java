package BackEND.MusicCORE;

import BackEND.EmbedPlaylist;
import BackEND.Move;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class MusicPlaylist extends ListenerAdapter {

    private static final String left = "U+2b05U+fe0f";
    private static final String right = "U+27a1U+fe0f";
    private static final String close = "U+274c";
    protected static final HashMap<Long,HashMap<Long, EmbedPlaylist>> playlistHashMap = new HashMap<>();
    /*protected static final HashMap<Long,EmbedPlaylist> playlistHashMap = new HashMap<>();*/



    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) { // Ловим реакции для измениния листа
        if (playlistHashMap.containsKey(event.getGuild().getIdLong()) && !event.getUser().isBot()) { // Если на этом сервере есть Лист, и это не бот то
            /*EmbedPlaylist embedPlaylist = playlistHashMap.get(event.getGuild().getIdLong());// Берем Лист*/
            HashMap<Long,EmbedPlaylist> serverList = playlistHashMap.get(event.getGuild().getIdLong()); // Берем Карту с Сервными Листами
            if (serverList.containsKey(event.getMessageIdLong())) { // Есть ли такой лист с этим айди?
                EmbedPlaylist embedPlaylist = serverList.get(event.getMessageIdLong()); // Берем с Серверной Карты лист
                if (embedPlaylist.getIdMessage() == event.getMessageIdLong()) { // Сверяем реакция поставлена на лист?

                    String emoji = event.getReactionEmote().toString(); // Узнаем какая это реакция и запоминаем её в виде строки

                    Move move = switchEmoji(emoji.substring(3)); // Убираем первые 3 символа которые обозначают что именно это реакция
                    // И отправляем на проверку что это за действия.
                    embedPlaylist.turnPage(move); // Делаем действия над листом.

                    EmbedBuilder embedBuilder = embedPlaylist.buildEmbed();

                    GuildMessageReceivedEvent eventMessage = embedPlaylist.getEventMessage();

                    if (embedBuilder == null) {
                        eventMessage.getMessage().delete().queue();
                        return;
                    }

                    event.getReaction().removeReaction(event.getUser()).queue(); // Удаляем реакцию пользователя

                    eventMessage.getMessage().editMessage(embedBuilder.build()).queue();

                }
            }
        }
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) { // Ловим сообщение от бота с плейлистом
        long idGuild = event.getGuild().getIdLong();
        try {
            if (playlistHashMap.containsKey(idGuild)
                    && Objects.requireNonNull(event.getMember()).getUser().isBot()) {
                HashMap<Long,EmbedPlaylist> serverList = playlistHashMap.get(idGuild);
                if (serverList.containsKey(idGuild)) {
                    EmbedPlaylist embedPlaylist = serverList.get(idGuild);
                    serverList.remove(idGuild);
                    serverList.put(event.getMessageIdLong(),embedPlaylist);
                    if (!embedPlaylist.getHaveButtons()) {
                        event.getMessage().addReaction(left).queue();
                        event.getMessage().addReaction(right).queue();
                        event.getMessage().addReaction(close).queue();
                        embedPlaylist.setHaveButtons(true);
                        embedPlaylist.setIdMessage(event.getMessageIdLong());
                        embedPlaylist.setPlaylistHashMap(serverList);
                        embedPlaylist.setEventMessage(event);
                    }
                }
            }
        } catch (NullPointerException e){ }
    }

    private Move switchEmoji(String emoji) {
        return switch (emoji) {
            case left -> Move.left;
            case right -> Move.right;
            case close -> Move.close;
            default -> Move.now;
        };
    }
}
