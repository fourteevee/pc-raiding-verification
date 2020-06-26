package listeners;

import main.Constants;
import main.NestBot;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class MessageListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){

        if (event.getChannel() == NestBot.getGuild().getTextChannelById(Constants.LOOTS)) {
            if (event.getMessage().getContentRaw().contains("http") || !event.getMessage().getAttachments().isEmpty())
                return;
            else
                event.getMessage().delete();
        }
        
    }

}
