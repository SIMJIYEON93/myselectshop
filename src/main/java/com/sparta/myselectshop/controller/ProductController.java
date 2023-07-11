package com.sparta.myselectshop.controller;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.security.UserDetailsImpl;
import com.sparta.myselectshop.service.ProductService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;


    //관심상품 등록하기 API
    @PostMapping("/products")
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        //관심상품 조회하는 데 회원정보 넘겨줘야해 -> AuthenticationPrincipal(회원정보 넘겨주는 방법)
        //넘어오는 httpbody데이터 받을거
        return productService.createProduct(requestDto,userDetails.getUser());
    }

    //관심상품 희망 최저가(update)등록하기 API
    @PutMapping("/productzs/{id}") //pathvariavle방식
    public ProductResponseDto updateaProducts(@PathVariable Long id, @RequestBody ProductMypriceRequestDto requestDto){
        //RequestBody에서 희망데이터(ProductMypriceRequestDto) 받아
        return productService.updateProduct(id,requestDto);
    }

    @GetMapping("/products")
    public Page<ProductResponseDto> getProduct(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails){//회원별로 조회->AuthenticationPrincipal

        return productService.getProducts(userDetails.getUser(),
                page-1,size, sortBy, isAsc);
    }

}

