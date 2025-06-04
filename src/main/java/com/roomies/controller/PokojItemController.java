package com.roomies.controller;

import com.roomies.model.Pokoj;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Locale;

public class PokojItemController {
    @FXML
    private VBox kartaPokojuKontener;
    @FXML
    private ImageView zdjeciePokojuImageView;
    @FXML
    private Label nazwaPokojuLabel;
    @FXML
    private Label rodzajPokojuLabel;
    @FXML
    private Label lokalizacjaPokojuLabel;
    @FXML
    private Label cenaPokojuLabel;
    @FXML
    private Label dostepnoscLabel;
    @FXML
    private Button zobaczSzczegolyButton;

    private Pokoj pokojObiekt;
    private MainController mainControllerRef;

    public void setData(Pokoj pokoj){
        this.pokojObiekt = pokoj;

        if(pokoj.getNazwa() != null && !pokoj.getNazwa().isEmpty()) {
            nazwaPokojuLabel.setText(pokoj.getNazwa());
        }else{
            nazwaPokojuLabel.setText("Brak nazwy");
        }

        if(pokoj.getRodzajPokoju() != null && pokoj.getRodzajPokoju().getNazwa() != null){
            rodzajPokojuLabel.setText("Rodzaj: " + pokoj.getRodzajPokoju().getNazwa());
        }else {
            rodzajPokojuLabel.setText("Rodzaj: Nieokreślony");
        }

        if(pokoj.getLokalizacja() != null && pokoj.getLokalizacja().getNazwaLokalizacji() != null){
            lokalizacjaPokojuLabel.setText("Lokalizacja: " + pokoj.getLokalizacja().getNazwaLokalizacji());
        }else{
            lokalizacjaPokojuLabel.setText("Lokalizacja: Nieokreślona");
        }

        if(pokoj.getCenaZaDobe() != null){
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pl", "PL"));
            cenaPokojuLabel.setText(currencyFormat.format(pokoj.getCenaZaDobe()));
        }else{
            cenaPokojuLabel.setText("Cena: Nieustalona");
        }

        if(pokoj.isDostepnosc()){
            dostepnoscLabel.setText("Dostępny");
            dostepnoscLabel.getStyleClass().remove("status-niedostepny");
            dostepnoscLabel.getStyleClass().add("status-dostepny");
        }else {
            dostepnoscLabel.setText("Niedostępny");
            dostepnoscLabel.getStyleClass().remove("status-dostepny");
            dostepnoscLabel.getStyleClass().add("status-niedostepny");
        }

        String sciezkaDoZdjeciaZBazy = pokoj.getSciezkaZdjecia();
        Image imageToSet = null;

        if (sciezkaDoZdjeciaZBazy != null && !sciezkaDoZdjeciaZBazy.trim().isEmpty()) {
            try (InputStream is = getClass().getResourceAsStream(sciezkaDoZdjeciaZBazy)) {
                if (is != null) {
                    imageToSet = new Image(is);
                    if (imageToSet.isError()) {
                        imageToSet = null;
                    }
                }
            } catch (Exception e) {

            }
        }

        if (imageToSet == null) {
            try (InputStream is = getClass().getResourceAsStream("/images/placeholder_400x300.png")) {
                if (is != null) {
                    imageToSet = new Image(is);
                }
            } catch (Exception e) {

            }
        }
        zdjeciePokojuImageView.setImage(imageToSet);
    }

    public void setMainController(MainController mainController) {
        this.mainControllerRef = mainController;
    }

    @FXML
    public void initialize() {
        if (zobaczSzczegolyButton != null) {
            zobaczSzczegolyButton.setOnAction(event -> handleZobaczSzczegoly());
        }
    }

    @FXML
    private void handleZobaczSzczegoly() {
        if (pokojObiekt == null) {
            pokazAlert("Brak danych", null, "Nie można wyświetlić szczegółów, brak danych pokoju.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/roomies/view/szczegoly-pokoju-view.fxml"));
            Parent root = loader.load();
            SzczegolyPokojuController szczgolyController = loader.getController();

            if (szczgolyController == null) {
                pokazAlert("Błąd Aplikacji", "Błąd Wewnętrzny", "Nie można załadować komponentów okna szczegółów.", Alert.AlertType.ERROR);
                return;
            }

            szczgolyController.wyswietlSzczegoly(this.pokojObiekt);

            Stage detailsStage = new Stage();
            detailsStage.initModality(Modality.WINDOW_MODAL);
            Stage owner = (Stage) zobaczSzczegolyButton.getScene().getWindow();
            if (owner != null) {
                detailsStage.initOwner(owner);
            }
            detailsStage.setTitle("Szczegoly Pokoju - " + (pokojObiekt.getNazwa() != null ? pokojObiekt.getNazwa() : ""));
            detailsStage.setScene(new Scene(root));
            detailsStage.showAndWait();

            if (mainControllerRef != null) {
                mainControllerRef.odswiezWszystkiePokojeNaKartach();
            }

        }catch (IOException e){
            e.printStackTrace();
            pokazAlert("Błąd aplikacji", null, "Nie można otworzyć okna ze szczegółami pokoju", Alert.AlertType.ERROR);
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