package com.roomies.controller;

import com.roomies.model.Klient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class PanelKontaController {

    @FXML
    private BorderPane panelKontaPane;
    @FXML
    private Button zamknijButton;
    @FXML
    private Button pokazInformacjeButton;
    @FXML
    private Button pokazRezerwacjeButton;
    @FXML
    private AnchorPane zawartoscPaneluAnchorPane;

    private Klient zalogowanyKlient;
    private Button aktualnieAktywnyPrzyciskMenu;

    @FXML
    public void initialize() {
    }

    public void initData(Klient klient) {
        this.zalogowanyKlient = klient;
        if (klient == null) {
            pokazAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się załadować danych użytkownika.");
            handleZamknijButton(null);
            return;
        }
        Platform.runLater(() -> {
            handlePokazInformacje(null);
            ustawAktywnyPrzyciskMenu(pokazInformacjeButton);
        });
    }

    private void ustawAktywnyPrzyciskMenu(Button nowyAktywny) {
        if (aktualnieAktywnyPrzyciskMenu != null) {
            aktualnieAktywnyPrzyciskMenu.getStyleClass().remove("selected");
        }
        if (nowyAktywny != null) {
            nowyAktywny.getStyleClass().add("selected");
            nowyAktywny.requestFocus();
        }
        aktualnieAktywnyPrzyciskMenu = nowyAktywny;
    }


    @FXML
    private void handlePokazInformacje(ActionEvent event) {
        if (zalogowanyKlient == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/roomies/view/konto-info-view.fxml"));
            Parent infoPane = loader.load();

            KontoInfoController controller = loader.getController();
            controller.wyswietlInformacje(zalogowanyKlient);

            zawartoscPaneluAnchorPane.getChildren().setAll(infoPane);
            AnchorPane.setTopAnchor(infoPane, 0.0);
            AnchorPane.setBottomAnchor(infoPane, 0.0);
            AnchorPane.setLeftAnchor(infoPane, 0.0);
            AnchorPane.setRightAnchor(infoPane, 0.0);

            if (event != null) {
                ustawAktywnyPrzyciskMenu(pokazInformacjeButton);
            }
        } catch (IOException e) {
            e.printStackTrace();
            pokazAlert(Alert.AlertType.ERROR, "Błąd ładowania", "Nie można załadować widoku informacji o koncie.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            pokazAlert(Alert.AlertType.ERROR, "Błąd Pliku", "Nie znaleziono pliku FXML dla informacji o koncie lub kontroler jest null.");
        }
    }

    @FXML
    private void handlePokazRezerwacje(ActionEvent event) {
        if (zalogowanyKlient == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/roomies/view/moje-rezerwacje-view.fxml"));
            Parent rezerwacjePane = loader.load();

            MojeRezerwacjeController controller = loader.getController();
            controller.initData(zalogowanyKlient);

            zawartoscPaneluAnchorPane.getChildren().setAll(rezerwacjePane);
            AnchorPane.setTopAnchor(rezerwacjePane, 0.0);
            AnchorPane.setBottomAnchor(rezerwacjePane, 0.0);
            AnchorPane.setLeftAnchor(rezerwacjePane, 0.0);
            AnchorPane.setRightAnchor(rezerwacjePane, 0.0);

            if (event != null) {
                ustawAktywnyPrzyciskMenu(pokazRezerwacjeButton);
            }
        } catch (IOException e) {
            e.printStackTrace();
            pokazAlert(Alert.AlertType.ERROR, "Błąd ładowania", "Nie można załadować widoku rezerwacji.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            pokazAlert(Alert.AlertType.ERROR, "Błąd Pliku", "Nie znaleziono pliku FXML dla rezerwacji lub kontroler jest null.");
        }
    }

    @FXML
    private void handleZamknijButton(ActionEvent event) {
        Stage stage = (Stage) zamknijButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    private void pokazAlert(Alert.AlertType typ, String tytul, String wiadomosc) {
        Alert alert = new Alert(typ);
        alert.setTitle(tytul);
        alert.setHeaderText(null);
        alert.setContentText(wiadomosc);
        alert.showAndWait();
    }
}