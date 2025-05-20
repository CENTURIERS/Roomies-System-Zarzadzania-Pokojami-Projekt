package com.roomies.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Lokalizacja")
public class Lokalizacja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lokalizacji")
    private Integer idLokalizacji;

    @Column(name = "nazwa_lokalizacji", nullable = false, length = 255)
    private String nazwaLokalizacji;

    @Column(name = "kraj", nullable = false, length = 255)
    private String kraj;

    @Column(name = "miasto", nullable = false,length = 255)
    private String miasto;

    @OneToMany(mappedBy = "lokalizacja", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Pokoj> pokoje = new ArrayList<>();

    public Lokalizacja() {

    }

    public Lokalizacja(String nazwaLokalizacji, String kraj, String miasto) {
        this.nazwaLokalizacji = nazwaLokalizacji;
        this.kraj = kraj;
        this.miasto = miasto;
    }

    public Integer getIdLokalizacji() {
        return idLokalizacji;
    }

    public void setIdLokalizacji(Integer idLokalizacji) {
        this.idLokalizacji = idLokalizacji;
    }

    public String getNazwaLokalizacji() {
        return nazwaLokalizacji;
    }

    public void setNazwaLokalizacji(String nazwaLokalizacji) {
        this.nazwaLokalizacji = nazwaLokalizacji;
    }

    public String getKraj() {
        return kraj;
    }

    public void setKraj(String kraj) {
        this.kraj = kraj;
    }

    public String getMiasto() {
        return miasto;
    }

    public void setMiasto(String miasto) {
        this.miasto = miasto;
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
        Lokalizacja that = (Lokalizacja) o;
        if(idLokalizacji == null){
            return false;
        }
        return Objects.equals(idLokalizacji, that.idLokalizacji);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLokalizacji);
    }

    @Override
    public String toString() {
        return "\nLokalizacja:" +
                "\n  ID: " + getIdLokalizacji() +
                "\n  Nazwa: " + getNazwaLokalizacji() +
                "\n  Kraj: " + getKraj() +
                "\n  Miasto: " + getMiasto();
    }
}
