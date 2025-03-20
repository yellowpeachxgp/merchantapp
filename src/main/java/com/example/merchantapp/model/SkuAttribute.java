package com.example.merchantapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sku_attributes")
public class SkuAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 所属 SKU，多对一
    @ManyToOne
    @JoinColumn(name = "sku_id")
    private Sku sku;

    // 属性定义，多对一
    @ManyToOne
    @JoinColumn(name = "attribute_id")
    private Attribute attribute;

    @Column(nullable = false)
    private String value;  // 属性值，例如"红色", "XL"
}
