package com.devee.devhive.domain.project.type;

import java.util.Arrays;
import java.util.List;

public enum RecruitmentType {
  ONLINE,
  OFFLINE,
  ALL;

  public static List<RecruitmentType> getAllRecruitmentTypes() {
    return Arrays.asList(ONLINE, OFFLINE, ALL);
  }
}
