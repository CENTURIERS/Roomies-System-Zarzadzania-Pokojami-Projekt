package com.roomies.controller;

import com.roomies.dao.LokalizacjaDao;
import com.roomies.dao.PokojDao;
import com.roomies.dao.RodzajPokojuDao;
import com.roomies.model.Klient;
import com.roomies.model.Lokalizacja;
import com.roomies.model.Pokoj;
import com.roomies.model.RodzajPokoju;
import com.roomies.util.UserSession;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML
    private Button zalogujButton;
    @FXML
    private Button mojeKontoButton;
    @FXML
    private Accordion filtryAccordion;
    @FXML
    private TitledPane dostepnoscPane;
    @FXML
    private VBox dostepnoscRadioVBox;
    @FXML
    private RadioButton radioDostepnoscTak;
    @FXML
    private RadioButton radioDostepnoscNie;
    @FXML
    private RadioButton radioDostepnoscWszystkie;
    @FXML
    private ToggleGroup dostepnoscGrupa;
    @FXML
    private TitledPane rodzajPokojuPane;
    @FXML
    private VBox rodzajPokojuRadioVBox;
    private List<RadioButton> dynamiczneRodzajePokojuRadioButton = new ArrayList<>();
    @FXML
    private RadioButton radioRodzajWszystkie;
    @FXML
    private ToggleGroup rodzajPokojuGrupa;
    @FXML
    private TitledPane lokalizacjaPane;
    @FXML
    private VBox lokalizacjaCheckboxVBox;
    private List<CheckBox> dynamiczneLokalizacjeCheckBoxes = new ArrayList<>();
    @FXML
    private Button wyczyscFiltryButton;
    @FXML
    private Button zastosujFiltryButton;
    @FXML
    private ScrollPane glownyScrollPane;
    @FXML
    private FlowPane glownaZawartoscFlowPane;

    private PokojDao pokojDao;
    private RodzajPokojuDao rodzajPokojuDao;
    private LokalizacjaDao lokalizacjaDao;
    private List<Pokoj> wszystkiePokojeZBazy;
    private Stage primaryStage;

    @FXML
    public void initialize() {
        pokojDao = new PokojDao();
        rodzajPokojuDao = new RodzajPokojuDao();
        lokalizacjaDao = new LokalizacjaDao();

        try {
            wszystkiePokojeZBazy = pokojDao.findAll();
            if (wszystkiePokojeZBazy == null){
                wszystkiePokojeZBazy = new ArrayList<>();
            }
        }catch (Exception e){
            wszystkiePokojeZBazy = new ArrayList<>();
            pokazAlert(Alert.AlertType.ERROR, "Błąd krytyczny", "Nie udało się załadować danych pokoi z bazy.\nSprawdź połączenie i konfigurację");
            e.printStackTrace();
        }

        stworzDynamiczneFiltryLokalizacji();
        stworzDynamiczneFiltryRodzajuPokoju();

        zastosujFiltryButton.setOnAction(event -> aplikujFiltry());
        wyczyscFiltryButton.setOnAction(event -> wyczyscFiltry());
        zalogujButton.setOnAction(event -> handleZalogujLubWylogujButton());
        mojeKontoButton.setOnAction(event -> otworzPanelKonta());

        aplikujFiltry();
        zaktualizujStatusZalogowania(UserSession.getInstance().getZalogowanyKlient());
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @FXML
    public void handleZalogujLubWylogujButton() {
        if (!UserSession.getInstance().isUserLoggedIn()) {
            otworzOknoLogowania();
        } else {
            UserSession.getInstance().logoutUser();
            zaktualizujStatusZalogowania(null);
            pokazAlert(Alert.AlertType.INFORMATION, "Wylogowano", "Pomyślnie wylogowano z systemu.");
            odswiezWszystkiePokojeNaKartach();
        }
    }

    private void otworzOknoLogowania() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/roomies/view/login-view.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setMainController(this);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Logowanie");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            if (this.primaryStage != null && this.primaryStage.isShowing()) {
                dialogStage.initOwner(this.primaryStage);
            }

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            pokazAlert(Alert.AlertType.ERROR, "Błąd Aplikacji", "Nie można otworzyć okna logowania.");
        }
    }

    public void zaktualizujStatusZalogowania(Klient klient) {
        if (klient != null && UserSession.getInstance().isUserLoggedIn()) {
            zalogujButton.setText("Wyloguj (" + klient.getImie() + ")");
            mojeKontoButton.setVisible(true);
            mojeKontoButton.setManaged(true);
        } else {
            zalogujButton.setText("Zaloguj");
            mojeKontoButton.setVisible(false);
            mojeKontoButton.setManaged(false);
        }
    }

    private void otworzPanelKonta() {
        if (!UserSession.getInstance().isUserLoggedIn()) {
            pokazAlert(Alert.AlertType.WARNING, "Brak dostępu", "Musisz być zalogowany, aby przejść do panelu konta.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/roomies/view/panel-konta-view.fxml"));
            Parent root = loader.load();

            PanelKontaController controller = loader.getController();
            Klient klient = UserSession.getInstance().getZalogowanyKlient();
            if (klient != null) {
                controller.initData(klient);
            } else {
                pokazAlert(Alert.AlertType.ERROR, "Błąd Sesji", "Błąd podczas pobierania danych zalogowanego użytkownika.");
                return;
            }

            Stage panelKontaStage = new Stage();
            panelKontaStage.setTitle("Moje Konto - " + klient.getImie() + " " + klient.getNazwisko());
            panelKontaStage.initModality(Modality.WINDOW_MODAL);
            if (this.primaryStage != null && this.primaryStage.isShowing()) {
                panelKontaStage.initOwner(this.primaryStage);
            }

            Scene scene = new Scene(root);
            panelKontaStage.setScene(scene);
            panelKontaStage.showAndWait();
            odswiezWszystkiePokojeNaKartach();

        } catch (IOException e) {
            e.printStackTrace();
            pokazAlert(Alert.AlertType.ERROR, "Błąd Aplikacji", "Nie można otworzyć panelu konta: " + e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            pokazAlert(Alert.AlertType.ERROR, "Błąd Aplikacji", "Nie znaleziono pliku FXML dla panelu konta lub błąd kontrolera: " + e.getMessage());
        }
    }

    @FXML
    public void aplikujFiltry() {
        List<Pokoj> przefiltrowanePokoje = new ArrayList<>(wszystkiePokojeZBazy);
        RadioButton wybranaDostepnoscRadio = (RadioButton) dostepnoscGrupa.getSelectedToggle();

        if(wybranaDostepnoscRadio != null){
            if (radioDostepnoscTak.equals(wybranaDostepnoscRadio)){
                przefiltrowanePokoje.removeIf(pokoj -> !pokoj.isDostepnosc());
            } else if (radioDostepnoscNie.equals(wybranaDostepnoscRadio)) {
                przefiltrowanePokoje.removeIf(Pokoj::isDostepnosc);
            }
        }

        RadioButton wybranyRodzajRadio = (RadioButton) rodzajPokojuGrupa.getSelectedToggle();
        if (wybranyRodzajRadio != null && !wybranyRodzajRadio.equals(radioRodzajWszystkie)) {
            Object userData = wybranyRodzajRadio.getUserData();
            if(userData instanceof RodzajPokoju){
                RodzajPokoju szukanyRodzaj = (RodzajPokoju) userData;
                przefiltrowanePokoje.removeIf(pokoj ->
                        pokoj.getRodzajPokoju() == null ||
                                !pokoj.getRodzajPokoju().equals(szukanyRodzaj)
                );
            }else{
                String szukanyRodzajText = wybranyRodzajRadio.getText();
                przefiltrowanePokoje.removeIf(pokoj ->
                        pokoj.getRodzajPokoju() == null ||
                                !pokoj.getRodzajPokoju().getNazwa().equals(szukanyRodzajText)
                );
            }
        }

        List<String> wybraneNazwyLokalizacji = new ArrayList<>();
        for(CheckBox cb : dynamiczneLokalizacjeCheckBoxes){
            if(cb.isSelected()){
                wybraneNazwyLokalizacji.add(cb.getText());
            }
        }

        if(!wybraneNazwyLokalizacji.isEmpty()){
            przefiltrowanePokoje.removeIf(pokoj ->
                    pokoj.getLokalizacja() == null ||
                            !wybraneNazwyLokalizacji.contains(pokoj.getLokalizacja().getNazwaLokalizacji())
            );
        }
        wyswietlPokojeNaKartach(przefiltrowanePokoje);
    }

    private void stworzDynamiczneFiltryLokalizacji(){
        if(lokalizacjaDao == null){
            System.err.println("Błąd lokalizacjaDao nie zostało zainicjowane w MainController!");
            return;
        }
        if (lokalizacjaCheckboxVBox == null){
            System.err.println("Błąd lokalizacjaCheckboxVBox nie zostało wstrzyknięte z FXML!");
            return;
        }

        lokalizacjaCheckboxVBox.getChildren().clear();
        dynamiczneLokalizacjeCheckBoxes.clear();
        try {
            List<Lokalizacja> wszystkieLokalizacje = lokalizacjaDao.findAll();
            if(wszystkieLokalizacje == null || wszystkieLokalizacje.isEmpty()){
                lokalizacjaCheckboxVBox.getChildren().add(new Label("Brak lokalizacji"));
                return;
            }
            for (Lokalizacja lok : wszystkieLokalizacje) {
                CheckBox cb = new CheckBox(lok.getNazwaLokalizacji());
                dynamiczneLokalizacjeCheckBoxes.add(cb);
                lokalizacjaCheckboxVBox.getChildren().add(cb);
            }
        }catch (Exception e){
            pokazAlert(Alert.AlertType.WARNING, "Błąd Filtrów lokalizacji", "Nie udało się załadować opcji filtrów dla lokalizacji.");
            e.printStackTrace();
        }
    }

    private void stworzDynamiczneFiltryRodzajuPokoju(){
        if(rodzajPokojuDao == null){
            System.err.println("Błąd rodzajPokojuDao nie zostało zainicjowane w MainController!");
            return;
        }
        if(rodzajPokojuRadioVBox == null){
            System.err.println("Błąd rodzajPokojuRadioVBox nie zostało wstrzyknięte z FXML!");
            return;
        }
        if (rodzajPokojuGrupa == null){
            System.err.println("Błąd rodzajPokojuGrupa nie zostalo wstrzykniete z FXML!");
            return;
        }

        rodzajPokojuRadioVBox.getChildren().clear();
        dynamiczneRodzajePokojuRadioButton.clear();
        try {
            List<RodzajPokoju> wszystkieRodzajePokoi = rodzajPokojuDao.findAll();
            if(wszystkieRodzajePokoi == null || wszystkieRodzajePokoi.isEmpty()){
                rodzajPokojuRadioVBox.getChildren().add(new Label("Brak tego rodzajów"));
                return;
            }
            for (RodzajPokoju rp : wszystkieRodzajePokoi) {
                RadioButton rb = new RadioButton(rp.getNazwa());
                rb.setToggleGroup(rodzajPokojuGrupa);
                rb.setUserData(rp);
                dynamiczneRodzajePokojuRadioButton.add(rb);
                rodzajPokojuRadioVBox.getChildren().add(rb);
            }
            radioRodzajWszystkie.setToggleGroup(rodzajPokojuGrupa);
            rodzajPokojuRadioVBox.getChildren().add(radioRodzajWszystkie);
        }catch (Exception e){
            pokazAlert(Alert.AlertType.WARNING, "Błąd Filtrów rodzaji pokoi", "Nie udało się załadować opcji filtrów rodzji pokoi.");
            e.printStackTrace();
        }
    }

    @FXML
    public void wyswietlPokojeNaKartach(List<Pokoj> pokojeDoWyswietlenia) {
        glownaZawartoscFlowPane.getChildren().clear();
        if (pokojeDoWyswietlenia.isEmpty()){
            Label brakWynikowLabel = new Label("Brak Pokoi spełanijących wybrane kryteria");
            brakWynikowLabel.setStyle("-fx-font-style: italic; -fx-padding: 20px; -fx-alignment: center");
            glownaZawartoscFlowPane.getChildren().add(brakWynikowLabel);
            return;
        }
        for (Pokoj pokoj : pokojeDoWyswietlenia) {
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/roomies/view/pokoj-item-card.fxml"));
                Node kartaPokojuNode = loader.load();
                PokojItemController itemController = loader.getController();
                if (itemController != null) {
                    itemController.setData(pokoj);
                    itemController.setMainController(this);
                    glownaZawartoscFlowPane.getChildren().add(kartaPokojuNode);
                } else {
                    System.err.println("BŁĄD KRYTYCZNY KONTROLERA: PokojItemController nie został zainicjowany dla pokoju ID: " + (pokoj != null ? pokoj.getId_pokoju() : "null"));
                }
            } catch (Exception ex){
                String idPokoju = (pokoj != null ? String.valueOf(pokoj.getId_pokoju()) : "null");
                System.err.println("BŁĄD: Inny wyjątek przy ładowaniu karty dla pokoju ID: " + idPokoju + " - " + ex.getMessage());
                ex.printStackTrace();
                pokazAlert(Alert.AlertType.WARNING, "Błąd Karty", "Wystąpił nieoczekiwany błąd podczas ładowania jednej z kart pokoju.");
            }
        }
    }

    @FXML
    public void wyczyscFiltry() {
        if(radioDostepnoscWszystkie != null) radioDostepnoscWszystkie.setSelected(true);
        if(radioRodzajWszystkie != null) radioRodzajWszystkie.setSelected(true);
        for (CheckBox cb : dynamiczneLokalizacjeCheckBoxes) {
            cb.setSelected(false);
        }
        aplikujFiltry();
    }

    public void odswiezWszystkiePokojeNaKartach() {
        try {
            wszystkiePokojeZBazy = pokojDao.findAll();
            if (wszystkiePokojeZBazy == null) {
                wszystkiePokojeZBazy = new ArrayList<>();
            }
        } catch (Exception e) {
            wszystkiePokojeZBazy = new ArrayList<>();
            pokazAlert(Alert.AlertType.ERROR, "Błąd krytyczny", "Nie udało się załadować danych pokoi z bazy przy odświeżaniu.");
            e.printStackTrace();
        }
        aplikujFiltry();
    }

    private void pokazAlert(Alert.AlertType type, String tytul, String wiadomosc){
        Alert alert = new Alert(type);
        alert.setTitle(tytul);
        alert.setHeaderText(null);
        alert.setContentText(wiadomosc);
        alert.showAndWait();
    }
}