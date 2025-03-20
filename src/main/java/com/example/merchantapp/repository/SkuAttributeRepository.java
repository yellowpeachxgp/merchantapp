package com.example.merchantapp.repository;

import com.example.merchantapp.model.SkuAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SkuAttributeRepository extends JpaRepository<SkuAttribute, Long> {
    // 根据SKU ID查询所有属性值
    List<SkuAttribute> findBySkuId(Long skuId);
}
