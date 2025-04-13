package com.mcmiddleearth.minigames.utils;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.game.AbstractGame;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;

import java.nio.charset.MalformedInputException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

public class DiscordUtil {

    public static void sendGameStart(AbstractGame game) {
        if(MiniGamesPlugin.isDiscordEnabled()) {
            String emoji = (MiniGamesPlugin.getDiscordEmoji() == null
                    || MiniGamesPlugin.getDiscordEmoji().equals("") ? "" : ":" + MiniGamesPlugin.getDiscordEmoji() + ":");
            Guild guild = DiscordSRV.getPlugin().getMainGuild();
            String tag;
            if(MiniGamesPlugin.getDiscordTag().equals("")) {
                tag = "Hey, ";
            } else {
                tag = github.scarsz.discordsrv.util.DiscordUtil.convertMentionsFromNames("@" + MiniGamesPlugin.getDiscordTag(), guild);
            }
            String discordMessage = emoji + " ***" + tag + " there is a new game!!!*** " + emoji
                    + "\n        __**Host:**__          " + game.getManager().getName()
                    + "\n        __**Title:**__            " + game.getName()
                    + "\n        __**World:**__            " + game.getWarp().getWorld().getName()
                    + "\n        __**Time Start:**__ " + getLondonTime()
                    + "\nTo join the game type in minecraft chat: ```css\n/game join " + game.getName() + "```";
            sendDiscord(discordMessage);
        }
    }

    private static String getLondonTime() {
        Calendar calendar = new GregorianCalendar();
        TimeZone zone = calendar.getTimeZone();
        zone.setID("Europe/London");
        zone.setRawOffset(0);
        calendar.setTimeZone(zone);
        SimpleDateFormat format = (SimpleDateFormat) DateFormat.getTimeInstance(DateFormat.SHORT, Locale.UK);
        format.setCalendar(calendar);
        format.applyPattern("HH:mm z");
        return format.format(calendar.getTime());
    }

    private static void sendDiscord(String message) {
        if ((MiniGamesPlugin.getDiscordChannel() != null) && (!MiniGamesPlugin.getDiscordChannel().equals("")))
        {
            DiscordSRV discordPlugin = DiscordSRV.getPlugin();
            if (discordPlugin != null)
            {
                TextChannel channel = discordPlugin.getDestinationTextChannelForGameChannelName(MiniGamesPlugin.getDiscordChannel());
                if (channel != null) {
                    github.scarsz.discordsrv.util.DiscordUtil.sendMessage(channel, message, 0, false);
                } else {
                    Logger.getLogger("MiniGamesPlugin").warning("Discord channel not found.");
                }
            }
            else
            {
                Logger.getLogger("MiniGamesPlugin").warning("DiscordSRV plugin not found.");
            }
        }
    }

}
