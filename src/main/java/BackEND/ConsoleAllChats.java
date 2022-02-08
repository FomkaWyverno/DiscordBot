package BackEND;

import GUI.Controller;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;


public class ConsoleAllChats extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (!event.getMessage().getCategory().toString().equals("GC:\uD83D\uDCCBЛоги\uD83D\uDCCB(612410423542808586)")) {
            String nick = event.getAuthor().getName();
            if (!nick.equals("Wyverno-Bot")) {
                Controller.sendMessage(event.getChannel().getName() +" | "+ event.getAuthor().getName() + ": " + event.getMessage().getContentRaw());
                System.out.println(event.getChannel().getName() +" - nickname: "+ event.getAuthor().getName() + " - Server nick: " + ((event.getMember().getNickname()!=null) ? event.getMember().getNickname() : " ") + ": " + event.getMessage().getContentRaw());
            }
        }
    }
}
