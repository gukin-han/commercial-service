package com.loopers.interfaces.api.point;


import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointCharge;

public class PointV1Dto {

    public record ChargeRequest(long amount) {

        public PointCharge toVo() {
            return new PointCharge(this.amount);
        }
    }

    public record PointResponse(String userId, long balance) {
        public static PointV1Dto.PointResponse fromEntity(Point point) {
            return new PointV1Dto.PointResponse(point.getLoginId(), point.getBalance());
        }
    }
}
