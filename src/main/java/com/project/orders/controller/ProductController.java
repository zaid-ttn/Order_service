package com.project.orders.controller;

import com.project.orders.dtos.ProductDTO;
import com.project.orders.model.Product;
import com.project.orders.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("/elastic/fetch")
    public ResponseEntity<List<Product>> fetchProduct() {
        List<Product> products = productService.fetchProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("list/products")
    public ResponseEntity<List<Product>> fetchProducts(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(defaultValue = "asc") String sortDirection){
        return ResponseEntity.ok(productService.fetchListProducts(page, size, sortDirection));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>>searchProducts(@RequestParam String name){
        List<Product>result=productService.fuzzySearchProducts(name);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/search/inbuilt")
    public List<Product> fuzzySearchProducts(@RequestParam String name) throws IOException {
        return productService.fuzzySearch(name);
    }
}