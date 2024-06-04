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

    @PostMapping("/add-product")
    public String processAddProductForm(@Valid @ModelAttribute("product") Product product, BindingResult result, @RequestParam("imageList") MultipartFile[] images, Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("selected", product.getCategory().getId());
            return "product/add-product";
        } else {
            productService.addProduct(product);
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        String imageUrl = saveImage(image);
                        Image productImage = new Image();
                        if (product.getImage().isEmpty()) {
                            productImage.setView(true);
                        }
                        productImage.setImage("/images/" + imageUrl);
                        productImage.setProduct(product);
                        product.getImages().add(productImage);
                        productService.addImage(productImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Handle image upload error
                    }
                }
            }

            return "redirect:/products";
        }
    }

    private String saveImage(MultipartFile image) throws IOException {
        File saveFile = new ClassPathResource("static/images").getFile();
        String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
        Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
        Files.copy(image.getInputStream(), path);
        return fileName;
    }

    @GetMapping("/edit-product/{id}")
    public String showFormEditProduct(@PathVariable int id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("selected", product.getCategory().getId());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("images", productService.getImageByProductId(id));
        return "product/edit-product";
    }

    @PostMapping("/edit-product")
    public String processEditProductForm(@Valid @ModelAttribute("product") Product product, BindingResult result, @RequestParam("imageList") MultipartFile[] images, Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("selected", product.getCategory().getId());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("images", productService.getImageByProductId(product.getId()));
            return "product/edit-product";
        } else {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        String imageUrl = saveImage(image);

                        // Create product image entity and associate it with the productl
                        Image productImage = new Image();
                        productImage.setImage("/images/" + imageUrl);
                        productImage.setProduct(product);
                        product.getImages().add(productImage);
                        productService.addImage(productImage);
                        product.setImages(productService.getProductById(product.getId()).getImages());
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Handle image upload error
                    }
                } else {
                    product.setImages(productService.getProductById(product.getId()).getImages());
                }
            }
            productService.saveProduct(product);
            return "redirect:/products";
        }
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


}
