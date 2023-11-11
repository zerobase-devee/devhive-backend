package com.devee.devhive.domain.project.type;

import java.util.Arrays;
import java.util.List;

public enum DevelopmentType {
  ALL,
  FRONTEND,
  BACKEND,
  FULLSTACK;

  public static List<DevelopmentType> getAllDevelopmentTypes() {
    return Arrays.asList(FRONTEND, BACKEND, FULLSTACK, ALL);
  }
}
