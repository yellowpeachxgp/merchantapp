package com.example.merchantapp.controller;

import com.example.merchantapp.model.Merchant;
import com.example.merchantapp.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 商家管理控制器
 * 提供商家信息的增删改查
 */
@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    @Autowired
    private MerchantRepository merchantRepository;

    @GetMapping
    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    @PostMapping
    public Merchant createMerchant(@RequestBody Merchant merchant) {
        return merchantRepository.save(merchant);
    }

    @GetMapping("/{id}")
    public Optional<Merchant> getMerchantById(@PathVariable Long id) {
        return merchantRepository.findById(id);
    }

    @PutMapping("/{id}")
    public Merchant updateMerchant(@PathVariable Long id, @RequestBody Merchant merchant) {
        merchant.setId(id);
        return merchantRepository.save(merchant);
    }

    @DeleteMapping("/{id}")
    public void deleteMerchant(@PathVariable Long id) {
        merchantRepository.deleteById(id);
    }
}
