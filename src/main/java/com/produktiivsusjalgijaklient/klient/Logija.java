package com.produktiivsusjalgijaklient.klient;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logija implements AutoCloseable {
    private BufferedWriter failiKirjutaja;

    public Logija() throws IOException {
        File fail = new File("logi.txt");
        if (!fail.exists())
            if (!fail.createNewFile()) throw new IOException("Logifaili loomine ebaõnnestus");

        failiKirjutaja = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fail, true), StandardCharsets.UTF_8));
    }

    /**
     * Kirjutab ühe rea logifaili
     * @param logi Rida, mida kirjutatakse
     * @throws IOException
     */
    public void kirjutaLogi(String logi) throws IOException {
        LocalDateTime hetkeAeg = LocalDateTime.now();  // Logi kirjutamise hetkel olev aeg
        DateTimeFormatter ajaFormaat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formaaditudHetkeAeg = hetkeAeg.format(ajaFormaat);

        failiKirjutaja.write(formaaditudHetkeAeg + ": " + logi);
        failiKirjutaja.newLine();
    }

    /**
     * Kirjutab logidesse erindi
     * @param erind Erind, millest lisainformatsiooni saadakse
     * @param selgitus Mida koodis parasjagu üritati teha
     * @throws IOException
     */
    public void kirjutaErind(SQLException erind, String selgitus) throws IOException {
        kirjutaLogi("SQL viga - %s - %s".formatted(selgitus, erind.getMessage()));
    }

    public void kirjutaErind(IOException erind, String selgitus) throws IOException {
        kirjutaLogi("Input-Output viga - %s - %s".formatted(selgitus, erind.getMessage()));
    }

    /**
     * Loeb viimased 10 rida, et vea hetkel näidata, mis taustal toimus
     * @return Logifaili viimased 10 rida
     * @throws IOException
     */
    public String loeViimased10Rida() throws IOException {
        failiKirjutaja.flush();
        String failinimi = "logi.txt";
        StringBuilder logid = new StringBuilder();
        int ridu = 10;

        try (RandomAccessFile fail = new RandomAccessFile(failinimi, "r")) {
            long failiPikkus = fail.length();
            long kursor = failiPikkus - 1;
            int loetudRidu = 0;

            while (0 <= kursor && loetudRidu < ridu) {
                fail.seek(kursor);
                char taht = (char) fail.read();
                logid.append(taht);
                kursor--;

                if (taht == '\n') {
                    loetudRidu++;
                }
            }
        }

        logid.reverse();
        return logid.toString();
    }

    @Override
    public void close() throws IOException {
        failiKirjutaja.close();
    }
}
