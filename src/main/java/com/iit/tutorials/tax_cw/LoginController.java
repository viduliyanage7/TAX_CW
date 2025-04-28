package com.iit.tutorials.tax_cw;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    @FXML private TextField username;
    @FXML private PasswordField password;

    @FXML
    private void onLogin() {
        if (username.getText().equals("Admin") && password.getText().equals("Admin")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
                Scene dashboardScene = new Scene(loader.load());

                Stage stage = (Stage) username.getScene().getWindow();
                stage.setScene(dashboardScene);

                HomeController dashboardController = loader.getController();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else  if (username.getText().equals("user") && password.getText().equals("user")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
                Scene dashboardScene = new Scene(loader.load());

                HomeController dashboardController = loader.getController();

                dashboardController.setUsertype("user");

                Stage stage = (Stage) username.getScene().getWindow();
                stage.setScene(dashboardScene);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Invalid Username or Password");
            alert.showAndWait();
        }
    }
}
