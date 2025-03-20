package com.example.merchantapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // 所属商家，多对一
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    // 所属品牌，多对一
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    private String description;
}
