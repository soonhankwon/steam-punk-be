package dev.steampunkproductbatch.domain;

import dev.steampunkproductbatch.enumtype.DiscountPolicy;
import dev.steampunkproductbatch.enumtype.ProductState;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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
    private String headerImage;
    private String webSite;
    private String developer;
    @Enumerated(EnumType.STRING)
    private DiscountPolicy discountPolicy;
    @Enumerated(EnumType.STRING)
    private ProductState productState;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private final List<ProductCategory> productCategories = new ArrayList<>();

    public Product(String name, Double price, String shortDescription, String headerImage, String webSite,
                   String developer) {
        this.name = name;
        this.price = price;
        this.shortDescription = shortDescription;
        this.headerImage = headerImage;
        this.webSite = webSite;
        this.developer = developer;
        this.discountPolicy = DiscountPolicy.REGULAR;
        this.productState = ProductState.REGULAR;
    }
}
