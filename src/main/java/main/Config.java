package main;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import org.apache.commons.lang3.ArrayUtils;
import utils.Utils;

import javax.security.auth.login.CredentialException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class Config {

    private static String path;

    static {

        try {
            path = Utils.getJarContainingFolder(NestBot.class) + "//config.properties";
            File file = new File(path);

            //create config if not existing
            if (!file.exists()) {

                file.createNewFile();

                FileInputStream fs = new FileInputStream(path);

                PropertiesConfiguration conf = new PropertiesConfiguration(file);

                conf.load(fs);


                conf.addProperty("TOKEN", "");
                conf.addProperty("GUILD", "");

                conf.addProperty("VERIFY_CHANNEL", "");
                conf.addProperty("EXVERIFY_CHANNEL", "");
                conf.addProperty("RAID_CHANNEL", "");
                conf.addProperty("EXRAID_CHANNEL", "");
                conf.addProperty("EVENTRAID_CHANNEL", "");
                conf.addProperty("RUNQUOTA_CHANNEL", "");
                conf.addProperty("WRRAID_CHANNEL", "");
                conf.addProperty("RAID_COMMANDS", "");
                conf.addProperty("FEEDBACK_LOGS", "");
                conf.addProperty("SUSPENSION_LOGS", "");
                conf.addProperty("RUN_LOGS", "");
                conf.addProperty("VERIFICATION_LOGS", "");
                conf.addProperty("RAID_LEADER_VOTE", "");
                conf.addProperty("LOOTS", "");
                conf.addProperty("LOUNGE_VOICE", "");
                conf.addProperty("RAID_CATEGORY", "");
                conf.addProperty("FEEDBACK_CATEGORY", "");

                conf.addProperty("SUSPENDED", "");
                conf.addProperty("VERIFIED", "");
                conf.addProperty("EXTERMINATOR", "");

                conf.addProperty("NEST", "");
                conf.addProperty("DUNGEON", "");
                conf.addProperty("NEST_KEY", "");
                conf.addProperty("EVENT_KEY", "");
                conf.addProperty("QOT", "");
                conf.addProperty("PRIEST", "");
                conf.addProperty("PALLY", "");
                conf.addProperty("WARRIOR", "");
                conf.addProperty("KNIGHT", "");
                conf.addProperty("MYSTIC", "");
                conf.addProperty("PURI", "");
                conf.addProperty("SLOW", "");
                conf.addProperty("NITRO", "");

                conf.addProperty("TRIAL_RL", "");
                conf.addProperty("ALMOST_RL", "");
                conf.addProperty("RL", "");
                conf.addProperty("EX_RL", "");
                conf.addProperty("SECURITY", "");
                conf.addProperty("OFFICER", "");
                conf.addProperty("HEAD_RL", "");
                conf.addProperty("ADMIN", "");
                conf.addProperty("OWNER", "");
                conf.addProperty("OVERRIDE_USERS", "");

                conf.addProperty("MAX_LOCATION_REACTS", 3L);

                conf.addProperty("ACTIVITY", "PLAYING NestBot");

                conf.addProperty("EXRL_QUOTA", 5L);
                conf.addProperty("RL_QUOTA", 5L);
                conf.addProperty("ARL_QUOTA", 5L);

                conf.save();

            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }


    }

    public static Object get(String option) {
        try{
            FileInputStream fs = new FileInputStream(Utils.getJarContainingFolder(NestBot.class) + "//config.properties");

            Properties properties = new Properties();
            properties.load(fs);

            String property = properties.getProperty(option);

            return property;
        } catch ( Exception e ){

        }
        return null;
    }

    public static List<Long> getList(String option) {
        try{
            FileInputStream fs = new FileInputStream(Utils.getJarContainingFolder(NestBot.class) + "//config.properties");

            Properties properties = new Properties();
            properties.load(fs);

            String property = properties.getProperty(option);

            List<Long> list = new ArrayList<>();

            for (String s : property.split(",")) {
                list.add(Long.parseLong(s));
            }

            return list;
        } catch ( Exception e ){

        }
        return null;
    }


    public static void setActivity(String activity) {
        try{

            FileInputStream fs = new FileInputStream(path);

            Properties conf = new Properties();

            conf.load(fs);

            conf.setProperty("ACTIVITY", activity);

            conf.store(new FileOutputStream(path),null);
        } catch ( Exception e ){
        }
    }




}
