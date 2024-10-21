package fr.LaurentFE.todolistserver;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class DBInfoReader {
    private final String db_url;
    private final String db_user;
    private final String db_pass;

    public DBInfoReader() {
        try {
            File db_credentials = new File("src/main/resources/db-connection-infos");
            Scanner myReader = new Scanner(db_credentials);
            db_url = myReader.nextLine();
            db_user = myReader.nextLine();
            db_pass = myReader.nextLine();
        } catch (IOException e) {
            throw new RuntimeException("db-connection-infos file not found or not formatted properly", e);
        }
    }

    public String getDb_url() {
        return db_url;
    }

    public String getDb_user() {
        return db_user;
    }

    public String getDb_pass() {
        return db_pass;
    }
}
