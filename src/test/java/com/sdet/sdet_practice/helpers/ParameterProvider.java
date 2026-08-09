package com.sdet.sdet_practice.helpers;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public final class ParameterProvider {
    private static final String PARAMETERS_PATH = "configurations/conf.properties";

    private static ParameterProvider instance;
    private final Map<String, String> parameters;

    private ParameterProvider() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PARAMETERS_PATH)) {
            if (inputStream == null) {
                throw new RuntimeException("Файл конфигурации не найден: " + PARAMETERS_PATH);
            }

            Properties prop = new Properties();
            prop.load(inputStream);

            parameters = prop.stringPropertyNames().stream().collect(Collectors.toMap(key -> key, prop::getProperty));

        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить конфигурацию", e);
        }
    }

    public static String get(String key) {
        if(instance == null) {
            instance = new ParameterProvider();
        }

        String value = instance.parameters.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Параметр не найден: " + key);
        }
        return value;
    }
}
