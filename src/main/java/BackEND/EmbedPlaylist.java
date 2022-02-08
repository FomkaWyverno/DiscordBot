package BackEND;

import BackEND.MusicCORE.MusicClass;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class EmbedPlaylist extends TimerTask { // Класс нужен для вывода Эмбеда
    private ArrayList<AudioTrack> playlist;
    private AudioTrack[] faceArray;
    private long idMessage;
    private boolean haveButtons;
    private int page;
    private final int maxPage;
    private final Timer timer;
    private GuildMessageReceivedEvent eventMessage;
    private final GuildMessageReceivedEvent eventMemberMessage;
    private HashMap<Long,EmbedPlaylist> playlistHashMap;

    public void setPlaylistHashMap(HashMap<Long, EmbedPlaylist> playlistHashMap) {
        this.playlistHashMap = playlistHashMap;
    }

    public EmbedPlaylist(GuildMessageReceivedEvent eventMemberMessage,ArrayList<AudioTrack> playlist, Timer timer) {
        this.eventMemberMessage = eventMemberMessage;
        this.timer = timer;
        this.playlist = playlist;
        this.faceArray = new AudioTrack[10];
        for (int i = 0; i < 10; i++) {
            faceArray[i] = playlist.get(i);
        }
        page = 1;
        maxPage = (int) Math.ceil((double) playlist.size() / 10);
    }

    public EmbedBuilder buildEmbed() {
        if (faceArray == null) {
            return null;
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.cyan);
        embedBuilder.setTitle("Playlist of this Server | Page: "+page+" of " + maxPage);
        for (int i = 0; i < faceArray.length; i++) {
            if (faceArray[i] != null) {
                embedBuilder.addField("#"+(page*10-10+1+i) + (i == 0 && page == 1 ? " :headphones: " : " ") + faceArray[i].getInfo().title,
                        "Время воспроизведение музыки: " + MusicClass.getTimeMusic(faceArray[i].getInfo().length),false);
            }
        }
        return embedBuilder;
    }

    public void setIdMessage(long idMessage) {
        this.idMessage = idMessage;
    }

    public long getIdMessage() {
        return idMessage;
    }

    public void setHaveButtons(boolean b) {
        haveButtons = b;
    }
    public boolean getHaveButtons() {
        return haveButtons;
    }

    public void turnPage(Move move) {
        switch (move) {
            case right -> nextPage();
            case left -> backPage();
            case close -> closePage();
        }
    }

    public GuildMessageReceivedEvent getEventMessage() {
        return eventMessage;
    }

    public void setEventMessage(GuildMessageReceivedEvent eventMessage) {
        this.eventMessage = eventMessage;
    }

    private void nextPage() {
        if (page+1 <= maxPage) {
            page++;
            int start = page * 10 + (page - 1) - 10;
            start = start - (page-1);
            int end = start + 10;
            int without = 0;
            for (int i = 0; start < end && start < playlist.size();start++ , i++) {
                faceArray[i] = playlist.get(start);
                without = i;
            }
            if (without != 9) {
                for (int i = without+1; i < faceArray.length; i++) {
                    faceArray[i] = null;
                }
            }
        }
    }

    private void backPage() {
        if (page-1 != 0) {
            page--;
            int start = page * 10 + (page - 1) - 10;
            start = start - (page - 1);
            int end = start + 10;
            int without = 0;
            for (int i = 0; start < end && start < playlist.size();start++ , i++) {
                faceArray[i] = playlist.get(start);
                without = i;
            }
            if (without != 9) {
                for (int i = without; i < faceArray.length; i++) {
                    faceArray[i] = null;
                }
            }
        }
    }

    private void closePage() {
        faceArray = null;
    }

    @Override
    public void run() {
        eventMemberMessage.getMessage().delete().queue();
        eventMessage.getMessage().delete().queue();
        playlistHashMap.remove(idMessage);
        timer.cancel();
    }
}
