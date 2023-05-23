package com.example.fxjgit;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangeLineController implements Initializable {
    public boolean isChecked;
    public String text;
    public CheckBox checkBox;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            isChecked = newValue;
        });
    }
}
