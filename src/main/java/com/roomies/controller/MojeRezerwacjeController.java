package com.roomies.controller;

import com.roomies.dao.PlatnoscDao;
import com.roomies.dao.PokojDao;
import com.roomies.dao.WynajemDao;
import com.roomies.model.Klient;
import com.roomies.model.Platnosc;
import com.roomies.model.Pokoj;
import com.roomies.model.Wynajem;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MojeRezerwacjeController {

    @FXML
    private TableView<Wynajem> rezerwacjeTableView;
    @FXML
    private TableColumn<Wynajem, String> pokojColumn;
    @FXML
    private TableColumn<Wynajem, String> dataOdColumn;
    @FXML
    private TableColumn<Wynajem, String> dataDoColumn;
    @FXML
    private TableColumn<Wynajem, String> kosztColumn;
    @FXML
    private TableColumn<Wynajem, String> statusPlatnosciColumn;

    @FXML
    private Button edytujButton;
    @FXML
    private Button usunButton;
    @FXML
    private VBox edycjaBox;
    @FXML
    private DatePicker dataRozpoczeciaDatePicker;
    @FXML
    private DatePicker dataZakonczeniaDatePicker;
    @FXML
    private Button zapiszEdycjeButton;
    @FXML
    private Button anulujEdycjeButton;
    @FXML
    private Label kosztEdycjiLabel;
    @FXML
    private Label errorLabel;

    private Klient zalogowanyKlient;
    private ObservableList<Wynajem> listaWynajmow = FXCollections.observableArrayList();
    private WynajemDao wynajemDao;
    private PlatnoscDao platnoscDao;
    private PokojDao pokojDao;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pl", "PL"));

    private Wynajem wybranyWynajemDoEdycji;
    private boolean trybEdycjiAktywny = false;

    @FXML
    public void initialize() {
        wynajemDao = new WynajemDao();
        platnoscDao = new PlatnoscDao();
        pokojDao = new PokojDao();

        errorLabel.managedProperty().bind(errorLabel.visibleProperty());
        errorLabel.setVisible(false);

        rezerwacjeTableView.setItems(listaWynajmow);
        rezerwacjeTableView.setPlaceholder(new Label("Brak rezerwacji do wyświetlenia."));

        rezerwacjeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            wybranyWynajemDoEdycji = newSelection;
            aktualizujStanPrzyciskow();
            if (newSelection == null || !czyMoznaEdytowac(newSelection)) {
                zakonczTrybEdycjiBezCzyszczeniaSelekcji();
            }
        });

        dataRozpoczeciaDatePicker.valueProperty().addListener((obs, oldV, newV) -> {
            if (trybEdycjiAktywny) przeliczKosztEdycji();
        });
        dataZakonczeniaDatePicker.valueProperty().addListener((obs, oldV, newV) -> {
            if (trybEdycjiAktywny) przeliczKosztEdycji();
        });

        zapiszEdycjeButton.setOnAction(event -> handleZapiszEdycje());
        anulujEdycjeButton.setOnAction(event -> handleAnulujEdycje());

        pokojColumn.setCellValueFactory(cellData -> {
            Pokoj pokoj = cellData.getValue().getPokoj();
            return new SimpleStringProperty(pokoj != null ? pokoj.getNazwa() : "Brak danych");
        });
        dataOdColumn.setCellValueFactory(cellData -> {
            LocalDate data = cellData.getValue().getDataRozpoczecia();
            return new SimpleStringProperty(data != null ? data.format(dateFormatter) : "-");
        });
        dataDoColumn.setCellValueFactory(cellData -> {
            LocalDate data = cellData.getValue().getDataZakonczenia();
            return new SimpleStringProperty(data != null ? data.format(dateFormatter) : "Do odwołania");
        });
        kosztColumn.setCellValueFactory(cellData -> {
            Platnosc platnosc = cellData.getValue().getPlatnosc();
            BigDecimal kwota = (platnosc != null && platnosc.getKwota() != null) ? platnosc.getKwota() : BigDecimal.ZERO;
            return new SimpleStringProperty(currencyFormatter.format(kwota));
        });
        statusPlatnosciColumn.setCellValueFactory(cellData -> {
            Platnosc platnosc = cellData.getValue().getPlatnosc();
            return new SimpleStringProperty(platnosc != null && platnosc.getStatus() != null ? platnosc.getStatus() : "Brak danych");
        });

        statusPlatnosciColumn.setCellFactory(column -> new TableCell<Wynajem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("status-oczekujaca", "status-zaplacona", "status-anulowana", "status-inna");
                setText(null);
                setStyle("");
                if (item != null && !empty) {
                    setText(item);
                    if ("OCZEKUJACE".equalsIgnoreCase(item)) getStyleClass().add("status-oczekujaca");
                    else if ("ZAPŁACONO".equalsIgnoreCase(item)) getStyleClass().add("status-zaplacona");
                    else if ("ANULOWANO".equalsIgnoreCase(item)) getStyleClass().add("status-anulowana");
                    else getStyleClass().add("status-inna");
                }
            }
        });
    }

    public void initData(Klient klient) {
        this.zalogowanyKlient = klient;
        ladowanieRezerwacji();
    }

    private void ladowanieRezerwacji() {
        ukryjBlad();
        listaWynajmow.clear();
        if (zalogowanyKlient != null) {
            List<Wynajem> wynajmyKlienta = wynajemDao.findByKlientId(zalogowanyKlient.getIdKlienta());
            if (wynajmyKlienta != null) {
                listaWynajmow.setAll(wynajmyKlienta);
            }
        }
        rezerwacjeTableView.getSelectionModel().clearSelection();
        zakonczTrybEdycjiBezCzyszczeniaSelekcji();
    }

    private void aktualizujStanPrzyciskow() {
        boolean zaznaczono = wybranyWynajemDoEdycji != null;
        boolean moznaEdytowac = zaznaczono && czyMoznaEdytowac(wybranyWynajemDoEdycji);
        boolean moznaUsunac = zaznaczono && czyMoznaUsunac(wybranyWynajemDoEdycji);

        edytujButton.setDisable(!moznaEdytowac);
        usunButton.setDisable(!moznaUsunac);

        if (trybEdycjiAktywny) {
            zapiszEdycjeButton.setDisable(!moznaEdytowac);
            anulujEdycjeButton.setDisable(false);
            dataRozpoczeciaDatePicker.setDisable(!moznaEdytowac);
            dataZakonczeniaDatePicker.setDisable(!moznaEdytowac);
        } else {
            zapiszEdycjeButton.setDisable(true);
            anulujEdycjeButton.setDisable(true);
            dataRozpoczeciaDatePicker.setDisable(true);
            dataZakonczeniaDatePicker.setDisable(true);
        }
    }

    private boolean czyMoznaEdytowac(Wynajem wynajem) {
        if (wynajem == null) return false;
        Platnosc platnosc = wynajem.getPlatnosc();
        boolean czyOczekujaca = platnosc != null && "OCZEKUJACE".equalsIgnoreCase(platnosc.getStatus());
        boolean czyPrzyszla = wynajem.getDataRozpoczecia() != null && !wynajem.getDataRozpoczecia().isBefore(LocalDate.now());
        return czyOczekujaca && czyPrzyszla;
    }

    private boolean czyMoznaUsunac(Wynajem wynajem) {
        if (wynajem == null) return false;
        Platnosc platnosc = wynajem.getPlatnosc();
        boolean czyNieZaplacona = platnosc != null && !"ZAPŁACONO".equalsIgnoreCase(platnosc.getStatus());
        boolean czyPrzyszla = wynajem.getDataRozpoczecia() != null && !wynajem.getDataRozpoczecia().isBefore(LocalDate.now());
        return czyNieZaplacona && czyPrzyszla;
    }

    @FXML
    private void handleEdytujButton(ActionEvent event) {
        ukryjBlad();
        if (wybranyWynajemDoEdycji == null || !czyMoznaEdytowac(wybranyWynajemDoEdycji)) {
            pokazBlad("Nie można edytować tej rezerwacji.");
            return;
        }
        trybEdycjiAktywny = true;
        dataRozpoczeciaDatePicker.setValue(wybranyWynajemDoEdycji.getDataRozpoczecia());
        dataZakonczeniaDatePicker.setValue(wybranyWynajemDoEdycji.getDataZakonczenia());
        przeliczKosztEdycji();
        aktualizujStanPrzyciskow();
        edytujButton.setDisable(true);
    }

    private void handleAnulujEdycje() {
        ukryjBlad();
        zakonczTrybEdycjiBezCzyszczeniaSelekcji();
        if(wybranyWynajemDoEdycji != null && czyMoznaEdytowac(wybranyWynajemDoEdycji)){
            edytujButton.setDisable(false);
        }
    }

    private void zakonczTrybEdycjiBezCzyszczeniaSelekcji() {
        trybEdycjiAktywny = false;
        kosztEdycjiLabel.setText("Nowy koszt: -");
        dataRozpoczeciaDatePicker.setValue(null);
        dataZakonczeniaDatePicker.setValue(null);
        dataRozpoczeciaDatePicker.setDisable(true);
        dataZakonczeniaDatePicker.setDisable(true);
        zapiszEdycjeButton.setDisable(true);
        anulujEdycjeButton.setDisable(true);
        if (wybranyWynajemDoEdycji != null) {
            aktualizujStanPrzyciskow();
        } else {
            edytujButton.setDisable(true);
            usunButton.setDisable(true);
        }
    }

    private void przeliczKosztEdycji() {
        ukryjBlad();
        LocalDate start = dataRozpoczeciaDatePicker.getValue();
        LocalDate end = dataZakonczeniaDatePicker.getValue();

        if (!trybEdycjiAktywny || start == null || end == null || wybranyWynajemDoEdycji == null || wybranyWynajemDoEdycji.getPokoj() == null || wybranyWynajemDoEdycji.getPokoj().getCenaZaDobe() == null) {
            kosztEdycjiLabel.setText("Nowy koszt: -");
            zapiszEdycjeButton.setDisable(true);
            return;
        }

        zapiszEdycjeButton.setDisable(false);

        if (start.isBefore(LocalDate.now())) {
            kosztEdycjiLabel.setText("Błąd: Data rozpoczęcia z przeszłości.");
            pokazBlad("Data rozpoczęcia nie może być z przeszłości.");
            zapiszEdycjeButton.setDisable(true);
            return;
        }
        if (end.isBefore(start) || end.equals(start)) {
            kosztEdycjiLabel.setText("Błąd: Nieprawidłowy okres.");
            pokazBlad("Data zakończenia musi być późniejsza niż data rozpoczęcia.");
            zapiszEdycjeButton.setDisable(true);
            return;
        }
        long dni = ChronoUnit.DAYS.between(start, end);
        if (dni <= 0) {
            kosztEdycjiLabel.setText("Błąd: Nieprawidłowa liczba dni.");
            pokazBlad("Liczba dni musi być większa od zera.");
            zapiszEdycjeButton.setDisable(true);
            return;
        }
        BigDecimal kosztCalkowity = wybranyWynajemDoEdycji.getPokoj().getCenaZaDobe().multiply(BigDecimal.valueOf(dni));
        kosztEdycjiLabel.setText("Nowy koszt: " + currencyFormatter.format(kosztCalkowity));
    }

    private void handleZapiszEdycje() {
        ukryjBlad();
        if (wybranyWynajemDoEdycji == null || !trybEdycjiAktywny) {
            pokazBlad("Błąd: Brak wybranej rezerwacji lub tryb edycji nieaktywny.");
            return;
        }
        if (!czyMoznaEdytowac(wybranyWynajemDoEdycji)) {
            pokazBlad("Tej rezerwacji nie można już edytować.");
            zakonczTrybEdycjiBezCzyszczeniaSelekcji();
            return;
        }

        LocalDate nowaDataOd = dataRozpoczeciaDatePicker.getValue();
        LocalDate nowaDataDo = dataZakonczeniaDatePicker.getValue();
        Pokoj pokoj = wybranyWynajemDoEdycji.getPokoj();

        if (nowaDataOd == null || nowaDataDo == null || pokoj == null || pokoj.getCenaZaDobe() == null) {
            pokazBlad("Proszę wybrać poprawne daty i upewnić się, że dane pokoju są kompletne.");
            return;
        }
        if (nowaDataOd.isBefore(LocalDate.now())) {
            pokazBlad("Nowa data rozpoczęcia nie może być z przeszłości.");
            return;
        }
        if (nowaDataDo.isBefore(nowaDataOd) || nowaDataDo.equals(nowaDataOd)) {
            pokazBlad("Nowa data zakończenia musi być późniejsza niż data rozpoczęcia.");
            return;
        }
        long dni = ChronoUnit.DAYS.between(nowaDataOd, nowaDataDo);
        if (dni <= 0) {
            pokazBlad("Liczba dni musi być większa od zera.");
            return;
        }
        BigDecimal nowyKoszt = pokoj.getCenaZaDobe().multiply(BigDecimal.valueOf(dni));

        Platnosc platnosc = wybranyWynajemDoEdycji.getPlatnosc();

        try {
            platnosc.setKwota(nowyKoszt);
            platnoscDao.update(platnosc);

            wybranyWynajemDoEdycji.setDataRozpoczecia(nowaDataOd);
            wybranyWynajemDoEdycji.setDataZakonczenia(nowaDataDo);
            wynajemDao.update(wybranyWynajemDoEdycji);

            pokazAlert(Alert.AlertType.INFORMATION, "Edycja Zakończona", "Pomyślnie zaktualizowano rezerwację.");
            ladowanieRezerwacji();
        } catch (Exception e) {
            e.printStackTrace();
            pokazBlad("Wystąpił błąd podczas zapisywania zmian: " + e.getMessage());
        }
    }

    @FXML
    private void handleUsunButton(ActionEvent event) {
        ukryjBlad();
        Wynajem wybranyWynajem = rezerwacjeTableView.getSelectionModel().getSelectedItem();
        if (wybranyWynajem == null || !czyMoznaUsunac(wybranyWynajem)) {
            pokazBlad("Nie można usunąć tej rezerwacji.");
            return;
        }

        Alert alertPotwierdzenia = new Alert(Alert.AlertType.CONFIRMATION);
        alertPotwierdzenia.setTitle("Potwierdzenie Usunięcia");
        alertPotwierdzenia.setHeaderText("Czy na pewno chcesz usunąć wybraną rezerwację?");
        alertPotwierdzenia.setContentText("Pokój: " + (wybranyWynajem.getPokoj() != null ? wybranyWynajem.getPokoj().getNazwa() : "Brak danych") +
                "\nData od: " + (wybranyWynajem.getDataRozpoczecia() != null ? wybranyWynajem.getDataRozpoczecia().format(dateFormatter) : "Brak danych") +
                "\n\nTa operacja jest nieodwracalna!");

        Optional<ButtonType> result = alertPotwierdzenia.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Pokoj pokoj = wybranyWynajem.getPokoj();
                Platnosc platnosc = wybranyWynajem.getPlatnosc();

                wynajemDao.delete(wybranyWynajem);

                if (platnosc != null) {
                    List<Wynajem> inneWynajmyDlaPlatnosci = wynajemDao.findAll().stream()
                            .filter(w -> w.getPlatnosc() != null && w.getPlatnosc().equals(platnosc))
                            .toList();
                    if (inneWynajmyDlaPlatnosci.isEmpty()) {
                        platnoscDao.delete(platnosc);
                    }
                }

                if (pokoj != null && !pokoj.isDostepnosc()) {
                    boolean inneAktywneRezerwacjeNaTenPokoj = wynajemDao.findAll().stream()
                            .anyMatch(w -> w.getPokoj() != null && w.getPokoj().equals(pokoj) &&
                                    w.getPlatnosc() != null &&
                                    ("OCZEKUJACE".equalsIgnoreCase(w.getPlatnosc().getStatus()) || "ZAPŁACONO".equalsIgnoreCase(w.getPlatnosc().getStatus())) &&
                                    !(w.getDataZakonczenia().isBefore(LocalDate.now()) || w.getDataRozpoczecia().isAfter(LocalDate.now().plusYears(10)))
                            );

                    if (!inneAktywneRezerwacjeNaTenPokoj) {
                        pokoj.setDostepnosc(true);
                        pokojDao.update(pokoj);
                    }
                }
                pokazAlert(Alert.AlertType.INFORMATION, "Usunięto Rezerwację", "Rezerwacja została pomyślnie usunięta.");
                ladowanieRezerwacji();
            } catch (Exception e) {
                e.printStackTrace();
                pokazBlad("Wystąpił błąd podczas usuwania rezerwacji: " + e.getMessage());
            }
        }
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
}