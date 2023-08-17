package com.devee.devhive.domain.auth.dto;

import lombok.Data;

@Data
public class LoginDTO {
  private String email;
  private String password;
}
