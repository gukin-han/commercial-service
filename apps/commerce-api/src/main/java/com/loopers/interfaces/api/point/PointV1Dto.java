package com.loopers.interfaces.api.point;


import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointCharge;
import com.loopers.domain.product.Money;

public class PointV1Dto {

    public record ChargeRequest(long amount) {

        public PointCharge toVo() {
            return new PointCharge(Money.of(this.amount));
        }
    }

    public record PointResponse(String userId, Money balance) {
        public static PointV1Dto.PointResponse fromEntity(Point point) {
            return new PointV1Dto.PointResponse(point.getLoginId(), point.getBalance());
        }
    }
}
