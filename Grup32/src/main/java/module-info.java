module com.example.grup32 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.grup32 to javafx.fxml;
    exports com.example.grup32;
}