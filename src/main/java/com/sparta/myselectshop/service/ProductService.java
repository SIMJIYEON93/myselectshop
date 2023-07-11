package com.sparta.myselectshop.service;


import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.*;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.repository.FolderRespository;
import com.sparta.myselectshop.repository.ProductFolderRepository;
import com.sparta.myselectshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final FolderRespository folderRespository;
    private final ProductFolderRepository productFolderRepository;

    public static final int MIN_MY_PRICE=100; //myprice는 100원 이상이여야 하는 제약조건


    public ProductResponseDto createProduct(ProductRequestDto requestDto, User user) {
        //받아온 requestDto를 우리가 저장할 entity객체로 만들어줘야해
        //이제 product넣어줄 때 user의 정보도 같이 넣어줘야해
        Product product = productRepository.save(new Product(requestDto, user));
        return new ProductResponseDto(product);
    }

    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductMypriceRequestDto requestDto) {
        int myprice = requestDto.getMyprice();
        if(myprice<MIN_MY_PRICE){
            throw new IllegalArgumentException("유효하지 않은 관심 가격입니다. 최소"+MIN_MY_PRICE+"이상으로 설정해 주세요.");
        }

        Product product = productRepository.findById(id).orElseThrow(()->
                new NullPointerException("해당 상품을 찾을 수 없습니다.")
                );
        product.update(requestDto);
        return new ProductResponseDto(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProducts(User user, int page, int size, String sortBy, boolean isAsc) {

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort  = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page,size,sort);

        UserRoleEnum userRoleEnum = user.getRole();

        Page<Product> productList;

        if(userRoleEnum==UserRoleEnum.USER){
            productList = productRepository.findAllByUser(user, pageable);
        }else{
            productList = productRepository.findAll(pageable);
        }

        return productList.map(ProductResponseDto::new);
    }

    @Transactional
    public void updateBySearch(Long id, ItemDto itemDto) {
        Product product =productRepository.findById(id).orElseThrow(()->
                new NullPointerException("해당 상품은 존재하지 않습니다.")
                );
        product.updateByItemDto(itemDto);

    }

    public List<ProductResponseDto> getAllProducts() {
        List<Product> productList = productRepository.findAll();
        List<ProductResponseDto> responseDtoList = new ArrayList<>();
        for (Product product : productList) {
            responseDtoList.add(new ProductResponseDto(product));
        }
        return responseDtoList;


    }

    public void addFolder(Long productId, Long folderId, User user) {
        Product product= productRepository.findById(productId).orElseThrow(
                ()-> new NullPointerException("해당 상품이 존재하지 않습니다.")
        );
        Folder folder = folderRespository.findById(folderId).orElseThrow(
                ()-> new NullPointerException("해당폴더가 존재하지 않습니다.")
        );

        if(!product.getUser().getId().equals(user.getId())
        || !folder.getUser().getId().equals(user.getId())){
            throw new IllegalArgumentException("회원님의 관심상품이 아니거나, 회원님의 폴더가 아닙니다.");
        }
        Optional<ProductFolder> overlapFolder = productFolderRepository.findByProductAndFolder(product, folder);
        if(overlapFolder.isPresent()){
            throw new IllegalArgumentException("중복된 폴더 입니다.");
        }
        productFolderRepository.save(new ProductFolder(product, folder));
    }
}



