package com.saturn.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @deprecated
 */
@RestController
public class TestSecurityController {

    @GetMapping("/admin/get")
    public String getAdmin() {
        return "Hi admin";
    }

//    @GetMapping("/user/get")
//    public String getUser() {
//        return "Hi user";
//    }
}