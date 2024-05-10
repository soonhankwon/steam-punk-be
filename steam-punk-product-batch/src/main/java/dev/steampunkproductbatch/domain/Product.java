package dev.steampunkproductbatch.domain;

import dev.steampunkproductbatch.dto.ProductRawInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    private String name;
    private Double price;
    @Column(columnDefinition = "TEXT")
    private String shortDescription;
    private String webSite;
    private String developer;

    public Product(String name, Double price, String shortDescription, String webSite, String developer) {
        this.name = name;
        this.price = price;
        this.shortDescription = shortDescription;
        this.webSite = webSite;
        this.developer = developer;
    }

    public Product(ProductRawInfo data) {
        this.name = data.name();
        this.price = data.price();
    }
}
