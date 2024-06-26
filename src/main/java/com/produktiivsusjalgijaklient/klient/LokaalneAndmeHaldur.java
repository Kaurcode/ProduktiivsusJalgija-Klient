package com.produktiivsusjalgijaklient.klient;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class LokaalneAndmeHaldur implements AndmeHaldur, AutoCloseable {
    private Andmebaas andmebaas;
    private Logija logija;
    private int kasutajaID;

    public LokaalneAndmeHaldur(String andmebaasiNimi) throws SQLException, IOException {
        logija = new Logija();
        try {
            this.andmebaas = new Andmebaas(andmebaasiNimi);
            logija.kirjutaLogi("Andmebaasiga uhendus loodud");
        } catch (SQLException viga) {
            logija.kirjutaErind(viga, "Andmebaasi klassi loomine");
            throw viga;
        }
    }

    public Andmebaas getAndmebaas() {
        return andmebaas;
    }

    public void kirjutaLogi(String logi) throws IOException {
        logija.kirjutaLogi(logi);
    }

    public void kirjutaErind(SQLException erind, String teade) throws IOException {
        logija.kirjutaErind(erind, teade);
    }

    public void kirjutaErind(IOException erind, String teade) throws IOException {
        logija.kirjutaErind(erind, teade);
    }

    /**
     * Kontrollib kasutajanime unikaalsust ning loob kasutaja
     * @param kasutajaNimi
     * @param parool
     * @return Kasutaja loomisedukus
     * @throws IOException
     * @throws SQLException
     */
    @Override
    public kasutajaLoomisOnnestumus looKasutaja(String kasutajaNimi, char[] parool) throws IOException, SQLException {
        try {
            if (andmebaas.kasKasutajanimiOlemas(kasutajaNimi))
                return kasutajaLoomisOnnestumus.MITTEUNIKAALNE_KASUTAJANIMI;
        } catch (SQLException viga) {
            logija.kirjutaErind(viga, "Kasutajanime unikaalsuse kontroll");
            throw viga;
        }

        String sool = ParooliRasija.genereeriSool();  // Suvaline sool
        String parooliRasi = ParooliRasija.looParooliRasi(parool, sool);  // Loob paroolile räsi
        try {
            andmebaas.lisaUusKasutaja(kasutajaNimi, sool, parooliRasi);  // Lisab kasutaja koos logimisinfoga andmebaasi
        } catch (SQLException viga) {
            logija.kirjutaErind(viga, "Uue kasutaja andmebaasi lisamine");
            throw viga;
        }
        logija.kirjutaLogi("Kasutaja loodud (nimi: %s)".formatted(kasutajaNimi));
        return kasutajaLoomisOnnestumus.KASUTAJA_LOODUD;
    }

    /**
     * Autendib kasutaja
     * @param kasutajaNimi
     * @param parool
     * @return Kasutaja autentimiõnnestumus
     * @throws SQLException
     * @throws IOException
     */
    @Override
    public autentimisOnnestumus logiSisse(String kasutajaNimi, char[] parool) throws SQLException, IOException {
        if (!andmebaas.kasKasutajanimiOlemas(kasutajaNimi)) {
            logija.kirjutaLogi("Sisselogimiseks kasutati vale kasutajanime (nimi: %s)".formatted(kasutajaNimi));
            return autentimisOnnestumus.VALE_KASUTAJANIMI;
        }
        String[] kasutajaAndmed = andmebaas.tagastaKasutajaSoolJaRasi(kasutajaNimi);
        String parooliRasi = ParooliRasija.looParooliRasi(parool, kasutajaAndmed[0]);
        // Kontrollib, kas kasutaja sisestatud parool ja andmebaasis hoitav parool (räsid) ühtivad
        if (parooliRasi.contentEquals(kasutajaAndmed[1])) {
            try {
                kasutajaID = andmebaas.tagastaKasutajaID(kasutajaNimi, parooliRasi);
            } catch (SQLException viga) {
                logija.kirjutaErind(viga, "Kasutaja ID tagastamine");
                throw viga;
            }
            logija.kirjutaLogi("Kasutaja logis edukalt sisse (nimi: %s)".formatted(kasutajaNimi));
            return autentimisOnnestumus.AUTENDITUD;
        }
        logija.kirjutaLogi("Kasutaja sisestas vale parooli (nimi: %s)".formatted(kasutajaNimi));
        return autentimisOnnestumus.VALE_PAROOL;
    }

    @Override
    public ArrayList<Ulesanne> tagastaUlesanded(int eesmargiID) throws SQLException, IOException {
        try {
            ArrayList<Ulesanne> ulesanded = andmebaas.tagastaUlesanneteOlemid(eesmargiID);
            logija.kirjutaLogi("ulesanded edukalt laetud (Eesmargi ID: %d)".formatted(eesmargiID));
            return ulesanded;
        } catch (SQLException viga) {
            logija.kirjutaErind(viga, "ulesannete olemite tagastamine");
            throw viga;
        }
    }

    public ArrayList<Eesmark> tagastaEesmargid(int kasutajaID) throws SQLException, IOException {
        ArrayList<Eesmark> eesmargid;
        try {
            eesmargid = andmebaas.tagastaEesmarkideOlemid(kasutajaID);
            logija.kirjutaLogi("Eesmargid edukalt laetud (Kasutaja ID: %d)".formatted(kasutajaID));
        } catch (SQLException viga) {
            logija.kirjutaErind(viga, "Eesmarkide olemite tagastamine");
            throw viga;
        }
        for (Eesmark eesmark : eesmargid) {
            eesmark.setUlesanded(tagastaUlesanded(eesmark.getEesmargiID()));
        }
        return eesmargid;
    }

    public void lisaProduktiivneAeg(int aegSekundites, int ulesanneID) throws SQLException, IOException {
        try {
            andmebaas.lisaUusProduktiivsusAeg(Timestamp.valueOf(LocalDateTime.now()), aegSekundites, ulesanneID);
            logija.kirjutaLogi("Uus produktiivsusaeg edukalt lisatud (ulesande ID: %d)".formatted(ulesanneID));
        } catch (SQLException viga) {
            logija.kirjutaErind(viga, "Produktiivsusaja loomisel tekkis viga");
            throw viga;
        }
    }

    /**
     * Tagastab, kui palju aega mingi ülesande peale aega on kulunud
     * @param ulesandeID Ülesande id andmebaasis
     * @return Aeg sekundites
     * @throws SQLException
     * @throws IOException
     */
    public int tagastaUlesandeProduktiivneAeg(int ulesandeID) throws SQLException, IOException {
        try {
            int ulesandeProduktiivneAeg = andmebaas.tagastaUlesandeProduktiivneAeg(ulesandeID);
            logija.kirjutaLogi("ulesande produktiivse aja summa edukalt tagastatud: (ulesande ID: %d)".formatted(ulesandeID));
            return ulesandeProduktiivneAeg;
        } catch (SQLException viga) {
            logija.kirjutaErind(viga, "ulesande, ID: %d, aja summa tagastamisel tekkis viga".formatted(ulesandeID));
            throw viga;
        }
    }

    public int tagastaEesmargiProduktiivneAeg(int eesmargiID) throws SQLException, IOException {
        try {
            int eesmargiProduktiivneAeg = andmebaas.tagastaEesmargiProduktiivneAeg(eesmargiID);
            logija.kirjutaLogi("Eesmargi produktiivse aja summa edukalt tagastatud: (Eesmargi ID: %d)".formatted(eesmargiID));
            return eesmargiProduktiivneAeg;
        } catch (SQLException viga) {
            logija.kirjutaErind(viga, "Eesmargi, ID: %d, aja summa tagastamisel tekkis viga".formatted(eesmargiID));
            throw viga;
        }
    }

    public String loeLogist10Rida() throws IOException {
        return logija.loeViimased10Rida();
    }

    public int getKasutajaID() {
        return kasutajaID;
    }

    @Override
    public void close() throws Exception {
        try {
            andmebaas.close();
            logija.kirjutaLogi("Andmebaas edukalt suletud");
        }
        catch (SQLException viga) {
            throw viga;
        }
        logija.close();
    }
}