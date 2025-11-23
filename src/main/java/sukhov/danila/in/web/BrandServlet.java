package sukhov.danila.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sukhov.danila.config.DataSourceConfig;
import sukhov.danila.config.UserContext;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.domain.services.BrandService;
import sukhov.danila.dtos.BrandDTO;
import sukhov.danila.dtos.CreateBrandDTO;
import sukhov.danila.out.persistence.jdbc.BrandRepositoryImpl;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {
        BrandEndpoints.FETCH_BRANDS,
        BrandEndpoints.CREATE_BRAND
})
public class BrandServlet extends HttpServlet {
    protected BrandService brandService;
    protected ObjectMapper objectMapper;

    @Override
    public void init() {
        var dataSource = DataSourceConfig.getDataSource();
        var brandRepo = new BrandRepositoryImpl(dataSource);
        this.brandService = new BrandService(brandRepo);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            var brands = brandService.findAllBrands();
            objectMapper.writeValue(resp.getWriter(), brands);
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            CreateBrandDTO dto = objectMapper.readValue(req.getInputStream(), CreateBrandDTO.class);
            UserEntity currentUser = getCurrentUserOrThrow();
            BrandDTO saved = brandService.createBrand(dto.getName(), currentUser);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), saved);
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    protected UserEntity getCurrentUserOrThrow() {
        UserEntity user = UserContext.getCurrentUser();
        if (user == null) {
            throw new RuntimeException("Пользователь не авторизован");
        }
        return user;
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        try (PrintWriter writer = resp.getWriter()) {
            writer.write("{\"error\": \"" + message + "\"}");
        }
    }
}
