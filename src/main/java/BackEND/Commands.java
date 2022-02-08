package BackEND;


import BackEND.MusicCORE.MusicClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Commands extends ListenerAdapter {

    private static final HashMap<Long,Boolean> leaders = new HashMap<>();
    private static final HashMap<Long,Boolean> playlist = new HashMap<>();


    private static Map<String, MusicClass> musicIdGuild;

    public static Map<String, MusicClass> getMusicIdGuild() {
        return musicIdGuild;
    }

    public static void removeMusicClass(String idGuild) {
        musicIdGuild.remove(idGuild);
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        boolean logs = event.getMessage().getCategory().toString().equals("GC:\uD83D\uDCCBЛоги\uD83D\uDCCB(612410423542808586)");
        if (!logs) {
            String nick = event.getAuthor().getName();
            String id = event.getAuthor().getId();

            boolean bot = event.getAuthor().isBot();

            try {
                if (leaders.get(event.getGuild().getIdLong())) {
                    if (event.getAuthor().isBot() &&
                            event.getAuthor().getId().equals("805383894613884949")) {
                        CheckReactionForLeaders.setId(event.getMessageId());
                        event.getMessage().addReaction("⬅️").queue();
                        event.getMessage().addReaction("➡️").queue();
                        event.getMessage().addReaction("❌").queue();
                        CheckReactionForLeaders.setEventMessage(event);

                        leaders.put(event.getGuild().getIdLong(),false);
                    }
                    return;
                }
            } catch (NullPointerException e) { }
            try {
                if (playlist.get(event.getGuild().getIdLong())) {

                }
            } catch (NullPointerException e) { }

            String messageSent = event.getMessage().getContentRaw();
            if (messageSent.split(" ",2)[0].equalsIgnoreCase("!play")) {
                try {
                    String music = messageSent.split(" ",2)[1].trim();
                    if (musicIdGuild == null) { // Если Map не создан создаем его и добавляем первый элемент
                        musicIdGuild = new HashMap<>();
                        musicIdGuild.put(event.getGuild().getId(),new MusicClass());
                    }
                    MusicClass musicClass; // Создаем переменную MusicClass
                    if (musicIdGuild.containsKey(event.getGuild().getId())) { // Узнаем если на сервере уже созданный
                        musicClass = musicIdGuild.get(event.getGuild().getId());
                    } else {
                        musicClass = new MusicClass();
                        musicIdGuild.put(event.getGuild().getId(),musicClass);
                    }
                    musicClass.startOrAddMusic(event,music, true);
                } catch (ArrayIndexOutOfBoundsException e) {
                    event.getChannel().sendMessage("Введите ссылку на музыку!").queue();
                }

                return;
            }

            /*if (messageSent.contains("!rankRL")) { Вырезаная команда !rankRL
                RankDiscordChecker.removeXp(id);
                String[] split = messageSent.split(" ",3);
                if (split.length <= 1) {
                    event.getChannel().sendMessage("Напишите команду правильно!" + "\n" + "!rankRL [Epic/PS4/XBox/Steam] [Nick or SteamLink]").queue();
                } else if (split.length == 3) {
                    String[] mmr = null;
                    try {
                        mmr = Check_Rank.mmr(split[1],split[2]);
                        byte[] image = DrawCard.getPanel(Check_Rank.name_rank(mmr[0], Check_Rank.type_match.oneS),
                                Check_Rank.name_rank(mmr[1], Check_Rank.type_match.doubles),
                                Check_Rank.name_rank(mmr[2], Check_Rank.type_match.standart),
                                Check_Rank.name_rank(mmr[3], Check_Rank.type_match.tournament),
                                Check_Rank.name_rank(mmr[4], Check_Rank.type_match.hoops),
                                Check_Rank.name_rank(mmr[5], Check_Rank.type_match.rumble),
                                Check_Rank.name_rank(mmr[6], Check_Rank.type_match.dropShot),
                                Check_Rank.name_rank(mmr[7], Check_Rank.type_match.snowDay),
                                mmr[0],mmr[1],mmr[2],mmr[3],mmr[4],mmr[5],mmr[6],mmr[7],Check_Rank.getSeasonReward(),
                                split[1],split[2]);
                        event.getChannel().sendFile(image,"result.jpg").queue();
                    } catch (Check_Rank.isNotNickOrPlatformException e) {
                        event.getChannel().sendMessage("Был введен не верный nickname или платформа, пожалуйста проверьте правильность nickname и платформы.")
                                .queue();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Check_Rank.infinityCycleException e) {
                        event.getChannel().sendMessage("Не удалось подключиться к серверу по поиску информации о игроке, попробуйте позже." + "\n" +
                                "Возможно вы не правильно написали ник :thinking: ")
                                .queue();
                    }
                }
            }*/
            messageSent = messageSent.trim();
            messageSent = messageSent.toLowerCase();
            switch (messageSent) {
                case "!stop" : {
                    MusicClass musicClass = musicIdGuild.get(event.getGuild().getId());
                    if (musicClass != null) {
                        VoiceChannel voiceChannelSelf = event
                                .getGuild()
                                .getSelfMember()
                                .getVoiceState()
                                .getChannel();
                        VoiceChannel voiceChannelMember = event
                                .getMember()
                                .getVoiceState()
                                .getChannel();
                        try {
                            if (musicClass.havePermission(event.getMember().getRoles())) {
                                if (voiceChannelMember.equals(voiceChannelSelf)) { // Если человек в одном находится голосовом канале с ботом то останавливаем его.
                                    event.getChannel().sendMessage("Бот остановлен, и очищен плейлист!").queue();
                                    event.getGuild().getAudioManager().closeAudioConnection(); // Отключаем бота от голосового чата.
                                    musicIdGuild.remove(event.getGuild().getId()); // Удаляем плейлист и управление им с листа.
                                } else { // Если нет то просим его зайти в канал с ботом.
                                    event.getChannel().sendMessage("Вы должны находится в одном голосовом чате с ботом!")
                                            .queue();
                                }
                            } else {

                            }

                        } catch (NullPointerException e) { // Если человек не в голосовом канале то перехватываем и отсылаем что он должен находится с ботом!
                            event.getChannel().sendMessage("Вы должны находится в одном голосовом чате с ботом!")
                                    .queue();
                        }
                    } else { // Если бот не подключен был
                        event.getChannel().sendMessage("Бот не подключен ни к единому голосовому чату на сервере!")
                                .queue();
                    }
                }
                    break;
                case "!skip" : {
                    MusicClass musicClass = musicIdGuild.get(event.getGuild().getId());
                    if (musicClass != null) {
                        VoiceChannel voiceChannelSelf = event.
                                getGuild().
                                getSelfMember().
                                getVoiceState().
                                getChannel();
                        VoiceChannel voiceChannelMember = event.
                                getMember().
                                getVoiceState().
                                getChannel();
                        try {
                            if (voiceChannelMember.equals(voiceChannelSelf)) {
                                musicClass.skip(event);
                            } else {
                                event.getChannel().sendMessage("Вы находитесь в другом голосовом чате, нужно быть с ботом что бы пропустить трек!")
                                        .queue();
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            event.getChannel().sendMessage("Вы должны находится в голосовом чате с ботом, что бы пропустить трек!")
                                    .queue();
                        }

                    } else {
                        event.getChannel().sendMessage("Бот как бы даже не работает)")
                                .queue();
                    }
                }
                    break;

                case "!playlist" : {
                    MusicClass musicClass;
                    try {
                        musicClass = musicIdGuild.get(event.getGuild().getId());
                    } catch (NullPointerException e) {
                        event.getChannel().sendMessage("Бот ещё не работает, что бы запустить напишите !play [URL-YT/YT_Playlist/Name-Music]")
                                .queue();
                        break;
                    }
                    musicClass.getPlaylist(event);
                    break;
                }
                case "!rank" :
                    if (bot) { break; }
                    try {
                        byte[] image = DrawRankCard.getRankInfo(event.getAuthor().getAvatarUrl(),
                                RankDiscordChecker.getRank(event.getMember().getId(),
                                        event.getMember().getEffectiveName()),
                                event.getMember().getOnlineStatus());
                        event.getChannel().sendFile(image,"rank.png").queue();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "!playserverplaylist" : {
                    if (musicIdGuild == null) {
                        musicIdGuild = new HashMap<>();
                        musicIdGuild.put(event.getGuild().getId(),new MusicClass());
                    }
                    if (musicIdGuild.containsKey(event.getGuild().getId())) {
                        if (musicIdGuild.get(event.getGuild().getId()).isMutePlaylist()) {
                            event.getChannel().sendMessage("Loading already!").queue();
                            break;
                        }
                    }
                    MusicClass musicClass; // Создаем переменную MusicClass
                    if (musicIdGuild.containsKey(event.getGuild().getId())) { // Узнаем если на сервере уже созданный
                        musicClass = musicIdGuild.get(event.getGuild().getId());
                    } else {
                        musicClass = new MusicClass();
                        musicIdGuild.put(event.getGuild().getId(),musicClass);
                    }
                    musicClass.playServerPlaylist(event);
                    break;
                }

                case "!help" :
                    if (bot) { break; }
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(":wrench: Commands");
                    embedBuilder.setColor(Color.cyan);
                    embedBuilder.addField("!rankRL [STEAM/EPIC/PS4/XBOX] [NickName]","Выводит полную информацию об игроке с Рокет Лигы.",false);
                    embedBuilder.addField("!rankRL","Скоро будет полностю вырезан.",false);
                    embedBuilder.addField("!rank","Выводит информацию о вашем ранге в Discord-Server.\n" +
                                                "Рекомендуется эту команду использовать если вы хотите проверить свой ранг.",false);
                    embedBuilder.addField("!rank [@Участник Discord-Server]","Выводит информацию о ранге этого человека в Discord-Server.",false);
                    embedBuilder.addField("!crystal","Выводит информацию о состоянии ваших кристалов",false);
                    embedBuilder.addField("!money","Аналог команды !crystal",false);
                    embedBuilder.addField("!balance","Аналог команды !crystal",false);
                    embedBuilder.addField("!stats","Выводит информацию что и сколько вы делалали что то на сервере", false);
                    embedBuilder.addField("!leaders","Выводит таблицу рейтингов нашего Discord-Server.",false);
                    embedBuilder.addField("!infoRank","Выводит таблицу которая покажет какой вам нужен уровень \n" +
                                                "что-бы получить новый ранг.",false);
                    embedBuilder.addField("!play [URL / URL Playlist / Название музыки]","Воспрозвести музыку ((В РАЗРАБОТКЕ!))",false);
                    embedBuilder.addField("!playServerPlaylist", "Запускает и загружает всю музыку из \"\uD83C\uDFBCarchive-cool-musics\", ((В РАЗРАБОТКЕ!))",false);
                    embedBuilder.addField("!stop","Остановить музыку((В РАЗРАБОТКЕ!))",false);
                    embedBuilder.addField("!skip","Пропустить музыку((В РАЗРАБОТКЕ!))",false);
                    event.getChannel().sendTyping().queue();
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                    embedBuilder.clear();

                    break;
                case "!top" :
                case "!ledaers" :
                case "!leaders" :
                    EmbedBuilder emb = new EmbedBuilder();
                    emb.setTitle("Страница 1 из " +
                            CheckReactionForLeaders.getPages(RankDiscordChecker.getSizeRating(),10.0)
                            + " - Всего людей в рейтинге - " + RankDiscordChecker.getSizeRating());
                    emb.setColor(Color.cyan);
                    event.getChannel().sendMessage(emb.build()).queue();
                    emb.clear();
                    event.getMessage().delete().queue();
                    leaders.put(event.getGuild().getIdLong(),true);
                    break;
                case "!inforank" :
                    try {
                        byte[] file = DrawRankCard.getInfoPanel();
                        event.getChannel().sendFile(file,"ranks.png").queue();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "!crystal" :
                case "!balance" :
                case "!money" :
                    getCrystals(event);
                    break;
                case "!stats" :
                    try {
                        byte[] image = DrawStatsChecker.getStats(event.getMember().getId(),
                                event.getMember().getEffectiveName(),
                                event.getMember().getOnlineStatus(),
                                event.getMember().getUser().getAvatarUrl());
                        event.getChannel().sendFile(image,"stats.png")
                                .queue();
                    } catch (NullPointerException e) {
                        event.getChannel().sendMessage("В процессе разработки.\n" +
                                "Вы ещё не сидели в голосовом чате.").queue();
                    }


                    break;
            }
            if (messageSent.contains("!rank <@!")) {
                try {
                    String rid = messageSent.substring(messageSent.lastIndexOf("!")+1,messageSent.indexOf(">"));
                    byte[] image = DrawRankCard.getOtherRank(rid);
                    event.getChannel().sendFile(image,"rankOther.png").queue();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                } catch (DrawRankCard.isBotException e) {
                    event.getChannel().sendMessage("У ботов нету рангов :sob:").queue();
                }
            }
            if (!bot && !messageSent.equals("!rank")) {
                RankDiscordChecker.addXp(id,nick);
            }

        }
    }

    private static void getCrystals(GuildMessageReceivedEvent event){
        try {
            byte[] image = DrawRankCard.getMyCrystal(event.getMember().getUser().getId(),event.getMember().getEffectiveName());
            event.getChannel().sendFile(image,"crystal.png").queue();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
