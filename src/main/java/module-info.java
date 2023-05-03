module com.example.java_course_lab_2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.google.gson;
    requires java.sql;

    opens com.example.java_course_lab_2 to javafx.fxml, com.google.gson;
    exports com.example.java_course_lab_2;
}