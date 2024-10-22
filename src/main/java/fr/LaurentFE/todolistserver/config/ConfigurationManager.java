package fr.LaurentFE.todolistserver.config;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class ConfigurationManager {
    private static ConfigurationManager instance;
    private DBConfig dbConfig;
    private static final Logger LOGGER = LogManager.getLogger("root");

    private ConfigurationManager() {

    }
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    private String readJsonFile(String filename){
        try {
            URL url = this.getClass()
                    .getClassLoader()
                    .getResource(filename);

            if(url == null) {
                LOGGER.error("{} is not found", filename);
                throw new IllegalArgumentException(filename + " is not found");
            }
            File db_credentials = new File(url.getFile());
            Scanner myReader = new Scanner(db_credentials);
            StringBuilder json_file = new StringBuilder();
            while (myReader.hasNextLine()) {
                json_file.append(myReader.nextLine());
            }
            return json_file.toString();
        } catch (IOException e) {
            String error_msg = "JSON file not formatted properly";
            LOGGER.error(error_msg, e);
            throw new RuntimeException(error_msg, e);
        }
    }

    public void loadDBConfigurationFile(String path) {
        Gson gson = new Gson();
        dbConfig = gson.fromJson(readJsonFile(path), DBConfig.class);
    }

    public DBConfig getDbConfig() {
        if (dbConfig == null) {
            String error_msg = "Trying to read DB Configuration before it was loaded";
            LOGGER.error(error_msg);
            throw new RuntimeException(error_msg);
        }
        return dbConfig;
    }
}
