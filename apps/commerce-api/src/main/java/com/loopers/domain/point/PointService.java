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

    @Transactional
    public Point getPointByUserId(String userId) {
        return pointRepository.findByLoginId(userId).orElse(null);
    }

    @Transactional
    public Point initializePoints(User user) {
        return pointRepository.save(Point.create(user, Money.ZERO));
    }

    @Transactional
    public Point findByUserId(UserId userId) {
        return pointRepository.findByUserIdForUpdate(userId)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void deductPoints(UserId userId, Money amount) {
        Point point = findByUserId(userId);
        point.deduct(amount);
    }


    public Point save(Point point) {
        return pointRepository.save(point);
    }
}
