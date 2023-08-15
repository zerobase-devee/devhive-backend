package com.devee.devhive.global.oauth2.controller;

import com.devee.devhive.global.oauth2.domain.dto.SessionUserDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TestController {

  private final HttpSession httpSession;

  @GetMapping("/index")
  public SessionUserDto test(Model model) {

    SessionUserDto userDto = (SessionUserDto) httpSession.getAttribute("user");

    if(userDto != null) {
      model.addAttribute("userName", userDto.getName());
    }

    return userDto;
  }

}
