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
    public kasutajaLoomisOnnestumus looKasutaja(String kasutajaNimi, String parool) {
        return null;
    }

    @Override
    public autentimisOnnestumus logiSisse(String kasutajaNimi, String parool) {
        return null;
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
