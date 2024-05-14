package com.produktiivsusjalgijaklient.klient;

public class KasutajaOlemasErind extends Exception{
    public KasutajaOlemasErind(String message) {
        super(message);
    }

    public String toString() {
        return getMessage();
    }
}
