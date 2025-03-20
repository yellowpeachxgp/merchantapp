package com.example.merchantapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "merchants")
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // 商家所属行业分类，多对一关系
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String address;  // 地址
    private String contact;  // 联系方式（电话或邮箱）
}
