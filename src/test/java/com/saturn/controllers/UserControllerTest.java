package com.saturn.controllers;

import com.saturn.configuration.jwt.JwtProvider;
import com.saturn.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.json.*;
import javax.servlet.http.Cookie;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAll() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(mvcResult -> {
                    String body = mvcResult.getResponse().getContentAsString();
                    JSONObject obj = new JSONObject(body);
                    System.out.println(body);
                    JSONArray arr = obj.getJSONArray("content");
                    assertEquals(userRepository.findAll().size(), arr.length());
                })
                .andReturn();
    }

    @Test
    void findMe() throws Exception {
        String testLogin = "anton";
        String token = jwtProvider.generateToken(testLogin);
        this.mvc.perform(MockMvcRequestBuilders.get("/users/profile")
                .cookie(new Cookie("auth", token)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(mvcResult -> {
                    String body = mvcResult.getResponse().getContentAsString();
                    System.out.println(body);
                    JSONObject obj = new JSONObject(body);
                    assertEquals(obj.getString("login"), testLogin);
                });
    }

    @Test
    void findByLogin() throws Exception {
        String testLogin = "anton";
        this.mvc.perform(MockMvcRequestBuilders.get("/users/" + testLogin))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(mvcResult -> {
                    String body = mvcResult.getResponse().getContentAsString();
                    JSONObject obj = new JSONObject(body);
                    assertEquals(testLogin, obj.getString("login"));
                });
    }
}