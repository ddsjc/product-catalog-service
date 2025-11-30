package sukhov.danila.in.web;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sukhov.danila.config.CurrentUserProvider;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.domain.services.ProductService;
import sukhov.danila.dtos.CreateProductDTO;
import sukhov.danila.dtos.ProductDTO;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ProductServletTest {

    private ProductServlet servlet;
    private ProductService mockService;
    private CurrentUserProvider mockUserProvider;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockService = mock(ProductService.class);
        mockUserProvider = mock(CurrentUserProvider.class);
        when(mockUserProvider.getCurrentUser()).thenReturn(new UserEntity(1L, "test", "hash", "SELLER"));

        objectMapper = new ObjectMapper();

        servlet = new ProductServlet();
        servlet.productService = mockService;
        servlet.currentUserProvider = mockUserProvider;
        servlet.objectMapper = objectMapper;
    }

    @AfterEach
    void tearDown() {
        reset(mockService, mockUserProvider);
    }
    @Test
    void shouldCreateProductSuccessfully() throws Exception {
        String json = "{\"name\":\"Laptop\",\"categoryId\":1,\"brandId\":1,\"price\":1299.99}";

        ProductDTO responseDto = new ProductDTO(
                null,
                "Laptop",
                1L,
                1L,
                new BigDecimal("1299.99"),
                1L
        );

        when(mockService.createProduct(any(CreateProductDTO.class), any(UserEntity.class)))
                .thenReturn(responseDto);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        ServletInputStream servletInputStream = new ServletInputStream() {
            private final ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());

            @Override
            public int read() {
                return inputStream.read();
            }

            @Override
            public boolean isFinished() {
                return inputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(jakarta.servlet.ReadListener readListener) {}
        };

        when(request.getInputStream()).thenReturn(servletInputStream);

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        assertThat(stringWriter.toString()).contains("Laptop");
    }
}