package com.roomies.controller;

import com.roomies.dao.KlientDao;
import com.roomies.dao.PlatnoscDao;
import com.roomies.dao.PokojDao;
import com.roomies.dao.WynajemDao;
import com.roomies.model.Klient;
import com.roomies.model.Platnosc;
import com.roomies.model.Pokoj;
import com.roomies.model.Wynajem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

public class RezerwacjaPokojuController {

    @FXML
    private Label nazwaPokojuLabelInfo;
    @FXML
    private Label cenaZaDobeInfoLabel;
    @FXML
    private TextField imieField;
    @FXML
    private TextField nazwiskoField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField hasloField;
    @FXML
    private TextField telefonField;
    @FXML
    private TextField ulicaField;
    @FXML
    private TextField numerBudynkuField;
    @FXML
    private TextField peselField;
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
    private Button rezerwujZarejestrujButton;

    private Pokoj rezerwowanyPokoj;

    private KlientDao klientDao;
    private PokojDao pokojDao;
    private WynajemDao wynajemDao;
    private PlatnoscDao platnoscDao;

    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pl", "PL"));
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    private static final Pattern PESEL_PATTERN = Pattern.compile("^[0-9]{11}$");

    @FXML
    public void initialize() {
        klientDao = new KlientDao();
        pokojDao = new PokojDao();
        wynajemDao = new WynajemDao();
        platnoscDao = new PlatnoscDao();

        errorLabel.managedProperty().bind(errorLabel.visibleProperty());
        errorLabel.setVisible(false);

        dataRozpoczeciaPicker.valueProperty().addListener((obs, oldValue, newValue) -> przeliczKoszt());
        dataZakonczeniaPicker.valueProperty().addListener((obs, oldValue, newValue) -> przeliczKoszt());

        dataRozpoczeciaPicker.setValue(LocalDate.now());
        dataZakonczeniaPicker.setValue(LocalDate.now().plusDays(1));
    }

    public void initData(Pokoj pokoj) {
        this.rezerwowanyPokoj = pokoj;

        if (rezerwowanyPokoj != null) {
            nazwaPokojuLabelInfo.setText("Rezerwujesz: " + rezerwowanyPokoj.getNazwa());
            if (rezerwowanyPokoj.getCenaZaDobe() != null) {
                cenaZaDobeInfoLabel.setText("Cena: " + currencyFormatter.format(rezerwowanyPokoj.getCenaZaDobe()) + " / doba");
            } else {
                cenaZaDobeInfoLabel.setText("Cena: Brak informacji");
                rezerwujZarejestrujButton.setDisable(true);
            }
        }
        przeliczKoszt();
    }

    private void przeliczKoszt() {
        LocalDate start = dataRozpoczeciaPicker.getValue();
        LocalDate end = dataZakonczeniaPicker.getValue();

        rezerwujZarejestrujButton.setDisable(false);
        ukryjBlad();

        if (start == null || end == null) {
            liczbaDniLabel.setText("-");
            calkowityKosztLabel.setText(currencyFormatter.format(BigDecimal.ZERO));
            return;
        }

        if (rezerwowanyPokoj == null || rezerwowanyPokoj.getCenaZaDobe() == null) {
            liczbaDniLabel.setText("-");
            calkowityKosztLabel.setText(currencyFormatter.format(BigDecimal.ZERO));
            pokazBlad("Błąd danych pokoju.");
            rezerwujZarejestrujButton.setDisable(true);
            return;
        }

        if (start.isBefore(LocalDate.now())) {
            liczbaDniLabel.setText("-");
            calkowityKosztLabel.setText(currencyFormatter.format(BigDecimal.ZERO));
            pokazBlad("Data rozpoczęcia nie może być z przeszłości.");
            rezerwujZarejestrujButton.setDisable(true);
            return;
        }

        if (end.isBefore(start) || end.equals(start)) {
            liczbaDniLabel.setText("-");
            calkowityKosztLabel.setText(currencyFormatter.format(BigDecimal.ZERO));
            pokazBlad("Data zakończenia musi być późniejsza niż data rozpoczęcia.");
            rezerwujZarejestrujButton.setDisable(true);
            return;
        }

        long dni = ChronoUnit.DAYS.between(start, end);
        liczbaDniLabel.setText(String.valueOf(dni));
        BigDecimal kosztCalkowity = rezerwowanyPokoj.getCenaZaDobe().multiply(BigDecimal.valueOf(dni));
        calkowityKosztLabel.setText(currencyFormatter.format(kosztCalkowity));
    }

    @FXML
    private void handleAnulujButton(ActionEvent event) {
        zamknijOkno();
    }

