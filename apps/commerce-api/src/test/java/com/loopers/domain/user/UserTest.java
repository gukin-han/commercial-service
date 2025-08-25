package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    /**
     * - [x]  ID 가 `영문 및 숫자 10자 이내` 형식에 맞지 않으면, User 객체 생성에 실패한다.
     * - [x]  이메일이 `xx@yy.zz` 형식에 맞지 않으면, User 객체 생성에 실패한다.
     * - [x]  생년월일이 `yyyy-MM-dd` 형식에 맞지 않으면, User 객체 생성에 실패한다.
     */
    @DisplayName("유저 엔티티를 생성할 때, ")
    @Nested
    class Create {

        @DisplayName("ID 가 `영문 및 숫자 10자 이내` 형식에 맞지 않으면, BAD_REQUEST 예외를 던진다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "user id",    // 공백 포함
                "user-id",    // 하이픈 포함
                "gukingukin1" // 10자 초과
        })
        void throwsBadRequestException_whenInvalidUserIdProvided(String userId) {
            //given
            String email = "gukin@gamil.com";
            String dateOfBirth = "2024-01-01";

            //when
            CoreException result = assertThrows(CoreException.class, () ->
                    User.builder()
                            .userId(userId)
                            .email(email)
                            .dateOfBirth(dateOfBirth)
                            .build());

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("이메일이 `xx@yy.zz` 형식에 맞지 않으면, BAD_REQUEST 예외를 던진다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "",                                       // 빈 문자열
                "@example.com",                           // 로컬파트 없음
                "userexample.com",                        // '@' 없음
                "user@domain",                            // 최상위 도메인(TLD) 없음
                "user@domain.c",                          // 1‑글자 TLD (경계‑1)
        })
        void throwsBadRequestException_whenInvalidEmailProvided(String email) {
            //given
            String userId = "gukin";
            String dateOfBirth = "2024-01-01";

            //when
            CoreException result = assertThrows(CoreException.class, () ->
                    User.builder()
                            .userId(userId)
                            .email(email)
                            .dateOfBirth(dateOfBirth)
                            .build());

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @DisplayName("생년월일이 `yyyy-MM-dd` 형식에 맞지 않으면, BAD_REQUEST 예외를 던진다.")
        @ParameterizedTest
        @ValueSource(strings = {
                "",         // 빈 문자열
                "20240101", // 잘못된 형식 (연도-월-일)
                "2024/01/01", // 슬래시 사용
                "2024.01.01", // 점 사용
                "2024-1-1",   // 월과 일이 한 자리 숫자
        })
        void throwsBadRequestException_whenInvalidDateOfBirthProvided(String dateOfBirth) {
            //given
            String userId = "gukin";
            String email = "gukin@gmail.com";

            //when
            CoreException result = assertThrows(CoreException.class, () ->
                    User.builder()
                            .userId(userId)
                            .email(email)
                            .dateOfBirth(dateOfBirth)
                            .build());

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
