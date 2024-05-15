package com.produktiivsusjalgijaklient.klient;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Timer;
import java.util.TimerTask;

public class Taimer implements AutoCloseable {
    private long algneAeg;
    private IntegerProperty aegSekundites;
    private Timer taimer;

    public Taimer(int aegSekundites) {
        this.aegSekundites = new SimpleIntegerProperty(aegSekundites);
        algneAeg = aegSekundites;
    }

    private TimerTask looLoendaja() {
        return new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    int hetkeAeg = aegSekunditesProperty().getValue();
                    aegSekunditesProperty().setValue(hetkeAeg - 1);
                });
            }
        };
    }

    public void alustaLoendust() {
        taimer = new Timer();
        taimer.scheduleAtFixedRate(looLoendaja(), 0, 1000);
    }

    public int getAegSekundites() {
        return aegSekundites.get();
    }

    public IntegerProperty aegSekunditesProperty() {
        return aegSekundites;
    }

    @Override
    public void close() {
        taimer.cancel();
    }
}
