package com.loopers.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.junit.Assert.assertThrows;

class PercentTest {

    @DisplayName("Percent 객체 생성시 : ")
    @Nested
    class Of {

        @DisplayName("NaN인 경우 IllegalArgumentException 을 던진다")
        @Test
        void throwsIllegalArgumentException_whenNaN() {
            assertThrows(IllegalArgumentException.class, () -> Percent.of(Double.NaN));
        }

        @DisplayName("유효하지 않은 값인 경우 IllegalArgumentException 을 던진다")
        @ParameterizedTest
        @ValueSource(doubles = {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, -0.1, 0.0, 1.0000001})
        void throwsIllegalArgumentException_onInvalidRanges(double v) {
            assertThrows(IllegalArgumentException.class, () -> Percent.of(v));
        }

        @ParameterizedTest
        @ValueSource(doubles = {1.0, 0.15, 0.5, 0.999999999})
        void createsSuccessfully_onValidRanges(double v) {
            // given
            Percent p = Percent.of(v);

            // when & then
            assertThat(p.getValue()).isCloseTo(v, within(1e-12));
        }
    }

}
