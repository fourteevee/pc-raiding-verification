package main;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import utils.Utils;

import java.io.*;

public class RaidHotSwap {
    private static String path;

    static {

        try {

            path = Utils.getJarContainingFolder(NestBot.class) + "//currentRaids.json";
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

                    raidObj.put("raidRoom", "");
                    raidObj.put("raidMsg", "");
                    raidObj.put("leader", "");
                    raidObj.put("startTime", "");
                    raidObj.put("location", "");
                    raidObj.put("raidType", "");
                    raidObj.put("raidActive", "");
                    raidObj.put("finished","");
                    raidObj.put("failed", "");
                    raidObj.put("isFullSkipped", "");

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

    public static void setRaid(String leader, String raidRoom, String raidMsg, Long startTime, String location, String raidType, boolean raidActive, boolean finished, boolean failed, boolean isFullSkipped) {
        createUser(leader);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject raidObj = (JSONObject) obj.get(leader);
                raidObj.put("raidRoom", raidRoom);
                raidObj.put("raidMsg", raidMsg);
                raidObj.put("leader", leader);
                raidObj.put("startTime", startTime);
                raidObj.put("location", location);
                raidObj.put("raidType", raidType);
                raidObj.put("raidActive", raidActive);
                raidObj.put("finished", finished);
                raidObj.put("failed", failed);
                raidObj.put("isFullSkipped", isFullSkipped);
                obj.put(leader, raidObj);

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

    public static String getRaidRoom(String leader) {
        createUser(leader);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject raidObj = (JSONObject) obj.get(leader);

                return (String) raidObj.get("raidRoom");

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return "";
    }

    public static String getRaidMsg(String leader) {
        createUser(leader);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject raidObj = (JSONObject) obj.get(leader);

                return (String) raidObj.get("raidMsg");

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return "";
    }

    public static Long getStartTime(String leader) {
        createUser(leader);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject raidObj = (JSONObject) obj.get(leader);

                return (Long) raidObj.get("startTime");

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return 0L;
    }

    public static String location(String leader) {
        createUser(leader);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject raidObj = (JSONObject) obj.get(leader);

                return (String) raidObj.get("location");

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return "";
    }

    public static String getRaidType(String leader) {
        createUser(leader);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject raidObj = (JSONObject) obj.get(leader);

                return (String) raidObj.get("raidType");

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return "";
    }


    public static boolean getRaidActive(String leader) {
        createUser(leader);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject raidObj = (JSONObject) obj.get(leader);

                return (boolean) raidObj.get("raidActive");

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean getFinished(String leader) {
        createUser(leader);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject raidObj = (JSONObject) obj.get(leader);

                return (boolean) raidObj.get("finished");

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean getFailed(String leader) {
        createUser(leader);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject raidObj = (JSONObject) obj.get(leader);

                return (boolean) raidObj.get("failed");

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean getFullskipped(String leader) {
        createUser(leader);
        try {
            JSONParser parser = new JSONParser();

            try ( Reader reader = new FileReader(path) ) {

                JSONObject obj = (JSONObject) parser.parse(reader);

                JSONObject raidObj = (JSONObject) obj.get(leader);

                return (boolean) raidObj.get("isFullskipped");

            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return false;
    }

}
