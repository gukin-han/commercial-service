package com.loopers.interfaces.api.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.loopers.interfaces.api.ApiHeader;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.point.PointV1Dto.PointResponse;
import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.support.constant.Gender;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PointV1ControllerTest {

    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Autowired
    public PointV1ControllerTest(TestRestTemplate testRestTemplate, DatabaseCleanUp databaseCleanUp) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
    }

    @DisplayName("GET /api/v1/points")
    @Nested
    class GetPointByUserId {

        @Test
        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
        void returnsPoint_whenGetPointSuccessfully() {
            //given
            UserV1Dto.SignUpRequest signUpRequest = createSignUpRequest();
            HttpHeaders registerHeaders = new HttpHeaders();
            testRestTemplate.exchange(
                    "/api/v1/users",
                    HttpMethod.POST,
                    new HttpEntity<>(signUpRequest, registerHeaders),
                    new ParameterizedTypeReference<>() {
                    }
            );

            HttpHeaders getPointHeaders = new HttpHeaders();
            getPointHeaders.set("X-USER-ID", "gukin");

            //when
            ResponseEntity<ApiResponse<PointResponse>> result = testRestTemplate.exchange(
                    "/api/v1/points",
                    HttpMethod.GET,
                    new HttpEntity<>(null, getPointHeaders),
                    new ParameterizedTypeReference<>() {}
            );

            //then
            assertAll(
                    () -> assertTrue(result.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(result.getBody().data().balance().getValue().compareTo(BigDecimal.ZERO)).isEqualTo(0) // 초기 포인트는 0으로 설정되어 있다고 가정합니다.
            );
        }

        @Test
        @DisplayName("`X-USER-ID` 헤더가 없을 경우, `400 Bad Request` 응답을 반환한다.")
        void returnsBadRequest_whenXUserIdHeaderIsMissing() {
            //given
            HttpHeaders headers = new HttpHeaders();
            ParameterizedTypeReference<ApiResponse<PointResponse>> responseType = new ParameterizedTypeReference<>() {
            };

            //when
            ResponseEntity<ApiResponse<PointResponse>> result = testRestTemplate.exchange(
                    "/api/v1/points",
                    HttpMethod.GET,
                    new HttpEntity<>(null, headers),
                    responseType);

            //then
            assertAll(
                    () -> assertTrue(result.getStatusCode().is4xxClientError()),
                    () -> assertThat(result.getStatusCodeValue()).isEqualTo(400)
            );
        }
    }


    /**
     * - [ ]  존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.
     * - [ ]  존재하지 않는 유저로 요청할 경우, `404 Not Found` 응답을 반환한다.
     */
    @DisplayName("POST /api/v1/points/charge")
    @Nested
    class Charge {

        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        void returnsChargedPoint_whenExistingUserChargePoint() {
            //given
            HttpHeaders headerWithUserId = new HttpHeaders();
            headerWithUserId.set(ApiHeader.LOGIN_ID, "gukin");

            // 1. 먼저 회원 가입을 수행하여 유저를 생성합니다.
            UserV1Dto.SignUpRequest signUpRequest = createSignUpRequest();
            testRestTemplate.exchange(
                    "/api/v1/users",
                    HttpMethod.POST,
                    new HttpEntity<>(signUpRequest, null),
                    new ParameterizedTypeReference<>() {
                    }
            );

            // 2. 이후 포인트 충전을 수행합니다.
            PointV1Dto.ChargeRequest chargeRequest = new PointV1Dto.ChargeRequest(1000L);

            //when
            ResponseEntity<ApiResponse<PointResponse>> response = testRestTemplate.exchange(
                    "/api/v1/points/charge",
                    HttpMethod.POST,
                    new HttpEntity<>(chargeRequest, headerWithUserId),
                    new ParameterizedTypeReference<>() {
                    }
            );

            //then
            Assertions.assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().balance().getValue().compareTo(BigDecimal.valueOf(1000L))).isEqualTo(0)
            );
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, `404 Not Found` 응답을 반환한다.")
        @Test
        void returnsNotFound_whenNonExistingUserChargePoint() {
            //given
            // 존재하지 않는 유저로 포인트 충전을 수행합니다.
            HttpHeaders headerWithUserId = new HttpHeaders();
            headerWithUserId.set(ApiHeader.LOGIN_ID, "gukin");
            PointV1Dto.ChargeRequest chargeRequest = new PointV1Dto.ChargeRequest(1000L); // 충전 후 보유 포인트가 1000으로 설정되어 있다고 가정합니다.

            //when
            ResponseEntity<ApiResponse<PointResponse>> response = testRestTemplate.exchange(
                    "/api/v1/points/charge",
                    HttpMethod.POST,
                    new HttpEntity<>(chargeRequest, headerWithUserId),
                    new ParameterizedTypeReference<>() {
                    }
            );

            //then
            Assertions.assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError())
            );
        }

    }

    private static UserV1Dto.SignUpRequest createSignUpRequest() {
        return UserV1Dto.SignUpRequest.builder()
                .loginId("gukin")
                .dateOfBirth("2025-07-15")
                .gender(Gender.FEMALE)
                .email("gukin@gmail.com")
                .build();
    }
}
