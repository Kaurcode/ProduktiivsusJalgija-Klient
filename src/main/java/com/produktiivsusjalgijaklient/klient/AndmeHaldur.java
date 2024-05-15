package com.produktiivsusjalgijaklient.klient;

import java.sql.SQLException;
import java.util.ArrayList;

public interface AndmeHaldur {
    public enum autentimisOnnestumus {
        AUTENDITUD,
        VALE_PAROOL,
        VALE_KASUTAJANIMI,
    }

    public enum kasutajaLoomisOnnestumus {
        KASUTAJA_LOODUD,
        MITTEUNIKAALNE_KASUTAJANIMI;
    }

    public kasutajaLoomisOnnestumus looKasutaja(String kasutajaNimi, char[] parool) throws SQLException;
    public autentimisOnnestumus logiSisse(String kasutajaNimi, char[] parool) throws SQLException;

    public ArrayList<Ulesanne> tagastaUlesanded(int eesmargiID);
}
