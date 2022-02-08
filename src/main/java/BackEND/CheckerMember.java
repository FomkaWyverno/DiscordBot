package BackEND;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CheckerMember extends ListenerAdapter { // Нужен класс для того что бы контролировать состояние голосовых чатов,
    // если Пользователь ушел и он был последний этот класс должен остановить музыкального бота!
    private static final HashMap<Long,Boolean> guild = new HashMap<>();
    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        boolean check;
        try {
            check = guild.get(event.getGuild().getIdLong());
        } catch (NullPointerException e) {
            return; // На сервере не подключен бот к голосовому чату
        }

        if (check) {
            VoiceChannel voiceChannelSelf = event
                    .getGuild()
                    .getSelfMember()
                    .getVoiceState()
                    .getChannel();
            if (voiceChannelSelf != null) {
                try {
                    if (voiceChannelSelf.getMembers().size() == 1) {
                        removeGuild(event.getGuild());
                        event.getGuild().getAudioManager().closeAudioConnection();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace(); // НуллПоинетер может возникать когда Self or Member VoiceChannel == null
                }

            }
        }
    }

    public static void putGuild(Guild guild) {
        Long l = guild.getIdLong();
        CheckerMember.guild.put(l,true);
    }
    public static void removeGuild(Guild guild) {
        Long l = guild.getIdLong();
        CheckerMember.guild.remove(l);
        Commands.removeMusicClass(guild.getId());
    }
}
