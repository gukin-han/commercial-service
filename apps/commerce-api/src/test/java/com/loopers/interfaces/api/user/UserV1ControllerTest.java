package com.loopers.interfaces.api.user;

import com.loopers.domain.user.UserRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.support.constant.Gender;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserV1ControllerTest {

    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Autowired
    public UserV1ControllerTest(TestRestTemplate testRestTemplate, DatabaseCleanUp databaseCleanUp) {
        this.testRestTemplate = testRestTemplate;
      this.databaseCleanUp = databaseCleanUp;
    }

    /**
     * - [ ]  회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.
     * - [ ]  회원 가입 시에 성별이 없을 경우, `400 Bad Request` 응답을 반환한다.
     */
    @DisplayName("POST /api/v1/users")
    @Nested
    class register {
        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsUserInfo_whenUserSuccessfullySignUp() {
            //given
            UserV1Dto.RegisterRequest request = new UserV1Dto.RegisterRequest("gukin@gmail.com", "gukin", "2025-07-15", Gender.FEMALE);
            HttpHeaders headers = new HttpHeaders();
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};

            //when
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response = testRestTemplate.exchange(
                    "/api/v1/users",
                    HttpMethod.POST,
                    new HttpEntity<>(request, headers),
                    responseType);

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().email()).isEqualTo("gukin@gmail.com"),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo("gukin"),
                    () -> assertThat(response.getBody().data().dateOfBirth()).isEqualTo("2025-07-15")
            );
        }

        @DisplayName("회원 가입 시에 성별이 없을 경우, `400 Bad Request` 응답을 반환한다.")
        @Test
        void failsWithBadRequest_whenGenderIsMissing() {
            //given
            UserV1Dto.RegisterRequest request = new UserV1Dto.RegisterRequest("gukin@gmail.com", "gukin", "2025-07-15", null);
            HttpHeaders headers = new HttpHeaders();
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};

            //when
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response = testRestTemplate.exchange(
                    "/api/v1/users",
                    HttpMethod.POST,
                    new HttpEntity<>(request, headers),
                    responseType
            );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }
    }

    /**
     - [ ]  내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.
     - [ ]  존재하지 않는 ID 로 조회할 경우, `404 Not Found` 응답을 반환한다.
     */
    @DisplayName("GET /api/v1/users/me")
    @Nested
    class Get {
        @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsUserInfo_when() {
            //given
            // 먼저 회원 가입을 수행하여 유저를 생성합니다.
            UserV1Dto.RegisterRequest registerRequest = new UserV1Dto.RegisterRequest("gukin@gmail.com", "gukin", "2025-07-15", Gender.FEMALE);
            HttpHeaders registerHeaders = new HttpHeaders();
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> registerResponseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> registerResponse = testRestTemplate.exchange(
                "/api/v1/users",
                HttpMethod.POST,
                new HttpEntity<>(registerRequest, registerHeaders),
                registerResponseType);

            // 내 정보 조회를 위한 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", "gukin");
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};

            //when
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response = testRestTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                responseType
            );

            //then
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody().data().email()).isEqualTo("gukin@gmail.com"),
                () -> assertThat(response.getBody().data().userId()).isEqualTo("gukin"),
                () -> assertThat(response.getBody().data().dateOfBirth()).isEqualTo("2025-07-15")
            );
        }

        @DisplayName("존재하지 않는 ID 로 조회할 경우, `404 Not Found` 응답을 반환한다.")
        @Test
        void throwsNotFoundException_whenUserIdDoesNotExist() {
            //given
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", "notExisting");
            ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>> responseType = new ParameterizedTypeReference<>() {};

            //when
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response = testRestTemplate.exchange(
                    "/api/v1/users/me",
                    HttpMethod.GET,
                    new HttpEntity<>(null, headers),
                    responseType
            );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }
}
