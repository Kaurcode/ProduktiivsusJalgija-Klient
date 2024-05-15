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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainUI extends Application {

    private LokaalneAndmeHaldur andmeHaldur;


    public static void main(String[] args) {
        /*try (LokaalneAndmeHaldur andmeHaldur = new LokaalneAndmeHaldur("produktiivsusjalgija")) {
            System.out.println(andmeHaldur.logiSisse("Hannes", "parool".toCharArray()));
            andmeHaldur.kirjutaLogi("Test");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Stage peaLava = new Stage();
        try {
            andmeHaldur = new LokaalneAndmeHaldur("produktiivsusjalgija");
            ArrayList<Eesmark> andmed = new ArrayList<>();
            //ObservableList<Eesmark> valikud = FXCollections.observableArrayList(andmed);
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
        /*ArrayList<String> andmed = new ArrayList<>(Arrays.asList("kasutaja", "postgre", "sql", "hannes", "kaur", "kaur2", "kaur3"));
        ObservableList<String> valikudNaidatuna = FXCollections.observableArrayList(andmed);
        peaLava.setScene(kuvaAndmed(valikudNaidatuna));
        peaLava.setTitle("Katsetus");
        peaLava.show();*/
    }

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

        VBox juur = new VBox();

        Button valiEesmark = new Button("Ok");

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

        juur.getChildren().addAll(valikuVaade, valiEesmark, looEesmark, tagasi);
        juur.setAlignment(Pos.CENTER);
        juur.setSpacing(10);
        VBox.setMargin(valiEesmark, new Insets(10, 0, 20, 0));
        VBox.setMargin(looEesmark, new Insets(10, 0, 20, 0));
        VBox.setMargin(tagasi, new Insets(10, 0, 20, 0));
        Scene stseen = new Scene(juur);
        stseen.getStylesheets().add("com/produktiivsusjalgijaklient/klient/Teema.css");
        return stseen;
    }

    private void saadaAndmed(String andmed) {
        // TODO
    }

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
        edasiNupp.setOnAction(actionEvent -> {
            try {
                AndmeHaldur.autentimisOnnestumus autentimisOnnestumus = andmeHaldur.logiSisse(kasutajaNimeVali.getText(), parooliVali.getText().toCharArray());
                switch (autentimisOnnestumus) {
                    case AUTENDITUD -> eesmarkideUI(edasiNupp.getScene(), edasiNupp.getScene().getWindow(), andmeHaldur, valikud);
                    case VALE_KASUTAJANIMI -> System.out.println("Kasutajanimi on vale");
                    case VALE_PAROOL -> System.out.println("Parool on vale");
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
            try {
                switch (andmeHaldur.looKasutaja(kasutajaNimeVali.getText(), parooliVali.getText().toCharArray())) {
                    case MITTEUNIKAALNE_KASUTAJANIMI -> System.out.println("Kasutajanimi peab olema unikaalne");
                    case KASUTAJA_LOODUD -> System.out.println("Kasutaja loodud");
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

    private void ulesanneteUI(Scene eelmine, Window omanik, LokaalneAndmeHaldur andmeHaldur, ArrayList<Ulesanne> ulesanded) {
        //Scene uus = kuvaeesmargid(andmeHaldur, ulesanded, 2);
        //Vaja saada eesmargi ID
    }

    private Scene kuvaulesanded(LokaalneAndmeHaldur andmeHaldur, ArrayList<Ulesanne> andmed, int eesmargiID) {
        ArrayList<Ulesanne> finalAndmed = andmed;
        try {
            andmed = andmeHaldur.getAndmebaas().tagastaUlesanneteOlemid(eesmargiID);
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
        ListView<Ulesanne> valikuVaade = new ListView<>();

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

        VBox juur = new VBox();

        Button valiUlesanne = new Button("Ok");

        Button looUlesanne = new Button("Loo uus ülesanne");

        Button tagasi = new Button("Tagasi");
        tagasi.setOnAction(actionEvent -> {
            ((Stage) juur.getScene().getWindow()).close();
            //kuvaAndmed(andmeHaldur, andmeHaldur.tagastaEesmargid(1))
            //Vaja lahendust kasutaja ID säilitamiseks
        });

        juur.getChildren().addAll(valikuVaade, valiUlesanne, looUlesanne, tagasi);
        juur.setAlignment(Pos.CENTER);
        juur.setSpacing(10);
        VBox.setMargin(valiUlesanne, new Insets(10, 0, 20, 0));
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
            try {
                int eesmarkID = andmeHaldur.getAndmebaas().lisaUusEesmark(eesmargiVali.getText(), andmeHaldur.getKasutajaID());
                valikud.add(new Eesmark(eesmarkID, eesmargiVali.getText(), false));
            } catch (SQLException viga) {
                //TODO
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

    public static Scene vigaUI(String paiseTekst, String veateateTekst, boolean fataalne) {
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

        nupp.setOnKeyPressed(enter);

        juur.getChildren().add(nupp);

        Scene stseen = new Scene(juur);
        stseen.getStylesheets().add("produktiivsustracker/server/Teema.css");
        return stseen;
    }

    @Override
    public void stop() throws Exception {
        if (andmeHaldur != null) {
            andmeHaldur.close();
        }
        super.stop();
    }
}
