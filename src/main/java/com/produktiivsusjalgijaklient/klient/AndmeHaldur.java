package com.produktiivsusjalgijaklient.klient;

import java.io.IOException;
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

    public kasutajaLoomisOnnestumus looKasutaja(String kasutajaNimi, char[] parool) throws SQLException, IOException;
    public autentimisOnnestumus logiSisse(String kasutajaNimi, char[] parool) throws SQLException, IOException;

    public ArrayList<Ulesanne> tagastaUlesanded(int eesmargiID) throws SQLException, IOException;
}
