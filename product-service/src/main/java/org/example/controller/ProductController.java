package org.example.controller;

import com.example.productservice.dto.ProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@RequestBody ProductRequest productRequest) {
        return productService.createProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }
    
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
    
    @GetMapping("/category/{category}")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getProductsByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category);
    }
    
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        return productService.updateProduct(id, productRequest);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
    
    // Endpoint di test per simulare un errore (per testare il Circuit Breaker)
    @GetMapping("/test-error")
    public ResponseEntity<String> testError(@RequestParam(defaultValue = "false") boolean throwError) {
        if (throwError) {
            throw new RuntimeException("Test error for circuit breaker");
        }
        return ResponseEntity.ok("Test endpoint working normally");
    }
}