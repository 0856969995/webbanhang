package com.example.webbanhang.controller;

import com.example.webbanhang.model.Product;
import com.example.webbanhang.service.CategoryService;
import com.example.webbanhang.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products/product-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult result,
                             @RequestParam("images") MultipartFile imageFile, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/add-product";
        }
        try {
            if (!imageFile.isEmpty()) {
                String filename = imageFile.getOriginalFilename();
                Path path = Paths.get(uploadPath + filename);
                Files.write(path, imageFile.getBytes());
                product.setImage(filename);
            }
            productService.addProduct(product);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/add-product";
        }
        return "redirect:/products";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/update-product";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product,
                                BindingResult result, @RequestParam("images") MultipartFile imageFile, Model model) {
        if (result.hasErrors()) {
            product.setId(id);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/update-product";
        }
        try {
            if (!imageFile.isEmpty()) {
                String filename = imageFile.getOriginalFilename();
                Path path = Paths.get(uploadPath + filename);
                Files.write(path, imageFile.getBytes());
                product.setImage(filename);
            }
            productService.updateProduct(product);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/update-product";
        }
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam("keyword") String keyword,
                                @RequestParam(value = "category", required = false) Long categoryId,
                                @RequestParam(value = "categoryName", required = false) String categoryName,
                                Model model) {
        List<Product> searchResults;
        if (categoryId != null) {
            searchResults = productService.searchProductsByKeywordAndCategory(keyword, categoryId);
        } else if (categoryName != null && !categoryName.isEmpty()) {
            searchResults = productService.searchProductsByKeywordAndCategoryName(keyword, categoryName);
        } else {
            searchResults = productService.searchProducts(keyword);
        }
        model.addAttribute("products", searchResults);
        return "products/product-list";
    }
}
