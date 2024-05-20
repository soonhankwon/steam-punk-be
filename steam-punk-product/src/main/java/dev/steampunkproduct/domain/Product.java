package dev.steampunkproduct.domain;

import dev.steampunkproduct.dto.request.ProductAddRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(columnDefinition = "TEXT")
    private String shortDescription;

    @Column(name = "header_image")
    private String headerImage;

    @Column(name = "web_site")
    private String webSite;

    @Column(name = "developer")
    private String developer;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_state")
    private ProductState productState;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_policy")
    private ProductDiscountPolicy productDiscountPolicy;

    private Product(String name, Double price, String shortDescription, String headerImage, String webSite,
                    String developer,
                    ProductState productState, ProductDiscountPolicy productDiscountPolicy) {
        this.name = name;
        this.price = price;
        this.shortDescription = shortDescription;
        this.headerImage = headerImage;
        this.webSite = webSite;
        this.developer = developer;
        this.productState = productState;
        this.productDiscountPolicy = productDiscountPolicy;
    }

    public static Product from(ProductAddRequest request) {
        return new Product(
                request.name(),
                request.price(),
                request.shortDescription(),
                request.headerImage(),
                request.webSite(),
                request.developer(),
                request.productState(),
                request.productDiscountPolicy()
        );
    }
}
