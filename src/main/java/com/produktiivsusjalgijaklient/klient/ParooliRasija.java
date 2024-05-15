package com.produktiivsusjalgijaklient.klient;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.util.encoders.Base64Encoder;

import java.security.SecureRandom;
import java.util.Base64;

public class ParooliRasija {
    private final static int RASIMISITERATSIOONE = 10;
    private final static int MALULIMIIT_KB = 66536;
    private final static int RASI_PIKKUS_BAIT = 32;
    private final static int PARALLEELSUSI = 1;
    private final static int SOOLA_PIKKUS_BAIT = 16;

    public static String looParooliRasi(char[] parool, String sool) {
        return Base64.getEncoder().encodeToString(genereeriParooliRasi(parool, sool));
    }

    public static byte[] genereeriParooliRasi(char[] parool, String sool) {
        Argon2Parameters.Builder argon2Ehitaja = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withIterations(RASIMISITERATSIOONE)
                .withMemoryAsKB(MALULIMIIT_KB)
                .withParallelism(PARALLEELSUSI)
                .withSalt(Base64.getDecoder().decode(sool));

        Argon2BytesGenerator rasiGenereerija = new Argon2BytesGenerator();
        rasiGenereerija.init(argon2Ehitaja.build());
        byte[] parooliRasi = new byte[RASI_PIKKUS_BAIT];
        rasiGenereerija.generateBytes(parool, parooliRasi, 0, parooliRasi.length);

        return parooliRasi;
    }

    public static String genereeriSool() {
        byte[] sool = new byte[SOOLA_PIKKUS_BAIT];
        SecureRandom suvalisusGeneraator = new SecureRandom();
        suvalisusGeneraator.nextBytes(sool);
        return Base64.getEncoder().encodeToString(sool);
    }
}