    @FXML
    private void handleRezerwujZarejestrujButton(ActionEvent event) {
        ukryjBlad();

        String imie = imieField.getText().trim();
        String nazwisko = nazwiskoField.getText().trim();
        String email = emailField.getText().trim();
        String haslo = hasloField.getText();
        String telefon = telefonField.getText().trim();
        String ulica = ulicaField.getText().trim();
        String numerBudynku = numerBudynkuField.getText().trim();
        String pesel = peselField.getText().trim();
        LocalDate dataRozpoczecia = dataRozpoczeciaPicker.getValue();
        LocalDate dataZakonczenia = dataZakonczeniaPicker.getValue();

        if (!walidujDane(imie, nazwisko, email, haslo, ulica, numerBudynku, pesel, dataRozpoczecia, dataZakonczenia)) {
            return;
        }

        Klient klientDoRezerwacji;
        Optional<Klient> istniejacyKlientOptional = klientDao.findByEmail(email);

        if (istniejacyKlientOptional.isPresent()) {
            Klient istniejacyKlient = istniejacyKlientOptional.get();
            if (!istniejacyKlient.getHaslo().equals(haslo)) {
                pokazBlad("Konto o tym emailu już istnieje, ale podane hasło jest nieprawidłowe.");
                return;
            }
            klientDoRezerwacji = istniejacyKlient;
            System.out.println("Użyto istniejącego klienta: " + klientDoRezerwacji.getEmail());
        } else {
            Klient nowyKlient = new Klient();
            nowyKlient.setImie(imie);
            nowyKlient.setNazwisko(nazwisko);
            nowyKlient.setEmail(email);
            nowyKlient.setHaslo(haslo);
            nowyKlient.setTelefon(telefon.isEmpty() ? null : telefon);
            nowyKlient.setUlica(ulica);
            nowyKlient.setNumerBudynku(numerBudynku);
            nowyKlient.setPesel(pesel);
            nowyKlient.setCzyAktywny(true);

            try {
                klientDao.save(nowyKlient);
                klientDoRezerwacji = nowyKlient;
                System.out.println("Utworzono nowego klienta: " + klientDoRezerwacji.getEmail());
            } catch (Exception e) {
                e.printStackTrace();
                pokazBlad("Błąd podczas tworzenia konta klienta: " + e.getMessage());
                return;
            }
        }

        try {
            long liczbaDniLong = ChronoUnit.DAYS.between(dataRozpoczecia, dataZakonczenia);
            BigDecimal calkowityKoszt = rezerwowanyPokoj.getCenaZaDobe().multiply(BigDecimal.valueOf(liczbaDniLong));

            Platnosc platnosc = new Platnosc();
            platnosc.setKwota(calkowityKoszt);
            platnosc.setStatus("OCZEKUJACE");
            platnoscDao.save(platnosc);

            Wynajem wynajem = new Wynajem();
            wynajem.setKlient(klientDoRezerwacji);
            wynajem.setPokoj(rezerwowanyPokoj);
            wynajem.setDataRozpoczecia(dataRozpoczecia);
            wynajem.setDataZakonczenia(dataZakonczenia);
            wynajem.setPlatnosc(platnosc);
            wynajemDao.save(wynajem);

            rezerwowanyPokoj.setDostepnosc(false);
            pokojDao.update(rezerwowanyPokoj);

            pokazAlert(Alert.AlertType.INFORMATION, "Rezerwacja Zakończona",
                    "Pomyślnie zarezerwowano pokój " + rezerwowanyPokoj.getNazwa() + " dla " +
                            klientDoRezerwacji.getImie() + " " + klientDoRezerwacji.getNazwisko() + ".");

            zamknijOkno();

        } catch (Exception e) {
            e.printStackTrace();
            pokazBlad("Wystąpił błąd podczas finalizowania rezerwacji: " + e.getMessage());
        }
    }

    private boolean walidujDane(String imie, String nazwisko, String email, String haslo,
                                String ulica, String numerBudynku, String pesel,
                                LocalDate dataRozpoczecia, LocalDate dataZakonczenia) {
        if (imie.isEmpty() || nazwisko.isEmpty() || email.isEmpty() || haslo.isEmpty() ||
                ulica.isEmpty() || numerBudynku.isEmpty() || pesel.isEmpty()) {
            pokazBlad("Wszystkie pola (oprócz telefonu) są wymagane.");
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            pokazBlad("Niepoprawny format adresu email.");
            return false;
        }
        if (haslo.length() < 6) {
            pokazBlad("Hasło musi mieć co najmniej 6 znaków.");
            return false;
        }
        if (!PESEL_PATTERN.matcher(pesel).matches()) {
            pokazBlad("PESEL musi składać się z 11 cyfr.");
            return false;
        }
        if (dataRozpoczecia == null || dataZakonczenia == null) {
            pokazBlad("Proszę wybrać datę rozpoczęcia i zakończenia rezerwacji.");
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