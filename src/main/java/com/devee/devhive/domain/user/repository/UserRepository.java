package com.devee.devhive.domain.user.repository;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.type.ProviderType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByNickName(String nickName);

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

  Optional<User> findByRefreshToken(String refreshToken);

  List<User> findAllByOrderByRankPointDesc();

  Optional<User> findByProviderTypeAndProviderId(ProviderType providerType, String providerId);
}
