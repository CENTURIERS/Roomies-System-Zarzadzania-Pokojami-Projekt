package com.roomies.controller;

import com.roomies.dao.KlientDao;
import com.roomies.model.Klient;
import com.roomies.util.UserSession;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button anulujButton;
    @FXML
    private Hyperlink rejestracjaLink;
    @FXML
    private Label errorLabel;

    private KlientDao klientDao;
    private MainController mainController;

    @FXML
    public void initialize() {
        klientDao = new KlientDao();
        errorLabel.managedProperty().bind(errorLabel.visibleProperty());
        errorLabel.setVisible(false);
        javafx.application.Platform.runLater(() -> emailField.requestFocus());
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleLoginButton(ActionEvent event) {
        ukryjBlad();
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            pokazBlad("Email i hasło nie mogą być puste.");
            return;
        }

        Optional<Klient> klientOptional = klientDao.findByEmail(email);

        if (klientOptional.isPresent()) {
            Klient klient = klientOptional.get();
            if (klient.getHaslo().equals(password)) {
                if (klient.isCzyAktywny()) {
                    UserSession.getInstance().loginUser(klient);
                    System.out.println("Zalogowano pomyślnie: " + klient.getEmail());
                    if (mainController != null) {
                        mainController.zaktualizujStatusZalogowania(klient);
                    }
                    zamknijOkno();
                } else {
                    pokazBlad("Twoje konto jest nieaktywne.");
                }
            } else {
                pokazBlad("Nieprawidłowy email lub hasło.");
            }
        } else {
            pokazBlad("Nieprawidłowy email lub hasło.");
        }
    }

    @FXML
    private void handleRejestracjaLink(ActionEvent event) {
        Window ownerWindow = null;
        if (rejestracjaLink != null && rejestracjaLink.getScene() != null) {
            ownerWindow = rejestracjaLink.getScene().getWindow();
        }
        otworzOknoRejestracjiModalnieNadLogowaniem(ownerWindow);
    }

    private void otworzOknoRejestracjiModalnieNadLogowaniem(Window ownerWindow) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/roomies/view/rejestracja-view.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Rejestracja Nowego Konta");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            if (ownerWindow instanceof Stage && ownerWindow.isShowing()) {
                dialogStage.initOwner(ownerWindow);
            } else {
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                if (mainController != null && mainController.getPrimaryStage() != null && mainController.getPrimaryStage().isShowing()){
                    dialogStage.initOwner(mainController.getPrimaryStage());
                }
            }

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (emailField != null) {
                emailField.requestFocus();
            }

        } catch (IOException e) {
            e.printStackTrace();
            pokazAlert(Alert.AlertType.ERROR,"Błąd Aplikacji", "Nie można otworzyć formularza rejestracji.\n" + e.getMessage());
        }
    }

    @FXML
    private void handleAnulujButton(ActionEvent event) {
        zamknijOkno();
    }

    private void pokazBlad(String wiadomosc) {
        errorLabel.setText(wiadomosc);
        errorLabel.setVisible(true);
    }

    private void ukryjBlad() {
        errorLabel.setVisible(false);
    }

    private void pokazAlert(Alert.AlertType typ, String tytul, String wiadomosc) {
        Alert alert = new Alert(typ);
        alert.setTitle(tytul);
        alert.setHeaderText(null);
        alert.setContentText(wiadomosc);
        alert.showAndWait();
    }

    private void zamknijOkno() {
        Stage stage = null;
        if (loginButton != null && loginButton.getScene() != null && loginButton.getScene().getWindow() instanceof Stage) {
            stage = (Stage) loginButton.getScene().getWindow();
        } else if (anulujButton != null && anulujButton.getScene() != null && anulujButton.getScene().getWindow() instanceof Stage) {
            stage = (Stage) anulujButton.getScene().getWindow();
        }

        if (stage != null && stage.isShowing()) {
            stage.close();
        }
    }
}