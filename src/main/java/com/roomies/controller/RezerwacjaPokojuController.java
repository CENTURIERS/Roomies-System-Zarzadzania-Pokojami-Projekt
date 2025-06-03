package com.roomies.controller;

import com.roomies.dao.KlientDao;
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
    private SzczegolyPokojuController szczegolyPokojuController;

    private KlientDao klientDao;
    private PokojDao pokojDao;
    private WynajemDao wynajemDao;
    private PlatnoscDao platnoscDao;

    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pl", "PL"));
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    private static final Pattern PESEL_PATTERN = Pattern.compile("^[0-9]{11}$");
    private static final int MIN_PASSWORD_LENGTH = 6;

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

        wypelnijDaneKlientaJesliZalogowany();
    }

    public void initData(Pokoj pokoj, SzczegolyPokojuController szczegolyController) {
        this.rezerwowanyPokoj = pokoj;
        this.szczegolyPokojuController = szczegolyController;

        if (rezerwowanyPokoj != null) {
            nazwaPokojuLabelInfo.setText("Rezerwujesz: " + rezerwowanyPokoj.getNazwa());
            if (rezerwowanyPokoj.getCenaZaDobe() != null) {
                cenaZaDobeInfoLabel.setText("Cena: " + currencyFormatter.format(rezerwowanyPokoj.getCenaZaDobe()) + " / doba");
            } else {
                cenaZaDobeInfoLabel.setText("Cena: Brak informacji");
                rezerwujZarejestrujButton.setDisable(true);
            }
        }
        wypelnijDaneKlientaJesliZalogowany();
        przeliczKoszt();
    }

    private void wypelnijDaneKlientaJesliZalogowany() {
        Klient zalogowany = UserSession.getInstance().getZalogowanyKlient();
        boolean jestZalogowany = UserSession.getInstance().isUserLoggedIn();

        if (jestZalogowany && zalogowany != null) {
            imieField.setText(zalogowany.getImie());
            nazwiskoField.setText(zalogowany.getNazwisko());
            emailField.setText(zalogowany.getEmail());
            peselField.setText(zalogowany.getPesel());
            ulicaField.setText(zalogowany.getUlica());
            numerBudynkuField.setText(zalogowany.getNumerBudynku());
            if (zalogowany.getTelefon() != null) {
                telefonField.setText(zalogowany.getTelefon());
            }

            imieField.setDisable(true);
            nazwiskoField.setDisable(true);
            emailField.setDisable(true);
            peselField.setDisable(true);
            ulicaField.setDisable(true);
            numerBudynkuField.setDisable(true);
            telefonField.setDisable(true);

            hasloField.clear();
            hasloField.setDisable(true);
            hasloField.getParent().setVisible(false);
            hasloField.getParent().setManaged(false);

            rezerwujZarejestrujButton.setText("Potwierdź Rezerwację");
        } else {
            imieField.setDisable(false);
            nazwiskoField.setDisable(false);
            emailField.setDisable(false);
            peselField.setDisable(false);
            ulicaField.setDisable(false);
            numerBudynkuField.setDisable(false);
            telefonField.setDisable(false);

            hasloField.setDisable(false);
            if(hasloField.getParent() != null){
                hasloField.getParent().setVisible(true);
                hasloField.getParent().setManaged(true);
            }
            rezerwujZarejestrujButton.setText("Rezerwuj i Utwórz Konto");
        }
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
        Klient klientDoRezerwacji;

        LocalDate dataRozpoczecia = dataRozpoczeciaPicker.getValue();
        LocalDate dataZakonczenia = dataZakonczeniaPicker.getValue();

        if (UserSession.getInstance().isUserLoggedIn()) {
            klientDoRezerwacji = UserSession.getInstance().getZalogowanyKlient();
            if (!walidujDaty(dataRozpoczecia, dataZakonczenia)) return;
        } else {
            String imie = imieField.getText().trim();
            String nazwisko = nazwiskoField.getText().trim();
            String email = emailField.getText().trim().toLowerCase();
            String haslo = hasloField.getText();
            String telefon = telefonField.getText().trim();
            String ulica = ulicaField.getText().trim();
            String numerBudynku = numerBudynkuField.getText().trim();
            String pesel = peselField.getText().trim();

            if (!walidujDaneNowegoKlientaIDat(imie, nazwisko, email, haslo, ulica, numerBudynku, pesel, telefon, dataRozpoczecia, dataZakonczenia)) {
                return;
            }

            Optional<Klient> istniejacyKlientOptional = klientDao.findByEmail(email);
            if (istniejacyKlientOptional.isPresent()) {
                pokazBlad("Konto o podanym adresie email już istnieje. Zaloguj się lub użyj innego adresu.");
                return;
            }

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

        if (klientDoRezerwacji == null) {
            pokazBlad("Błąd: Nie udało się ustalić danych klienta.");
            return;
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

    private boolean walidujDaneNowegoKlientaIDat(String imie, String nazwisko, String email, String haslo,
                                                 String ulica, String numerBudynku, String pesel, String telefon,
                                                 LocalDate dataRozpoczecia, LocalDate dataZakonczenia) {
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
        return walidujDaty(dataRozpoczecia, dataZakonczenia);
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