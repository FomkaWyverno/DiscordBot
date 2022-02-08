package BackEND.MusicCORE;

import BackEND.App;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

public class SpectatorServerPlaylist extends ListenerAdapter {
    private static final JDA JDA = App.getJDA();
    private static final long ID_GUILD = 560916170920951851L;
    private static final long ID_SERVER_PLAYLIST_OFFER = 845230708875198464L;
    private static final long ID_SERVER_PLAYLIST = 798142407706411018L;
    private static final Set<Long> ID_ROLE_ADMIN = new HashSet<>();
    private static final String GOOD = "✅";
    private static final String BAD = "❌";

    public static long getIdServerPlaylist() {
        return ID_SERVER_PLAYLIST;
    }

    static {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File(
                                            Objects.requireNonNull(SpectatorServerPlaylist
                                                    .class
                                                    .getResource("/IDs/ID_ROLE/ID_ROLE_ADMINISTRATION.cfg")).toURI()
                                    )
                            )
                    )
            );
            while (reader.ready()) {
                String line = reader.readLine();
                ID_ROLE_ADMIN.add(Long.parseLong(line));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // Загружаем ID ROLE ADMIN

    static {
        TextChannel textChannel = JDA.getGuildById(ID_GUILD).getTextChannelById(ID_SERVER_PLAYLIST_OFFER);
        MessageHistory messageHistory = MessageHistory.getHistoryFromBeginning(textChannel.getHistory().getChannel()).complete();
        List<Message> messageList = messageHistory.getRetrievedHistory();
        for (Message message: messageList) {
            if (message.getAuthor().getIdLong() != 299872345047302144L && !message.getAuthor().isBot()) {
                message.addReaction(GOOD).queue();
                message.addReaction(BAD).queue();
            }
        }
    } // Выставляем всем эмоции на сообщение пока бот был офлайн.

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        long idChannel = event.getChannel().getIdLong();
        if (idChannel == ID_SERVER_PLAYLIST_OFFER && !event.getAuthor().isBot()) {
            event.getMessage().addReaction(GOOD).queue();
            event.getMessage().addReaction(BAD).queue();
            event.getChannel().sendMessage("Ваш трек ожидает одобрение администрации! \n" +
                    "**Это сообщение удалится через 10 секунд.**").queue();
        } else if (idChannel == ID_SERVER_PLAYLIST_OFFER && event.getAuthor().isBot()) {
            Timer timer = new Timer();
            final Integer[] second = {10};

            timer.scheduleAtFixedRate(new TimerForMessage(timer, event) {
                @Override
                public void run() {
                    second[0]--;
                    if (second[0] == 0) {
                        this.getEvent().getMessage().delete().queue();
                        this.getTimer().cancel();
                        return;
                    }
                    try {
                        this.getEvent().getMessage().editMessage("Ваш трек ожидает одобрение администрации! \n" +
                                "**Это сообщение удалится через "+ second[0] +" секунд.**").queue();
                    } catch (Exception e) {/* (Ловим ContextException)Игнорируем исклюсение так как оно возникает тогда когда не успевает удалить эмоцию, когда уже сообщение уже удалено.*/}

                }
            },1000,1000);
        }
    } // Ставим смайлики на одобрение трека
                                                                                       // А так же сообщаем что трек в ожидании одобрении.


    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if (event.getChannel().getIdLong() == ID_SERVER_PLAYLIST_OFFER && !event.getUser().isBot()) {
            List<Role> rolesMember = event.getMember().getRoles();
            String emoji = event.getReaction().getReactionEmote().getEmoji();
            try {
                event.getReaction().removeReaction(event.getUser()).queue();
            } catch (Exception e) {/*Игнорируем исклюсение так как оно возникает тогда когда не успевает удалить эмоцию, когда уже сообщение уже удалено.*/}
            try {
                for (Role role : rolesMember) {
                    if (ID_ROLE_ADMIN.contains(role.getIdLong())) {
                        if (GOOD.equals(emoji)) {
                            MessageHistory messageHistory = MessageHistory // Обращаемся к API Discord с запросом о выдаче информации об сообщение
                                    .getHistoryAround(event.getChannel(), event.getMessageId()) // Указываем Канал, и Айди Сообщение
                                    .limit(1).complete(); // Устанавливаем ограничение что нам нужно 1 одно сообщение.
                            String urlMusic = messageHistory.getRetrievedHistory().get(0).getContentRaw(); // Присваем строке сообщение которе получили от API
                            JDA.awaitReady()
                                    .getGuildById(ID_GUILD)
                                    .getTextChannelById(ID_SERVER_PLAYLIST)
                                    .sendMessage(urlMusic).queue(); // Отправляем музыку в Архив



                            Message message = MessageHistory
                                    .getHistoryAround(event.getChannel(),event.getMessageId())
                                    .limit(1)
                                    .complete()
                                    .getRetrievedHistory().get(0);
                            message.delete().queue(); // Удаляем сообщение из предложки

                            RestAction<PrivateChannel> privateChannel = message.getAuthor().openPrivateChannel(); // Открываем личные сообщение
                            privateChannel.flatMap(channel -> channel.sendMessage("Вашу музыку добавили в плейлист Сервера!\n" + urlMusic))
                                    .queue(); // Отправляем в личные сообщение о добавление музыки
                        } else {
                            Message message = MessageHistory
                                    .getHistoryAround(event.getChannel(),event.getMessageId())
                                    .limit(1)
                                    .complete()
                                    .getRetrievedHistory().get(0);
                            message.delete().queue(); // Удаляем сообщение из предложки

                            MessageHistory messageHistory = MessageHistory // Обращаемся к API Discord с запросом о выдаче информации об сообщение
                                    .getHistoryAround(event.getChannel(), event.getMessageId()) // Указываем Канал, и Айди Сообщение
                                    .limit(1).complete(); // Устанавливаем ограничение что нам нужно 1 одно сообщение.

                            String urlMusic = messageHistory.getRetrievedHistory().get(0).getContentRaw(); // Присваем строке сообщение которе получили от API

                            RestAction<PrivateChannel> privateChannel = message.getAuthor().openPrivateChannel(); // Открываем личные сообщение
                            privateChannel.flatMap(channel -> channel.sendMessage("Вашу музыку не добавили в плейлист Сервера!\n" + urlMusic))
                                    .queue(); // Отправляем в личные сообщение о добавление музыки
                        }
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    } // Ловим сообщение из текстового канала где предлагают музыку

    public static abstract class TimerForMessage extends TimerTask {
        private Timer timer;
        private GuildMessageReceivedEvent event;
        private int second = 10;
        public TimerForMessage(Timer timer, GuildMessageReceivedEvent event) {
            this.timer = timer;
            this.event = event;
        }

        @Override
        public abstract void run(); /*{
            second--;
            if (second == 0) {
                event.getMessage().delete().queue();
                timer.cancel();
                return;
            }
            event.getMessage().editMessage("Ваш трек ожидает одобрение администрации! \n" +
                    "**Это сообщение удалится через "+ second +" секунд.**").queue();
        }*/

        public Timer getTimer() {
            return timer;
        }

        public GuildMessageReceivedEvent getEvent() {
            return event;
        }

        public void setEvent(GuildMessageReceivedEvent event) {
            this.event = event;
        }

        public int getSecond() {
            return second;
        }

        public void setSecond(int second) {
            this.second = second;
        }
    } // ТаймерТаск через которое время удалится сообщение.
}
