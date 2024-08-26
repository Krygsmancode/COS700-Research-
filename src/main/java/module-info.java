module com.example.trongp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jfree.jfreechart;
    requires java.desktop;

    opens com.example.trongp to javafx.fxml;
    exports com.example.trongp;
}
