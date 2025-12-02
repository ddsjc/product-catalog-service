package sukhov.danila.in.web;
import sukhov.danila.in.web.controllers.BrandController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sukhov.danila.aspect.AuditAspect;
import sukhov.danila.domain.services.*;


@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
public class TestWebConfig implements WebMvcConfigurer {
    @Bean
    public MappingJackson2HttpMessageConverter jackson2Converter() {
        return new MappingJackson2HttpMessageConverter(new ObjectMapper());
    }

    @Bean
    public BrandController brandController() {
        return new BrandController(
                brandService(),
                userService()
        );
    }

    @Bean
    public BrandService brandService() {
        return Mockito.mock(BrandService.class);
    }

    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }

}
