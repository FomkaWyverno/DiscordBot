package BackEND.MusicCORE;

import BackEND.CheckerMember;
import BackEND.EmbedPlaylist;
import BackEND.Util.NotTypeString;
import BackEND.Util.RandomArrayList;
import BackEND.YouTubeAPI.YouTubeAPI;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class MusicClass {
    private static final Set<Long> ID_ROLE_MANAGE_MUSIC = new HashSet<>();
    AudioPlayerManager audioPlayerManager;
    private final Map<Long, GuildMusicManager> musicManagers;
    private Future<Void> future;
    protected static final String START_PLAYLIST = ":hourglass_flowing_sand: Start load server playlist.";
    private boolean mutePlaylist = false;


    static {
        System.out.println(MusicClass.class.getResource("/IDs/ID_ROLE/ID_ROLE_MANAGE_MUSIC.cfg"));
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            MusicClass.class.getResource("/IDs/ID_ROLE/ID_ROLE_MANAGE_MUSIC.cfg").openStream()));
            while (reader.ready()) {
                ID_ROLE_MANAGE_MUSIC.add(Long.parseLong(reader.readLine()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MusicClass() {
        this.musicManagers = new HashMap<>();

        this.audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
    }

    public void playServerPlaylist(GuildMessageReceivedEvent event) {
        long idPlaylist = SpectatorServerPlaylist.getIdServerPlaylist();
        MessageHistory messageHistory = MessageHistory
                .getHistoryFromBeginning(event.getGuild()
                        .getTextChannelById(idPlaylist)
                        .getHistory().getChannel()).limit(100).complete();
        RandomArrayList<String> listURL = new RandomArrayList<>(messageHistory.getRetrievedHistory().size(),String.class);
        try {
            listURL.addAllMessage(messageHistory
                            .getRetrievedHistory());
        } catch (NotTypeString notTypeString) {
            notTypeString.printStackTrace();
        }
        getGuildAudioPlayer(event.getGuild()).needLoad = listURL.size();
        startMusicList(event,listURL);

    }

    public void startMusicList(GuildMessageReceivedEvent event,RandomArrayList<String> music){
        System.out.println(event.getMember());
        VoiceChannel voiceChannelMember = event.getMember().getVoiceState().getChannel();
        VoiceChannel voiceChannelSelf = event.getGuild().getSelfMember().getVoiceState().getChannel();

        try {
            if (voiceChannelMember != null && voiceChannelSelf == null || voiceChannelMember.equals(voiceChannelSelf)) {
                if (!event.getGuild().getAudioManager().isConnected()
                        && !event.getGuild().getAudioManager().isAttemptingToConnect()) {
                    event.getGuild().getAudioManager().openAudioConnection(voiceChannelMember);
                }
                mutePlaylist = true;
                event.getChannel().sendMessage(START_PLAYLIST).queue();
                for (String s : music) {
                    if (!isURL(s)) {
                        try {
                            s = getURL(s);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (isYouTube(s)) {

                        if (!isPlaylist(s)) {
                            loadMusic(s, event, voiceChannelMember, false, false);
                        } else {
                            RestAction<PrivateChannel> privateChannel = event.getAuthor().openPrivateChannel();
                            String finalS = s;
                            privateChannel.flatMap(channel -> channel.sendMessage("Это Playlist на YT - " + finalS +
                                    "\n Попросите Администрацию удалить эту ссылку из Архива."))
                                    .queue(); // Отправляем в личные сообщение об ошибке что это Playlist и просим удалить
                            // Его из архива с музыкой.
                        }
                    } else {
                        RestAction<PrivateChannel> privateChannel = event.getAuthor().openPrivateChannel();
                        String finalS = s;
                        privateChannel.flatMap(channel -> channel.sendMessage("Это не YT ссылка - " + finalS +
                                "\nПопросите эту ссылку удалить из Архива."))
                                .queue(); // Отправляем в личные сообщение об ошибке что это не Ютуб
                    }
                }
                Timer timer = new Timer();

                SpectatorServerPlaylist.TimerForMessage task = new SpectatorServerPlaylist.TimerForMessage(timer, event) {
                    @Override
                    public void run() {
                        if (future.isDone()) {
                            if (getGuildAudioPlayer(event.getGuild()).selfMessage != null)
                                getGuildAudioPlayer(event.getGuild()).selfMessage.getMessage().editMessage("Nice, playlist loaded! :thumbsup: ").queue();
                            getGuildAudioPlayer(event.getGuild()).needLoad = 0;
                            getGuildAudioPlayer(event.getGuild()).trackScheduler.loaded = 0;
                            getGuildAudioPlayer(event.getGuild()).setSelfMessage(null);
                            mutePlaylist = false;
                            timer.cancel();
                        } else {
                            int loaded = getGuildAudioPlayer(event.getGuild()).trackScheduler.loaded;
                            if (getGuildAudioPlayer(event.getGuild()).selfMessage != null) {
                                getGuildAudioPlayer(event.getGuild()).selfMessage.getMessage().editMessage(":rocket: Loaded " + loaded + " of " + getGuildAudioPlayer(event.getGuild()).needLoad).queue();
                            }

                        }

                    }
                };
                timer.scheduleAtFixedRate(task, 1000, 1000);

            }
        } catch (NullPointerException e) {
            event.getChannel().sendMessage("Вы должны находится в голосовом чате!").queue();
        }
    }

    public void startOrAddMusic(GuildMessageReceivedEvent event, String music, boolean needSend) {


        VoiceChannel voiceChannelMember = event.getMember().getVoiceState().getChannel();
        VoiceChannel voiceChannelSelf = event.getGuild().getSelfMember().getVoiceState().getChannel();

        try {
            if (voiceChannelMember != null && voiceChannelSelf == null || voiceChannelMember.equals(voiceChannelSelf)) {   // Человек который вызвал команду !play
                // находится в голосовом канале,
                // Челевок находится в голосовом канале и бот ни где не сидит в голосовом канале.
                // ИЛИ
                // Человек находится в одном том же канале где и бот.

                if (!isURL(music)) {
                    try {
                        music = getURL(music);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (isYouTube(music)) {

                    if (!isPlaylist(music)) {
                        loadMusic(music, event, voiceChannelMember,needSend,true); // Загружаем трек


                    /*if (!audioManager.isConnected()) {
                        audioManager.openAudioConnection(voiceChannelMember);
                    }*/
                    } else {
                        ArrayList<String> musics = null;
                        try {
                            musics = YouTubeAPI.getYouTubeMusicsPlaylist(music); // Запрашиваем заполнить Лист ссылками на музыку!
                            RandomArrayList<String> list = new RandomArrayList<>(musics.size(),String.class);
                            for (String url : musics) {
                                list.add(url);
                            }
                            for (String play : list) {
                                startOrAddMusic(event,play,false);
                            }
                            event.getChannel().sendMessage("Треки из плейлиста добавлены в список! Что бы проверить состояние плейлиста напишите \"!playlist\"\n" +
                                    "Ссылка на плейлист: " + music)
                                    .queue();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    event.getChannel().sendMessage("Включать музыку можно только с YouTube, можно как и одно видео, так и плейлист(in developing)").queue();
                }
            } else if (voiceChannelSelf != null) { // Когда человек не находится в голосовом канале или находится в другом канале когда бот сидит уже в голосовом канале.
                event.getChannel().sendMessage("Я уже нахожусь в голосовом чате: " + voiceChannelSelf.getName()).queue();
            } else { // Когда человек не находится в голосовом канале, и бот не сидит в голосовом канале.
                event.getChannel().sendMessage("Вы должны быть в голосовом канале!").queue();
            }
        } catch (NullPointerException e) {
            event.getChannel().sendMessage("Вы не находитесь в голосовом канале, пожалуйста подключитесь!").queue();
        }
    }

    public boolean isMutePlaylist() {
        return mutePlaylist;
    }

    public boolean havePermission(List<Role> Roles) {
        for (Role r : Roles) {

            if (ID_ROLE_MANAGE_MUSIC.contains(r.getIdLong())) {
                return true;
            }
        }
        return false;
    }
    private boolean isURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getURL(String nameMusic) throws IOException {
        // нужно сделать обращение в класс YouTubeAPI_Search
        return YouTubeAPI.getYoutubeUrl(nameMusic);
    }

    private void loadMusic(String url, GuildMessageReceivedEvent event,VoiceChannel voiceChannel,boolean needSend,boolean needConnection) { // Принимает ссылку на трек, ивент на сообщение, Голосовой чат участника
        GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild()); // Гетим Серверный АудиоПлейр
        CheckerMember.putGuild(event.getGuild());
        future = audioPlayerManager.loadItemOrdered(musicManager,url, new AudioLoadResultHandler() { // Загружаем в плейлист трек
            @Override
            public void trackLoaded(AudioTrack audioTrack) { // Трек добавлен
                if (needSend) event.getChannel().sendMessage("Трек добавлен! Информация о треке: "
                        + audioTrack.getInfo().title
                ).queue(); // Оповещаем пользователе о добавление трека
                play(event.getGuild(),musicManager,audioTrack,voiceChannel,needConnection); // Загружаем трек в очередь
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) { // Пока что не особо знаю что это делает
                System.out.println("Пришел Трек в playlistLoaded");
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                if (needSend) event.getChannel().sendMessage("Трек добавлен в очередь, ожидайте! Информация о треке: " + firstTrack.getInfo().title
                ).queue();

                play(event.getGuild(), musicManager, firstTrack,voiceChannel,needConnection);
            }

            @Override
            public void noMatches() { // Ссылка недействительная.
                //event.getChannel().sendMessage("Мы к сожалению не нашли ваш трек!").queue();
            }

            @Override
            public void loadFailed(FriendlyException e) { // Ссылка недействительная или нет доступа к видео, не удается загрузить видео.
                System.out.println("К сожалению не могу запустить трек по причине: " + e.getMessage());
            }
        });

    }

    public void skip(GuildMessageReceivedEvent event) {
        List<Role> roles = event.getMember().getRoles();

        if (havePermission(roles)) {
            GuildMusicManager guildMusicManager = getGuildAudioPlayer(event.getGuild());
            guildMusicManager.trackScheduler.nextTrack();
            event.getChannel().sendMessage("Трек был пропущен!")
                    .queue();
        } else {
            event.getChannel().sendMessage("Ваша роль не позволяет пропускать музыку из плейлиста.").queue();
        }
    }

    public void getPlaylist(GuildMessageReceivedEvent event) { // Создаем лист плейлиста
        GuildMusicManager guild = getGuildAudioPlayer(event.getGuild()); // Гетим Музыкального мэнеджера
        ArrayList<AudioTrack> playlist = guild.trackScheduler.getPlaylist(); // Гетим лист с музыкой
        EmbedBuilder embedBuilder = new EmbedBuilder(); // Создаем Ембед
        embedBuilder.setColor(Color.cyan); // Устанавливаем цвет Цян
        if (playlist.size() <= 10) { // Если очередь меньше или равна 10
            embedBuilder.setTitle("Playlist of this Server"); // Уставнавливаем титл.
            int queue = 1;
            for (AudioTrack track : playlist) { // Прещитуем всех и добавляем в список.

                embedBuilder.addField(
                        "#"+queue+ (queue == 1 ? " :headphones: " : " ") + track.getInfo().title,
                        "Время воспроизведение музыки: " + getTimeMusic(track.getInfo().length),
                        false);
                queue++;
            }
        } else { // Иначе
            int pages = (int) Math.ceil((double) playlist.size()/10); // Считаем сколько нужно страниц.
            embedBuilder.setTitle("Playlist of this Server | Page: 1 of " + pages); // Устанавливаем Титл.
            for (int i = 0; i < 10; i++) { // Прещитуем 10 и добавляем в список.
                embedBuilder.addField(
                        "#"+(i+1)+ ((i+1) == 1 ? " :headphones: " : " ") + playlist.get(i).getInfo().title,
                        "Время воспроизведение музыки: " + getTimeMusic(playlist.get(i).getInfo().length),
                        false);
            }
            Timer timer = new Timer();
            EmbedPlaylist embedPlaylist = new EmbedPlaylist(event,playlist,timer); // Создаем обьект Эмбед
            timer.schedule(embedPlaylist,600000);
            HashMap<Long,EmbedPlaylist> serverList; // Создаем серверный список списков
            if (MusicPlaylist.playlistHashMap.containsKey(event.getGuild().getIdLong())) {
                serverList = MusicPlaylist.playlistHashMap.get(event.getGuild().getIdLong());
            } else {
                serverList = new HashMap<>();
            }
            serverList.put(event.getGuild().getIdLong(),embedPlaylist); // Кладем лист в серверный лист
            MusicPlaylist.playlistHashMap.put(event.getGuild().getIdLong(),serverList); // Добавляем в общий серверный Плейлист где находятся в каждом сервере своих листов.
        }
        event.getChannel().sendMessage(embedBuilder.build()).queue(); // Отправляем сообщение с Эмбед.
    }

    private static boolean isYouTube(String url) {

        return url.contains("www.youtube.com") || url.contains("youtu.be");
    }

    private static boolean isPlaylist(String url) {
        return url.contains("list=");
    }

    protected synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) { // Гетер Серверного АудиоПлейра
        long guildId = Long.parseLong(guild.getId()); // Айди сервера закидуем в перемнную лонг
        GuildMusicManager musicManager = musicManagers.get(guildId); // Гетим с Серверный АудиоПлейр

        if (musicManager == null) { // Если его нету то создаем
            musicManager = new GuildMusicManager(audioPlayerManager,guild);
            musicManagers.put(guildId, musicManager); // Кладем его в Карту с АудиоПлейлистами сервера
        }
        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler()); // Устанавливаем Хандлер от АудиоПлейра сервера

        return musicManager; // Возращаем аудиоплейр сервера
    }

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track,VoiceChannel voiceChannel,boolean needConnection) { // Загружаем трек в очередь
        connectToFirstVoiceChannel(guild.getAudioManager(),voiceChannel,needConnection); // Подключаемся к голосовому чату если это нужно

        musicManager.trackScheduler.queue(track); // Трек Шелдер отвечает за очередь плейлиста. (Добавляем в очередь трек)
    }

    private static void connectToFirstVoiceChannel(AudioManager audioManager, VoiceChannel voiceChannel, boolean needConnect) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect() && needConnect) { // Если бот не подключен и не пытается подключится
            audioManager.openAudioConnection(voiceChannel); // Подключаемся к голосовому чату.
        }
    }

    private class TrackScheduler extends AudioEventAdapter {

        private final AudioPlayer player;
        private final Queue<AudioTrack> queue; // Очередь треков
        private final AudioManager audioManager; // Контроль голосового чата
        private final Guild guild;
        private int loaded = 0;

        private TrackScheduler(AudioPlayer player, AudioManager audioManager, Guild guild) {
            this.guild = guild;
            this.player = player;
                this.queue = new LinkedBlockingQueue<>();
            this.audioManager = audioManager;
        }

        public void queue(AudioTrack track) {
            loaded++;
            if (!player.startTrack(track,true)) { // Если трек уже какой то играет то попадает в очередь.
                queue.add(track);
            }

        }

        public void nextTrack() {
            if (queue.size() == 0) { // Когда закончатся треки то бот отключается от голосового чата!
                CheckerMember.removeGuild(guild);
                audioManager.closeAudioConnection();
            }
            System.out.println(guild.getName() +" | Размер очереди: " + queue.size());
            player.startTrack(queue.poll(), false); // Запускается следующий трек, при вызове queue.size будет на один меньше
        }

        @Override
        public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
            if (endReason.mayStartNext) {
                nextTrack();
            }
        }

        public ArrayList<AudioTrack> getPlaylist() {
            GuildMusicManager manager = getGuildAudioPlayer(guild);
            ArrayList<AudioTrack> playlist = new ArrayList<>(queue);
            System.out.print("\n| ");
            for (AudioTrack a: playlist) {
                System.out.print(a.getInfo().title+" | ");
            }
            playlist.add(0,manager.audioPlayer.getPlayingTrack());
            return playlist;
        }
    }

    protected class GuildMusicManager {
        public final AudioPlayer audioPlayer;
        public final TrackScheduler trackScheduler;
        private int needLoad; // Переменная которая выводит в чат дс сколько нужно загрузить всего музыки.
        private GuildMessageReceivedEvent selfMessage;

        public GuildMusicManager(AudioPlayerManager manager,Guild guild) {
            audioPlayer = manager.createPlayer(); // Создаем плейлист у АудиоМенеджера бота
            trackScheduler = new TrackScheduler(audioPlayer, guild.getAudioManager(), guild);// Создаем обьект очереди
            audioPlayer.addListener(trackScheduler); // Добавляем плейлист.
        }

        public void setSelfMessage(GuildMessageReceivedEvent selfMessage) {
            this.selfMessage = selfMessage;
        }

        public AudioPlayerSendHandler getSendHandler() {
            return new AudioPlayerSendHandler(audioPlayer); // Возращаем настройки AudioSendHandler
        }
    }

    private class AudioPlayerSendHandler implements AudioSendHandler { // Настройки AudioSendHandler
        private final AudioPlayer audioPlayer;
        private final ByteBuffer buffer;
        private final MutableAudioFrame frame;

        public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
            this.audioPlayer = audioPlayer;
            this.buffer = ByteBuffer.allocate(1024);
            this.frame = new MutableAudioFrame();
            this.frame.setBuffer(this.buffer);
        }

        @Override
        public boolean canProvide() {
            return audioPlayer.provide(frame);
        }

        @Nullable
        @Override
        public ByteBuffer provide20MsAudio() {
            ((Buffer) buffer).flip();
            return buffer;
        }

        @Override
        public boolean isOpus() {
            return true;
        }
    }

    public static String getTimeMusic(long ms) { // Метод для вывода сколько временни в музыке.
        long seconds = ms / 1000;
        int minutes = (int) seconds / 60;
        int addSeconds = (int) seconds - minutes * 60;
        return minutes + ":" + ((addSeconds < 10) ? "0" + addSeconds : addSeconds);
    }
}
