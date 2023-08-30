package com.devee.devhive.domain.user.exithistory.repository.custom;

import java.util.List;

public interface CustomExitHistoryRepository {

  List<Long> getReactivatingUsers();
}