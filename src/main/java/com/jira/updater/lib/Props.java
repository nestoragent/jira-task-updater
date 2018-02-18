package com.jira.updater.lib;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by sbt-velichko-aa on 02.03.2016.
 */
public class Props {

    private static Props instance;
    private static Properties props;

    public synchronized static Props getInstance() {
        if (instance == null) {
            instance = new Props();
        }
        return instance;
    }

    /**
     * load properties
     */
    public Props() {
        try {
            props = new Properties();
            
            String configFile = System.getProperty("BDDConfigFile", "config/application.properties");
            System.out.println("Loading properties from: " + configFile);
            //first try
            
            /**
             * Load common properties
             */
            InputStream in = Props.class.getClassLoader().getResourceAsStream(configFile);
            //if failed, second try
            if (in == null) {
                in = new FileInputStream(configFile);
            }
            props.load(in);
        } catch (IOException e) {
            System.err.println("Failed to initialize props. Error Message = " + e.getMessage());
        }
    }

    public String getProp(String name) {
        String val = getProps().getProperty(name, "");
        if (val.isEmpty()) {
            System.err.printf("Property %s was not found on Props", val);
        }
        return val.trim();
    }

    public static String get(String prop) {
        return Props.getInstance().getProp(prop);
    }

    public static String get(String prop, String defaultValue) {
        String val = getProps().getProperty(prop);
        if (val.isEmpty()) {
            return defaultValue;
        }
        return val.trim();
    }

    public static Properties getProps() {
        return props;
    }
}
