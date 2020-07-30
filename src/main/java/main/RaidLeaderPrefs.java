package main;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import utils.Utils;

import java.io.*;

public class RaidLeaderPrefs {
    private static String path;

    static {

        try {

            path = Utils.getJarContainingFolder(NestBot.class) + "//rlPrefs.json";
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
                    JSONObject raidObj = new JSONObject();
                    raidObj.put("rusherPreference", true);
                    raidObj.put("colorPreference", "#00ff00");

                    obj.put(userId, raidObj);

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

    public static void toggleRusher(String userId, boolean status){
        createUser(userId);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject raidObj = (JSONObject) obj.get(userId);

                raidObj.put("rusherPreference", status);

                obj.put(userId, raidObj);

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

    public static boolean getRusherPreference(String userId){
        createUser(userId);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject raidObj = (JSONObject) obj.get(userId);

                return (boolean) raidObj.get("rusherPreference");

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return true;
    }


    public static void setColorPreference(String userId, String color){
        createUser(userId);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject raidObj = (JSONObject) obj.get(userId);

                raidObj.put("colorPreference", color);

                obj.put(userId, raidObj);

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

    public static String getColorPreference(String userId){
        createUser(userId);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);
                JSONObject raidObj = (JSONObject) obj.get(userId);

                return (String) raidObj.get("colorPreference");

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return "00ff00";
    }

}
