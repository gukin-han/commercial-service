package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.application.point.PointAppService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PointServiceIntegrationTest {

    private final PointAppService pointAppService;
    private final PointRepository pointRepository;
    private final DatabaseCleanUp databaseCleanUp;


    @Autowired
    public PointServiceIntegrationTest(PointAppService pointAppService, PointRepository pointRepository, DatabaseCleanUp databaseCleanUp) {
        this.pointAppService = pointAppService;
        this.pointRepository = pointRepository;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    /**
     * - [x]  해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.
     * - [x]  해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.
     */
    @DisplayName("userId로 포인트 조회 시")
    @Nested
    class GetPointsByUserId {

        @DisplayName("해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.")
        @Test
        void returnsPoints_whenUserExist() {
            //given
            String userId = "gukin";
            Point point = Point.builder()
                    .userId(userId)
                    .balance(1000L)
                    .build();
            pointRepository.save(point);

            //when
            Point result = pointAppService.getPointByUserId(userId);

            //then
            Assertions.assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getBalance()).isEqualTo(1000L)
            );
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        @Test
        void returnsNull_whenUserNotExist() {
            //given
            String userId = "gukin";

            //when
            Point result = pointAppService.getPointByUserId(userId);

            //then
            assertThat(result).isNull();
        }
    }

    /**
     * - [ ] 존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.
     */
    @DisplayName("포인트 충전시")
    @Nested
    class Charge {

        @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.")
        @Test
        void fails_whenUserIdDoesNotExist() {
            //given
            String userId = "gukin";
            PointCharge pointCharge = new PointCharge(1000L);// 충전 금액은 1000원으로 설정

            //when
            CoreException exception = Assertions.assertThrows(CoreException.class, () -> pointAppService.chargePoint(userId, pointCharge));

            //then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }
}
