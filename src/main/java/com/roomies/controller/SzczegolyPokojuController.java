package com.roomies.controller;

import com.roomies.dao.PokojDao;
import com.roomies.model.Pokoj;
import com.roomies.util.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

public class SzczegolyPokojuController {
    @FXML
    private BorderPane szczegolyPokojuMainPane;
    @FXML
    private Label nazwaPokojuLabel;
    @FXML
    private ImageView zdjecieGlowne;
    @FXML
    private Label rodzajLabel;
    @FXML
    private Label lokalizacjaLabel;
    @FXML
    private Label metrazLabel;
    @FXML
    private Label wyposazenieLabel;
    @FXML
    private Label cenaLabel;
    @FXML
    private Label dostepnoscLabel;
    @FXML
    private Button rezerwujButton;
    @FXML
    private Button zamknijButton;

    private Pokoj wyswietlanyPokoj;

    private static final String DEFAULT_IMAGE_PATH = "/images/placeholder_400x300.png";

    @FXML
    public void initialize() {
        if (rezerwujButton != null) {
            rezerwujButton.setOnAction(event -> handleRezerwujButton());
        }
        if (zamknijButton != null) {
            zamknijButton.setOnAction(event -> handleZamknijOkno());
        }
    }

    public void wyswietlSzczegoly(Pokoj pokoj) {
        this.wyswietlanyPokoj = pokoj;
        if (pokoj == null){
            nazwaPokojuLabel.setText("Błąd: brak danych pokoju");
            rodzajLabel.setText("Rodzaj: -");
            lokalizacjaLabel.setText("Lokalizacja: -");
            metrazLabel.setText("Metraz: - m²");
            wyposazenieLabel.setText("Wyposazenie: -");
            cenaLabel.setText("Cena: -");
            dostepnoscLabel.setText("Dostepnosc: -");
            if(zdjecieGlowne != null) zdjecieGlowne.setImage(null);
            if(rezerwujButton != null) rezerwujButton.setDisable(true);
            return;
        }

        nazwaPokojuLabel.setText(pokoj.getNazwa() != null ? pokoj.getNazwa() : "Pokój bez nazwy");

        if (pokoj.getRodzajPokoju() != null) {
            rodzajLabel.setText(pokoj.getRodzajPokoju().getNazwa() != null ? pokoj.getRodzajPokoju().getNazwa() : "N/A");
            wyposazenieLabel.setText(pokoj.getRodzajPokoju().getWyposazenie() != null ? pokoj.getRodzajPokoju().getWyposazenie() : "Brak informacji");
            metrazLabel.setText(pokoj.getRodzajPokoju().getIlosc_metrow() > 0 ? pokoj.getRodzajPokoju().getIlosc_metrow() + " m²" : "N/A");
        } else {
            rodzajLabel.setText("Rodzaj: Nieokreślony");
            wyposazenieLabel.setText("Wyposażenie: Brak informacji");
            metrazLabel.setText("Metraż: N/A");
        }

        if (pokoj.getLokalizacja() != null) {
            lokalizacjaLabel.setText(pokoj.getLokalizacja().getNazwaLokalizacji() != null ? pokoj.getLokalizacja().getNazwaLokalizacji() : "N/A");
        } else {
            lokalizacjaLabel.setText("Lokalizacja: Nieokreślona");
        }

        if (pokoj.getCenaZaDobe() != null) {
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pl", "PL"));
            cenaLabel.setText(currencyFormatter.format(pokoj.getCenaZaDobe()) + " / doba");
        } else {
            cenaLabel.setText("Cena: Nieustalona");
        }

        dostepnoscLabel.getStyleClass().removeAll("status-dostepny", "status-niedostepny");
        if (pokoj.isDostepnosc()) {
            dostepnoscLabel.setText("Dostępny");
            dostepnoscLabel.getStyleClass().add("status-dostepny");
            if (rezerwujButton != null) rezerwujButton.setDisable(false);
        } else {
            dostepnoscLabel.setText("Niedostępny");
            dostepnoscLabel.getStyleClass().add("status-niedostepny");
            if (rezerwujButton != null) rezerwujButton.setDisable(true);
        }

        String sciezkaDoZdjeciaZBazy = pokoj.getSciezkaZdjecia();
        Image imageToSet = null;
        if (sciezkaDoZdjeciaZBazy != null && !sciezkaDoZdjeciaZBazy.trim().isEmpty()) {
            try (InputStream is = getClass().getResourceAsStream(sciezkaDoZdjeciaZBazy)) {
                if (is != null) imageToSet = new Image(is);
                if (imageToSet != null && imageToSet.isError()) {
                    imageToSet = null;
                }
            } catch (Exception e) {

            }
        }
        if (imageToSet == null) {
            try (InputStream is = getClass().getResourceAsStream(DEFAULT_IMAGE_PATH)) {
                if (is != null) imageToSet = new Image(is);
            } catch (Exception e) {
             
            }
        }
        if (zdjecieGlowne != null) zdjecieGlowne.setImage(imageToSet);
    }

