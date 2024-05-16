package com.produktiivsusjalgijaklient.klient;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TaimerUI extends Application {

    Taimer taimer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Font digitalFont = Font.loadFont(getClass().getResourceAsStream("digital-7.ttf"), 100);
        BorderPane juur = new BorderPane();
        taimer = new Taimer(10);
        Label taimeriSilt = new Label();
        taimeriSilt.textProperty().bind(Bindings.createStringBinding(() -> {
            int aegSekundites = taimer.getAegSekundites();
            String mark;
            if (Integer.signum(aegSekundites) == -1) {
                mark = "+";
            } else {
                mark = "";
            }
            aegSekundites = Math.abs(aegSekundites);
            int minuteid = aegSekundites / 60;
            int sekundeid = aegSekundites % 60;
            return String.format("%s%02d:%02d", mark, minuteid, sekundeid);
        }, taimer.aegSekunditesProperty()));

        taimeriSilt.setFont(digitalFont);

        juur.setCenter(taimeriSilt);
        Button lopetaNupp = new Button("Stop");
        lopetaNupp.getStyleClass().add("lopeta-nupp");
        lopetaNupp.setOnAction(e -> {taimer.close(); primaryStage.close();});

        BorderPane.setAlignment(lopetaNupp, Pos.CENTER);
        juur.setBottom(lopetaNupp);


        juur.setBackground(Background.EMPTY);

        Scene stseen = new Scene(juur, 400, 200);
        stseen.getStylesheets().add("com/produktiivsusjalgijaklient/klient/Taimer.css");
        primaryStage.setScene(stseen);
        primaryStage.show();

        taimer.alustaLoendust();
    }

    @Override
    public void stop() {
        taimer.close();
    }
}
