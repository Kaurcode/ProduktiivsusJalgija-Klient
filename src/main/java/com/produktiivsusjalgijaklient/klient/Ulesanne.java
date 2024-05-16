package com.produktiivsusjalgijaklient.klient;

import java.sql.Timestamp;

public class Ulesanne {
    private int ulesandeID;
    private String ulesandeNimi;
    private boolean tehtud;
    private Timestamp tahtaeg;

    public Ulesanne(int ulesandeID, String ulesandeNimi, boolean tehtud) {
        this.ulesandeID = ulesandeID;
        this.ulesandeNimi = ulesandeNimi;
        this.tehtud = tehtud;
    }

    public Ulesanne(int ulesandeID, String ulesandeNimi) {
        this(ulesandeID, ulesandeNimi, false);
    }

    public Ulesanne(int ulesandeID, String ulesandeNimi, boolean tehtud, Timestamp tahtaeg) {
        this(ulesandeID, ulesandeNimi, tehtud);
        this.tahtaeg = tahtaeg;
    }

    public String getUlesandeNimi() {
        return ulesandeNimi;
    }

    public int getUlesandeID() {
        return ulesandeID;
    }
}
