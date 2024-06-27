package com.example.webbanhang.service;

import com.example.webbanhang.model.Product;
import com.example.webbanhang.repository.ProductRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    @Value("${upload.path}")
    private String uploadPath;

    // Retrieve all products from the database
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Retrieve a product by its id
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Add a new product to the database
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(@NotNull Product product, MultipartFile imageFile) throws IOException {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " +
                        product.getId() + " does not exist."));

        // Update fields
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());

        if (imageFile != null && !imageFile.isEmpty()) {
            String filename = imageFile.getOriginalFilename();
            Path path = Paths.get(uploadPath, filename);
            Files.write(path, imageFile.getBytes());

            // Assign the new image filename to the existing product
            existingProduct.setImage(filename);
        }

        return productRepository.save(existingProduct);
    }



    // Delete a product by its id
    public void deleteProductById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (!productOptional.isPresent()) {
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        Product product = productOptional.get();

        // Delete image file
        Path imagePath = Paths.get(uploadPath, product.getImage());
        try {
            if (Files.exists(imagePath)) {
                Files.delete(imagePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error accordingly
        }

        // Delete product from database
        productRepository.deleteById(id);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByKeyword(keyword);
    }

    public List<Product> searchProductsByKeywordAndCategory(String keyword, Long categoryId) {
        if (categoryId != null) {
            return productRepository.findByKeywordAndCategory(keyword, categoryId);
        } else {
            return productRepository.findByKeyword(keyword);
        }
    }
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
    public List<Product> searchProductsByKeywordAndCategoryName(String keyword, String categoryName) {
        return productRepository.findByKeywordAndCategoryName(keyword, categoryName);
    }
}
