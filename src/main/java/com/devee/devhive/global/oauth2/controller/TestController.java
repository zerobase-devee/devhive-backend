package com.devee.devhive.global.oauth2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

  @GetMapping("/api/auth/hello")
  public String hello() {
    return "Hello!!!!!!!!!!!";
  }

}
