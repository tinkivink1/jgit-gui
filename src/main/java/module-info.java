module com.example.fxjgit {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.eclipse.jgit;
    requires java.sql;
    requires org.apache.poi.ooxml;


    opens com.example.fxjgit to javafx.fxml;
    exports com.example.fxjgit;
    exports com.example.fxjgit.forms.popups;
    exports com.example.fxjgit.forms.enter;
    opens com.example.fxjgit.forms.popups to javafx.fxml;
    exports com.example.fxjgit.forms;
    opens com.example.fxjgit.forms to javafx.fxml;
}