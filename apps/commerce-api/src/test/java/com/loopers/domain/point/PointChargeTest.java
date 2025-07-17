package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PointChargeTest {

  /**
   * - [ ] 0 이하의 정수로 포인트를 충전 시 실패한다.
   */
  @DisplayName("포인트 충전 생성할때")
  @Nested
  class Create {

    @DisplayName("0 이하의 정수로 포인트를 충전 시 실패한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void fails_whenAmountIsZeroOrLess(long amount) {
      //given
      String userId = "gukin";

      //when
      CoreException result = assertThrows(CoreException.class, () -> new PointCharge(amount));

      //then
      Assertions.assertAll(
          () -> assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST),
          () -> assertThat(result.getMessage()).isEqualTo("충전 금액은 0보다 커야 합니다.")
      );
    }
  }

}