module com.example.fxjgit {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.eclipse.jgit;


    opens com.example.fxjgit to javafx.fxml;
    exports com.example.fxjgit;
}