package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiHeader;
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

import java.util.Objects;

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

    @DisplayName("POST /api/v1/users")
    @Nested
    class register {
        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsUserInfo_whenUserSuccessfullySignUp() {
            //given
            UserV1Dto.SignUpRequest request = UserV1Dto.SignUpRequest.builder()
                    .loginId("gukin")
                    .birthday("2025-07-15")
                    .gender(Gender.FEMALE)
                    .email("gukin@gmail.com")
                    .build();

            HttpHeaders headers = new HttpHeaders();

            //when
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response = testRestTemplate.exchange(
                    "/api/v1/users",
                    HttpMethod.POST,
                    new HttpEntity<>(request, headers),
                    new ParameterizedTypeReference<>() {});

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(Objects.requireNonNull(response.getBody()).data()).isNotNull(),
                    () -> assertThat(Objects.requireNonNull(response.getBody()).data().getEmail()).isEqualTo("gukin@gmail.com"),
                    () -> assertThat(Objects.requireNonNull(response.getBody()).data().getLoginId()).isEqualTo("gukin"),
                    () -> assertThat(Objects.requireNonNull(response.getBody()).data().getDateOfBirth()).isEqualTo("2025-07-15")
            );
        }

        @DisplayName("회원 가입 시에 성별이 없을 경우, `400 Bad Request` 응답을 반환한다.")
        @Test
        void returns400BadRequestResponse_whenGenderIsMissing() {
            //given
            UserV1Dto.SignUpRequest request = UserV1Dto.SignUpRequest.builder()
                    .loginId("gukin")
                    .birthday("2025-07-15")
                    .gender(null)
                    .email("gukin@gmail.com")
                    .build();

            HttpHeaders headers = new HttpHeaders();

            //when
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response = testRestTemplate.exchange(
                    "/api/v1/users",
                    HttpMethod.POST,
                    new HttpEntity<>(request, headers),
                    new ParameterizedTypeReference<>() {
                    }
            );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }
    }

    @DisplayName("GET /api/v1/users/me")
    @Nested
    class Get {
        @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsUserInfo_whenProcessedSuccessfully() {
            //given
            UserV1Dto.SignUpRequest request = UserV1Dto.SignUpRequest.builder()
                    .loginId("gukin")
                    .birthday("2025-07-15")
                    .gender(Gender.FEMALE)
                    .email("gukin@gmail.com")
                    .build();

            testRestTemplate.exchange(
                    "/api/v1/users",
                    HttpMethod.POST,
                    new HttpEntity<>(request, new HttpHeaders()),
                    new ParameterizedTypeReference<>() {
                    }
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set(ApiHeader.LOGIN_ID, "gukin");

            //when
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response = testRestTemplate.exchange(
                    "/api/v1/users/me",
                    HttpMethod.GET,
                    new HttpEntity<>(null, headers),
                    new ParameterizedTypeReference<>() {
                    }
            );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().getEmail()).isEqualTo("gukin@gmail.com"),
                    () -> assertThat(response.getBody().data().getLoginId()).isEqualTo("gukin"),
                    () -> assertThat(response.getBody().data().getDateOfBirth()).isEqualTo("2025-07-15")
            );
        }

        @DisplayName("존재하지 않는 ID 로 조회할 경우, `404 Not Found` 응답을 반환한다.")
        @Test
        void returns404NotFoundResponse_whenLoginIdNotExist() {
            //given
            HttpHeaders headers = new HttpHeaders();
            headers.set(ApiHeader.LOGIN_ID, "notExisting");

            //when
            ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> response = testRestTemplate.exchange(
                    "/api/v1/users/me",
                    HttpMethod.GET,
                    new HttpEntity<>(null, headers),
                    new ParameterizedTypeReference<>() {}
            );

            //then
            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }
}
