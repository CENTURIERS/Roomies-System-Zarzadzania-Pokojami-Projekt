package com.roomies.controller;

import com.roomies.dao.*;
import com.roomies.model.Lokalizacja;
import com.roomies.model.Pokoj;
import com.roomies.model.RodzajPokoju;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

//   === HEADER ===
    @FXML
    private Button zalogujButton;

//    === LEWY PANEL ===
    @FXML
    private Accordion filtryAccordion;

//    == Dostępność ==
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

//    == Rodzaj Pokoju ==
    @FXML
    private TitledPane rodzajPokojuPane;

    @FXML
    private VBox rodzajPokojuRadioVBox;

    private List<RadioButton> dynamiczneRodzajePokojuRadioButton = new ArrayList<>();

    @FXML
    private RadioButton radioRodzajWszystkie;

    @FXML
    private ToggleGroup rodzajPokojuGrupa;

//    == Lokalizacja ==
    @FXML
    private TitledPane lokalizacjaPane;

    @FXML
    private VBox lokalizacjaCheckboxVBox;

    private List<CheckBox> dynamiczneLokalizacjeCheckBoxes = new ArrayList<>();

//    == Przyciski filtrów ==
    @FXML
    private Button wyczyscFiltryButton;

    @FXML
    private Button zastosujFiltryButton;

//    === GŁÓWNY PANEL ===
    @FXML
    private ScrollPane glownyScrollPane;

    @FXML
    private FlowPane glownaZawartoscFlowPane;

//    === DAO ===
    private PokojDao pokojDao;
    private RodzajPokojuDao rodzajPokojuDao;
    private LokalizacjaDao lokalizacjaDao;

    private List<Pokoj> wszystkiePokojeZBazy;

    @FXML
    public void initialize() {
        pokojDao = new PokojDao();
        rodzajPokojuDao = new RodzajPokojuDao();
        lokalizacjaDao = new LokalizacjaDao();

        try {
            wszystkiePokojeZBazy = pokojDao.findAll();
            if (wszystkiePokojeZBazy == null){
                wszystkiePokojeZBazy = new ArrayList<Pokoj>();
            }
        }catch (Exception e){
            wszystkiePokojeZBazy = new ArrayList<>();
            pokazAlert("Błąd krytyczny", "Nie udało się załadować danych pokoi z bazy.\nSprawdź połączenie i konfigurację", Alert.AlertType.ERROR);
            e.printStackTrace();
        }

        stworzDynamiczneFiltryLokalizacji();
        stworzDynamiczneFiltryRodzajuPokoju();

        zastosujFiltryButton.setOnAction(event -> aplikujFiltry());
        wyczyscFiltryButton.setOnAction(event -> wyczyscFiltry());
        zalogujButton.setOnAction(event -> handleZalogujButton());

        aplikujFiltry();
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
            System.out.println("Błąd lokalizacjaDao nie zostało zainicjowane w MainController!");
            return;
        }

        if (lokalizacjaCheckboxVBox == null){
            System.out.println("Błąd lokalizacjaCheckboxVBox nie zostało wstrzyknięte z FXML!");
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
            pokazAlert("Błąd Filtrów lokalizacji", "Nie udało się załadować opcji filtrów dla lokalizacji.", Alert.AlertType.WARNING);
            e.printStackTrace();
        }
    }

    private void stworzDynamiczneFiltryRodzajuPokoju(){
        if(rodzajPokojuDao == null){
            System.out.println("Błąd rodzajPokojuDao nie zostało zainicjowane w MainController!");
            return;
        }

        if(rodzajPokojuRadioVBox == null){
            System.out.println("Błąd rodzajPokojuRadioVBox nie zostało wstrzyknięte z FXML!");
            return;
        }

        if (rodzajPokojuGrupa == null){
            System.out.println("Błąd rodzajPokojuGrupa nie zostalo wstrzykniete z FXML!");
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
            pokazAlert("Błąd Filtrów rodzaji pokoi", "Nie udało się załadować opcji filtrów rodzji pokoi.", Alert.AlertType.WARNING);
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
                    glownaZawartoscFlowPane.getChildren().add(kartaPokojuNode);
                } else {
                    System.out.println("BŁĄD KRYTYCZNY KONTROLERA: PokojItemController nie został zainicjowany dla pokoju ID: " + (pokoj != null ? pokoj.getId_pokoju() : "null"));
                }
            }catch (IOException e){
                String nazwaPokoju = (pokoj != null && pokoj.getNazwa() != null) ? pokoj.getNazwa() : (pokoj != null ? "ID " + pokoj.getId_pokoju() : "Nieznany pokój");
                pokazAlert("Błąd Ładowania Widoku", "Wystąpił błąd wejścia/wyjścia podczas ładowania karty dla pokoju: " + nazwaPokoju, Alert.AlertType.WARNING);
                e.printStackTrace();
            }catch (Exception ex){
                String idPokoju = (pokoj != null ? String.valueOf(pokoj.getId_pokoju()) : "null");
                System.out.println("BŁĄD: Inny wyjątek przy ładowaniu karty dla pokoju ID: \" + idPokoju + \" - \" + ex.getMessage()");
                ex.printStackTrace();
                pokazAlert("Błąd Karty", "Wystąpił nieoczekiwany błąd podczas ładowania jednej z kart pokoju.", Alert.AlertType.WARNING);
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

    @FXML
    public void handleZalogujButton() {
        pokazAlert("Informacja", "Funkcja logowanie jest obecnie niedostępna", Alert.AlertType.INFORMATION);
    }

//    === Metoda pomocnicza ===
    private void pokazAlert(String tytul, String wiadomosc, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(tytul);
        alert.setHeaderText(null);
        alert.setContentText(wiadomosc);
        alert.showAndWait();
    }
}
