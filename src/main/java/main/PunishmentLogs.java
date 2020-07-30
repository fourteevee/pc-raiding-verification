package main;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import utils.Utils;

import java.io.*;

public class PunishmentLogs {
    private static String path;

    static {

        try {

            path = Utils.getJarContainingFolder(NestBot.class) + "//punishmentLogs.json";
            File file = new File(path);

            try {
                if (!file.exists()){

                    FileWriter fileWriter = new FileWriter(path);

                    JSONObject obj = new JSONObject();

                    fileWriter.write(obj.toJSONString());

                    fileWriter.flush();
                    fileWriter.close();

                }
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    public static boolean createUser(String userId){

        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                if (!obj.containsKey(userId)){
                    JSONObject userObj = new JSONObject();

                    JSONObject punishmentObj = new JSONObject();
                    punishmentObj.put("suspensionHistory", "");
                    punishmentObj.put("warningHistory", "");
                    punishmentObj.put("banHistory", "");

                    userObj.put("punishmentHistory", punishmentObj);

                    obj.put(userId, userObj);

                    try ( FileWriter file = new FileWriter(path)) {
                        file.write(obj.toJSONString());
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void logPunishment(String userId, String punishmentType, String punisher, String reason, String displayDate){
        createUser(userId);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject userObj = (JSONObject) obj.get(userId);
                JSONObject punishmentHistory = (JSONObject) userObj.get("punishmentHistory");
                String suspensionHistory = (String) punishmentHistory.get("suspensionHistory");
                String warningHistory = (String) punishmentHistory.get("warningHistory");
                String banHistory = (String) punishmentHistory.get("banHistory");

                if (punishmentType.equalsIgnoreCase("suspension")) {
                    punishmentHistory.put("suspensionHistory" , "**Punisher: **" + punisher + "\n**Reason: **" + reason + "\n**Date:** " + displayDate + "\n" + "\n" + suspensionHistory);
                } if (punishmentType.equalsIgnoreCase("warning")) {
                    punishmentHistory.put("warningHistory" , "**Punisher: **" + punisher + "\n**Reason: **" + reason + "\n**Date:** " + displayDate + "\n" + "\n" + warningHistory + "\n****");
                } if (punishmentType.equalsIgnoreCase("ban")) {
                    punishmentHistory.put("banHistory" , "**Punisher: **" + punisher + "\n**Reason: **" + reason + "\n**Date:** " + displayDate + "\n" + "\n" + banHistory + "\n****");
                }

                userObj.put("punishmentHistory", punishmentHistory);
                obj.put(userId, userObj);

                try ( FileWriter file = new FileWriter(path)) {
                    file.write(obj.toJSONString());
                }

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    public static String getPunishments(String userId, String punishmentType){
        createUser(userId);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject userObj = (JSONObject) obj.get(userId);
                JSONObject punishmentHistory = (JSONObject) userObj.get("punishmentHistory");
                String suspensionHistory = (String) punishmentHistory.get("suspensionHistory");
                String warningHistory = (String) punishmentHistory.get("warningHistory");
                String banHistory = (String) punishmentHistory.get("banHistory");


                if (punishmentType.equalsIgnoreCase("suspension")) {
                    return suspensionHistory;
                } if (punishmentType.equalsIgnoreCase("warning")) {
                    return warningHistory;
                } if (punishmentType.equalsIgnoreCase("ban")) {
                    return banHistory;
                }


            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return "";
    }
}

