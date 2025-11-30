package sukhov.danila.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@ComponentScan(
        basePackages = "sukhov.danila",
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "sukhov\\.danila\\.Application")
)
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public DataSource dataSource() {
        return DataSourceConfig.getDataSource();
    }

    @Bean
    public MappingJackson2HttpMessageConverter jackson2Converter() {
        return new MappingJackson2HttpMessageConverter(new ObjectMapper());
    }

    @Bean
    public sukhov.danila.out.persistence.jdbc.UserRepositoryImpl userRepository() {
        return new sukhov.danila.out.persistence.jdbc.UserRepositoryImpl(dataSource());
    }

    @Bean
    public sukhov.danila.domain.services.AuthService authService() {
        return new sukhov.danila.domain.services.AuthService(userRepository());
    }

    @Bean
    public sukhov.danila.domain.services.ProductService productService() {
        return new sukhov.danila.domain.services.ProductService(
                new sukhov.danila.out.persistence.jdbc.ProductRepositoryImpl(dataSource()),
                new sukhov.danila.out.persistence.jdbc.BrandRepositoryImpl(dataSource()),
                new sukhov.danila.out.persistence.jdbc.CategoryRepositoryImpl(dataSource())
        );
    }

    @Bean
    public sukhov.danila.aspect.AuditAspect auditAspect() {
        return new sukhov.danila.aspect.AuditAspect();
    }
}
