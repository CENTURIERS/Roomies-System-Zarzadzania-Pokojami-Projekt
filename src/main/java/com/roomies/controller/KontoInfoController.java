package com.roomies.controller;

import com.roomies.model.Klient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class KontoInfoController {

    @FXML
    private Label imieLabel;
    @FXML
    private Label nazwiskoLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label telefonLabel;
    @FXML
    private Label ulicaLabel;
    @FXML
    private Label numerBudynkuLabel;
    @FXML
    private Label peselLabel;
    @FXML
    private Label statusKontaLabel;


    @FXML
    public void initialize() {
    }

    public void wyswietlInformacje(Klient klient) {
        if (klient != null) {
            imieLabel.setText(klient.getImie() != null ? klient.getImie() : "-");
            nazwiskoLabel.setText(klient.getNazwisko() != null ? klient.getNazwisko() : "-");
            emailLabel.setText(klient.getEmail() != null ? klient.getEmail() : "-");
            telefonLabel.setText(klient.getTelefon() != null && !klient.getTelefon().isEmpty() ? klient.getTelefon() : "Nie podano");
            ulicaLabel.setText(klient.getUlica() != null ? klient.getUlica() : "-");
            numerBudynkuLabel.setText(klient.getNumerBudynku() != null ? klient.getNumerBudynku() : "-");
            peselLabel.setText(klient.getPesel() != null ? klient.getPesel() : "-");
            statusKontaLabel.setText(klient.isCzyAktywny() ? "Aktywne" : "Nieaktywne");

            if (klient.isCzyAktywny()) {
                statusKontaLabel.setStyle("-fx-text-fill: #38A169; -fx-font-weight: bold;");
            } else {
                statusKontaLabel.setStyle("-fx-text-fill: #E53E3E; -fx-font-weight: bold;");
            }

        } else {
            imieLabel.setText("Błąd danych");
            nazwiskoLabel.setText("Błąd danych");
            emailLabel.setText("Błąd danych");
            telefonLabel.setText("Błąd danych");
            ulicaLabel.setText("Błąd danych");
            numerBudynkuLabel.setText("Błąd danych");
            peselLabel.setText("Błąd danych");
            statusKontaLabel.setText("Błąd danych");
        }
    }
}