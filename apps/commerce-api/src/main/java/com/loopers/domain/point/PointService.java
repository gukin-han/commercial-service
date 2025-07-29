package com.loopers.domain.point;

import com.loopers.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    public Point getPointByUserId(String userId) {
        return pointRepository.findByLoginId(userId).orElse(null);
    }

    public void initializePoints(User user) {
        pointRepository.save(new Point(0, user.getLoginId()));

    }

    public Point save(Point point) {
        return pointRepository.save(point);
    }
}
