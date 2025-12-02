package sukhov.danila.in.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.domain.services.BrandService;
import sukhov.danila.domain.services.UserService;
import sukhov.danila.dtos.BrandDTO;


@RestController
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;
    private  final UserService userService;
    public static final String CREATE_BRAND = "/api/brands/{id}";

    @PostMapping(CREATE_BRAND)
    public BrandDTO createBrandController(@RequestParam(value = "brand_name") String brandName, @PathVariable (name = "id") Long userId){
        UserEntity user = userService.findUserEntityById(userId).orElseThrow(() -> new RuntimeException("Пользователь не найден")); //понятно, что эт тупость полная, но пока секьюрити нет - такая "заглушка"
        return brandService.createBrand(brandName, user);
    }

}
