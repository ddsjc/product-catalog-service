package sukhov.danila.in.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import sukhov.danila.config.UserContext;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.domain.services.ProductService;
import sukhov.danila.domain.services.UserService;
import sukhov.danila.dtos.CreateProductDTO;
import sukhov.danila.dtos.ProductDTO;
import sukhov.danila.dtos.UpdateProductDTO;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final UserService userService;
    public static final String FETCH_PRODUCTS = "/api/products/{id}";
    public static final String CREATE_PRODUCT = "/api/products/";
    public static final String GET_PRODUCT_BY_ID = "/api/products/{id}";
    public static final String DELETE_PRODUCT = "/api/products/{id}";

    @PostMapping(CREATE_PRODUCT)
    public ProductDTO createProductController(@RequestBody CreateProductDTO createProductDTO){
        UserEntity user = UserContext.getCurrentUser();
        return productService.createProduct(createProductDTO, user );
    }

    @PatchMapping(FETCH_PRODUCTS)
    public ProductDTO fetchProductController(@RequestParam(name = "product_id" ) Long productId,@PathVariable (name = "id") Long userId,  @RequestBody UpdateProductDTO updateProductDTO){
        UserEntity user = userService.findUserEntityById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return productService.updateProduct(productId, updateProductDTO, user);
    }

    @GetMapping(GET_PRODUCT_BY_ID)
    public ProductDTO getProductController(@PathVariable(name = "product_id") Long productId){
        return productService.findById(productId);
    }
    @DeleteMapping(DELETE_PRODUCT)
    public void deleteProductController(@RequestParam(name = "product_id") Long productId, @PathVariable(name = "id") Long userId){
        UserEntity user = userService.findUserEntityById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        productService.deleteProduct(productId, user);
    }

}
