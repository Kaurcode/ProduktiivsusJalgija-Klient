package com.produktiivsusjalgijaklient.klient;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/******************************************************************
 * Tegemist on suure koodiga, kuid paljud
 * meetodid on sarnase ülesehitusega. Programm avab mitmeid aknaid,
 * mistõttu edasi liikudes tekib meetoditel parameetreid juurde.
 ******************************************************************/

public class MainUI extends Application {

    private LokaalneAndmeHaldur andmeHaldur;
    private Taimer taimer;


    public static void main(String[] args) {
        launch(args);
    }

    /********
     * Meetod, mis käivitab andmehalduri andmebaasile ja avab esimese sisselogimis ekraani
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        Stage peaLava = new Stage();
        try {
            andmeHaldur = new LokaalneAndmeHaldur("produktiivsusjalgija");
            ArrayList<Eesmark> andmed = new ArrayList<>();
            peaLava.setScene(sisselogimisUI(andmeHaldur, andmed));
            peaLava.setTitle("Sisselogimine");
            peaLava.show();
        } catch (IOException viga) {
            Stage veateade = new Stage();
            veateade.setScene(vigaUI("Viga graafika loomisel",
                    viga.getMessage(), true));
            veateade.show();
        } catch (SQLException viga) {
            Stage veateade = new Stage();
            veateade.setScene(vigaUI("Viga programmi alustamisel",
                    viga.getMessage(), true));
            veateade.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**************
     * Eesmärkide nimekirja meetod, võtab parameetriteks andmehalduri ja eesmärgi klassi isendite massiivi
     * Meetodis on ActionEventid nuppudele vajutamiseks
     * @param andmeHaldur
     * @param andmed
     * @return
     */
    private Scene kuvaAndmed(LokaalneAndmeHaldur andmeHaldur, ArrayList<Eesmark> andmed) {
        ArrayList<Eesmark> finalAndmed = andmed;
        try {
            andmed = andmeHaldur.tagastaEesmargid(andmeHaldur.getKasutajaID());
        } catch (SQLException viga) {
            Stage veateade = new Stage();
            veateade.setScene(vigaUI("Viga andmebaasist eesmärkide tagastamisel",
                    viga.getMessage(), false));
            veateade.show();
        } catch (IOException viga) {
            Stage veateade = new Stage();
            veateade.setScene(vigaUI("Viga logifaili kirjutamisel (andmebaasist eesmärkide tagastamine)",
                    viga.getMessage(), false));
            veateade.show();
        }
        ObservableList<Eesmark> valikud = FXCollections.observableArrayList(andmed);
        ListView<Eesmark> valikuVaade = new ListView<>(valikud);

        valikuVaade.setCellFactory(new Callback<ListView<Eesmark>, ListCell<Eesmark>>() {
            @Override
            public ListCell<Eesmark> call(ListView<Eesmark> param) {
                return new ListCell<Eesmark>() {
                    protected void updateItem(Eesmark eesmark, boolean kasTuhi) {
                        super.updateItem(eesmark, kasTuhi);
                        if (kasTuhi || eesmark == null) {
                            setText(null);
                        } else {
                            setText(eesmark.getEesmargiNimi());
                            setPrefHeight(50);
                            setPrefWidth(200);
                            setFont(Font.font(14));
                        }
                    }
                };
            }
        });

        valikuVaade.setPadding(new Insets(5));
        valikuVaade.setOnMouseClicked(mouseEvent -> {
            Eesmark valitudeesmark = valikuVaade.getSelectionModel().getSelectedItem();
            System.out.println(valitudeesmark.getEesmargiNimi());
            try {
                ulesanneteUI(valikuVaade.getScene(), valikuVaade.getScene().getWindow(), andmeHaldur, valitudeesmark, finalAndmed);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        VBox juur = new VBox();

        Button looEesmark = new Button("Loo uus eesmärk");
        looEesmark.setOnAction(actionEvent -> {
            ((Stage) juur.getScene().getWindow()).close();
            uusEesmark(looEesmark.getScene(), looEesmark.getScene().getWindow(), andmeHaldur, finalAndmed);
        });

        Button tagasi = new Button("Tagasi");
        tagasi.setOnAction(actionEvent -> {
            ((Stage) juur.getScene().getWindow()).close();
            algneStseen(andmeHaldur, finalAndmed);
        });

        juur.getChildren().addAll(valikuVaade, looEesmark, tagasi);
        juur.setAlignment(Pos.CENTER);
        juur.setSpacing(10);
        VBox.setMargin(looEesmark, new Insets(10, 0, 20, 0));
        VBox.setMargin(tagasi, new Insets(10, 0, 20, 0));
        Scene stseen = new Scene(juur);
        stseen.getStylesheets().add("com/produktiivsusjalgijaklient/klient/Teema.css");
        return stseen;
    }

    /**********
     * Sisselogimisekraani stseen, klahvivajutused väljade vahel liikumiseks ning nupud uue kasutaja loomiseks
     * ja sisse logimiseks. Sisse logimisel meetod suhtleb andmehalduriga ja autentib kasutaja andmed
     * @param andmeHaldur
     * @param valikud
     * @return
     */
    private Scene sisselogimisUI(LokaalneAndmeHaldur andmeHaldur, ArrayList<Eesmark> valikud) {
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
        edasiNupp.setOnAction(actionEvent -> {
            try {
                AndmeHaldur.autentimisOnnestumus autentimisOnnestumus = andmeHaldur.logiSisse(kasutajaNimeVali.getText(), parooliVali.getText().toCharArray());
                switch (autentimisOnnestumus) {
                    case AUTENDITUD -> eesmarkideUI(edasiNupp.getScene(), edasiNupp.getScene().getWindow(), andmeHaldur, valikud);
                    case VALE_KASUTAJANIMI -> {
                        Stage veateade = new Stage();
                        veateade.setScene(vigaUI("Kasutaja viga", "Kasutajanimi on vale", false));
                        veateade.show();
                    }
                    case VALE_PAROOL -> {
                        Stage veateade = new Stage();
                        veateade.setScene(vigaUI("Kasutaja viga", "Sisestatud parool on vale", false));
                        veateade.show();
                    }
                }
            } catch (IOException e) {
                Stage veateade = new Stage();
                try {
                    andmeHaldur.kirjutaErind(e, "Probleem sisselogimisel");
                } catch (IOException viga) {
                    throw new RuntimeException(viga);
                }
                veateade.setScene(vigaUI("Probleem sisselogimisel",
                        e.getMessage(), false));
                veateade.show();
            } catch (SQLException e) {
                Stage veateade = new Stage();
                try {
                    andmeHaldur.kirjutaErind(e, "Probleem sisselogimisel");
                } catch (IOException viga) {
                    throw new RuntimeException(viga);
                }
                veateade.setScene(vigaUI("Probleem sisselogimisel",
                        e.getMessage(), false));
                veateade.show();
            }
        });

        Button lookasutajaNupp = new Button("Loo uus kasutaja");
        lookasutajaNupp.setOnAction(actionEvent -> {
            looUusKasutaja(lookasutajaNupp.getScene(), lookasutajaNupp.getScene().getWindow(), andmeHaldur, valikud);
        });

        juur.getChildren().addAll(edasiNupp, lookasutajaNupp);
        Scene stseen = new Scene(juur);
        stseen.getStylesheets().add("com/produktiivsusjalgijaklient/klient/Teema.css");

        return stseen;
    }

    /*************
     * Meetod, mis sulgeb eelneva akna ja avab uue. Selline meetod on iga uue akna avamisel ning ülesehitus on umbes sama,
     * seega tegemist on n-ö "abimeetodiga".
     * @param eelmine
     * @param omanik
     * @param andmeHaldur
     * @param valikud
     */
    private void looUusKasutaja(Scene eelmine, Window omanik, LokaalneAndmeHaldur andmeHaldur, ArrayList<Eesmark> valikud) {

        Scene uus = uueKasutajaUI(eelmine, omanik, andmeHaldur, valikud);

        Stage peaLava = (Stage) omanik;
        peaLava.close();

        Stage uusLava = new Stage();
        uusLava.setScene(uus);
        uusLava.initOwner(omanik);
        uusLava.initModality(Modality.WINDOW_MODAL);
        uusLava.show();
    }

    /**************
     * Uue kasutaja loomise stseen, kontrollib olemasolevate kasutajate nimesid ja eduka loomise korral
     * salvestab kasutaja andmebaasi
     * @param eelmine
     * @param omanik
     * @param andmeHaldur
     * @param valikud
     * @return
     */
    private Scene uueKasutajaUI(Scene eelmine, Window omanik, LokaalneAndmeHaldur andmeHaldur, ArrayList<Eesmark> valikud) {
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
            if (!kasutajaNimeVali.getText().isEmpty() || !parooliVali.getText().isEmpty()) {
                try {
                    switch (andmeHaldur.looKasutaja(kasutajaNimeVali.getText(), parooliVali.getText().toCharArray())) {
                        case MITTEUNIKAALNE_KASUTAJANIMI -> {
                            Stage veateade = new Stage();
                            veateade.setScene(vigaUI("Kasutaja viga", "Kasutajanimi peab olema unikaalne", false));
                            veateade.show();
                        }
                    case KASUTAJA_LOODUD -> {
                        Stage veateade = new Stage();
                        veateade.setScene(vigaUI("Kasutaja loomine edukas", "Uus kasutaja on edukalt loodud", false));
                        veateade.show();
                    }
                }
            } catch (IOException e) {
                Stage veateade = new Stage();
                try {
                    andmeHaldur.kirjutaErind(e, "Viga kasutaja loomisel");
                } catch (IOException viga) {
                    throw new RuntimeException(viga);
                }
                veateade.setScene(vigaUI("Viga kasutaja loomisel",
                        e.getMessage(), false));
                    veateade.show();
                } catch (SQLException e) {
                    Stage veateade = new Stage();
                try {
                    andmeHaldur.kirjutaErind(e, "Viga kasutaja lisamisel andmebaasi");
                } catch (IOException viga) {
                    throw new RuntimeException(viga);
                }
                veateade.setScene(vigaUI("Viga kasutaja lisamisel andmebaasi",
                        e.getMessage(), false));
                veateade.show();
                }
            } else {
                Stage veateade = new Stage();
                veateade.setScene(vigaUI("Kasutaja viga", "Kasutaja väljad ei tohi olla tühjad", false));
                veateade.show();
            }
        });

        Button tagasi = new Button("Tagasi");
        tagasi.setOnAction(actionEvent -> {
            ((Stage) juur.getScene().getWindow()).close();
            algneStseen(andmeHaldur, valikud);
        });
        juur.getChildren().addAll(edasiNupp, tagasi);

        Scene stseen = new Scene(juur);
        stseen.getStylesheets().add("com/produktiivsusjalgijaklient/klient/Teema.css");

        return stseen;
    }

    private void algneStseen(LokaalneAndmeHaldur andmeHaldur, ArrayList<Eesmark> valikud) {
        Stage peaLava = new Stage();
        Scene algusStseen = kuvaAlgne(andmeHaldur, valikud);
        peaLava.setScene(algusStseen);
        peaLava.setTitle("Sisse logimine");
        peaLava.show();
    }

    private Scene kuvaAlgne(LokaalneAndmeHaldur andmeHaldur, ArrayList<Eesmark> valikud) {
        return sisselogimisUI(andmeHaldur, valikud);
    }

    private void eesmarkideUI(Scene eelmine, Window omanik, LokaalneAndmeHaldur andmeHaldur, ArrayList<Eesmark> valikud ) {

            Scene uus = kuvaAndmed(andmeHaldur, valikud);

            Stage peaLava = (Stage) omanik;
            peaLava.close();

            Stage uusLava = new Stage();
            uusLava.setScene(uus);
            uusLava.initOwner(omanik);
            uusLava.initModality(Modality.WINDOW_MODAL);
            uusLava.show();
    }

    private void ulesanneteUI(Scene eelmine, Window omanik, LokaalneAndmeHaldur andmeHaldur, Eesmark eesmark, ArrayList<Eesmark> andmed) throws SQLException, IOException {
        Scene uus = kuvaulesanded(andmeHaldur, andmeHaldur.tagastaUlesanded(eesmark.getEesmargiID()), eesmark);
        Stage peaLava = (Stage) omanik;
        peaLava.close();
        Stage uusLava = new Stage();
        uusLava.setScene(uus);
        uusLava.initOwner(omanik);
        uusLava.initModality(Modality.WINDOW_MODAL);
        uusLava.show();
    }

    /***********
     * Sarnane meetod eesmärkide kuvamisele, seekord ülesannetega. Koodis edasi minnes tekib parameetreid juurde,
     * kuna mitmeid väljasid on vaja salvestada, et ekraanide vahel liikuda
     * @param andmeHaldur
     * @param andmed
     * @param eesmark
     * @return
     * @throws SQLException
     * @throws IOException
     */
    private Scene kuvaulesanded(LokaalneAndmeHaldur andmeHaldur, ArrayList<Ulesanne> andmed, Eesmark eesmark) throws SQLException, IOException {
        VBox juur = new VBox();

        ArrayList<Ulesanne> finalAndmed = andmed;
        try {
            andmed = andmeHaldur.getAndmebaas().tagastaUlesanneteOlemid(eesmark.getEesmargiID());
        } catch (SQLException viga) {
            Stage veateade = new Stage();
            try {
                andmeHaldur.kirjutaErind(viga, "Viga ulesanneteolemite tagastamisel");
            } catch (IOException logimisViga) {
                throw new RuntimeException(viga);
            }
            veateade.setScene(vigaUI("Viga andmebaasist ulesanneteolemite tagastamisel",
                    viga.getMessage(), false));
            veateade.show();
        }
        ObservableList<Ulesanne> valikud = FXCollections.observableArrayList(andmed);
        ListView<Ulesanne> valikuVaade = new ListView<>(valikud);

        valikuVaade.setCellFactory(new Callback<ListView<Ulesanne>, ListCell<Ulesanne>>() {
            @Override
            public ListCell<Ulesanne> call(ListView<Ulesanne> param) {
                return new ListCell<Ulesanne>() {
                    protected void updateItem(Ulesanne ulesanne, boolean kasTuhi) {
                        super.updateItem(ulesanne, kasTuhi);
                        if (kasTuhi || ulesanne == null) {
                            setText(null);
                        } else {
                            setText(ulesanne.getUlesandeNimi());
                            setPrefHeight(50);
                            setPrefWidth(200);
                            setFont(Font.font(14));
                        }
                    }
                };
            }
        });

        valikuVaade.setPadding(new Insets(5));
        valikuVaade.setOnMouseClicked(mouseEvent -> {
            Ulesanne ulesanne = valikuVaade.getSelectionModel().getSelectedItem();
            try {
                taimeriEkraan(juur.getScene(), juur.getScene().getWindow(), andmeHaldur, andmeHaldur.tagastaEesmargid(andmeHaldur.getKasutajaID()), eesmark, ulesanne);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button looUlesanne = new Button("Loo uus ülesanne");
        looUlesanne.setOnAction(actionEvent -> {
            ((Stage) juur.getScene().getWindow()).close();
            try {
                uusUlesanne(looUlesanne.getScene(), looUlesanne.getScene().getWindow(), andmeHaldur, finalAndmed, andmeHaldur.tagastaEesmargid(andmeHaldur.getKasutajaID()), eesmark);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button tagasi = new Button("Tagasi");
        tagasi.setOnAction(actionEvent -> {
            ((Stage) juur.getScene().getWindow()).close();
            try {
                eesmarkideUI(tagasi.getScene(), tagasi.getScene().getWindow(), andmeHaldur, andmeHaldur.tagastaEesmargid(andmeHaldur.getKasutajaID()));
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        juur.getChildren().addAll(valikuVaade, looUlesanne, tagasi);
        juur.setAlignment(Pos.CENTER);
        juur.setSpacing(10);
        VBox.setMargin(looUlesanne, new Insets(10, 0, 20, 0));
        VBox.setMargin(tagasi, new Insets(10, 0, 20, 0));
        Scene stseen = new Scene(juur);
        stseen.getStylesheets().add("com/produktiivsusjalgijaklient/klient/Teema.css");
        return stseen;
    }

    private void uusEesmark(Scene eelmine, Window omanik, LokaalneAndmeHaldur andmeHaldur, ArrayList<Eesmark> valikud) {

        Scene uus = uusEesmarkUI(andmeHaldur, valikud);

        Stage peaLava = (Stage) omanik;
        peaLava.close();

        Stage uusLava = new Stage();
        uusLava.setScene(uus);
        uusLava.initOwner(omanik);
        uusLava.initModality(Modality.WINDOW_MODAL);
        uusLava.show();
    }

    /**********
     * Uue eesmärgi loomise meetod, lisatud tühja välja kontroll.
     * @param andmeHaldur
     * @param valikud
     * @return
     */
    private Scene uusEesmarkUI(LokaalneAndmeHaldur andmeHaldur, ArrayList<Eesmark> valikud) {
        VBox juur = new VBox();

        juur.setPadding(new Insets(15));
        juur.setSpacing(20);
        juur.setAlignment(Pos.CENTER);

        juur.setPrefHeight(200);
        juur.setPrefWidth(500);

        Label paiseTekst = new Label("Loo uus eesmärk");
        juur.getChildren().add(paiseTekst);

        Label eesmargiSilt = new Label("Uus eesmärk:");
        TextField eesmargiVali = new TextField();

        juur.getChildren().addAll(eesmargiSilt, eesmargiVali);

        Button edasiNupp = new Button("Loo uus eesmärk:");
        edasiNupp.setOnAction(actionEvent -> {
            if (!eesmargiVali.getText().isEmpty()) {
                int eesmarkID = 0;
                try {
                    eesmarkID = andmeHaldur.getAndmebaas().lisaUusEesmark(eesmargiVali.getText(), andmeHaldur.getKasutajaID());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                valikud.add(new Eesmark(eesmarkID, eesmargiVali.getText(), false));
            } else {
                Stage veateade = new Stage();
                veateade.setScene(vigaUI("Eesmärgi viga", "Eesmärgi väli ei tohi olla tühi", false));
                veateade.show();
            }
        });

        Button tagasi = new Button("Tagasi");
        tagasi.setOnAction(actionEvent -> {
            ((Stage) juur.getScene().getWindow()).close();
            eesmarkideUI(tagasi.getScene(), tagasi.getScene().getWindow(), andmeHaldur, valikud);
        });
        juur.getChildren().addAll(edasiNupp, tagasi);

        Scene stseen = new Scene(juur);
        stseen.getStylesheets().add("com/produktiivsusjalgijaklient/klient/Teema.css");

        return stseen;
    }

    private void uusUlesanne(Scene eelmine, Window omanik, LokaalneAndmeHaldur andmeHaldur, ArrayList<Ulesanne> valikud, ArrayList<Eesmark> eesmargid, Eesmark eesmark) {

        Scene uus = uusUlesanneUI(andmeHaldur, valikud, eesmargid, eesmark);

        Stage peaLava = (Stage) omanik;
        peaLava.close();

        Stage uusLava = new Stage();
        uusLava.setScene(uus);
        uusLava.initOwner(omanik);
        uusLava.initModality(Modality.WINDOW_MODAL);
        uusLava.show();
    }

    /*********
     * Sama meetod, mis uue eesmärgi loomise puhul
     * @param andmeHaldur
     * @param valikud
     * @param eesmargid
     * @param eesmark
     * @return
     */
    private Scene uusUlesanneUI(LokaalneAndmeHaldur andmeHaldur, ArrayList<Ulesanne> valikud, ArrayList<Eesmark> eesmargid, Eesmark eesmark) {
        VBox juur = new VBox();

        juur.setPadding(new Insets(15));
        juur.setSpacing(20);
        juur.setAlignment(Pos.CENTER);

        juur.setPrefHeight(200);
        juur.setPrefWidth(500);

        Label paiseTekst = new Label("Loo uus ülesanne");
        juur.getChildren().add(paiseTekst);

        Label ulesanneSilt = new Label("Uus ülesanne:");
        TextField ulesanneVali = new TextField();

        juur.getChildren().addAll(ulesanneSilt, ulesanneVali);

        Button edasiNupp = new Button("Loo uus ülesanne:");
        edasiNupp.setOnAction(actionEvent -> {
            if (!ulesanneVali.getText().isEmpty()) {
                int ülesandeID = 0;
                try {
                    ülesandeID = andmeHaldur.getAndmebaas().lisaUusUlesanne(ulesanneVali.getText(), eesmark.getEesmargiID());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                valikud.add(new Ulesanne(ülesandeID, ulesanneVali.getText()));
            } else {
                Stage veateade = new Stage();
                veateade.setScene(vigaUI("Ülesande viga", "Ülesande väli ei tohi olla tühi", false));
                veateade.show();
            }
        });

        Button tagasi = new Button("Tagasi");
        tagasi.setOnAction(actionEvent -> {
            ((Stage) juur.getScene().getWindow()).close();
            try {
                ulesanneteUI(tagasi.getScene(), tagasi.getScene().getWindow(), andmeHaldur, eesmark, eesmargid);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        juur.getChildren().addAll(edasiNupp, tagasi);

        Scene stseen = new Scene(juur);
        stseen.getStylesheets().add("com/produktiivsusjalgijaklient/klient/Teema.css");

        return stseen;
    }

    /************
     * Baasmeetod, mis võimaldab vigade tekkimisel anda kasutajale teada probleemist.
     * @param paiseTekst
     * @param veateateTekst
     * @param fataalne
     * @return
     */
    public Scene vigaUI(String paiseTekst, String veateateTekst, boolean fataalne) {
        VBox juur = new VBox();

        juur.setPadding(new Insets(15));
        juur.setSpacing(20);
        juur.setAlignment(Pos.CENTER);

        Label pais = new Label(paiseTekst);
        juur.getChildren().add(pais);

        Label veateade = new Label(veateateTekst);
        juur.getChildren().add(veateade);

        Button nupp = new Button(fataalne ? "Sulge" : "Ok");
        nupp.setOnMouseClicked(mouseEvent -> {
            Stage veaAken = (Stage) nupp.getScene().getWindow();
            veaAken.close();
        });

        EventHandler<KeyEvent> enter = keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                Stage veaAken = (Stage) nupp.getScene().getWindow();
                veaAken.close();
                keyEvent.consume();
            }
        };

        Button logid = new Button("Ava logid");
        logid.setOnAction(actionEvent -> {
            try {
                logideUI(new Stage(), andmeHaldur.loeLogist10Rida());
            } catch (IOException viga) {
                throw new RuntimeException(viga);
            }
        });

        nupp.setOnKeyPressed(enter);

        juur.getChildren().addAll(nupp, logid);

        Scene stseen = new Scene(juur);
        stseen.getStylesheets().add("com/produktiivsusjalgijaklient/klient/Taimer.css");
        return stseen;
    }

    @Override
    public void stop() throws Exception {
        if (andmeHaldur != null) {
            andmeHaldur.close();
        }
        super.stop();
    }

    /************
     * Meetod taimeri isendi ekraani loomiseks, loeb aega ning aja lõpus sulgeb iseennast.
     * @param primaryStage
     * @param aeg
     * @return
     */
    public void kuvaTaimer(Stage primaryStage, int aeg) {
        Font digitalFont = Font.loadFont(getClass().getResourceAsStream("digital-7.ttf"), 100);
        BorderPane juur = new BorderPane();
        aeg = aeg * 60;
        taimer = new Taimer(aeg);
        Label taimeriSilt = new Label();
        taimeriSilt.textProperty().bind(Bindings.createStringBinding(() -> {
            int aegSekundites = taimer.getAegSekundites();
            String mark;
            if (Integer.signum(aegSekundites) == -1) {
                mark = "+";
                primaryStage.close();
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

    private void taimeriEkraan(Scene eelmine, Window omanik, LokaalneAndmeHaldur andmeHaldur, ArrayList<Eesmark> andmed, Eesmark eesmark, Ulesanne ulesanne) throws SQLException, IOException {

        Scene uus = taimerUI(eelmine, omanik, andmeHaldur, andmed, eesmark, ulesanne);

        Stage peaLava = (Stage) omanik;
        peaLava.close();

        Stage uusLava = new Stage();
        uusLava.setScene(uus);
        uusLava.initOwner(omanik);
        uusLava.initModality(Modality.WINDOW_MODAL);
        uusLava.show();
    }

    /**********
     * Taimeri loomise ekraan, loeb andmebaasist ülesandele kulutatud aja ning võimaldab luua uut taimerit.
     * @param eelmine
     * @param omanik
     * @param andmeHaldur
     * @param andmed
     * @param eesmark
     * @param ulesanne
     * @return
     * @throws SQLException
     * @throws IOException
     */
    private Scene taimerUI(Scene eelmine, Window omanik, LokaalneAndmeHaldur andmeHaldur, ArrayList<Eesmark> andmed, Eesmark eesmark, Ulesanne ulesanne) throws SQLException, IOException {
        VBox juur = new VBox();

        juur.setPadding(new Insets(15));
        juur.setSpacing(20);
        juur.setAlignment(Pos.CENTER);

        juur.setPrefHeight(200);
        juur.setPrefWidth(500);

        Label paiseTekst = new Label("Taimeri loomine");
        juur.getChildren().add(paiseTekst);

        // Ankeet

        GridPane infoSisend = new GridPane();
        infoSisend.setAlignment(Pos.BASELINE_LEFT);
        infoSisend.setHgap(20);
        infoSisend.setVgap(10);

        Text tekst = new Text("Seda ülesannet oled juba teinud " + andmeHaldur.tagastaUlesandeProduktiivneAeg(ulesanne.getUlesandeID()) + " minutit");
        tekst.setFill(Color.WHITE);

        Label prodAegSilt = new Label("Produktiivsusaeg (minutites):");
        TextField prodAegVali = new TextField();

        juur.getChildren().addAll(prodAegSilt, prodAegVali, tekst);

        Button edasiNupp = new Button("Edasi");
        edasiNupp.setOnAction(actionEvent -> {
            if (!prodAegVali.getText().isEmpty()) {
                try {
                    andmeHaldur.lisaProduktiivneAeg(Integer.parseInt(prodAegVali.getText()), ulesanne.getUlesandeID());
                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }
                kuvaTaimer(new Stage(), Integer.parseInt(prodAegVali.getText()));
            } else {
                Stage veateade = new Stage();
                veateade.setScene(vigaUI("Taimeri viga", "Taimeri väli ei tohi olla tühi", false));
                veateade.show();
            }
        });

        Button tagasi = new Button("Tagasi");
        tagasi.setOnAction(actionEvent -> {
            try {
                ulesanneteUI(tagasi.getScene(), tagasi.getScene().getWindow(), andmeHaldur, eesmark, andmed);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        juur.getChildren().addAll(edasiNupp, tagasi);
        Scene stseen = new Scene(juur);
        stseen.getStylesheets().add("com/produktiivsusjalgijaklient/klient/Teema.css");

        return stseen;
    }

    public static void logideUI(Stage primaryStage, String logid) {
        Label mitmeReaLogid = new Label(logid);
        mitmeReaLogid.setWrapText(true);
        StackPane juur = new StackPane(mitmeReaLogid);

        Scene stseen = new Scene(juur, 300, 200);

        primaryStage.setScene(stseen);
        primaryStage.setTitle("Logid");
        primaryStage.show();
    }
}
