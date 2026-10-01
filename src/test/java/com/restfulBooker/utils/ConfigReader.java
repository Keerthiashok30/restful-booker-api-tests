package com.restfulBooker.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader
{
    private static final Properties props = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream(System.getProperty("user.dir") +"/src/test/resources/test.properties"))
        {
            props.load(fis);
        } catch (IOException e)
        {
            throw new RuntimeException("Could not load test.properties", e);
        }
    }

    private ConfigReader()
    {
    }

    public static String get(String key)
    {
        String fromEnv = System.getenv("API_" + key.toUpperCase());
        if (fromEnv != null && !fromEnv.isBlank())
        {
            return fromEnv.trim();
        }

        String value = props.getProperty(key);
        if (value == null)
        {
            throw new IllegalArgumentException("Key not found in test.properties: " + key);
        }
        return value.trim();
    }
}
