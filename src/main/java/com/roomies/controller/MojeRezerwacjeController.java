package com.roomies.controller;

import com.roomies.model.Klient;
import com.roomies.model.Platnosc;
import com.roomies.model.Pokoj;
import com.roomies.model.Wynajem;
import com.roomies.dao.WynajemDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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

    private Klient zalogowanyKlient;
    private ObservableList<Wynajem> listaWynajmow = FXCollections.observableArrayList();
    private WynajemDao wynajemDao;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pl", "PL"));

    @FXML
    public void initialize() {
        wynajemDao = new WynajemDao();

        rezerwacjeTableView.setItems(listaWynajmow);
        rezerwacjeTableView.setPlaceholder(new Label("Brak rezerwacji do wyświetlenia."));

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
                getStyleClass().removeAll("status-oczekujaca", "status-zaplacona", "status-anulowana");
                setText(null);
                setStyle("");

                if (item == null || empty) {
                } else {
                    setText(item);
                    if ("OCZEKUJACE".equalsIgnoreCase(item)) {
                        getStyleClass().add("status-oczekujaca");
                    } else if ("ZAPŁACONO".equalsIgnoreCase(item)) {
                        getStyleClass().add("status-zaplacona");
                    } else if ("ANULOWANO".equalsIgnoreCase(item)) {
                        getStyleClass().add("status-anulowana");
                    }
                }
            }
        });
    }

    public void initData(Klient klient) {
        this.zalogowanyKlient = klient;
        ladowanieRezerwacji();
    }

    private void ladowanieRezerwacji() {
        listaWynajmow.clear();
        if (zalogowanyKlient != null) {
            List<Wynajem> wynajmyKlienta = wynajemDao.findByKlientId(zalogowanyKlient.getIdKlienta());
            if (wynajmyKlienta != null) {
                listaWynajmow.setAll(wynajmyKlienta);
            }
        }
    }
}