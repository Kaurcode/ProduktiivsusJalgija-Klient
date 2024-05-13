package com.produktiivsusjalgijaklient.klient;

import java.util.ArrayList;

public interface AndmeHaldur {
    public enum autentimisOnnestumus {
        AUTENDITUD,
        VALE_PAROOL,
        VALE_KASUTAJANIMI,
    }

    public enum kasutajaLoomisOnnestumus {
        KASUTAJA_LOODUD,
        MITTEUNIKAALNE_KASUTAJANIMI,
    }

    public kasutajaLoomisOnnestumus looKasutaja(String kasutajaNimi, String parool);
    public autentimisOnnestumus logiSisse(String kasutajaNimi, String parool);

    public ArrayList<Ulesanne> tagastaUlesanded(int eesmargiID);
}
