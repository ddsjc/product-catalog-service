package sukhov.danila.domain.services.helpers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import sukhov.danila.domain.entities.BrandEntity;
import sukhov.danila.domain.entities.CategoryEntity;
import sukhov.danila.domain.entities.ProductEntity;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.domain.exceptions.AlreadyExistsException;
import sukhov.danila.domain.exceptions.NotFoundException;
import sukhov.danila.out.persistence.jdbc.BrandRepositoryImpl;
import sukhov.danila.out.persistence.jdbc.CategoryRepositoryImpl;
import sukhov.danila.out.persistence.jdbc.ProductRepositoryImpl;
import sukhov.danila.out.persistence.jdbc.UserRepositoryImpl;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ControllerHelper {
    BrandRepositoryImpl brandRepository;
    CategoryRepositoryImpl categoryRepository;
    ProductRepositoryImpl productRepository;
    UserRepositoryImpl userRepository;

    public BrandEntity getBrandByIdOrThrowException(Long brandId){
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new NotFoundException(String.format("Бренд по id не найден, id : " , brandId))
                );
    }

    public BrandEntity getBrandByNameOrThrowException(String brandName){
        return brandRepository.findByName(brandName)
                .orElseThrow(() -> new NotFoundException(String.format("Бренд по id не найден, введеное название : " , brandName))
                );
    }

    public UserEntity getUserByIdOrThrowException(Long userId){
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователь по id не найден, id : " , userId))
        );
    }
    public UserEntity getUserByUsernameOrThrowException(String userName){
        return userRepository.findByName(userName).orElseThrow(() -> new NotFoundException(String.format("Пользователь по id не найден, userName : " , userName))
        );
    }
    public CategoryEntity getCategoryByIdOrThrowException(Long categoryId){
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException(String.format("Категория по id не найдена, id : " , categoryId))
        );
    }
    public CategoryEntity getCategoryByNameOrThrowException(String categoryName){
        return categoryRepository.findByName(categoryName).orElseThrow(() -> new NotFoundException(String.format("Категория по названию не найдена, название : " , categoryName))
        );
    }
    public ProductEntity getProductByIdOrThrowException(Long id){
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Товар по id не найден, id : " , id))
        );
    }

    public ProductEntity getProductByNameOrThrowException(String productName){
        return productRepository.findByName(productName).orElseThrow(() -> new NotFoundException(String.format("Товар по названию не найден, название : " , productName))
        );
    }

    public void userNotExistsOrThrowException(String username){
        if(userRepository.findByName(username).isPresent()){
            throw new AlreadyExistsException("Пользователь " + username + " уже существует");
        }
    }
    public void brandNotExistsOrThrowException(String brandName){
        if(brandRepository.findByName(brandName).isPresent()){
            throw new AlreadyExistsException("Бренд  " + brandName + " уже существует");
        }
    }
    public void categoryNotExistsOrThrowException(String categoryName){
        if(categoryRepository.findByName(categoryName).isPresent()){
            throw new AlreadyExistsException("Категория  " + categoryName + " уже существует");
        }
    }
}
