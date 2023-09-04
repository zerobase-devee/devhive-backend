package com.devee.devhive.domain.user.repository;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.type.ProviderType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByNickName(String nickName);

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

  Optional<User> findByRefreshToken(String refreshToken);

  Page<User> findAllByOrderByRankPointDesc(Pageable pageable);

  Optional<User> findByProviderTypeAndProviderId(ProviderType providerType, String providerId);
}
