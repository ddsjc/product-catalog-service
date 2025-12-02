package sukhov.danila.in.web;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sukhov.danila.domain.entities.UserEntity;
import sukhov.danila.domain.services.BrandService;
import sukhov.danila.domain.services.UserService;
import sukhov.danila.dtos.BrandDTO;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = TestWebConfig.class)
public class BrandControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private BrandService brandService;

    @Autowired
    private UserService userService;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void createBrandTest() throws Exception {

        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername("danila");

        when(userService.findUserEntityById(userId)).thenReturn(Optional.of(user));

        BrandDTO brandDTO = new BrandDTO(10L, "Nike", userId);
        when(brandService.createBrand("Nike", user)).thenReturn(brandDTO);

        mockMvc.perform(
                        post("/api/brands/{id}", userId)
                                .param("brand_name", "Nike")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Nike"))
                .andExpect(jsonPath("$.userOwnerId").value(1L));
    }
}
