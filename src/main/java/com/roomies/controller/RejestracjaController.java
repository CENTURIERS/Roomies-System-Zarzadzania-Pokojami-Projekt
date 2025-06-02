package com.roomies.controller;

import com.roomies.dao.KlientDao;
import com.roomies.model.Klient;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.regex.Pattern;

public class RejestracjaController {

    @FXML
    private TextField imieField;
    @FXML
    private TextField nazwiskoField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField hasloField;
    @FXML
    private PasswordField powtorzHasloField;
    @FXML
    private TextField telefonField;
    @FXML
    private TextField ulicaField;
    @FXML
    private TextField numerBudynkuField;
    @FXML
    private TextField peselField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button anulujButton;
    @FXML
    private Button zarejestrujButton;

    private KlientDao klientDao;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    private static final Pattern PESEL_PATTERN = Pattern.compile("^[0-9]{11}$");
    private static final int MIN_PASSWORD_LENGTH = 6;

    @FXML
    public void initialize() {
        klientDao = new KlientDao();
        errorLabel.managedProperty().bind(errorLabel.visibleProperty());
        errorLabel.setVisible(false);

        Platform.runLater(() -> {
            if (imieField != null) {
                imieField.requestFocus();
            }
        });
    }

    @FXML
    private void handleZarejestrujButton(ActionEvent event) {
        ukryjBlad();

        String imie = imieField.getText().trim();
        String nazwisko = nazwiskoField.getText().trim();
        String email = emailField.getText().trim();
        String haslo = hasloField.getText();
        String powtorzHaslo = powtorzHasloField.getText();
        String telefon = telefonField.getText().trim();
        String ulica = ulicaField.getText().trim();
        String numerBudynku = numerBudynkuField.getText().trim();
        String pesel = peselField.getText().trim();

        if (!walidujDaneRejestracji(imie, nazwisko, email, haslo, powtorzHaslo, ulica, numerBudynku, pesel)) {
            return;
        }

        Optional<Klient> istniejacyKlientOptional = klientDao.findByEmail(email);
        if (istniejacyKlientOptional.isPresent()) {
            pokazBlad("Konto o podanym adresie email już istnieje.");
            return;
        }

        Klient nowyKlient = new Klient();
        nowyKlient.setImie(imie);
        nowyKlient.setNazwisko(nazwisko);
        nowyKlient.setEmail(email);
        nowyKlient.setHaslo(haslo);

        if (!telefon.isEmpty()) {
            nowyKlient.setTelefon(telefon);
        }
        nowyKlient.setUlica(ulica);
        nowyKlient.setNumerBudynku(numerBudynku);
        nowyKlient.setPesel(pesel);
        nowyKlient.setCzyAktywny(true);

        try {
            klientDao.save(nowyKlient);
            pokazAlert(Alert.AlertType.INFORMATION, "Rejestracja Pomyślna",
                    "Konto dla " + imie + " " + nazwisko + " zostało utworzone.\nMożesz się teraz zalogować.");
            zamknijOkno();
        } catch (Exception e) {
            e.printStackTrace();
            pokazBlad("Wystąpił błąd podczas tworzenia konta. Spróbuj ponownie.\nSzczegóły: " + e.getMessage());
        }
    }

    private boolean walidujDaneRejestracji(String imie, String nazwisko, String email, String haslo, String powtorzHaslo,
                                           String ulica, String numerBudynku, String pesel) {
        StringBuilder sb = new StringBuilder();

        if (imie.isEmpty()) sb.append("Pole 'Imię' jest wymagane.\n");
        if (nazwisko.isEmpty()) sb.append("Pole 'Nazwisko' jest wymagane.\n");
        if (email.isEmpty()) {
            sb.append("Pole 'Email' jest wymagane.\n");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            sb.append("Niepoprawny format adresu email.\n");
        }
        if (haslo.isEmpty()) {
            sb.append("Pole 'Hasło' jest wymagane.\n");
        } else if (haslo.length() < MIN_PASSWORD_LENGTH) {
            sb.append("Hasło musi mieć co najmniej ").append(MIN_PASSWORD_LENGTH).append(" znaków.\n");
        }
        if (powtorzHaslo.isEmpty()) {
            sb.append("Pole 'Powtórz hasło' jest wymagane.\n");
        } else if (!haslo.isEmpty() && !haslo.equals(powtorzHaslo)) {
            sb.append("Podane hasła nie są identyczne.\n");
        }
        if (ulica.isEmpty()) sb.append("Pole 'Ulica' jest wymagane.\n");
        if (numerBudynku.isEmpty()) sb.append("Pole 'Numer budynku' jest wymagane.\n");
        if (pesel.isEmpty()) {
            sb.append("Pole 'PESEL' jest wymagane.\n");
        } else if (!PESEL_PATTERN.matcher(pesel).matches()) {
            sb.append("PESEL musi składać się z 11 cyfr.\n");
        }

        if (sb.length() > 0) {
            pokazBlad(sb.toString().trim());
            return false;
        }
        return true;
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
        if (zarejestrujButton != null && zarejestrujButton.getScene() != null){
            stage = (Stage) zarejestrujButton.getScene().getWindow();
        } else if (anulujButton != null && anulujButton.getScene() != null) {
            stage = (Stage) anulujButton.getScene().getWindow();
        }

        if (stage != null) {
            stage.close();
        }
    }
}