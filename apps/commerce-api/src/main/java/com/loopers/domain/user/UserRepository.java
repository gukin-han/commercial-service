package com.loopers.domain.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByLoginId(String loginId);

    User save(User user);

    Optional<User> findByUserId(Long userId);

    List<User> saveAll(List<User> users);
}
