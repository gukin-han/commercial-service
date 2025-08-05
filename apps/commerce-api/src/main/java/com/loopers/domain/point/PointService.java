package com.loopers.domain.point;

import com.loopers.domain.product.Money;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserId;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    public Point getPointByUserId(String userId) {
        return pointRepository.findByLoginId(userId).orElse(null);
    }

    public void initializePoints(User user) {
        pointRepository.save(new Point(Money.ZERO, user.getLoginId()));
    }

    public Point findByUserId(UserId userId) {
        return pointRepository.findByUserId(userId.getValue()).orElseThrow(EntityNotFoundException::new);
    }

    public void deductPoint(Point point, Money amount) {
        point.deduct(amount);
    }

    public Point save(Point point) {
        return pointRepository.save(point);
    }
}
