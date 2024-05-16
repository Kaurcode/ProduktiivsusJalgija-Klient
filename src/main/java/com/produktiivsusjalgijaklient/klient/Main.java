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
            System.out.println(andmeHaldur.logiSisse("Kaur", "TestParool".toCharArray()));
            andmeHaldur.kirjutaLogi("Test");
        }
    }
}