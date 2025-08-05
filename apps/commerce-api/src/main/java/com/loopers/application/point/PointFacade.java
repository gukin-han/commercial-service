package com.loopers.application.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointCharge;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.Money;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class PointFacade {

    private final PointService pointService;
    private final PointRepository pointRepository;

    @Transactional(readOnly = true)
    public Point getPointByUserId(String userId) {
        return pointService.getPointByUserId(userId);
    }

    public Point chargePoint(String userId, Money amount) {

        // TODO 유저를 찾고, 포인트 내역을 찾아야 할지 검토
        Point point = pointService.getPointByUserId(userId);
        if (point == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다. 먼저 회원가입을 해주세요.");
        }

        point.add(amount);
        return pointRepository.save(point);
    }

}
