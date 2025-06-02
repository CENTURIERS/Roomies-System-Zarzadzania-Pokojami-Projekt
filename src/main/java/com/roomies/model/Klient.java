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

    @Column(name = "email", length = 255, unique = true, nullable = false)
    private String email;

    @Column(name = "telefon", length = 20, nullable = true)
    private String telefon;

    @Column(name = "ulica", length = 255, nullable = false)
    private String ulica;

    @Column(name = "numer_budynku",length = 10, nullable = false)
    private String numerBudynku;

    @Column(name = "pesel", length = 11, unique = true, nullable = false)
    private String pesel;

    @Column(name = "czy_aktywny")
    private boolean czyAktywny;

    @Column(name = "haslo", length = 255, nullable = false)
    private String haslo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "klient", cascade = CascadeType.ALL)
    private List<Wynajem> wynajmy = new ArrayList<>();

    public Klient(){

    }

    public Klient(String pesel, String numerBudynku, String ulica, String telefon, String email, String nazwisko, String imie, String haslo) {
        this.pesel = pesel;
        this.numerBudynku = numerBudynku;
        this.ulica = ulica;
        this.telefon = telefon;
        this.email = email;
        this.nazwisko = nazwisko;
        this.imie = imie;
        this.haslo = haslo;
        this.czyAktywny = true;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public void setNumerBudynku(String numerBudynku) {
        this.numerBudynku = numerBudynku;
    }

    public String getHaslo() {
        return haslo;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
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
        return "Klient{" +
                "idKlienta=" + idKlienta +
                ", imie='" + imie + '\'' +
                ", nazwisko='" + nazwisko + '\'' +
                ", email='" + email + '\'' +
                ", telefon='" + (telefon != null ? telefon : "brak") + '\'' +
                ", ulica='" + ulica + '\'' +
                ", numerBudynku='" + numerBudynku + '\'' +
                ", pesel='" + pesel + '\'' +
                ", czyAktywny=" + czyAktywny +
                '}';
    }
}
