package com.roomies.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Pokoje")
public class Pokoj {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pokoju")
    private Integer id_pokoju;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rodzaju_pokoju", nullable = false)
    private RodzajPokoju rodzajPokoju;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_lokalizacji")
    private Lokalizacja lokalizacja;

    @OneToMany(mappedBy = "pokoj", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Wynajem> wynajmy = new ArrayList<>();

    @Column(name = "dostepnosc", nullable = false)
    private boolean dostepnosc;

    @Column(name = "nazwa", nullable = false, length = 255)
    private String nazwa;

    public Pokoj() {

    }

    public Pokoj(Boolean dostepnosc, String nazwa, RodzajPokoju rodzajPokoju, Lokalizacja lokalizacja) {
        this.dostepnosc = dostepnosc;
        this.nazwa = nazwa;
        this.rodzajPokoju = rodzajPokoju;
        this.lokalizacja = lokalizacja;
    }

    public Integer getId_pokoju() {
        return id_pokoju;
    }

    public void setId_pokoju(Integer id_pokoju) {
        this.id_pokoju = id_pokoju;
    }

    public RodzajPokoju getRodzajPokoju() {
        return rodzajPokoju;
    }

    public void setRodzajPokoju(RodzajPokoju rodzajPokoju) {
        this.rodzajPokoju = rodzajPokoju;
    }

    public Lokalizacja getLokalizacja() {
        return lokalizacja;
    }

    public void setLokalizacja(Lokalizacja lokalizacja) {
        this.lokalizacja = lokalizacja;
    }

    public boolean isDostepnosc() {
        return dostepnosc;
    }

    public void setDostepnosc(boolean dostepnosc) {
        this.dostepnosc = dostepnosc;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public List<Wynajem> getWynajmy() {
        return wynajmy;
    }

    public void setWynajmy(List<Wynajem> wynajmy) {
        this.wynajmy = wynajmy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pokoj that = (Pokoj) o;
        if(id_pokoju == null){
            return false;
        }
        return Objects.equals(id_pokoju, that.id_pokoju);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_pokoju);
    }

    @Override
    public String toString() {
        String rodzajPokojuInfo = "Brak informacji";
        if (getRodzajPokoju() != null) {
            rodzajPokojuInfo = getRodzajPokoju().getNazwa();
        }

        String lokalizacjaInfo = "Brak informacji";
        if (getLokalizacja() != null) {
            lokalizacjaInfo = getLokalizacja().getNazwaLokalizacji();
        }

        return "\nPokój:" +
                "\n  ID: " + getId_pokoju() +
                "\n  Nazwa: " + getNazwa() +
                "\n  Dostępność: " + isDostepnosc() +
                "\n  Rodzaj Pokoju: " + rodzajPokojuInfo +
                "\n  Lokalizacja: " + lokalizacjaInfo;
    }
}
