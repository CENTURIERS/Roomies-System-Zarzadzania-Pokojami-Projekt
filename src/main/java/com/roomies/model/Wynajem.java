package com.roomies.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "Wynajem")
public class Wynajem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_wynajmu")
    private Integer id_wynajmu;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pokoju")
    private Pokoj pokoj;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name  = "id_klienta")
    private Klient klient;

    @Column(name = "data_rozpoczecia", nullable = false)
    private LocalDate dataRozpoczecia;

    @Column(name = "data_zakoczenia")
    private LocalDate dataZakoczenia;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_platnosci")
    private Platnosc platnosc;

    public Wynajem() {

    }

    public Wynajem(Pokoj pokoj, Klient klient, LocalDate dataRozpoczecia, LocalDate dataZakoczenia, Platnosc platnosc) {
        this.pokoj = pokoj;
        this.klient = klient;
        this.dataRozpoczecia = dataRozpoczecia;
        this.dataZakoczenia = dataZakoczenia;
        this.platnosc = platnosc;
    }

    public Integer getId_wynajmu() {
        return id_wynajmu;
    }

    public void setId_wynajmu(Integer id_wynajmu) {
        this.id_wynajmu = id_wynajmu;
    }

    public Pokoj getPokoj() {
        return pokoj;
    }

    public void setPokoj(Pokoj pokoj) {
        this.pokoj = pokoj;
    }

    public Klient getKlient() {
        return klient;
    }

    public void setKlient(Klient klient) {
        this.klient = klient;
    }

    public LocalDate getDataRozpoczecia() {
        return dataRozpoczecia;
    }

    public void setDataRozpoczecia(LocalDate dataRozpoczecia) {
        this.dataRozpoczecia = dataRozpoczecia;
    }

    public LocalDate getDataZakonczenia() {
        return dataZakoczenia;
    }

    public void setDataZakonczenia(LocalDate dataZakoczenia) {
        this.dataZakoczenia = dataZakoczenia;
    }

    public Platnosc getPlatnosc() {
        return platnosc;
    }

    public void setPlatnosc(Platnosc platnosc) {
        this.platnosc = platnosc;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Wynajem that = (Wynajem) o;
        if(id_wynajmu == null){
            return false;
        }
        return Objects.equals(id_wynajmu, that.id_wynajmu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_wynajmu);
    }

    @Override
    public String toString() {
        String pokojInfo = "Brak informacji";
        if (getPokoj() != null) {
            pokojInfo = getPokoj().getNazwa();
        }

        String klientInfo = "Brak informacji";
        if (getKlient() != null) {
            klientInfo = getKlient().getImie() + " " + getKlient().getNazwisko();
        }

        String dataZakonczeniaInfo = "Brak informacji";
        if (getDataZakonczenia() != null) {
            dataZakonczeniaInfo = getDataZakonczenia().toString();
        }

        String platnoscInfo = "Brak informacji";
        if (getPlatnosc() != null) {
            platnoscInfo = "ID: " + getPlatnosc().getId_platnosci();
        }

        return "\nWynajem:" +
                "\n  ID: " + getId_wynajmu() +
                "\n  Pokój: " + pokojInfo +
                "\n  Klient: " + klientInfo +
                "\n  Data rozpoczęcia: " + (getDataRozpoczecia() != null ? getDataRozpoczecia().toString() : "Brak") +
                "\n  Data zakończenia: " + dataZakonczeniaInfo +
                "\n  Płatność: " + platnoscInfo;
    }
}
