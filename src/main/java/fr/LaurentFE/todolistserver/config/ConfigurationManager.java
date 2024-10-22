package fr.LaurentFE.todolistserver.config;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ConfigurationManager {
    private static ConfigurationManager instance;
    private DBConfig dbConfig;

    private ConfigurationManager() {

    }
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    private String readJsonFile(String path){
        try {
            File db_credentials = new File(path);
            Scanner myReader = new Scanner(db_credentials);
            StringBuilder json_file = new StringBuilder();
            while (myReader.hasNextLine()) {
                json_file.append(myReader.nextLine());
            }
            return json_file.toString();
        } catch (IOException e) {
            throw new RuntimeException("JSON file not found or not formatted properly", e);
        }
    }

    public void loadDBConfigurationFile(String path) {
        Gson gson = new Gson();
        dbConfig = gson.fromJson(readJsonFile(path), DBConfig.class);
    }

    public DBConfig getDbConfig() {
        if (dbConfig == null) {
            throw new RuntimeException("Trying to read DB Configuration before it was properly loaded");
        }
        return dbConfig;
    }
}
