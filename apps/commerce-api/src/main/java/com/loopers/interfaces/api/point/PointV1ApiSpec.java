package com.loopers.interfaces.api.point;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.point.PointV1Dto.PointResponse;

public interface PointV1ApiSpec {
  ApiResponse<PointV1Dto.PointResponse> charge(String userId, PointV1Dto.ChargeRequest request);

  ApiResponse<PointResponse> get(String userId);
}
