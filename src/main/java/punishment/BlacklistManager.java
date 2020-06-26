package punishment;

import utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the black listed realm eye users.
 * Created by MistaCat 10/10/2018
 */
public class BlacklistManager {
    private static final File PUNISHMENT_FOLDER = new File("Punishments");
    private static final String BLACKLIST = "blacklist.txt";

    public static void setupBlacklist() {
        if (PUNISHMENT_FOLDER.exists())
            return;

        PUNISHMENT_FOLDER.mkdir();
        File f = new File(PUNISHMENT_FOLDER + "/" + BLACKLIST);
        try {
            f.createNewFile();
        } catch (Exception ex) {
            Utils.sendConsoleDebug("Failed to create blacklist!");
        }
    }

    /**
     * Gets a list of blacklisted realmeye names.
     * @return
     */
    public static List<String> getBlacklist() {
        try {
            List<String> blacklist = new ArrayList<>();
            BufferedReader data = new BufferedReader(new FileReader(new File(PUNISHMENT_FOLDER, BLACKLIST)));
            data.lines().forEach(blacklist::add);
            return blacklist;
        } catch (Exception ex) {
            Utils.sendConsoleDebug("Exception in getting blacklist!");
        }

        return null;
    }

    /**
     * Adds a string to the black list
     * @param name
     */
    public static void addToBlacklist(String name) {
        List<String> blacklist = getBlacklist();
        blacklist.add(name.toLowerCase());
        saveBlacklist(blacklist);
    }

    /**
     * Removes a string from the blacklist
     * @param name
     */
    public static void removeFromBlacklist(String name) {
        List<String> blacklist = getBlacklist();
        blacklist.remove(name.toLowerCase());
        saveBlacklist(blacklist);
    }

    /**
     * Checks if a name is blacklisted.
     * @param name
     * @return
     */
    public static boolean isBlacklisted(String name) {
        return getBlacklist().contains(name.toLowerCase());
    }

    /**
     * Saves the blacklist.
     * @param blacklist
     */
    public static void saveBlacklist(List<String> blacklist) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(PUNISHMENT_FOLDER, BLACKLIST)));
            for (String line : blacklist)
                writer.write(line);
            writer.close();
        } catch (Exception ex) {
            Utils.sendConsoleDebug("Failed to save blacklist!");
        }
    }
}