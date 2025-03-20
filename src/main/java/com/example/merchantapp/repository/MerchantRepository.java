package com.example.merchantapp.repository;

import com.example.merchantapp.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    // 根据分类ID查询商家列表
    List<Merchant> findByCategoryId(Long categoryId);
}
