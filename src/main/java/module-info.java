module com.example.fxjgit {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.eclipse.jgit;
    requires java.sql;


    opens com.example.fxjgit to javafx.fxml;
    exports com.example.fxjgit;
    exports com.example.fxjgit.popups;
    exports com.example.fxjgit.enter;
    opens com.example.fxjgit.popups to javafx.fxml;
}