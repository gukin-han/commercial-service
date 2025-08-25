package com.loopers.mock.jpa;

import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;

import java.util.Optional;


public class FakeUserRepository extends FakeJpaRepository<User> implements UserRepository {

    @Override
    public Optional<User> findByLoginId(String loginId) {
        return data.stream()
                .filter(user -> user.getLoginId().equals(loginId))
                .findFirst();
    }

    @Override
    public Optional<User> findByUserId(Long userId) {
        return data.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();
    }

    @Override
    protected Long getId(User entity) {
        return entity.getId();
    }
}
