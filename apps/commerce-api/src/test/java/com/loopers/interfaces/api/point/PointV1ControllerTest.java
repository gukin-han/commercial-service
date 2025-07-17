package com.loopers.interfaces.api.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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

    /**
     * - [ ]  포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다. - [ ]  `X-USER-ID` 헤더가 없을 경우, `400 Bad Request` 응답을 반환한다.
     */
    @DisplayName("GET /api/v1/points")
    @Nested
    class GetPointByUserId {

        @Test
        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
        void returnsPoint_whenGetPointSuccessfully() {
            //given
            // 1. 먼저 회원 가입을 수행하여 유저를 생성합니다.
            UserV1Dto.RegisterRequest registerRequest = new UserV1Dto.RegisterRequest("gukin@gmail.com", "gukin", "2025-07-15", Gender.FEMALE);
            HttpHeaders registerHeaders = new HttpHeaders();
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> registerResponseType = new ParameterizedTypeReference<>() {
            };
            testRestTemplate.exchange(
                    "/api/v1/users", HttpMethod.POST, new HttpEntity<>(registerRequest, registerHeaders), registerResponseType);

            // 2. 이후 포인트 조회를 수행합니다.
            HttpHeaders getPointHeaders = new HttpHeaders();
            getPointHeaders.set("X-USER-ID", "gukin");
            ParameterizedTypeReference<ApiResponse<PointResponse>> responseType = new ParameterizedTypeReference<>() {
            };

            //when
            ResponseEntity<ApiResponse<PointResponse>> result = testRestTemplate.exchange(
                    "/api/v1/points",
                    HttpMethod.GET,
                    new HttpEntity<>(null, getPointHeaders),
                    responseType);

            //then
            assertAll(
                    () -> assertTrue(result.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(result.getBody().data().balance()).isEqualTo(0L) // 초기 포인트는 0으로 설정되어 있다고 가정합니다.
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
            headerWithUserId.set("X-USER-ID", "gukin");

            // 1. 먼저 회원 가입을 수행하여 유저를 생성합니다.
            UserV1Dto.RegisterRequest registerRequest = new UserV1Dto.RegisterRequest("gukin@gmail.com", "gukin", "2025-07-15", Gender.FEMALE);
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> registerResponseType = new ParameterizedTypeReference<>() {
            };
            testRestTemplate.exchange(
                    "/api/v1/users",
                    HttpMethod.POST,
                    new HttpEntity<>(registerRequest, null),
                    registerResponseType);

            // 2. 이후 포인트 충전을 수행합니다.
            PointV1Dto.ChargeRequest chargeRequest = new PointV1Dto.ChargeRequest(1000L); // 충전 후 보유 포인트가 1000으로 설정되어 있다고 가정합니다.
            ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResponse>> chargeResponseType = new ParameterizedTypeReference<>() {
            };

            //when
            ResponseEntity<ApiResponse<PointResponse>> response = testRestTemplate.exchange(
                    "/api/v1/points/charge",
                    HttpMethod.POST,
                    new HttpEntity<>(chargeRequest, headerWithUserId),
                    chargeResponseType);

            //then
            Assertions.assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().balance()).isEqualTo(1000L) // 충전 후 보유 포인트가 1000으로 설정되어 있다고 가정합니다.
            );
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, `404 Not Found` 응답을 반환한다.")
        @Test
        void returnsNotFound_whenNonExistingUserChargePoint() {
            //given
            // 존재하지 않는 유저로 포인트 충전을 수행합니다.
            HttpHeaders headerWithUserId = new HttpHeaders();
            headerWithUserId.set("X-USER-ID", "gukin");
            PointV1Dto.ChargeRequest chargeRequest = new PointV1Dto.ChargeRequest(1000L); // 충전 후 보유 포인트가 1000으로 설정되어 있다고 가정합니다.
            ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResponse>> chargeResponseType = new ParameterizedTypeReference<>() {
            };

            //when
            ResponseEntity<ApiResponse<PointResponse>> response = testRestTemplate.exchange(
                    "/api/v1/points/charge",
                    HttpMethod.POST,
                    new HttpEntity<>(chargeRequest, headerWithUserId),
                    chargeResponseType);

            //then
            Assertions.assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError())
            );
        }

    }
}
