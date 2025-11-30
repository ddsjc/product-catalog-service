package sukhov.danila.in.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import sukhov.danila.config.UserContext;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.domain.services.BrandService;
import sukhov.danila.domain.services.UserService;
import sukhov.danila.dtos.BrandDTO;
import sukhov.danila.dtos.UserDTO;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;
    private  final UserService userService;
    public static final String CREATE_BRAND = "/api/brands";

   /* @PostMapping(CREATE_BRAND)
    public BrandDTO createBrandController(@RequestParam(value = "brand_name") String brandName, @PathVariable (name = "user_id") Long userId){
        UserDTO user = userService.findUserById(userId);
        return brandService.createBrand(brandName, user);
    }*/

}
