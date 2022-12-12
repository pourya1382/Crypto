package com.example.crypto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Entity(name = "cryptocurrencies")
@Table
@Getter
@Setter
public class Cryptocurrencie {
    @Id
    @SequenceGenerator(name = "cryptocurrencie_sequence",
            sequenceName = "cryptocurrencie_sequence",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "cryptocurrencie_sequence"
    )
    private Long id;
    private String symbol;
    private String name;
    @Column(name = "price_irt")
    private float priceIRT;
    @Column(name = "price_usdt")

    private float priceUSDT;

    private float lastDayChange;

    public Cryptocurrencie() {
    }

    public Cryptocurrencie(Long id, String symbol, String name, float priceIRT, float priceUSDT, float lastDayChange) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.priceIRT = priceIRT;
        this.priceUSDT = priceUSDT;
        this.lastDayChange = lastDayChange;
    }

    public Cryptocurrencie(String symbol, String name, float priceIRT, float priceUSDT, float lastDayChange) {
        this.symbol = symbol;
        this.name = name;
        this.priceIRT = priceIRT;
        this.priceUSDT = priceUSDT;
        this.lastDayChange = lastDayChange;
    }

    @Override
    public String toString() {
        return "Cryptocurrencie{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", priceIRT=" + priceIRT +
                ", priceUSDT=" + priceUSDT +
                ", lastDayChange=" + lastDayChange +
                '}';
    }


}

