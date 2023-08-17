package com.devee.devhive.domain.user.repository;

import com.devee.devhive.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByNickName(String nickName);

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);


  Optional<User> findByRefreshToken(String refreshToken);

  Optional<User> findByNickName(String nickname);
}
