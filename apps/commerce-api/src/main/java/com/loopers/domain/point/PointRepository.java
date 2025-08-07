package com.loopers.domain.point;

import com.loopers.domain.user.UserId;

import java.util.Optional;

public interface PointRepository {

    Optional<Point> findByLoginId(String loginId);

    Point save(Point point);

    Optional<Point> findByUserIdForUpdate(UserId userId);

    Optional<Point> findByUserId(UserId userId);
}
