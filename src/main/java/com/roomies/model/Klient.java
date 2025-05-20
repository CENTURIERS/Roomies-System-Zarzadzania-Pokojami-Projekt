package com.roomies.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Klienci")
public class Klient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_klienta")
    private Integer idKlienta;

    @Column(name = "imie", length = 255, nullable = false)
    private String imie;

    @Column(name = "nazwisko", length = 255, nullable = false)
    private String nazwisko;

    @Column(name = "ulica", length = 255, nullable = false)
    private String ulica;

    @Column(name = "numer_budynku", nullable = false)
    private int numerBudynku;

    @Column(name = "pesel", length = 11, unique = true, nullable = false)
    private String pesel;

    @Column(name = "czy_aktywny")
    private boolean czyAktywny;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "klient", cascade = CascadeType.ALL)
    private List<Wynajem> wynajmy = new ArrayList<>();

    public Klient(){

    }

    public Klient(String imie, String nazwisko, String ulica, int numerBudynku, String pesel, boolean czyAktywny) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.ulica = ulica;
        this.numerBudynku = numerBudynku;
        this.pesel = pesel;
        this.czyAktywny = czyAktywny;
    }

    public Integer getIdKlienta() {
        return idKlienta;
    }

    public void setIdKlienta(Integer idKlienta) {
        this.idKlienta = idKlienta;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public String getUlica() {
        return ulica;
    }

    public void setUlica(String ulica) {
        this.ulica = ulica;
    }

    public int getNumerBudynku() {
        return numerBudynku;
    }

    public void setNumerBudynku(int numerBudynku) {
        this.numerBudynku = numerBudynku;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public boolean isCzyAktywny() {
        return czyAktywny;
    }

    public void setCzyAktywny(boolean czyAktywny) {
        this.czyAktywny = czyAktywny;
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
        Klient that = (Klient) o;
        if (idKlienta == null){
            return false;
        }
        return Objects.equals(idKlienta, that.idKlienta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idKlienta);
    }

    @Override
    public String toString() {
        return "\nKlient:" +
                "\n  ID: " + getIdKlienta() +
                "\n  Imię: " + getImie() +
                "\n  Nazwisko: " + getNazwisko() +
                "\n  Ulica: " + getUlica() +
                "\n  Numer budynku: " + getNumerBudynku() +
                "\n  PESEL: " + getPesel();
    }
}