    @FXML
    private void handleZamknijOkno(){
        Stage stage = (Stage) zamknijButton.getScene().getWindow();
        if(stage != null) {
            stage.close();
        }
    }

    @FXML
    private void handleRezerwujButton() {
        if (wyswietlanyPokoj == null) {
            pokazAlert("Błąd", null, "Brak danych pokoju do rezerwacji.", Alert.AlertType.WARNING);
            return;
        }

        if (!wyswietlanyPokoj.isDostepnosc()) {
            pokazAlert("Informacja", null, "Ten pokój jest obecnie niedostępny.", Alert.AlertType.INFORMATION);
            return;
        }

        try {
            FXMLLoader loader;
            String fxmlPath;
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Stage ownerStage = (Stage) rezerwujButton.getScene().getWindow();
            dialogStage.initOwner(ownerStage);

            if (UserSession.getInstance().isUserLoggedIn()) {
                fxmlPath = "/com/roomies/view/rezerwacja-zalogowany-view.fxml";
                loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                RezerwacjaZalogowanyController dialogController = loader.getController();
                dialogController.initData(this.wyswietlanyPokoj, this);
                dialogStage.setTitle("Rezerwacja Pokoju (Zalogowany): " + wyswietlanyPokoj.getNazwa());
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);
            } else {
                fxmlPath = "/com/roomies/view/rezerwacja-pokoju-view.fxml";
                loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                RezerwacjaPokojuController dialogController = loader.getController();
                dialogController.initData(this.wyswietlanyPokoj, this);
                dialogStage.setTitle("Rezerwacja Pokoju i Rejestracja: " + wyswietlanyPokoj.getNazwa());
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);
            }
            dialogStage.showAndWait();
            odswiezWidokPokojuPoRezerwacji();

        } catch (IOException e) {
            e.printStackTrace();
            pokazAlert("Błąd aplikacji", null, "Nie można otworzyć formularza rezerwacji.", Alert.AlertType.ERROR);
        }
    }

    public void odswiezWidokPokojuPoRezerwacji() {
        if (wyswietlanyPokoj != null) {
            PokojDao pokojDao = new PokojDao();
            Optional<Pokoj> zaktualizowanyPokojOptional = pokojDao.findById(wyswietlanyPokoj.getId_pokoju());
            if (zaktualizowanyPokojOptional.isPresent()){
                this.wyswietlanyPokoj = zaktualizowanyPokojOptional.get();
                wyswietlSzczegoly(this.wyswietlanyPokoj);
            } else {
                pokazAlert("Błąd", null, "Nie udało się odświeżyć danych pokoju.", Alert.AlertType.WARNING);
            }
        }
    }

    private void pokazAlert(String tytul, String naglowek, String wiadomosc, Alert.AlertType typAlerta) {
        Alert alert = new Alert(typAlerta);
        alert.setTitle(tytul);
        alert.setHeaderText(naglowek);
        alert.setContentText(wiadomosc);
        alert.showAndWait();
    }
}