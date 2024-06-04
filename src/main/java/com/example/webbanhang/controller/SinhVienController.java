package com.example.webbanhang.controller;

import com.example.webbanhang.model.SinhVien;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class SinhVienController {
    private static final String UPLOADED_FOLDER = "src/main/resources/static/images/";
    @GetMapping("/sinhvien")
    public String showForm(Model model) {
        model.addAttribute("sinhVien", new SinhVien());
        return "sinhvien/form-sinhvien";
    }
    @PostMapping("/sinhvien")
    public String submitForm(@Valid SinhVien sinhVien, BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "sinhvien/form-sinhvien";
        }

        MultipartFile file = sinhVien.getImage();
        String imagePath = null;
        if (file != null && !file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                // Ensure unique filename to prevent overwriting existing files
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path path = Paths.get(UPLOADED_FOLDER + fileName);
                Files.write(path, bytes);
                // Update imagePath with the correct URL format
                imagePath = "/images/" + fileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        model.addAttribute("sinhVien", sinhVien);
        model.addAttribute("message", "Sinh viên đã được thêm thành công!");
        model.addAttribute("imagePath", imagePath);
        return "sinhvien/result-sinhvien";
    }
}
