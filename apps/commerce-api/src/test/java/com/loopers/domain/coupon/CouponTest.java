package com.loopers.domain.coupon;

import com.loopers.domain.product.Money;
import com.loopers.domain.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    @DisplayName("쿠폰 생성시")
    @Nested
    class create {

        @Test
        @DisplayName("정상적으로 정액 쿠폰을 생성한다.")
        void createFixedAmountCoupon() {
            // given
            CouponType type = CouponType.FIXED_AMOUNT;
            UserId userId = UserId.of(1L);
            Money amount = Money.of(1000L);

            // when
            Coupon coupon = Coupon.create(type, userId, amount, null);

            // then
            assertNotNull(coupon);
            assertEquals(type, coupon.getType());
            assertEquals(userId, coupon.getUserId());
            assertEquals(amount, coupon.getAmount());
            assertNull(coupon.getPercent());
        }

        @Test
        @DisplayName("정상적으로 정률 쿠폰을 생성한다")
        void createPercentageCoupon_withZeroOrNegativePercent() {
            // given
            CouponType type = CouponType.PERCENTAGE;
            UserId userId = UserId.of(1L);
            Double percent = 10d;

            // when
            Coupon coupon = Coupon.create(type, userId, null, percent);

            // then
            assertNotNull(coupon);
            assertEquals(type, coupon.getType());
            assertEquals(userId, coupon.getUserId());
            assertNull(coupon.getAmount());
            assertEquals(percent, coupon.getPercent());
        }

        @Test
        @DisplayName("쿠폰 타입이 null이면 예외가 발생한다.")
        void throwsException_whenTypeIsNull() {
            // given
            UserId userId = UserId.of(1L);
            CouponType type = null;

            // when
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> Coupon.create(null, userId, null, null));

            // then
            assertEquals("쿠폰 타입과 유저아이디는 필수 입력값입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("유저 아이디가 null이면 예외가 발생한다.")
        void throwsException_whenUserIdIsNull() {
            // given
            CouponType type = CouponType.FIXED_AMOUNT;
            Money amount = Money.of(1000L);
            UserId userId = null;


            // when
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> Coupon.create(type, userId, amount, null));

            // then
            assertEquals("쿠폰 타입과 유저아이디는 필수 입력값입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("정액 쿠폰의 할인금액이 null이면 예외가 발생한다.")
        void throwsException_whenFixedAmountCouponAmountIsNull() {
            // given
            CouponType type = CouponType.FIXED_AMOUNT;
            UserId userId = UserId.of(1L);

            // when
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> Coupon.create(type, userId, null, null));

            // then
            assertEquals("정액 쿠폰은 할인금액이 필수 입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("정률 쿠폰의 할인율이 null이면 예외가 발생한다.")
        void throwsException_whenPercentageCouponPercentIsNull() {
            // given
            CouponType type = CouponType.PERCENTAGE;
            UserId userId = UserId.of(1L);

            // when
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> Coupon.create(type, userId, null, null));

            // then
            assertEquals("정률 쿠폰은 할인율이 필수 입니다..", exception.getMessage());
        }

        @Test
        @DisplayName("정률 쿠폰의 할인율이 0 이하 음수이면 예외가 발생한다.")
        void throwsException_whenPercentageCouponPercentIsLessThanAndEqualsZero() {
            // given
            CouponType type = CouponType.PERCENTAGE;
            UserId userId = UserId.of(1L);
            Double percent = -10.0;

            // when
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> Coupon.create(type, userId, null, percent));

            // then
            assertEquals("정률 쿠폰은 할인율이 필수 입니다..", exception.getMessage());
        }

        @Test
        @DisplayName("정액 쿠폰의 할인금액이 0 이하 음수이면 예외가 발생한다.")
        void throwsException_whenFixedAmountCouponAmountIsLessThanAndEqualsZero() {
            // given
            CouponType type = CouponType.FIXED_AMOUNT;
            UserId userId = UserId.of(1L);
            long amount = -10;

            // when
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> Coupon.create(type, userId, Money.of(amount), null));

            // then
            assertEquals("금액은 음수일 수 없습니다.", exception.getMessage());
        }
    }
}
