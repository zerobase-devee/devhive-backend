package com.devee.devhive.global.oauth2.controller;

import com.devee.devhive.global.oauth2.domain.dto.SessionUserDto;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TestController {

  private final HttpSession httpSession;

  @GetMapping("/api/auth/hello")
  public String hello() {
    return "Hello!!!!!!!!!!!";
  }

  @GetMapping("/test")
  public Map<String, Object> test(Model model, Authentication authentication) {

    SessionUserDto userDto = (SessionUserDto) httpSession.getAttribute("user");
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

    if(userDto != null) {
      model.addAttribute("userName", userDto.getName());
    }

    return oAuth2User.getAttributes();
  }

}
