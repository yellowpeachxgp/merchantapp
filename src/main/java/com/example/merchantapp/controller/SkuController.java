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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

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
        // 检查产品是否存在
        if (!productRepository.existsById(productId)) {
            return ResponseEntity.notFound().build();
        }
        List<Sku> skus = skuRepository.findByProductId(productId);
        return ResponseEntity.ok(skus);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sku> getSkuById(@PathVariable Long productId, @PathVariable Long id) {
        return skuRepository.findById(id)
                .filter(sku -> sku.getProduct().getId().equals(productId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 创建SKU请求DTO
    static class SkuRequest {
        public BigDecimal price;
        public Integer stock;
        public String skuCode;
        public List<AttrValue> attributes;
    }
    // 属性值DTO
    static class AttrValue {
        public Long attributeId;
        public String value;
    }

    @PostMapping
    public ResponseEntity<Sku> createSku(@PathVariable Long productId, @RequestBody SkuRequest request) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().build();
        }
        // 创建并保存SKU
        Sku sku = new Sku();
        sku.setProduct(product);
        sku.setPrice(request.price);
        sku.setStock(request.stock);
        sku.setSkuCode(request.skuCode);
        Sku savedSku = skuRepository.save(sku);
        // 保存SKU的属性值
        if (request.attributes != null) {
            for (AttrValue attrVal : request.attributes) {
                Attribute attr = attributeRepository.findById(attrVal.attributeId).orElse(null);
                if (attr != null) {
                    SkuAttribute skuAttr = new SkuAttribute();
                    skuAttr.setSku(savedSku);
                    skuAttr.setAttribute(attr);
                    skuAttr.setValue(attrVal.value);
                    skuAttributeRepository.save(skuAttr);
                }
            }
        }
        return ResponseEntity.ok(savedSku);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sku> updateSku(@PathVariable Long productId, @PathVariable Long id, @RequestBody SkuRequest request) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().build();
        }
        return skuRepository.findById(id).map(sku -> {
            if (!sku.getProduct().getId().equals(productId)) {
                // SKU不属于该产品
                return ResponseEntity.badRequest().build();
            }
            if (request.price != null) sku.setPrice(request.price);
            if (request.stock != null) sku.setStock(request.stock);
            if (request.skuCode != null) sku.setSkuCode(request.skuCode);
            Sku updatedSku = skuRepository.save(sku);
            // 此处简化处理：SKU属性更新可根据需要实现（例如先删除旧属性再添加新属性）
            return ResponseEntity.ok(updatedSku);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSku(@PathVariable Long productId, @PathVariable Long id) {
        return skuRepository.findById(id).map(sku -> {
            if (!sku.getProduct().getId().equals(productId)) {
                return ResponseEntity.badRequest().build();
            }
            // 删除SKU关联的属性记录
            List<SkuAttribute> attrs = skuAttributeRepository.findBySkuId(id);
            for (SkuAttribute sa : attrs) {
                skuAttributeRepository.delete(sa);
            }
            // 删除SKU本身
            skuRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
