package com.sdet.sdet_practice.helpers;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class ParameterProvider {
    private static final String PARAMETERS_PATH = "configurations/conf.properties";

    private static ParameterProvider instance;
    private final Map<String, String> parameters;

    private ParameterProvider() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PARAMETERS_PATH)){
            if (inputStream == null) {
                throw new RuntimeException("Configuration file not found: " + PARAMETERS_PATH);
            }
            parameters = new HashMap<>();
            Properties prop = new Properties();
            prop.load(inputStream);
            prop.stringPropertyNames().forEach(key -> parameters.put(key, prop.getProperty(key)));
        } catch (IOException e){
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public static String get(String key){
        if(instance == null){
            instance = new ParameterProvider();
        }
        return instance.parameters.get(key);
    }
}
