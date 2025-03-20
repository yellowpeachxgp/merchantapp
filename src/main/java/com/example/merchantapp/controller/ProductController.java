package com.example.merchantapp.controller;

import com.example.merchantapp.model.Product;
import com.example.merchantapp.model.Merchant;
import com.example.merchantapp.model.Brand;
import com.example.merchantapp.repository.ProductRepository;
import com.example.merchantapp.repository.MerchantRepository;
import com.example.merchantapp.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private BrandRepository brandRepository;

    @GetMapping
    public List<Product> getAllProducts(@RequestParam(required = false) Long merchantId) {
        // 可选按商家过滤
        if (merchantId != null) {
            return productRepository.findByMerchantId(merchantId);
        }
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        // 确认Merchant和Brand的有效性
        if (product.getMerchant() != null && product.getMerchant().getId() != null) {
            product.setMerchant(merchantRepository.findById(product.getMerchant().getId()).orElse(null));
        }
        if (product.getBrand() != null && product.getBrand().getId() != null) {
            product.setBrand(brandRepository.findById(product.getBrand().getId()).orElse(null));
        }
        if (product.getMerchant() == null || product.getBrand() == null) {
            // 如果未提供有效的商家或品牌
            return ResponseEntity.badRequest().build();
        }
        Product created = productRepository.save(product);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return productRepository.findById(id).map(product -> {
            product.setName(productDetails.getName());
            if (productDetails.getDescription() != null) {
                product.setDescription(productDetails.getDescription());
            }
            if (productDetails.getMerchant() != null && productDetails.getMerchant().getId() != null) {
                Merchant merchant = merchantRepository.findById(productDetails.getMerchant().getId()).orElse(null);
                product.setMerchant(merchant);
            }
            if (productDetails.getBrand() != null && productDetails.getBrand().getId() != null) {
                Brand brand = brandRepository.findById(productDetails.getBrand().getId()).orElse(null);
                product.setBrand(brand);
            }
            Product updated = productRepository.save(product);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
