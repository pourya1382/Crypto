package com.example.crypto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity(name = "cryptocurrencies")
@Table
@Getter
@Setter
public class Crypto {
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

    private String fiat;
    @Column(name = "price_irt")
    private float price;

    @Column(name = "last_day_change")

    private float lastDayChange;
    public Crypto() {
    }

    public Crypto(String symbol, String name, String fiat, float price, float lastDayChange) {
        this.symbol = symbol;
        this.name = name;
        this.fiat = fiat;
        this.price = price;
        this.lastDayChange = lastDayChange;
    }

    public Crypto(Long id, String symbol, String name, String fiat, float price, float lastDayChange) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.fiat = fiat;
        this.price = price;
        this.lastDayChange = lastDayChange;
    }

    @Override
    public String toString() {
        return "Crypto{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", fiat='" + fiat + '\'' +
                ", price=" + price +
                ", lastDayChange=" + lastDayChange +
                '}';
    }
}

