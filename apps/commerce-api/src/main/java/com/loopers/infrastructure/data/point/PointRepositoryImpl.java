package com.loopers.infrastructure.data.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;

import java.util.Optional;

import com.loopers.domain.user.UserId;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Override
    public Optional<Point> findByLoginId(String loginId) {
        return pointJpaRepository.findByLoginId(loginId);
    }

    @Override
    public Point save(Point point) {
        return pointJpaRepository.save(point);
    }


    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Optional<Point> findByUserIdForUpdate(UserId userId) {
        return pointJpaRepository.findByUserIdForUpdate(userId);
    }

    @Override
    public Optional<Point> findByUserId(UserId userId) {
        return pointJpaRepository.findByUserId(userId);
    }
}
