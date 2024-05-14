package com.produktiivsusjalgijaklient.klient;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class MainUI2 extends Application {

    public static void main(String[] args) throws SQLException {
        launch(args);
    }

    private void saadaAndmed(String kasutajanimi, String parool) {
        System.out.println(kasutajanimi + " " + parool);
        /*try {
            lisaKasutaja(kasutajanimi, parool);
        } catch (KasutajaOlemasErind e) {
            System.out.println(e);
        } finally {
            System.out.println(kasutajad);
        }*/
    }

    public void start(Stage primaryStage) throws IOException {
        VBox juur = new VBox();

        juur.setPadding(new Insets(15));
        juur.setSpacing(20);
        juur.setAlignment(Pos.CENTER);

        juur.setPrefHeight(200);
        juur.setPrefWidth(500);

        Label paiseTekst = new Label("Logi sisse");
        juur.getChildren().add(paiseTekst);

        // Ankeet

        GridPane infoSisend = new GridPane();
        infoSisend.setAlignment(Pos.BASELINE_LEFT);
        infoSisend.setHgap(20);
        infoSisend.setVgap(10);

        Label kasutajaNimeSilt = new Label("Kasutajanimi:");
        TextField kasutajaNimeVali = new TextField();

        Label parooliSilt = new Label("Parool:");
        PasswordField parooliVali = new PasswordField();

        Label[] sildid = new Label[] {kasutajaNimeSilt, parooliSilt};
        TextField[] tekstiValjad = new TextField[] {kasutajaNimeVali, parooliVali};

        final int[] fokuseeritudVali = {0};

        EventHandler<KeyEvent> nupuVajutus = keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (fokuseeritudVali[0] == tekstiValjad.length - 1) saadaAndmed(kasutajaNimeVali.getText(), parooliVali.getText());
                fokuseeritudVali[0]++;
                fokuseeritudVali[0] = Math.min(fokuseeritudVali[0], tekstiValjad.length - 1);
                keyEvent.consume();
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                fokuseeritudVali[0]++;
                fokuseeritudVali[0] = Math.min(fokuseeritudVali[0], tekstiValjad.length - 1);
                keyEvent.consume();
            } else if (keyEvent.getCode() == KeyCode.UP) {
                fokuseeritudVali[0]--;
                fokuseeritudVali[0] = Math.max(fokuseeritudVali[0], 0);
                keyEvent.consume();
            }
            tekstiValjad[fokuseeritudVali[0]].requestFocus();
        };

        for (int elemendiNr = 0; elemendiNr < tekstiValjad.length; elemendiNr++) {
            infoSisend.addRow(elemendiNr, sildid[elemendiNr], tekstiValjad[elemendiNr]);
            GridPane.setHgrow(tekstiValjad[elemendiNr], Priority.ALWAYS);

            tekstiValjad[elemendiNr].setOnKeyPressed(nupuVajutus);

            final int finalElemendiNr = elemendiNr;
            tekstiValjad[elemendiNr].focusedProperty().addListener((observableValue, vanaVaartus, uusVaartus) -> {
                if (uusVaartus) {
                    fokuseeritudVali[0] = finalElemendiNr;
                }
            });
        }

        juur.getChildren().add(infoSisend);

        Button edasiNupp = new Button("Edasi");
        juur.getChildren().add(edasiNupp);

        Scene stseen1 = new Scene(juur);

        Scene stseen2 = uusKasutaja();

        stseen1.getStylesheets().add("com/produktiivsusjalgijaklient/klient/Teema.css");
        Stage peaLava = new Stage();
        peaLava.setScene(stseen1);
        peaLava.setTitle("Sisselogimine");
        peaLava.show();
        edasiNupp.setOnAction(actionEvent -> {
            saadaAndmed(kasutajaNimeVali.getText(), parooliVali.getText());
            peaLava.setScene(stseen2);
        });
    }

    /*public void lisaKasutaja(String kasutajanimi, String parool) throws KasutajaOlemasErind {
        if (kasutajad.isEmpty()) {
            kasutajad.put(kasutajanimi, parool);
        } else if (!kasutajad.containsKey(kasutajanimi)) {
            kasutajad.put(kasutajanimi, parool);
        } else {
            throw new KasutajaOlemasErind("Kasutaja juba olemas");
        }
    }*/

    private Scene uusKasutaja() {
        VBox juur = new VBox();

        juur.setPadding(new Insets(15));
        juur.setSpacing(20);
        juur.setAlignment(Pos.CENTER);

        juur.setPrefHeight(200);
        juur.setPrefWidth(500);

        Label paiseTekst = new Label("Loo uus kasutaja");
        juur.getChildren().add(paiseTekst);

        // Ankeet

        GridPane infoSisend = new GridPane();
        infoSisend.setAlignment(Pos.BASELINE_LEFT);
        infoSisend.setHgap(20);
        infoSisend.setVgap(10);

        Label kasutajaNimeSilt = new Label("Uus kasutajanimi:");
        TextField kasutajaNimeVali = new TextField();

        Label parooliSilt = new Label("Uus parool:");
        PasswordField parooliVali = new PasswordField();

        Label[] sildid = new Label[] {kasutajaNimeSilt, parooliSilt};
        TextField[] tekstiValjad = new TextField[] {kasutajaNimeVali, parooliVali};

        final int[] fokuseeritudVali = {0};

        EventHandler<KeyEvent> nupuVajutus = keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (fokuseeritudVali[0] == tekstiValjad.length - 1) saadaAndmed(kasutajaNimeVali.getText(), parooliVali.getText());
                fokuseeritudVali[0]++;
                fokuseeritudVali[0] = Math.min(fokuseeritudVali[0], tekstiValjad.length - 1);
                keyEvent.consume();
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                fokuseeritudVali[0]++;
                fokuseeritudVali[0] = Math.min(fokuseeritudVali[0], tekstiValjad.length - 1);
                keyEvent.consume();
            } else if (keyEvent.getCode() == KeyCode.UP) {
                fokuseeritudVali[0]--;
                fokuseeritudVali[0] = Math.max(fokuseeritudVali[0], 0);
                keyEvent.consume();
            }
            tekstiValjad[fokuseeritudVali[0]].requestFocus();
        };

        for (int elemendiNr = 0; elemendiNr < tekstiValjad.length; elemendiNr++) {
            infoSisend.addRow(elemendiNr, sildid[elemendiNr], tekstiValjad[elemendiNr]);
            GridPane.setHgrow(tekstiValjad[elemendiNr], Priority.ALWAYS);

            tekstiValjad[elemendiNr].setOnKeyPressed(nupuVajutus);

            final int finalElemendiNr = elemendiNr;
            tekstiValjad[elemendiNr].focusedProperty().addListener((observableValue, vanaVaartus, uusVaartus) -> {
                if (uusVaartus) {
                    fokuseeritudVali[0] = finalElemendiNr;
                }
            });
        }

        juur.getChildren().add(infoSisend);

        Button edasiNupp = new Button("Edasi");
        juur.getChildren().add(edasiNupp);

        Scene stseen = new Scene(juur);
        stseen.getStylesheets().add("com/produktiivsusjalgijaklient/klient/Teema.css");
        return stseen;
    }
}
