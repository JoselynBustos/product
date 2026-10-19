package com.example.products.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.products.dto.ProductRequest;
import com.example.products.dto.ProductResponse;
import com.example.products.model.Product;
import com.example.products.service.ProductService;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;





@RestController 
@RequestMapping ("/products")
public class ProductController {


    private final ProductService service;

public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity<List<ProductResponse>> findAll() {
       List<ProductResponse> list= new ArrayList<>();
       for(Product product: service.findAll()){
        list.add(toResponse(product));
       }  
        return ResponseEntity.ok(list); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        Optional<Product> product = service.findById(id);
        if (product.isEmpty()) {
             ResponseEntity.notFound().build();
        } 
    return ResponseEntity.ok(toResponse(product.get()));
        
    }

    
    @PostMapping 
    public ResponseEntity<ProductResponse> save(@Valid @RequestBody ProductRequest product) {
        Product ProductSaved= service.save(toEntity(product), product.categoryId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(ProductSaved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @RequestBody ProductRequest productUpdate) {
        Optional<Product> product1 = service.update(id, toEntity(productUpdate), productUpdate.categoryId()
    );

        if (product1.isPresent()) {
       return ResponseEntity.ok(toResponse(product1.get()));
        }
        
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = service.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        } 
            return ResponseEntity.noContent().build();
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getStock(),"",0L
        );
    }

    private Product toEntity(ProductRequest product) {
        Product productEntity = new Product();
        productEntity.setName(product.name());
        productEntity.setPrice(product.price());
        productEntity.setStock(product.stock());
        return productEntity;
    }
    
    


}
