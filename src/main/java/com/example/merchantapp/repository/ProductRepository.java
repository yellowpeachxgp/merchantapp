package com.example.merchantapp.repository;

import com.example.merchantapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 根据商家ID查询商品列表
    List<Product> findByMerchantId(Long merchantId);
}
