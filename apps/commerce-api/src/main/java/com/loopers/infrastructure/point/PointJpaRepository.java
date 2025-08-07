package com.loopers.infrastructure.point;

import com.loopers.domain.point.Point;

import java.util.Optional;

import com.loopers.domain.user.UserId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface PointJpaRepository extends JpaRepository<Point, Long> {

    Optional<Point> findByLoginId(String loginId);

    Optional<Point> findByUserId(UserId userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Point p where p.userId = :userId")
    Optional<Point> findByUserIdForUpdate(UserId userId);
}
