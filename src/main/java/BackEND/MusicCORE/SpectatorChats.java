package BackEND.MusicCORE;

import BackEND.App;
import BackEND.Commands;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SpectatorChats extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (Commands.getMusicIdGuild() != null) {
            if(event.getAuthor().getId().equals(App.getJDA().getSelfUser().getId())) {
                if (MusicClass.START_PLAYLIST.equals(event.getMessage().getContentRaw())) {
                    Commands.getMusicIdGuild().get(event.getGuild().getId())
                            .getGuildAudioPlayer(event.getGuild()).setSelfMessage(event);

                }
            }
        }

    }
}
