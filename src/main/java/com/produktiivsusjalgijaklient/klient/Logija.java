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

    public void kirjutaLogi(String logi) throws IOException {
        LocalDateTime hetkeAeg = LocalDateTime.now();
        DateTimeFormatter ajaFormaat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formaaditudHetkeAeg = hetkeAeg.format(ajaFormaat);

        failiKirjutaja.write(formaaditudHetkeAeg + ": " + logi);
        failiKirjutaja.newLine();
    }

    public void kirjutaErind(SQLException erind, String selgitus) throws IOException {
        kirjutaLogi("SQL viga - %s - %s".formatted(selgitus, erind.getMessage()));
    }

    public void kirjutaErind(IOException erind, String selgitus) throws IOException {
        kirjutaLogi("Input-Output viga - %s - %s".formatted(selgitus, erind.getMessage()));
    }

    @Override
    public void close() throws IOException {
        failiKirjutaja.close();
    }
}
