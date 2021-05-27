package com.saturn.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saturn.configuration.jwt.JwtProvider;
import com.saturn.models.authModels.AuthRequest;
import com.saturn.models.authModels.RegistrationRequest;
import com.saturn.repositories.UserRepository;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.json.*;
import javax.servlet.http.Cookie;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerUser() throws Exception {
        String testLogin = "huan";
        RegistrationRequest request = new RegistrationRequest(testLogin, testLogin + "@mail.ru", "1234");
        mvc.perform(MockMvcRequestBuilders.post("/register")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(mvcResult -> {
                    assertTrue(userRepository.existsByLogin(testLogin));
                });

        userRepository.delete(userRepository.findByLogin(testLogin));
    }

    @Test
    void auth() throws Exception {
        String testLogin = "anton";
        AuthRequest request = new AuthRequest(testLogin, "1234");
        mvc.perform(MockMvcRequestBuilders.post("/login")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(mvcResult -> {
                    assertNotNull(mvcResult.getResponse().getCookies());
                });
    }

    @Test
    void exit() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/exit"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is2xxSuccessful());
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}