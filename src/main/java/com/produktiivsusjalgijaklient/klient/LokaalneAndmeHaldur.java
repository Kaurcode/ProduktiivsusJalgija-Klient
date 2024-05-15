package com.produktiivsusjalgijaklient.klient;

import java.sql.SQLException;
import java.util.ArrayList;

public class LokaalneAndmeHaldur implements AndmeHaldur, AutoCloseable {
    private Andmebaas andmebaas;

    public LokaalneAndmeHaldur(String andmebaasiNimi) throws SQLException {
        try {
            this.andmebaas = new Andmebaas(andmebaasiNimi);
        } catch (SQLException viga) {
            throw viga;
        }
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
    public autentimisOnnestumus logiSisse(String kasutajaNimi, char[] parool) throws SQLException {
        if (!andmebaas.kasKasutajanimiOlemas(kasutajaNimi)) return autentimisOnnestumus.VALE_KASUTAJANIMI;
        String[] kasutajaAndmed = andmebaas.tagastaKasutajaSoolJaRasi(kasutajaNimi);
        String parooliRasi = ParooliRasija.looParooliRasi(parool, kasutajaAndmed[0]);
        if (parooliRasi.contentEquals(kasutajaAndmed[1])) return autentimisOnnestumus.AUTENDITUD;
        return autentimisOnnestumus.VALE_PAROOL;
    }

    @Override
    public ArrayList<Ulesanne> tagastaUlesanded(int eesmargiID) {
        return andmebaas.tagastaUlesanneteOlemid(eesmargiID);
    }

    @Override
    public void close() throws Exception {
        try {
            andmebaas.close();
        }
        catch (SQLException viga) {
            throw viga;
        }
    }
}
