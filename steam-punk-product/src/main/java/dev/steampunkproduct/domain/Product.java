package dev.steampunkproduct.domain;

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
    @Column(name = "discount_state")
    private ProductDiscountState productDiscountState;
}
