package com.produktiivsusjalgijaklient.klient;

import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try (LokaalneAndmeHaldur andmeHaldur = new LokaalneAndmeHaldur("produktiivsusjalgija")) {
            andmeHaldur.looKasutaja("Hannes", "parool".toCharArray());
            andmeHaldur.kirjutaLogi("Test");
        }
    }
}