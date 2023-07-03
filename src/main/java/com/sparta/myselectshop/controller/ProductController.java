package com.sparta.myselectshop.controller;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.service.ProductService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;


    //관심상품 등록하기 API
    @PostMapping("/products")
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto requestDto){ //넘어오는 httpbody데이터 받을거
        return productService.createProduct(requestDto);
    }

    //관심상품 희망 최저가(update)등록하기 API
    @PutMapping("/products/{id}") //pathvariavle방식
    public ProductResponseDto updateaProducts(@PathVariable Long id, @RequestBody ProductMypriceRequestDto requestDto){
        //RequestBody에서 희망데이터(ProductMypriceRequestDto) 받아
        return productService.updateProduct(id,requestDto);
    }

    @GetMapping("/products")
    public List<ProductResponseDto> getProduct(){

        return productService.getProducts();
    }
}
