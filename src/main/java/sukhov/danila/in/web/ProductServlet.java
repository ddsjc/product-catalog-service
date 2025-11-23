package sukhov.danila.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sukhov.danila.config.CurrentUserProvider;
import sukhov.danila.config.DataSourceConfig;
import sukhov.danila.config.UserContextProvider;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.domain.services.ProductService;
import sukhov.danila.dtos.CreateProductDTO;
import sukhov.danila.dtos.ProductDTO;
import sukhov.danila.dtos.UpdateProductDTO;
import sukhov.danila.out.persistence.jdbc.BrandRepositoryImpl;
import sukhov.danila.out.persistence.jdbc.CategoryRepositoryImpl;
import sukhov.danila.out.persistence.jdbc.ProductRepositoryImpl;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {
        ProductEndpoints.FETCH_PRODUCTS,
        ProductEndpoints.CREATE_PRODUCT,
        ProductEndpoints.GET_PRODUCT_BY_ID,
        ProductEndpoints.UPDATE_PRODUCT,
        ProductEndpoints.DELETE_PRODUCT
})
public class ProductServlet extends HttpServlet {
    protected ProductService productService;
    protected ObjectMapper objectMapper;
    protected CurrentUserProvider currentUserProvider = new UserContextProvider();

    @Override
    public void init() {
        var dataSource = DataSourceConfig.getDataSource();

        var productRepo = new ProductRepositoryImpl(dataSource);
        var brandRepo = new BrandRepositoryImpl(dataSource);
        var categoryRepo = new CategoryRepositoryImpl(dataSource);

        this.productService = new ProductService(productRepo, brandRepo, categoryRepo);
        this.objectMapper = new ObjectMapper();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String path = req.getRequestURI();

        try {
            if (path.equals(ProductEndpoints.FETCH_PRODUCTS)) {
                handleFetchProducts(resp);
            } else if (path.matches("/api/products/\\d+")) {
                handleGetProductById(path, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при получении товаров");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            CreateProductDTO dto = objectMapper.readValue(req.getInputStream(), CreateProductDTO.class);
            UserEntity currentUser = getCurrentUserOrThrow();
            ProductDTO saved = productService.createProduct(dto, currentUser);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), saved);
        } catch (InvalidFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Некорректный формат данных");
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            String path = req.getRequestURI();
            Long id = extractIdFromPath(path);
            UpdateProductDTO dto = objectMapper.readValue(req.getInputStream(), UpdateProductDTO.class);
            UserEntity currentUser = getCurrentUserOrThrow();
            ProductDTO updated = productService.updateProduct(id, dto, currentUser);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), updated);
        } catch (InvalidFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Некорректный формат данных");
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String path = req.getRequestURI();
            Long id = extractIdFromPath(path);
            UserEntity currentUser = getCurrentUserOrThrow();
            productService.deleteProduct(id, currentUser);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void handleFetchProducts(HttpServletResponse resp) throws IOException {
        var products = productService.findAll();
        objectMapper.writeValue(resp.getWriter(), products);
    }

    private void handleGetProductById(String path, HttpServletResponse resp) throws IOException {
        Long id = extractIdFromPath(path);
        ProductDTO product = productService.findById(id);
        objectMapper.writeValue(resp.getWriter(), product);
    }

    private Long extractIdFromPath(String path) {
        String[] parts = path.split("/");
        return Long.valueOf(parts[parts.length - 1]);
    }

    private UserEntity getCurrentUserOrThrow() {
        UserEntity user = currentUserProvider.getCurrentUser();
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
