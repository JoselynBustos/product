package com.example.products.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.products.Repository.CategoryRepository;
import com.example.products.Repository.ProductRepository;
import com.example.products.exception.ResourceNotFoundException;
import com.example.products.model.Category;
import com.example.products.model.Product;

@Service
public class ProductService {

    private final ProductRepository repo;

    private final CategoryRepository repoCategory;

    public ProductService(ProductRepository repo, CategoryRepository repoCategory) {
        this.repo = repo;
        this.repoCategory = repoCategory;
    }

    public List<Product> findAll() {
        return repo.findAll();

    }

    public Optional<Product> findById(Long id) {
        return repo.findById(id);
    }

    public Product save(Product product, Long id) {
        Category category = repoCategory.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found with id:" + id));
        product.setCategory(category);
        return repo.save(product);
    }

    public Optional<Product> update(Long id, Product product, Long idCategory) {

        Product productBd = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found with id:" + id));

        Category category = repoCategory.findById(idCategory).orElseThrow(
                () -> new ResourceNotFoundException("Category not found with id:" + idCategory));
        // Product productBd = optionalProduct.get();

        productBd.setName(product.getName() + "");
        productBd.setPrice((double) product.getPrice());
        productBd.setStock(product.getStock());
        productBd.setCategory(category);
        return Optional.of(repo.save(productBd));
    }

    public boolean delete(long id) {

        if (!repo.existsById(id)) {
            return false;

        }
        repo.deleteById(id);
        return true;
    }
}
