package com.loopers.domain.user;

import com.loopers.support.constant.Gender;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserServiceIntegrationTest {

    private final UserService userService;
    private final UserRepository userRepository;
    private final DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Autowired
    public UserServiceIntegrationTest(UserService userService, UserRepository userRepository, DatabaseCleanUp databaseCleanUp) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.databaseCleanUp = databaseCleanUp;
    }

    /**
     * - [ ]  회원 가입시 User 저장이 수행된다. ( spy 검증 )
     * - [ ]  이미 가입된 ID 로 회원가입 시도 시, 실패한다.
     */
    @DisplayName("회원 가입 시")
    @Nested
    class signUp{

        @DisplayName("User 저장이 수행된다")
        @Test
        void savesUserEntity_whenSignUpSuccessfully(){
            //given
            UserEntity userEntity = UserEntity.builder()
                    .loginId("gukin")
                    .email("gukin@gmail.com")
                    .dateOfBirth("2025-07-15")
                    .gender(Gender.FEMALE)
                    .build();

            //when
            userService.signUp(userEntity);

            //then
            UserEntity savedUser = userRepository.findByLoginId("gukin").get();
            assertAll(
                    () -> assertThat(savedUser.getLoginId()).isEqualTo(userEntity.getLoginId()),
                    () -> assertThat(savedUser.getEmail()).isEqualTo(userEntity.getEmail()),
                    () -> assertThat(savedUser.getDateOfBirth()).isEqualTo(userEntity.getDateOfBirth()),
                    () -> assertThat(savedUser.getGender()).isEqualTo(userEntity.getGender()));
        }

        @DisplayName("이미 가입된 ID로 회원가입 시도 시, 실패한다")
        @Test
        void throwsBadRequestException_whenSignUpWithExistingUserId(){
            //given
            UserEntity userEntity = UserEntity.builder()
                    .loginId("gukin")
                    .email("gukin@gmail.com")
                    .dateOfBirth("2025-07-15")
                    .gender(Gender.FEMALE)
                    .build();
            userService.signUp(userEntity);

            //when
            CoreException result = assertThrows(CoreException.class, () -> userService.signUp(userEntity));

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }

    /**
     * - [ ]  해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.
     * - [ ]  해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.
     */
    @DisplayName("내 정보 조회 시")
    @Nested
    class getMe {
        @DisplayName("해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다")
        @Test
        void returnsUserEntity_whenUserExists() {
            //given
            UserEntity userEntity = UserEntity.builder()
                .loginId("gukin")
                .email("gukin@gmail.com")
                .dateOfBirth("2025-07-15")
                .gender(Gender.FEMALE)
                .build();
            userService.signUp(userEntity);

            //when
            UserEntity result = userService.getMe(userEntity.getLoginId());

            //then
            assertAll(
                () -> assertThat(result.getLoginId()).isEqualTo(userEntity.getLoginId()),
                () -> assertThat(result.getEmail()).isEqualTo(userEntity.getEmail()),
                () -> assertThat(result.getDateOfBirth()).isEqualTo(userEntity.getDateOfBirth()),
                () -> assertThat(result.getGender()).isEqualTo(userEntity.getGender()));
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다")
        @Test
        void returnsNull_whenUserDoesNotExist() {
            //given
            String nonExistentLoginId = "nonExistentUser";

            //when
            UserEntity result = userService.getMe(nonExistentLoginId);

            //then
            assertThat(result).isNull();
        }
    }
}
