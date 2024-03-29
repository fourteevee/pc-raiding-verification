package utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Various utilities for the nest bot.
 * Created by MistaCat 10/7/2018
 */
public class Utils {

    /**
     * Sends a msg to console. Good for logging what people are doing or testing code.
     * @param msg
     */
    public static void sendConsoleDebug(String msg) {
        System.out.println(msg);
    }

    /**
     * Sends a plain text msg to a specified channel.
     * @param channel
     * @param msg
     */
    public static Message sendMessage(TextChannel channel, String msg) {
        try {
            return channel.sendMessage(msg).submit().get();
        } catch ( Exception e ){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sends an embed object message to a specified channel.
     * @param channel
     * @param embedBuilder
     * @return
     */
    public static Message sendEmbed(TextChannel channel, EmbedBuilder embedBuilder) {
        try {
            return channel.sendMessage(embedBuilder.build()).submit().get();
        } catch ( Exception e ){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sends an embed and normal text message to a specified channel.
     * @param channel
     * @param msg
     * @param embedBuilder
     * @return
     */
    public static Message sendMessageWithEmbed(TextChannel channel, String msg, EmbedBuilder embedBuilder) {
        try {
            return channel.sendMessage(msg).embed(embedBuilder.build()).submit().get();
        } catch ( Exception e ){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds a reaction emoji to a message.
     * @param msg
     * @param emote
     */
    public static void addReaction(Message msg, Emote emote) {
        msg.addReaction(emote).submit();
    }

    /**
     * Adds a reaction emoji to a message.
     * @param msg
     * @param unicode
     */
    public static void addReaction(Message msg, String unicode) {
        msg.addReaction(unicode).submit();
    }

    /**
     * Changes a text channel's permission on a user basis.
     * @param channel
     * @param permissionHolder
     * @param allow
     * @param deny
     */
    public static void updateChannelPerms(GuildChannel channel, IPermissionHolder permissionHolder, EnumSet<Permission> allow, EnumSet<Permission> deny) {
        channel.upsertPermissionOverride(permissionHolder).grant(allow).deny(deny).submit();
    }

    /**
     * Changes a text channel's permission on a user basis.
     * @param channel
     * @param user
     * @param allow
     * @param deny
     */
    public static void updateChannelPerms(GuildChannel channel, User user, EnumSet<Permission> allow, EnumSet<Permission> deny) {
        channel.upsertPermissionOverride(channel.getGuild().getMember(user)).grant(allow).deny(deny).submit();
    }

    /**
     * Moves a discord user to a specified channel.
     * @param member
     * @param target
     */
    public static void moveUser(Member member, VoiceChannel target) {
        member.getGuild().moveVoiceMember(member, target).submit();
    }



    /**
     * Creates a single string from an array of args.
     * @param args
     * @return
     */
    public static String singleArg(String[] args) {
        String arg = args[0];
        for (String word : Arrays.copyOfRange(args, 1, args.length))
            arg = arg + " " + word;

        return arg;
    }

    /**
     * Sends a PM to a specific discord user.
     * @param user
     * @param msg
     * @return
     */
    public static Message sendPM(User user, String msg) {
        return user.openPrivateChannel().complete().sendMessage(msg).complete();
    }

    public static Message sendPMEmbed(User user, EmbedBuilder embedBuilder) {
        AtomicReference<Message> message = new AtomicReference<>();
        user.openPrivateChannel().queue(privateChannel -> {
            try {
                message.set(privateChannel.sendMessage(embedBuilder.build()).submit().get());
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        });
        return message.get();
    }

    /**
     * Turn milliseconds into a user friendly string in relation to the current time epoch.
     * @param time
     * @return formatted
     */
    public static String formatTimeFromNow(long time) {
        return formatTime(time == -1 ? -1 : time - System.currentTimeMillis());
    }

    /**
     * Turn milliseconds into a user friendly string.
     * @param time
     * @return formatted
     */
    public static String formatTime(long time) {
        if (time == -1)
            return "never";

        time /= 1000;
        StringBuilder display = new StringBuilder();
        for (int i = 0; i < TimeInterval.values().length; i++) {
            TimeInterval iv = TimeInterval.values()[TimeInterval.values().length - i - 1];
            if (time >= iv.getInterval()) {
                int temp = (int) (time - (time % iv.getInterval()));
                int add = temp / iv.getInterval();
                display.append(" ");
                display.append(add);
                display.append(iv.getSuffix());
                display.append(add > 1 && iv != TimeInterval.SECOND ? "s" : "");
                time -= temp;
            }
        }

        return display.length() > 0 ? display.toString().substring(1) : "0s";
    }

    /**
     * Format milliseconds based into a friendly display based on their distance from the current time.
     * @param time
     * @return formatted
     */
    public static String formatTimeFullFromNow(long time) {
        return formatTimeFull(time == -1 ? -1 : time - System.currentTimeMillis());
    }

    /**
     * Formats milliseconds into a user friendly display.
     * Different from formatTime because this does not use abbreviations.
     * @param time
     * @return formatted
     */
    public static String formatTimeFull(long time) {
        if (System.currentTimeMillis() + time == -1 || time == -1)
            return "Never";

        time /= 1000;
        StringBuilder display = new StringBuilder();
        for (int i = 0; i < TimeInterval.values().length; i++) {
            TimeInterval iv = TimeInterval.values()[TimeInterval.values().length - i - 1];
            if (time >= iv.getInterval()) {
                int temp = (int) (time - (time % iv.getInterval()));
                int add = temp / iv.getInterval();
                display.append(" ");
                display.append(add);
                display.append(" ");
                display.append(iv.name().toLowerCase());
                display.append(add > 1 ? "s" : "");
                time -= temp;
            }
        }

        return display.length() > 0 ? display.toString().substring(1) : "0 Seconds";
    }

    /**
     * Convert user input ie: "3d 2h" into a date relative to zero.
     * @param input
     * @return date
     */
    public static long fromInput(String input) {
        if (input.startsWith("-"))
            return -1; // "Never"

        long time = 0;
        for (String s : input.split(" ")) {
            String code = StringUtils.isNumeric(s) ? "s" : s.substring(s.length() - 1);
            time += Long.parseLong(s.substring(0, Math.max(1,(StringUtils.isNumeric(s) ? s.length() : s.length() - 1)))) * (long) TimeInterval.getByCode(code).getInterval() * 1000L;
        }
        return time;
    }

    /**
     * Load the amount of time from now a string such as "3d 2h" is.
     * @param input
     * @return date
     */
    public static long fromInputFuture(String input) {
        long time = fromInput(input);
        return time >= 0 ? time + System.currentTimeMillis() : -1;
    }

    /**
     * Get the Folder that contains the .jar file
     * @param aclass
     * @return string
     * @throws Exception
     */
    public static String getJarContainingFolder(Class aclass) throws Exception {
        CodeSource codeSource = aclass.getProtectionDomain().getCodeSource();

        File jarFile;

        if (codeSource.getLocation() != null) {
            jarFile = new File(codeSource.getLocation().toURI());
        }
        else {
            String path = aclass.getResource(aclass.getSimpleName() + ".class").getPath();
            String jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
            jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
            jarFile = new File(jarFilePath);
        }
        return jarFile.getParentFile().getAbsolutePath();
    }
    public static String toAlphaNumeric(String string) {
        String newString = StringUtils.remove(string, "*");
        newString = StringUtils.remove(newString, "$");
        newString = StringUtils.remove(newString, "'");
        newString = StringUtils.remove(newString, "(");
        newString = StringUtils.remove(newString, ")");
        newString = StringUtils.remove(newString, "!");
        newString = StringUtils.remove(newString, "#");
        newString = StringUtils.remove(newString, "^");
        newString = StringUtils.remove(newString, "%");
        newString = StringUtils.remove(newString, "@");
        newString = StringUtils.remove(newString, "-");
        newString = StringUtils.remove(newString, ">");
        newString = StringUtils.remove(newString, "<");
        newString = StringUtils.remove(newString, "?");
        return newString;
    }
}

