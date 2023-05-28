package com.example.fxjgit;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangeLineController implements Initializable {
    public CheckBox checkBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
    }

    public String getText(){
        return checkBox.getText();
    }

    public void setText(String text){
        checkBox.setText(text);
    }

}
