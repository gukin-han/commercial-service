package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.coupon.CouponType;
import com.loopers.domain.product.Money;
import com.loopers.domain.user.UserId;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CouponRepositoryImplIntegrationTest {

    private final DatabaseCleanUp databaseCleanUp;
    private final CouponRepository couponRepository;

    @Autowired
    public CouponRepositoryImplIntegrationTest(DatabaseCleanUp databaseCleanUp, CouponRepository couponRepository) {
        this.databaseCleanUp = databaseCleanUp;
        this.couponRepository = couponRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }


    @DisplayName("쿠폰 엔티티를 UserId로 조회시")
    @Nested
    class FindByUserId {

        @DisplayName("엔티티가 존재한다")
        @Test
        void isPresent() {
            //given
            Coupon coupon = Coupon.create(CouponType.FIXED_AMOUNT, UserId.of(1L), Money.of(1000L), null);
            Coupon savedCoupon = couponRepository.save(coupon);

            //when
            Optional<Coupon> optionalCoupon = couponRepository.findByIdAndCouponId(savedCoupon.getCouponId(), savedCoupon.getUserId());

            //then
            assertThat(optionalCoupon.isPresent()).isTrue();
            assertThat(optionalCoupon.get().getCouponId()).isEqualTo(savedCoupon.getCouponId());
        }
    }


}
