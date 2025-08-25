package com.loopers.domain.point;

import java.util.Optional;

public interface PointRepository {

    Optional<Point> findByLoginId(String loginId);

    Point save(Point point);

    Optional<Point> findByUserId(Long value);
}
