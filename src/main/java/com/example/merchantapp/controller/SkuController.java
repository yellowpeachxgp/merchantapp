package com.example.merchantapp.controller;

import com.example.merchantapp.model.Sku;
import com.example.merchantapp.model.Product;
import com.example.merchantapp.model.SkuAttribute;
import com.example.merchantapp.model.Attribute;
import com.example.merchantapp.repository.SkuRepository;
import com.example.merchantapp.repository.ProductRepository;
import com.example.merchantapp.repository.SkuAttributeRepository;
import com.example.merchantapp.repository.AttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products/{productId}/skus")
public class SkuController {

    @Autowired
    private SkuRepository skuRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private SkuAttributeRepository skuAttributeRepository;

    @GetMapping
    public ResponseEntity<List<Sku>> getSkusByProduct(@PathVariable Long productId) {
        if (!productRepository.existsById(productId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skuRepository.findByProductId(productId));
    }

   @GetMapping("/{id}")
    public ResponseEntity<Sku> getSkuById(@PathVariable Long productId, @PathVariable Long id) {
        return skuRepository.findById(id)
                .filter(sku -> sku.getProduct().getId().equals(productId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    static class SkuRequest {
        public BigDecimal price;
        public Integer stock;
        public String skuCode;
        public List<AttrValue> attributes;
    }

    static class AttrValue {
        public Long attributeId;
        public String value;
    }

    @PostMapping
    public ResponseEntity<Sku> createSku(@PathVariable Long productId, @RequestBody SkuRequest request) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Product product = optionalProduct.get();

        Sku sku = new Sku();
        sku.setProduct(product);
        sku.setPrice(request.price);
        sku.setStock(request.stock);
        sku.setSkuCode(request.skuCode);
        Sku savedSku = skuRepository.save(sku);

        if (request.attributes != null) {
            for (AttrValue attrVal : request.attributes) {
                attributeRepository.findById(attrVal.attributeId).ifPresent(attr -> {
                    SkuAttribute skuAttr = new SkuAttribute();
                    skuAttr.setSku(savedSku);
                    skuAttr.setAttribute(attr);
                    skuAttr.setValue(attrVal.value);
                    skuAttributeRepository.save(skuAttr);
                });
            }
        }
        return ResponseEntity.ok(savedSku);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sku> updateSku(@PathVariable Long productId,
                                         @PathVariable Long id,
                                         @RequestBody SkuRequest request) {
        if (!productRepository.existsById(productId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Sku> optionalSku = skuRepository.findById(id);
        if (optionalSku.isEmpty() || !optionalSku.get().getProduct().getId().equals(productId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Sku sku = optionalSku.get();
        if (request.price != null) sku.setPrice(request.price);
        if (request.stock != null) sku.setStock(request.stock);
        if (request.skuCode != null) sku.setSkuCode(request.skuCode);
        return ResponseEntity.ok(skuRepository.save(sku));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSku(@PathVariable Long productId, @PathVariable Long id) {
        if (!skuRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        skuRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
