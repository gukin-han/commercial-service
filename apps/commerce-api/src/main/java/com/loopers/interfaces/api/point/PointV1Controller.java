package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointFacade;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointCharge;
import com.loopers.interfaces.api.ApiHeader;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.point.PointV1Dto.PointResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
public class PointV1Controller implements PointV1ApiSpec {

    private final PointFacade pointFacade;

    @Override
    @GetMapping
    public ApiResponse<PointResponse> get(
            @RequestHeader(ApiHeader.LOGIN_ID) String loginId
    ) {
        if (loginId == null || loginId.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST);
        }

        Point point = pointFacade.getPointByUserId(loginId);
        PointV1Dto.PointResponse response = PointResponse.fromEntity(point);
        return ApiResponse.success(response);
    }

    @Override
    @PostMapping("/charge")
    public ApiResponse<PointV1Dto.PointResponse> charge(
            @RequestHeader(ApiHeader.LOGIN_ID) String userId,
            @RequestBody PointV1Dto.ChargeRequest request
    ) {
        PointCharge pointCharge = request.toVo();
        Point chargedPoint = pointFacade.chargePoint(userId, pointCharge.getAmount());

        return ApiResponse.success(PointV1Dto.PointResponse.fromEntity(chargedPoint));
    }
}
