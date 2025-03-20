package com.example.merchantapp.controller;

import com.example.merchantapp.model.Merchant;
import com.example.merchantapp.model.Category;
import com.example.merchantapp.service.MerchantService;
import com.example.merchantapp.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public List<Merchant> getAllMerchants(@RequestParam(required = false) Long categoryId) {
        // 可选按分类过滤
        if (categoryId != null) {
            return merchantService.getAllMerchantsByCategory(categoryId);
        }
        return merchantService.getAllMerchants();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Merchant> getMerchantById(@PathVariable Long id) {
        return merchantService.getMerchantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Merchant> createMerchant(@RequestBody Merchant merchant) {
        try {
            Merchant created = merchantService.createMerchant(merchant);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            // 比如指定的分类不存在
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Merchant> updateMerchant(@PathVariable Long id, @RequestBody Merchant merchant) {
        try {
            Merchant updated = merchantService.updateMerchant(id, merchant);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchant(@PathVariable Long id) {
        merchantService.deleteMerchant(id);
        return ResponseEntity.noContent().build();
    }
}
