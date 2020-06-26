package commands.miscCommands;

import commands.Command;
import main.Constants;
import main.NestBot;
import main.Rank;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import punishment.BlacklistManager;
import stats.Character;
import stats.RealmPlayer;
import stats.Verification;
import utils.Utils;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class CommandVerify extends Command {
    public CommandVerify() {
        setAliases(new String[] {"verify"});
        setMinRank(Rank.DEFAULT);
    }

    @Override
    public void execute(Message msg, String alias, String[] args) {
        if (msg.getMember().getRoles().contains(NestBot.getGuild().getRoleById(Constants.VERIFIED))) {
            Utils.sendPM(msg.getAuthor(), "You're already verified silly!");
            return;
        }

        if (args.length == 0) {
            if (Verification.getVerificationRequests().containsKey(msg.getAuthor())) {
                Utils.sendPM(msg.getAuthor(), "You currently have an active vericode: " + Verification.getVerificationRequests().get(msg.getAuthor()) +
                        "\nPlease enter that into your realmeye description and type -verify [realmeye name]");
                return;
            }

            String vericode = Verification.requestVerificationUser(msg.getAuthor());
            Utils.sendPM(msg.getAuthor(), "Thank you for verifying your account on pest control!\n" +
                    "Your verification code is: " + vericode +
                    "\nPlace the verification code in any of your realmeye description bars.\n" +
                    "Once you have saved the code to your realmeye description type -verify [realmeye name].\n" +
                    "You can verify in the verification channel! Realm eye names are NOT case-sensitive!");
            return;
        }

        if (BlacklistManager.isBlacklisted(args[0].toLowerCase())) {
            Utils.sendPM(msg.getAuthor(), "This realmeye name is blacklisted! You may not verify on this server!");
            return;
        }

        if (!Verification.getVerificationRequests().containsKey(msg.getAuthor())) {
            Utils.sendPM(msg.getAuthor(), "You don't have a pending verification!\n" +
                    "Please type -verify in the verification channel to get a code!");
            return;
        }

        RealmPlayer realmPlayer;
        try {
            realmPlayer = Verification.getRealmPlayer(args[0].replaceAll("[^A-Za-z0-9]", ""));
        } catch ( RealmPlayer.PrivateProfileException e ) {
            Utils.sendPM(msg.getAuthor(), "Please ensure your realmeye information is public!");
            return;
        }

        if (realmPlayer == null) {
            Utils.sendPM(msg.getAuthor(), "Could not get realmeye information.\n" +
                    "Please make sure your realmeye name is correct. If issues persist contact Amelia#3301 on discord!");
            return;
        }

        if (realmPlayer.getFame() < Verification.FAME_REQ) {
            Utils.sendPM(msg.getAuthor(), "You do not meet the required " + Verification.FAME_REQ + " alive fame!");
            return;
        }

        if (realmPlayer.getRank() < Verification.STAR_REQ) {
            Utils.sendPM(msg.getAuthor(), "You do not meet the required " + Verification.FAME_REQ + " class stars!");
            return;
        }

        List<Character> characters = realmPlayer.getCharacters();
        int points = 0;
        for (Character character : characters) {
            if (character.getStats_maxed() == 6 || character.getStats_maxed() == 7)
                points = points + 2;
            else if (character.getStats_maxed() == 8)
                points = points + 3;
        }

        if (points < Verification.CLASS_REQ) {
            Utils.sendPM(msg.getAuthor(), "You do not have the classes required to verify! (1 - 6/8)");
            return;
        }

        String description = realmPlayer.getDescription();
        if (StringUtils.containsIgnoreCase(description, Verification.getVerificationRequests().get(msg.getAuthor()).toLowerCase())) {
            NestBot.getGuild().addRoleToMember(msg.getMember(), NestBot.getGuild().getRoleById(Constants.VERIFIED)).submit();

            String ignName = realmPlayer.getUsername();
            if (!ignName.equals(msg.getMember().getEffectiveName())){
                msg.getMember().modifyNickname(ignName).submit();
            } else if (!ignName.toLowerCase().equals(msg.getMember().getEffectiveName())){
                msg.getMember().modifyNickname(ignName.toLowerCase()).submit();
            } else {
                msg.getMember().modifyNickname(StringUtils.capitalize(ignName)).submit();
            }
            Verification.getVerificationRequests().remove(msg.getAuthor());
            Utils.sendPM(msg.getAuthor(), "Thank you for verifying in pest control! Good luck on the nest raids!");
            logVerification(msg.getAuthor(), ignName);
            return;
        }

        Utils.sendPM(msg.getAuthor(), "Couldn't find the vericode in your description!\n" +
                "Please make sure its in it's own line and wait a few seconds for the servers to save before trying again!");
    }

    private void logVerification(User user, String name) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Verification success! - " + user.getName());
        embedBuilder.setColor(new Color(0, 255, 0));
        embedBuilder.setThumbnail(user.getAvatarUrl());
        embedBuilder.setDescription("Name: " + name +
                "\nRealmeye: " + "https://www.realmeye.com/player/" + name);
        embedBuilder.setFooter("Date: " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));

        Utils.sendEmbed(NestBot.getGuild().getTextChannelById(Constants.VERIFICATION_LOGS), embedBuilder);
    }

    @Override
    public EmbedBuilder getInfo() {
        String aliases = "";
        for (String alias : getAliases())
            aliases = aliases + " " + alias;

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Command: Verify");
        embedBuilder.addField("Required rank", "None", false);
        embedBuilder.addField("Syntax", "-alias <realmeye name>", false);
        embedBuilder.addField("Aliases", aliases, false);
        embedBuilder.addField("Information", "Verifies that a discord account is linked to a realmeye account", false);
        return embedBuilder;
}
}
