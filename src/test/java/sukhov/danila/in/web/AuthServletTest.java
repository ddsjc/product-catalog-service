package sukhov.danila.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sukhov.danila.domain.services.AuthService;
import sukhov.danila.dtos.UserDTO;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AuthServletTest {

    private AuthServlet servlet;
    private AuthService mockService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockService = mock(AuthService.class);
        objectMapper = new ObjectMapper();

        servlet = new AuthServlet();
        servlet.authService = mockService;
        servlet.objectMapper = objectMapper;
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        String json = "{\"username\":\"test\",\"password\":\"pass\",\"role\":\"SELLER\"}";
        UserDTO responseDto = new UserDTO(1L, "test", "hash", "SELLER");

        when(mockService.register("test", "pass", "SELLER")).thenReturn(responseDto);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/auth/register");

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
        assertThat(writer.toString()).contains("test");
    }
}