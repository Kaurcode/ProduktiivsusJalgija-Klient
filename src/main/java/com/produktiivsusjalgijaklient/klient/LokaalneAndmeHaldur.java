package com.produktiivsusjalgijaklient.klient;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class LokaalneAndmeHaldur implements AndmeHaldur, AutoCloseable {
    private Andmebaas andmebaas;
    private Logija logija;
    private int kasutajaID;

    public LokaalneAndmeHaldur(String andmebaasiNimi) throws SQLException, IOException {
        logija = new Logija();
        try {
            this.andmebaas = new Andmebaas(andmebaasiNimi);
        } catch (SQLException viga) {
            logija.kirjutaErind(viga, "Andmebaasi klassi loomine");
            throw viga;
        }
    }

    public void kirjutaLogi(String logi) throws IOException {
        logija.kirjutaLogi(logi);
    }

    @Override
    public kasutajaLoomisOnnestumus looKasutaja(String kasutajaNimi, char[] parool) throws SQLException {
        if (andmebaas.kasKasutajanimiOlemas(kasutajaNimi)) return kasutajaLoomisOnnestumus.MITTEUNIKAALNE_KASUTAJANIMI;

        String sool = ParooliRasija.genereeriSool();
        String parooliRasi = ParooliRasija.looParooliRasi(parool, sool);
        andmebaas.lisaUusKasutaja(kasutajaNimi, sool, parooliRasi);
        return kasutajaLoomisOnnestumus.KASUTAJA_LOODUD;
    }

    @Override
    public autentimisOnnestumus logiSisse(String kasutajaNimi, char[] parool) throws SQLException, IOException {
        if (!andmebaas.kasKasutajanimiOlemas(kasutajaNimi)) return autentimisOnnestumus.VALE_KASUTAJANIMI;
        String[] kasutajaAndmed = andmebaas.tagastaKasutajaSoolJaRasi(kasutajaNimi);
        String parooliRasi = ParooliRasija.looParooliRasi(parool, kasutajaAndmed[0]);
        if (parooliRasi.contentEquals(kasutajaAndmed[1])) {
            try {
                kasutajaID = andmebaas.tagastaKasutajaID(kasutajaNimi, parooliRasi);
            } catch (SQLException viga) {
                logija.kirjutaErind(viga, "Kasutaja ID tagastamine");
                throw viga;
            }
            return autentimisOnnestumus.AUTENDITUD;
        }
        return autentimisOnnestumus.VALE_PAROOL;
    }

    @Override
    public ArrayList<Ulesanne> tagastaUlesanded(int eesmargiID) {
        return andmebaas.tagastaUlesanneteOlemid(eesmargiID);
    }

    public ArrayList<Eesmark> tagastaEesmargid(int kasutajaID) {
        ArrayList<Eesmark> eesmargid = andmebaas.tagastaEesmarkideOlemid(kasutajaID);
        for (Eesmark eesmark : eesmargid) {
            eesmark.setUlesanded(tagastaUlesanded(eesmark.getEesmargiID()));
        }
        return eesmargid;
    }

    @Override
    public void close() throws Exception {
        try {
            andmebaas.close();
        }
        catch (SQLException viga) {
            throw viga;
        }
        logija.close();
    }
}
