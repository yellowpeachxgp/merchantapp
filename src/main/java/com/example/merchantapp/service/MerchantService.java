package com.example.merchantapp.service;

import com.example.merchantapp.model.Merchant;
import com.example.merchantapp.model.Category;
import com.example.merchantapp.repository.MerchantRepository;
import com.example.merchantapp.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MerchantService {
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    public List<Merchant> getAllMerchantsByCategory(Long categoryId) {
        return merchantRepository.findByCategoryId(categoryId);
    }

    public Optional<Merchant> getMerchantById(Long id) {
        return merchantRepository.findById(id);
    }

    public Merchant createMerchant(Merchant merchant) {
        // 确保关联的分类存在
        if (merchant.getCategory() != null && merchant.getCategory().getId() != null) {
            Category category = categoryRepository.findById(merchant.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            merchant.setCategory(category);
        }
        return merchantRepository.save(merchant);
    }

    public Merchant updateMerchant(Long id, Merchant merchantDetails) {
        return merchantRepository.findById(id).map(m -> {
            m.setName(merchantDetails.getName());
            if (merchantDetails.getCategory() != null && merchantDetails.getCategory().getId() != null) {
                Category category = categoryRepository.findById(merchantDetails.getCategory().getId())
                        .orElseThrow(() -> new RuntimeException("Category not found"));
                m.setCategory(category);
            }
            m.setAddress(merchantDetails.getAddress());
            m.setContact(merchantDetails.getContact());
            return merchantRepository.save(m);
        }).orElseThrow(() -> new RuntimeException("Merchant not found"));
    }

    public void deleteMerchant(Long id) {
        merchantRepository.deleteById(id);
    }
}
