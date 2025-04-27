module com.iit.tutorials.tax_cw {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.iit.tutorials.tax_cw to javafx.fxml;
    exports com.iit.tutorials.tax_cw;
}