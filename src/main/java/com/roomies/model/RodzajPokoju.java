package com.roomies.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "RodzajPokoju")
public class RodzajPokoju {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rodzaj")
    private Integer id_rodzaj;

    @Column(name = "nazwa", length = 255, nullable = false)
    private String nazwa;

    @Column(name = "wyposazenie", length = 255)
    private String wyposazenie;

    @Column(name = "ilosc_metrow")
    private int ilosc_metrow;

    @OneToMany(mappedBy = "rodzajPokoju", fetch = FetchType.LAZY)
    private List<Pokoj> pokoje = new ArrayList<>();

    public RodzajPokoju() {

    }

    public RodzajPokoju(String nazwa, String wyposazenie, int ilosc_metrow) {
        this.nazwa = nazwa;
        this.wyposazenie = wyposazenie;
        this.ilosc_metrow = ilosc_metrow;
    }

    public Integer getId_rodzaj() {
        return id_rodzaj;
    }

    public void setId_rodzaj(Integer id_rodzaj) {
        this.id_rodzaj = id_rodzaj;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getWyposazenie() {
        return wyposazenie;
    }

    public void setWyposazenie(String wyposazenie) {
        this.wyposazenie = wyposazenie;
    }

    public int getIlosc_metrow() {
        return ilosc_metrow;
    }

    public void setIlosc_metrow(int ilosc_metrow) {
        this.ilosc_metrow = ilosc_metrow;
    }

    public List<Pokoj> getPokoje() {
        return pokoje;
    }

    public void setPokoje(List<Pokoj> pokoje) {
        this.pokoje = pokoje;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RodzajPokoju that = (RodzajPokoju) o;
        if (id_rodzaj == null){
            return false;
        }
        return Objects.equals(id_rodzaj, that.id_rodzaj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_rodzaj);
    }

    @Override
    public String toString() {
        String wyposazenieInfo = "Brak informacji";
        if (getWyposazenie() != null && !getWyposazenie().isEmpty()) {
            wyposazenieInfo = getWyposazenie();
        }

        return "\nRodzaj Pokoju:" +
                "\n  ID: " + getId_rodzaj() +
                "\n  Nazwa: " + getNazwa() +
                "\n  Wyposażenie: " + wyposazenieInfo +
                "\n  Ilość metrów: " + getIlosc_metrow();
    }
}
