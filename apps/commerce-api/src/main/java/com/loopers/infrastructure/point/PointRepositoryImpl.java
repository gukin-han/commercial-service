package com.loopers.infrastructure.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

  private final PointJpaRepository pointJpaRepository;

  @Override
  public Optional<Point> findByUserId(String userId) {
    return pointJpaRepository.findByUserId(userId);
  }

  @Override
  public Point save(Point point) {
    return pointJpaRepository.save(point);
  }
}
