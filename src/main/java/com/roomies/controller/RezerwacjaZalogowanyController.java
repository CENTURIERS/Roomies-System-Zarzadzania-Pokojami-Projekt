package com.roomies.controller;

import com.roomies.dao.PlatnoscDao;
import com.roomies.dao.PokojDao;
import com.roomies.dao.WynajemDao;
import com.roomies.model.Klient;
import com.roomies.model.Platnosc;
import com.roomies.model.Pokoj;
import com.roomies.model.Wynajem;
import com.roomies.util.UserSession;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class RezerwacjaZalogowanyController {

    @FXML
    private Label zalogowanyInfoLabel;
    @FXML
    private Label nazwaPokojuLabelInfo;
    @FXML
    private Label cenaZaDobeInfoLabel;
    @FXML
    private DatePicker dataRozpoczeciaPicker;
    @FXML
    private DatePicker dataZakonczeniaPicker;
    @FXML
    private Label liczbaDniLabel;
    @FXML
    private Label calkowityKosztLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Button anulujButton;
    @FXML
    private Button potwierdzRezerwacjeButton;

    private Pokoj rezerwowanyPokoj;
    private Klient zalogowanyKlient;
    private SzczegolyPokojuController szczegolyPokojuController;

    private PokojDao pokojDao;
    private WynajemDao wynajemDao;
    private PlatnoscDao platnoscDao;

    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pl", "PL"));

    @FXML
    public void initialize() {
        pokojDao = new PokojDao();
        wynajemDao = new WynajemDao();
        platnoscDao = new PlatnoscDao();

        this.zalogowanyKlient = UserSession.getInstance().getZalogowanyKlient();

        errorLabel.managedProperty().bind(errorLabel.visibleProperty());
        errorLabel.setVisible(false);

        dataRozpoczeciaPicker.valueProperty().addListener((obs, oldValue, newValue) -> przeliczKoszt());
        dataZakonczeniaPicker.valueProperty().addListener((obs, oldValue, newValue) -> przeliczKoszt());

        dataRozpoczeciaPicker.setValue(LocalDate.now());
        dataZakonczeniaPicker.setValue(LocalDate.now().plusDays(1));

        if (zalogowanyKlient != null) {
            zalogowanyInfoLabel.setText("Zalogowano jako: " + zalogowanyKlient.getImie() + " " + zalogowanyKlient.getNazwisko());
        } else {
            zalogowanyInfoLabel.setText("Błąd: Użytkownik nie jest zalogowany.");
            potwierdzRezerwacjeButton.setDisable(true);
        }
    }

    public void initData(Pokoj pokoj, SzczegolyPokojuController szczegolyCtrl) {
        this.rezerwowanyPokoj = pokoj;
        this.szczegolyPokojuController = szczegolyCtrl;

        if (rezerwowanyPokoj != null) {
            nazwaPokojuLabelInfo.setText("Rezerwujesz: " + rezerwowanyPokoj.getNazwa());
            if (rezerwowanyPokoj.getCenaZaDobe() != null) {
                cenaZaDobeInfoLabel.setText("Cena: " + currencyFormatter.format(rezerwowanyPokoj.getCenaZaDobe()) + " / doba");
            } else {
                cenaZaDobeInfoLabel.setText("Cena: Brak informacji");
                potwierdzRezerwacjeButton.setDisable(true);
            }
        }
        przeliczKoszt();
    }

    private void przeliczKoszt() {
        LocalDate start = dataRozpoczeciaPicker.getValue();
        LocalDate end = dataZakonczeniaPicker.getValue();
        potwierdzRezerwacjeButton.setDisable(false);
        ukryjBlad();

        if (start == null || end == null || rezerwowanyPokoj == null || rezerwowanyPokoj.getCenaZaDobe() == null) {
            liczbaDniLabel.setText("-");
            calkowityKosztLabel.setText(currencyFormatter.format(BigDecimal.ZERO));
            if (rezerwowanyPokoj == null || rezerwowanyPokoj.getCenaZaDobe() == null) {
                pokazBlad("Błąd danych pokoju.");
                potwierdzRezerwacjeButton.setDisable(true);
            }
            return;
        }
        if (start.isBefore(LocalDate.now())) {
            liczbaDniLabel.setText("-");
            calkowityKosztLabel.setText(currencyFormatter.format(BigDecimal.ZERO));
            pokazBlad("Data rozpoczęcia nie może być z przeszłości.");
            potwierdzRezerwacjeButton.setDisable(true);
            return;
        }
        if (end.isBefore(start) || end.equals(start)) {
            liczbaDniLabel.setText("-");
            calkowityKosztLabel.setText(currencyFormatter.format(BigDecimal.ZERO));
            pokazBlad("Data zakończenia musi być późniejsza niż data rozpoczęcia.");
            potwierdzRezerwacjeButton.setDisable(true);
            return;
        }
        long dni = ChronoUnit.DAYS.between(start, end);
        liczbaDniLabel.setText(String.valueOf(dni));
        BigDecimal kosztCalkowity = rezerwowanyPokoj.getCenaZaDobe().multiply(BigDecimal.valueOf(dni));
        calkowityKosztLabel.setText(currencyFormatter.format(kosztCalkowity));
    }

    @FXML
    private void handlePotwierdzRezerwacjeButton(ActionEvent event) {
        ukryjBlad();
        if (zalogowanyKlient == null) {
            pokazBlad("Błąd sesji użytkownika. Proszę zalogować się ponownie.");
            return;
        }
        LocalDate dataRozpoczecia = dataRozpoczeciaPicker.getValue();
        LocalDate dataZakonczenia = dataZakonczeniaPicker.getValue();

        if (!walidujDaty(dataRozpoczecia, dataZakonczenia)) return;

        try {
            long liczbaDniLong = ChronoUnit.DAYS.between(dataRozpoczecia, dataZakonczenia);
            BigDecimal calkowityKoszt = rezerwowanyPokoj.getCenaZaDobe().multiply(BigDecimal.valueOf(liczbaDniLong));

            Platnosc platnosc = new Platnosc();
            platnosc.setKwota(calkowityKoszt);
            platnosc.setStatus("OCZEKUJACE");
            platnoscDao.save(platnosc);

            Wynajem wynajem = new Wynajem();
            wynajem.setKlient(zalogowanyKlient);
            wynajem.setPokoj(rezerwowanyPokoj);
            wynajem.setDataRozpoczecia(dataRozpoczecia);
            wynajem.setDataZakonczenia(dataZakonczenia);
            wynajem.setPlatnosc(platnosc);
            wynajemDao.save(wynajem);

            rezerwowanyPokoj.setDostepnosc(false);
            pokojDao.update(rezerwowanyPokoj);

            pokazAlert(Alert.AlertType.INFORMATION, "Rezerwacja Zakończona",
                    "Pomyślnie zarezerwowano pokój " + rezerwowanyPokoj.getNazwa() + ".");

            if (szczegolyPokojuController != null) {
                szczegolyPokojuController.odswiezWidokPokojuPoRezerwacji();
            }
            zamknijOkno();

        } catch (Exception e) {
            e.printStackTrace();
            pokazBlad("Wystąpił błąd podczas finalizowania rezerwacji: " + e.getMessage());
        }
    }

    private boolean walidujDaty(LocalDate dataRozpoczecia, LocalDate dataZakonczenia) {
        if (dataRozpoczecia == null || dataZakonczenia == null) {
            pokazBlad("Proszę wybrać daty rezerwacji.");
            return false;
        }
        if (dataRozpoczecia.isBefore(LocalDate.now())) {
            pokazBlad("Data rozpoczęcia nie może być z przeszłości.");
            return false;
        }
        if (dataZakonczenia.isBefore(dataRozpoczecia) || dataZakonczenia.equals(dataRozpoczecia)) {
            pokazBlad("Data zakończenia musi być późniejsza niż data rozpoczęcia.");
            return false;
        }
        if (rezerwowanyPokoj == null || !rezerwowanyPokoj.isDostepnosc()) {
            pokazBlad("Wybrany pokój jest już niedostępny lub wystąpił błąd danych pokoju.");
            return false;
        }
        if (rezerwowanyPokoj.getCenaZaDobe() == null || rezerwowanyPokoj.getCenaZaDobe().compareTo(BigDecimal.ZERO) <= 0) {
            pokazBlad("Błąd ceny pokoju. Nie można dokonać rezerwacji.");
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
        Stage stage = (Stage) anulujButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}