module com.produktiivsusjalgijaklient.klient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.bouncycastle.provider;


    opens com.produktiivsusjalgijaklient.klient to javafx.fxml;
    exports com.produktiivsusjalgijaklient.klient;
}