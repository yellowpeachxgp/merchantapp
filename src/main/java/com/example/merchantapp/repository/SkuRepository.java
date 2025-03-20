package com.example.merchantapp.repository;

import com.example.merchantapp.model.Sku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SkuRepository extends JpaRepository<Sku, Long> {
    // 根据产品ID查询所有SKU
    List<Sku> findByProductId(Long productId);
}
