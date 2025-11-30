package sukhov.danila.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sukhov.danila.config.DataSourceConfig;
import sukhov.danila.config.UserContext;
import sukhov.danila.domain.services.AuthService;
import sukhov.danila.dtos.AuthRequestDTO;
import sukhov.danila.dtos.UserDTO;
import sukhov.danila.out.persistence.jdbc.UserRepositoryImpl;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {
        AuthEndpoints.REGISTER,
        AuthEndpoints.LOGIN
})
public class AuthServlet extends HttpServlet {
    protected AuthService authService;
    protected ObjectMapper objectMapper;

    @Override
    public void init() {
        var dataSource = DataSourceConfig.getDataSource();
        var userRepo = new UserRepositoryImpl(dataSource);
        this.authService = new AuthService(userRepo);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String path = req.getRequestURI();

        try {
            if (path.equals(AuthEndpoints.REGISTER)) {
                handleRegister(req, resp);
            } else if (path.equals(AuthEndpoints.LOGIN)) {
                handleLogin(req, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AuthRequestDTO dto = objectMapper.readValue(req.getInputStream(), AuthRequestDTO.class);
        UserDTO user = authService.register(dto.getUsername(), dto.getPassword(), dto.getRole());
        resp.setStatus(HttpServletResponse.SC_CREATED);
        objectMapper.writeValue(resp.getWriter(), user);
        UserContext.setCurrentUser(
                new sukhov.danila.domain.entities.UserEntity(
                        null, dto.getUsername(), "", dto.getRole()
                )
        );
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AuthRequestDTO dto = objectMapper.readValue(req.getInputStream(), AuthRequestDTO.class);
        UserDTO user = authService.login(dto.getUsername(), dto.getPassword());
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), user);
        UserContext.setCurrentUser(
                new sukhov.danila.domain.entities.UserEntity(
                        null, dto.getUsername(), "", user.getRole()
                )
        );
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write("{\"error\": \"" + message + "\"}");
        }
    }
}
