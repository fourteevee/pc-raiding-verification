package main;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import utils.Utils;

import java.io.*;

public class StatsJson {

    private static String path;

    static {

        try {

            path = Utils.getJarContainingFolder(NestBot.class) + "//userStats.json";
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
                    userObj.put("completedRaids", 0L);
                    userObj.put("keysPopped", 0L);

                    JSONObject runQuota = new JSONObject();

                    runQuota.put("EXRL_QUOTA", 0L);
                    runQuota.put("RL_QUOTA", 0L);
                    runQuota.put("ARL_QUOTA", 0L);

                    userObj.put("runQuota", runQuota);

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

    public static boolean containsUser(String userId){

        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                return obj.containsKey(userId);

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return false;
    }

    public static long getCompletedRaids(String userId){
        createUser(userId);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject userObj = (JSONObject) obj.get(userId);
                return (long) userObj.get("completedRaids");

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return 0L;
    }


    public static void addCompletedRaid(String userId){
        createUser(userId);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject userObj = (JSONObject) obj.get(userId);
                userObj.put("completedRaids", (getCompletedRaids(userId) + 1L));

                obj.put(userId, userObj);

                try ( FileWriter file = new FileWriter(path)) {
                    file.write(obj.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }


    public static long getKeysPopped(String userId){
        createUser(userId);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject userObj = (JSONObject) obj.get(userId);
                return (long) userObj.get("keysPopped");

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return 0L;
    }


    public static void addKeysPopped(String userId){
        createUser(userId);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject userObj = (JSONObject) obj.get(userId);
                userObj.put("keysPopped", (getKeysPopped(userId) + 1L));

                obj.put(userId, userObj);

                try ( FileWriter file = new FileWriter(path)) {
                    file.write(obj.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }


    public static long getQuota(String userId, String quota){
        createUser(userId);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject userObj = (JSONObject) obj.get(userId);
                JSONObject runQuota = (JSONObject) userObj.get("runQuota");

                return (long) runQuota.get(quota);

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return 0L;
    }


    public static void incrementQuota(String userId, String quota){
        createUser(userId);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject userObj = (JSONObject) obj.get(userId);
                JSONObject runQuota = (JSONObject) userObj.get("runQuota");

                runQuota.put(quota, getQuota(userId, quota) + 1L);

                userObj.put("runQuota", runQuota);
                obj.put(userId, userObj);

                try ( FileWriter file = new FileWriter(path)) {
                    file.write(obj.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }


    public static void resetWeekly() {
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                for (Object userId : obj.keySet()){
                    JSONObject userObj = (JSONObject) obj.get(userId);
                    JSONObject runQuota = new JSONObject();

                    runQuota.put("EXRL_QUOTA", 0L);
                    runQuota.put("RL_QUOTA", 0L);
                    runQuota.put("ARL_QUOTA", 0L);

                    userObj.put("runQuota", runQuota);
                    obj.put(userId, userObj);
                }

                try ( FileWriter file = new FileWriter(path)) {
                    file.write(obj.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }
}
