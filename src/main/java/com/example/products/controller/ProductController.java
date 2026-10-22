package com.example.products.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import com.example.products.dto.DiscountRequestDTO;
import com.example.products.dto.ProductRequest;
import com.example.products.dto.ProductResponse;
import com.example.products.exception.OutOfStockException;
import com.example.products.exception.ResourceNotFoundException;
import com.example.products.model.Product;
import com.example.products.service.ProductService;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
       Product product = service.findById(id).orElseThrow(
        ()->
        new ResourceAccessException("Prodducto no encontrado" +id)
       );
       
    return ResponseEntity.ok(toResponse(product));
        
    }

    
    @PostMapping 
    public ResponseEntity<ProductResponse> save(@Valid @RequestBody ProductRequest product) {
        Product ProductSaved= service.save(toEntity(product), product.categoryId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(ProductSaved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @RequestBody ProductRequest productUpdate) {
        Product product1 = service.update(
            id,
             toEntity(productUpdate),
              productUpdate.categoryId());
        
        return ResponseEntity.ok(toResponse(product1));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = service.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        } 
            return ResponseEntity.noContent().build();
    }

    @PatchMapping ("/{id}")
    public ResponseEntity<ProductResponse> updatePrice(@PathVariable Long id, @RequestBody DiscountRequestDTO request){

    Product product = service.findById(id).orElseThrow(
        ()-> new ResourceNotFoundException("Product not found with id:" +id)
    );

    product.getStock();
    return ResponseEntity.ok(toResponse(product));
}
@PatchMapping("/{id}/discount")
    public ResponseEntity<ProductResponse> applyDiscount(
            @PathVariable Long id, 
            @Valid @RequestBody DiscountRequestDTO request) {

        
        Product product = service.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Producto no encontrado con id: " + id)
        );

     
        if (product.getStock()<= 0) {
            throw new OutOfStockException("No se puede aplicar descuento a un producto sin stock");
        }

        double newPrice = product.getPrice() * (1 - (request.getPercentage() / 100.0));
        product.setPrice(newPrice);

    
        Product updatedProduct = service.save(product, product.getCategory() != null ? product.getCategory().getId() : null);

      
        return ResponseEntity.ok(toResponse(updatedProduct));
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
