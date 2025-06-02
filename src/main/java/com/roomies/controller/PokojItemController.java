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
                        System.out.println("Błąd w pliku obrazka (uszkodzony lub nieprawidłowy format): " + sciezkaDoZdjeciaZBazy);
                        imageToSet = null;
                    }
                } else {
                    System.out.println("Nie znaleziono zasobu obrazka na ścieżce: " + sciezkaDoZdjeciaZBazy);
                }
            } catch (Exception e) {
                System.out.println("Wyjątek podczas ładowania obrazka " + sciezkaDoZdjeciaZBazy + ": " + e.getMessage());
            }
        }

        if (imageToSet == null) {
            try (InputStream is = getClass().getResourceAsStream("/images/placeholder_400x300.png")) {
                if (is != null) {
                    imageToSet = new Image(is);
                } else {
                    System.out.println("KRYTYCZNY BŁĄD: Nie znaleziono domyślnego obrazka placeholder: " + "/images/placeholder_400x300.png");
                }
            } catch (Exception e) {
                System.out.println("Błąd podczas ładowania domyślnego obrazka placeholder: " + e.getMessage());
            }
        }
        zdjeciePokojuImageView.setImage(imageToSet);
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
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Brak danych");
            alert.setHeaderText(null);
            alert.setContentText("Nie można wyświetlić szczegółów, brak danych pokoju.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/roomies/view/szczegoly-pokoju-view.fxml"));

            Parent root = loader.load();

            SzczegolyPokojuController szczgolyController = loader.getController();
            if (szczgolyController == null) {
                System.out.println("KRYTYCZNY BŁĄD: Nie udało się pobrać kontrolera dla szczegoly-pokoju-view.fxml");
                pokazAlert("Błąd Aplikacji", "Błąd Wewnętrzny", "Nie można załadować komponentów okna szczegółów.", Alert.AlertType.ERROR);
                return;
            }

            szczgolyController.wyswietlSzczegoly(this.pokojObiekt);

            Stage detailsStage = new Stage();
            detailsStage.setTitle("Szczegoly Pokoju - " + (pokojObiekt.getNazwa() != null ? pokojObiekt.getNazwa() : ""));
            detailsStage.setScene(new Scene(root));
            detailsStage.show();
        }catch (IOException e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd aplikacji");
            alert.setHeaderText(null);
            alert.setContentText("Nie można otworzyć okna ze szczegółami pokoju");
            alert.showAndWait();
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
