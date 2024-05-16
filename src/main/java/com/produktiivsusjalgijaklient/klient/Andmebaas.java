package com.produktiivsusjalgijaklient.klient;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class Andmebaas implements AutoCloseable {
    private Connection andmebaas;
    private String andmebaasiNimi;

    public Andmebaas(String andmebaasiNimi) throws SQLException {
        this.andmebaasiNimi = andmebaasiNimi;
        looUhendus();
        looKasutajadOlem();
        looEesmargidOlem();
        looUlesandedOlem();
        looProduktiivsusAegOlem();
    }

    public void looUhendus() throws SQLException {
        File kaust = new File("SQLite");
        if (!kaust.isDirectory()) kaust.mkdirs();
        String url = "jdbc:sqlite:SQLite/%s.db".formatted(andmebaasiNimi);
        andmebaas = DriverManager.getConnection(url);
    }

    public boolean kasOlemOlemas(String olemiNimi) throws SQLException {
        DatabaseMetaData metaAndmed = andmebaas.getMetaData();
        try (ResultSet kasOlemOlemas = metaAndmed.getTables(null, null, olemiNimi, new String[]{"TABLE"})) {
            return kasOlemOlemas.next();
        }
    }

    public void looKasutajadOlem() throws SQLException {
        final String tabeliNimi = "kasutajad";

        if (kasOlemOlemas(tabeliNimi)) {
            System.out.printf("%s olem juba olemas\n", tabeliNimi);
            return;
        }
        final String looKasutajadOlem =
                "CREATE TABLE " + tabeliNimi + " (" +
                        "kasutaja_id INTEGER PRIMARY KEY NOT NULL UNIQUE," +
                        "nimi VARCHAR(100) NOT NULL UNIQUE," +
                        "parooli_sool VARCHAR(24) NOT NULL," +
                        "parooli_rasi VARCHAR(44) NOT NULL" +
                        ");";

        try (PreparedStatement looKasutajadOlemLause = andmebaas.prepareStatement(looKasutajadOlem)) {
            looKasutajadOlemLause.executeUpdate();
            System.out.printf("%s olem loodud\n", tabeliNimi);
        }
    }

    public void looEesmargidOlem() throws SQLException {
        final String tabeliNimi = "eesmargid";

        if (kasOlemOlemas(tabeliNimi)) {
            System.out.printf("%s olem juba olemas\n", tabeliNimi);
            return;
        }

        final String looEesmargidOlem =
                "CREATE TABLE " + tabeliNimi + " (" +
                        "eesmark_id INTEGER PRIMARY KEY NOT NULL UNIQUE," +
                        "eesmark_nimi VARCHAR(100) NOT NULL, " +
                        "kasutaja_id INT NOT NULL," +
                        "kas_tehtud BOOLEAN DEFAULT FALSE NOT NULL," +
                        "tahtaeg INTEGER," +
                        "FOREIGN KEY (kasutaja_id) REFERENCES kasutajad(kasutaja_id)," +
                        "CONSTRAINT kasutajal_ainulaadsed_eesmargid UNIQUE (eesmark_nimi, kasutaja_id)" +
                        ");";
        try (PreparedStatement looEesmargidOlemLause = andmebaas.prepareStatement(looEesmargidOlem)) {
            looEesmargidOlemLause.executeUpdate();
            System.out.printf("%s olem loodud\n", tabeliNimi);
        }
    }

    public void looUlesandedOlem() throws SQLException {
        final String tabeliNimi = "ulesanded";

        if (kasOlemOlemas(tabeliNimi)) {
            System.out.printf("%s olem juba olemas\n", tabeliNimi);
            return;
        }

        final String looUlesandedOlem =
                "CREATE TABLE " + tabeliNimi + " (" +
                        "ulesanne_id INTEGER PRIMARY KEY NOT NULL UNIQUE," +
                        "ulesanne_nimi VARCHAR(100) NOT NULL," +
                        "eesmark_id INT NOT NULL," +
                        "kas_tehtud BOOLEAN DEFAULT FALSE NOT NULL," +
                        "tahtaeg INTEGER," +
                        "FOREIGN KEY (eesmark_id) REFERENCES eesmargid(eesmark_id)," +
                        "CONSTRAINT kasutajal_ainulaadsed_ulesanded UNIQUE (ulesanne_nimi, eesmark_id)" +
                        ");";

        try (PreparedStatement looUlesandedOlemLause = andmebaas.prepareStatement(looUlesandedOlem)) {
            looUlesandedOlemLause.executeUpdate();
            System.out.printf("%s olem loodud\n", tabeliNimi);
        }
    }

    public void looProduktiivsusAegOlem() throws SQLException {
        final String tabeliNimi = "produktiivne_aeg";
        final String kustutaProduktiivsusAegOlem =
                "DROP TABLE IF EXISTS produktiivne_aeg;";

        try (PreparedStatement kustutaTabelLause = andmebaas.prepareStatement(kustutaProduktiivsusAegOlem)) {
            kustutaTabelLause.executeUpdate();
        }


        if (kasOlemOlemas(tabeliNimi)) {
            System.out.printf("%s olem juba olemas\n", tabeliNimi);
            return;
        }

        final String looProduktiivsusAegOlem =
                "CREATE TABLE " + tabeliNimi + " (" +
                        "produktiivne_aeg_id INTEGER PRIMARY KEY NOT NULL UNIQUE," +
                        "kuupaev INTEGER NOT NULL," +
                        "produktiivne_aeg_sekundites INTEGER," +
                        "ulesanne_id INT NOT NULL," +
                        "FOREIGN KEY (ulesanne_id) REFERENCES ulesanded(ulesanne_id)" +
                        ");";

        try (PreparedStatement looProduktiivsusAegOlemLause = andmebaas.prepareStatement(looProduktiivsusAegOlem)) {
            looProduktiivsusAegOlemLause.executeUpdate();
            System.out.printf("%s olem loodud\n", tabeliNimi);
        }
    }

    public void lisaUusProduktiivsusAeg(Timestamp kuupaev, int aegSekundites, int ulesanneID) throws SQLException {
        final String lisaUusProduktiivsusAeg =
                "INSERT INTO produktiivne_aeg (kuupaev, produktiivne_aeg_sekundites, ulesanne_id) " +
                        "VALUES (?, ?, ?)";

        try (PreparedStatement lisaUusProduktiivsusAegLause = andmebaas.prepareStatement(lisaUusProduktiivsusAeg)) {
            lisaUusProduktiivsusAegLause.setLong(1, kuupaev.getTime());
            lisaUusProduktiivsusAegLause.setInt(2, aegSekundites);
            lisaUusProduktiivsusAegLause.setInt(3, ulesanneID);
            lisaUusProduktiivsusAegLause.executeUpdate();
        }
    }

    public int lisaUusKasutaja(String kasutajaNimi, String parooli_sool, String parooli_rasi) throws SQLException {
        final String lisaUusKasutaja =
                "INSERT INTO kasutajad (nimi, parooli_sool, parooli_rasi) " +
                        "VALUES (?, ?, ?)";
        int kasutajaID = -1;  // Näitab kasutaja loomise ebaõnnestumist

        try (PreparedStatement lisaUusKasutajaLause =
                     andmebaas.prepareStatement(lisaUusKasutaja, PreparedStatement.RETURN_GENERATED_KEYS)) {
            lisaUusKasutajaLause.setString(1, kasutajaNimi);
            lisaUusKasutajaLause.setString(2, parooli_sool);
            lisaUusKasutajaLause.setString(3, parooli_rasi);
            kasutajaID = kontrolliLisatudOlemit(lisaUusKasutajaLause, "Kasutaja");
        }

        return kasutajaID;
    }

    public int tagastaUlesandeProduktiivneAeg(int ulesanneID) throws SQLException {
        final String tagastaUlesandeProduktiivneAeg =
                "SELECT SUM(produktiivne_aeg_sekundites) " +
                        "FROM produktiivne_aeg " +
                        "WHERE ulesanne_id=? ";

        try (PreparedStatement tagastaUlesandeProduktiivneAegLause = andmebaas.prepareStatement(tagastaUlesandeProduktiivneAeg)) {
            tagastaUlesandeProduktiivneAegLause.setInt(1, ulesanneID);
            ResultSet tagastus = tagastaUlesandeProduktiivneAegLause.executeQuery();
            tagastus.next();
            return tagastus.getInt("produktiivne_aeg_sekundites");
        }
    }

    public boolean kasKasutajanimiOlemas(String kasutajaNimi) throws SQLException {
        final String kontrolliUnikaalsust =
                "SELECT COUNT(*) AS kasutajate_arv " +
                        "FROM kasutajad " +
                        "WHERE kasutajad.nimi=?";

        try (PreparedStatement kontrolliUnikaalsustLause = andmebaas.prepareStatement(kontrolliUnikaalsust)) {
            kontrolliUnikaalsustLause.setString(1, kasutajaNimi);
            ResultSet tagastus = kontrolliUnikaalsustLause.executeQuery();
            tagastus.next();
            int kasutajateArv = tagastus.getInt("kasutajate_arv");
            return kasutajateArv > 0;
        }
    }

    public String[] tagastaKasutajaSoolJaRasi(String kasutajaNimi) throws SQLException {
        final String tagastaKasutajaSoolJaRasi =
                "SELECT parooli_sool, parooli_rasi " +
                        "FROM kasutajad " +
                        "WHERE kasutajad.nimi=?";

        try (PreparedStatement tagastaKasutajaSoolJaRasiLause = andmebaas.prepareStatement(tagastaKasutajaSoolJaRasi)) {
            tagastaKasutajaSoolJaRasiLause.setString(1, kasutajaNimi);
            ResultSet tagastus = tagastaKasutajaSoolJaRasiLause.executeQuery();
            String[] kasutajaAndmed = new String[2];
            tagastus.next();
            kasutajaAndmed[0] = tagastus.getString("parooli_sool");
            kasutajaAndmed[1] = tagastus.getString("parooli_rasi");
            return kasutajaAndmed;
        }
    }

    public int tagastaKasutajaID(String kasutajaNimi, String parool) throws SQLException {
        final String tagastaKasutajaID =
                "SELECT kasutaja_id " +
                        "FROM kasutajad " +
                        "WHERE nimi=? AND parooli_rasi=?";

        try (PreparedStatement tagastaKasutajaIDLause = andmebaas.prepareStatement(tagastaKasutajaID)) {
            tagastaKasutajaIDLause.setString(1, kasutajaNimi);
            tagastaKasutajaIDLause.setString(2, parool);
            ResultSet tagastus = tagastaKasutajaIDLause.executeQuery();
            tagastus.next();
            return tagastus.getInt("kasutaja_id");
        }
    }

    public int lisaUusEesmark(String eesmargiNimi, int kasutajaID) throws SQLException {
        final String lisaUusEesmark =
                "INSERT INTO eesmargid (eesmark_nimi, kasutaja_id) " +
                        "VALUES (?, ?)";
        int eesmarkID = -1;  // Näitab ülesande loomise ebaõnnestumist

        try (PreparedStatement lisaUusEesmarkLause =
                     andmebaas.prepareStatement(lisaUusEesmark, PreparedStatement.RETURN_GENERATED_KEYS)) {
            lisaUusEesmarkLause.setString(1, eesmargiNimi);
            lisaUusEesmarkLause.setInt(2, kasutajaID);

            eesmarkID = kontrolliLisatudOlemit(lisaUusEesmarkLause, "Ülesanne");
        }

        return eesmarkID;
    }

    public int lisaUusEesmark(String eesmargiNimi, int kasutajaID, Timestamp tahtaeg) throws SQLException {
        final String lisaUusEesmark =
                "INSERT INTO eesmargid (eesmark_nimi, kasutaja_id, tahtaeg) " +
                        "VALUES (?, ?, ?)";
        int eesmarkID = -1;  // Näitab ülesande loomise ebaõnnestumist

        try (PreparedStatement lisaUusEesmarkLause =
                     andmebaas.prepareStatement(lisaUusEesmark, PreparedStatement.RETURN_GENERATED_KEYS)) {
            lisaUusEesmarkLause.setString(1, eesmargiNimi);
            lisaUusEesmarkLause.setInt(2, kasutajaID);
            lisaUusEesmarkLause.setLong(3, tahtaeg.getTime());

            eesmarkID = kontrolliLisatudOlemit(lisaUusEesmarkLause, "Ülesanne");
        }

        return eesmarkID;
    }

    public int lisaUusUlesanne(String ulesandeNimi, int eesmarkID) throws SQLException {
        final String lisaUusUlesanne =
                "INSERT INTO ulesanded (ulesanne_nimi, eesmark_id) " +
                        "VALUES (?, ?)";
        int ulesandeID = -1;  // Näitab ülesande loomise ebaõnnestumist

        try (PreparedStatement lisaUusUlesanneLause =
                     andmebaas.prepareStatement(lisaUusUlesanne, PreparedStatement.RETURN_GENERATED_KEYS)) {
            lisaUusUlesanneLause.setString(1, ulesandeNimi);
            lisaUusUlesanneLause.setInt(2, eesmarkID);

            ulesandeID = kontrolliLisatudOlemit(lisaUusUlesanneLause, "Ülesanne");
        }

        return ulesandeID;
    }

    public int lisaUusUlesanne(String ulesandeNimi, int eesmarkID, Timestamp tahtaeg) throws SQLException {
        final String lisaUusUlesanne =
                "INSERT INTO ulesanded (ulesanne_nimi, eesmark_id, tahtaeg) " +
                        "VALUES (?, ?, ?)";
        int ulesandeID = -1;  // Näitab ülesande loomise ebaõnnestumist

        try (PreparedStatement lisaUusUlesanneLause =
                     andmebaas.prepareStatement(lisaUusUlesanne, PreparedStatement.RETURN_GENERATED_KEYS)) {
            lisaUusUlesanneLause.setString(1, ulesandeNimi);
            lisaUusUlesanneLause.setInt(2, eesmarkID);
            lisaUusUlesanneLause.setLong(3, tahtaeg.getTime());

            ulesandeID = kontrolliLisatudOlemit(lisaUusUlesanneLause, "Ülesanne");
        }

        return ulesandeID;
    }

    public int kontrolliLisatudOlemit(PreparedStatement uueOlemiLause, String olemiTuup) throws SQLException {
        int olemiID = -1;
        int lisatudOlemeid = uueOlemiLause.executeUpdate();
        if (lisatudOlemeid != 1)  {
            System.out.printf("%s olemi loomisel tekkis viga: VEATEATETA\n", olemiTuup);
            return olemiID;
        }

        try (ResultSet uueOlemiID = uueOlemiLause.getGeneratedKeys()) {
            if (!uueOlemiID.next()) {
                System.out.printf("%s olemi võtme id genereerimisel tekkis viga: VEATEATETA\n", olemiTuup);
                return olemiID;
            }

            olemiID = uueOlemiID.getInt(1);
            System.out.printf("%s lisamine edukas\n", olemiTuup);

        }

        return olemiID;
    }

    public ArrayList<Ulesanne> tagastaUlesanneteOlemid(int eesmarkID) throws SQLException {
        ArrayList<Ulesanne> ulesanded = new ArrayList<Ulesanne>();
        final String tagastaUlesanneteOlemid =
                "SELECT ulesanne_id, ulesanne_nimi, kas_tehtud, tahtaeg " +
                        "FROM ulesanded " +
                        "WHERE eesmark_id = ?";

        try (PreparedStatement tagastaUlesanneteOlemidLause = andmebaas.prepareStatement(tagastaUlesanneteOlemid)) {
            tagastaUlesanneteOlemidLause.setInt(1, eesmarkID);

            try (ResultSet tagastaOlemidLauseTulem = tagastaUlesanneteOlemidLause.executeQuery()) {
                while (tagastaOlemidLauseTulem.next()) {
                    int ulesandeID = tagastaOlemidLauseTulem.getInt("ulesanne_id");
                    String ulesandeNimi = tagastaOlemidLauseTulem.getString("ulesanne_nimi");
                    boolean tehtud = tagastaOlemidLauseTulem.getBoolean("kas_tehtud");
                    int aegEpochist = tagastaOlemidLauseTulem.getInt("tahtaeg");

                    if (tagastaOlemidLauseTulem.wasNull()) {
                        ulesanded.add(new Ulesanne(ulesandeID, ulesandeNimi, tehtud));
                    } else {
                        ulesanded.add(new Ulesanne(ulesandeID, ulesandeNimi, tehtud, new Timestamp(aegEpochist)));
                    }

                }
            }
        }

        return ulesanded;
    }

    public ArrayList<Eesmark> tagastaEesmarkideOlemid(int kasutajaID) throws SQLException {
        ArrayList<Eesmark> eesmargid = new ArrayList<Eesmark>();
        final String tagastaEesmarkideOlemid =
                "SELECT eesmark_id, eesmark_nimi, kas_tehtud, tahtaeg " +
                        "FROM eesmargid " +
                        "WHERE kasutaja_id = ?";

        try (PreparedStatement tagastaEesmargidOlemidLause = andmebaas.prepareStatement(tagastaEesmarkideOlemid)) {
            tagastaEesmargidOlemidLause.setInt(1, kasutajaID);

            try (ResultSet tagastaOlemidLauseTulem = tagastaEesmargidOlemidLause.executeQuery()) {
                while (tagastaOlemidLauseTulem.next()) {
                    int ulesandeID = tagastaOlemidLauseTulem.getInt("eesmark_id");
                    String ulesandeNimi = tagastaOlemidLauseTulem.getString("eesmark_nimi");
                    boolean tehtud = tagastaOlemidLauseTulem.getBoolean("kas_tehtud");
                    int aegEpochist = tagastaOlemidLauseTulem.getInt("tahtaeg");

                    if (tagastaOlemidLauseTulem.wasNull()) {
                        eesmargid.add(new Eesmark(ulesandeID, ulesandeNimi, tehtud));
                    } else {
                        eesmargid.add(new Eesmark(ulesandeID, ulesandeNimi, tehtud, new Timestamp(aegEpochist)));
                    }
                }
            }
        }

        return eesmargid;
    }

    @Override
    public void close() throws Exception {
        andmebaas.close();
    }
}