package com.produktiivsusjalgijaklient.klient;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainUI extends Application {

    public static void main(String[] args) {
        launch(args);
        System.out.println("Tere");
    }

    @Override
    public void start(Stage primaryStage) {
        Stage peaLava = new Stage();
//        try {
//            peaLava.setScene(sisselogimisUI());
//            peaLava.setTitle("Sisselogimine");
//            peaLava.showAndWait();
//        } catch (IOException viga) {
//
//        }
        ArrayList<String> andmed = new ArrayList<>(Arrays.asList("kasutaja", "postgre", "sql", "hannes", "kaur", "kaur2", "kaur3"));
        ObservableList<String> valikudNaidatuna = FXCollections.observableArrayList(andmed);
        peaLava.setScene(kuvaAndmed(valikudNaidatuna));
        peaLava.setTitle("Katsetus");
        peaLava.show();
    }

    private Scene kuvaAndmed(ObservableList<String> valikud) {
        ListView<String> valikuVaade = new ListView<>(valikud);

        valikuVaade.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String valik, boolean kasTuhi) {
                super.updateItem(valik, kasTuhi);
                if (kasTuhi || valik == null) {
                    setText(null);
                } else {
                    setText(valik);
                    setPrefHeight(50);
                    setPrefWidth(200);
                    setFont(Font.font(14));
                }
            }
        });

        valikuVaade.setPadding(new Insets(5));

        Button lookasutajaNupp = new Button("Loo uus kasutaja");
        lookasutajaNupp.setOnAction(actionEvent -> {
            try {
                looUusKasutaja(lookasutajaNupp.getScene(), lookasutajaNupp.getScene().getWindow(), valikud);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button valikasutajaNupp = new Button("Vali kasutaja");
        valikasutajaNupp.setOnAction(actionEvent -> {
            String valitud = valikuVaade.getSelectionModel().getSelectedItem();
            if (valitud != null) {
                valitudKasutaja(valikasutajaNupp.getScene(), valikasutajaNupp.getScene().getWindow(), valitud);
            } else {
                System.out.println("Pead valima kasutaja enne edasi minemist");
            }
        });

        VBox juur = new VBox();
        juur.getChildren().addAll(valikuVaade, lookasutajaNupp, valikasutajaNupp);
        juur.setAlignment(Pos.CENTER);
        juur.setSpacing(10);
        VBox.setMargin(lookasutajaNupp, new Insets(10, 0, 20, 0));
        Scene stseen = new Scene(juur);
        stseen.getStylesheets().add("com/produktiivsusjalgijaklient/klient/Teema.css");
        return stseen;
    }

    private void saadaAndmed(String andmed) {
        // TODO
    }

    private Scene sisselogimisUI() throws IOException {
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
                if (fokuseeritudVali[0] == tekstiValjad.length - 1) saadaAndmed(parooliVali.getText());
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

    private void looUusKasutaja(Scene eelmine, Window omanik, ObservableList<String> valikud) throws IOException {

        Scene uus = uueKasutajaUI(valikud, eelmine, omanik);

        Stage peaLava = (Stage) omanik;
        peaLava.close();

        Stage uusLava = new Stage();
        uusLava.setScene(uus);
        uusLava.initOwner(omanik);
        uusLava.initModality(Modality.WINDOW_MODAL);
        uusLava.showAndWait();
    }

    private Scene uueKasutajaUI(ObservableList<String> valikud, Scene eelmine, Window omanik) throws IOException {
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
                if (fokuseeritudVali[0] == tekstiValjad.length - 1) saadaAndmed(parooliVali.getText());
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

        Button edasiNupp = new Button("Loo uus kasutaja");
        edasiNupp.setOnAction(actionEvent -> {
            if (kontrolliNime(valikud, kasutajaNimeVali.getText())) {
                System.out.println("Kasutaja loodud");
                valikud.add(kasutajaNimeVali.getText());
            } else {
                System.out.println("Kasutaja juba olemas");
            }
        });

        Button tagasi = new Button("Tagasi");
        tagasi.setOnAction(actionEvent -> {
            ((Stage) juur.getScene().getWindow()).close();
            algneStseen(valikud);
        });
        juur.getChildren().addAll(edasiNupp, tagasi);

        Scene stseen = new Scene(juur);
        stseen.getStylesheets().add("com/produktiivsusjalgijaklient/klient/Teema.css");

        return stseen;
    }

    private void valitudKasutaja(Scene eelmine, Window omanik, String valitudKasutaja) {
        VBox juur = new VBox();
        juur.setAlignment(Pos.CENTER);
        juur.setSpacing(10);

        Label kasutajaNimiLabel = new Label("Valitud kasutaja: " + valitudKasutaja);

        GridPane infoSisend = new GridPane();
        infoSisend.setAlignment(Pos.BASELINE_LEFT);
        infoSisend.setHgap(20);
        infoSisend.setVgap(10);

        Label parooliSilt = new Label("Sisesta parool:");
        PasswordField parooliVali = new PasswordField();


        Button logiSisse = new Button("Logi sisse");
        Button tagasiNupp = new Button("Tagasi");


        juur.getChildren().addAll(kasutajaNimiLabel, parooliSilt, parooliVali, logiSisse, tagasiNupp);
        Scene uusStseen = new Scene(juur, 400, 300);
        // Sulgeme eelneva stseeni
        Stage peaLava = (Stage) omanik;
        peaLava.close();

        Stage uusLava = new Stage();
        uusLava.setScene(uusStseen);
        uusLava.initOwner(omanik);
        uusLava.initModality(Modality.WINDOW_MODAL);
        uusLava.show();
    }

    private boolean kontrolliNime(ObservableList<String> valikud, String kasutajanimi) {
        if (valikud.contains(kasutajanimi)) {
            System.out.println("Kasutaja juba olemas");
            return false;
        } else {
            System.out.println("Kasutaja loodud");
            return true;
            //create new kasutaja
        }
    }

    private void algneStseen(ObservableList<String> valikud) {
        Stage peaLava = new Stage();
        Scene algusStseen = kuvaAlgne(valikud);
        peaLava.setScene(algusStseen);
        peaLava.setTitle("Katsetus");
        peaLava.show();
    }

    private Scene kuvaAlgne(ObservableList<String> valikud) {
        return kuvaAndmed(valikud);
    }
}