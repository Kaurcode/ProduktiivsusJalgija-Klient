package com.produktiivsusjalgijaklient.klient;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Eesmark {
    private int eesmargiID;
    private String eesmargiNimi;
    private boolean kasTehtud;
    private Timestamp tahtaeg;

    private ArrayList<Ulesanne> ulesanded;

    public Eesmark(int eesmargiID, String eesmargiNimi, boolean kasTehtud, ArrayList<Ulesanne> ulesanded) {
        this.eesmargiID = eesmargiID;
        this.eesmargiNimi = eesmargiNimi;
        this.kasTehtud = kasTehtud;
        this.ulesanded = ulesanded;
    }

    public Eesmark(int eesmargiID, String eesmargiNimi, boolean kasTehtud, Timestamp tahtaeg) {
        this(eesmargiID, eesmargiNimi, kasTehtud);
        this.tahtaeg = tahtaeg;
    }

    public Eesmark(int eesmargiID, String eesmargiNimi, boolean kasTehtud) {
        this.eesmargiID = eesmargiID;
        this.eesmargiNimi = eesmargiNimi;
        this.kasTehtud = kasTehtud;
        this.ulesanded = new ArrayList<>();
    }

    public Eesmark(int eesmargiID, String eesmargiNimi, boolean kasTehtud, Timestamp tahtaeg, ArrayList<Ulesanne> ulesanded) {
        this(eesmargiID, eesmargiNimi, kasTehtud, ulesanded);
        this.tahtaeg = tahtaeg;
    }

    public Eesmark(int eesmargiID, String eesmargiNimi, ArrayList<Ulesanne> ulesanded) {
        this(eesmargiID, eesmargiNimi, false, ulesanded);
    }

    public int getEesmargiID() {
        return eesmargiID;
    }

    public void setUlesanded(ArrayList<Ulesanne> ulesanded) {
        this.ulesanded = ulesanded;
    }
}
