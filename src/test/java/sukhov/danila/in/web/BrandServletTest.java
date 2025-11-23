package sukhov.danila.in.web;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.domain.services.BrandService;
import sukhov.danila.dtos.BrandDTO;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class BrandServletTest {

    private BrandServlet servlet;
    private BrandService mockService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockService = mock(BrandService.class);
        objectMapper = new ObjectMapper();

        servlet = new BrandServlet() {
            @Override
            protected UserEntity getCurrentUserOrThrow() {
                return new UserEntity(1L, "test", "hash", "SELLER");
            }
        };
        servlet.brandService = mockService;
        servlet.objectMapper = objectMapper;
    }

    @Test
    void shouldCreateBrandSuccessfully() throws Exception {
        String json = "{\"name\":\"Apple\"}";
        BrandDTO responseDto = new BrandDTO(1L, "Apple", 1L);

        when(mockService.createBrand(eq("Apple"), any(UserEntity.class))).thenReturn(responseDto);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/brands");

        ServletInputStream inputStream = new ServletInputStream() {
            private final ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes());
            @Override public int read() { return bais.read(); }
            @Override public boolean isFinished() { return bais.available() == 0; }
            @Override public boolean isReady() { return true; }
            @Override public void setReadListener(jakarta.servlet.ReadListener readListener) {}
        };
        when(request.getInputStream()).thenReturn(inputStream);

        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        assertThat(writer.toString()).contains("Apple");
    }
}