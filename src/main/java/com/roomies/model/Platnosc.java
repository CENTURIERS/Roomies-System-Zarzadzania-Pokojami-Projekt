package com.roomies.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Platnosci")
public class Platnosc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_platnosci")
    private Integer id_platnosci;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "kwota", nullable = false, precision = 10, scale = 2)
    private BigDecimal kwota;

    @OneToMany(mappedBy = "platnosc", fetch = FetchType.LAZY)
    private List<Wynajem> wynajmy = new ArrayList<>();

    public Platnosc() {

    }

    public Platnosc(String status, BigDecimal kwota) {
        this.status = status;
        this.kwota = kwota;
    }

    public Integer getId_platnosci() {
        return id_platnosci;
    }

    public void setId_platnosci(Integer id_platnosci) {
        this.id_platnosci = id_platnosci;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getKwota() {
        return kwota;
    }

    public void setKwota(BigDecimal kwota) {
        this.kwota = kwota;
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
        Platnosc that = (Platnosc) o;
        if(id_platnosci == null){
            return false;
        }
        return Objects.equals(id_platnosci, that.id_platnosci);
    }

    @Override public int hashCode() {
        return Objects.hash(id_platnosci);
    }

    @Override
    public String toString() {
        return "\nPłatność:" +
                "\n  ID: " + getId_platnosci() +
                "\n  Status: " + getStatus() +
                "\n  Kwota: " + getKwota();
    }
}
