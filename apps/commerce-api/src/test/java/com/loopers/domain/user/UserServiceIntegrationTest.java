package com.loopers.domain.user;

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
                    .userId("gukin")
                    .email("gukin@gmail.com")
                    .dateOfBirth("2025-07-15")
                    .build();

            //when
            userService.signUp(userEntity);

            //then
            UserEntity savedUser = userRepository.findByUserId("gukin").get();
            assertAll(
                    () -> assertThat(savedUser.getUserId()).isEqualTo(userEntity.getUserId()),
                    () -> assertThat(savedUser.getEmail()).isEqualTo(userEntity.getEmail()),
                    () -> assertThat(savedUser.getDateOfBirth()).isEqualTo(userEntity.getDateOfBirth()));
        }

        @DisplayName("이미 가입된 ID로 회원가입 시도 시, 실패한다")
        @Test
        void throwsBadRequestException_whenSignUpWithExistingUserId(){
            //given
            UserEntity userEntity = UserEntity.builder()
                    .userId("gukin")
                    .email("gukin@gmail.com")
                    .dateOfBirth("2025-07-15")
                    .build();
            userService.signUp(userEntity);

            //when
            CoreException result = assertThrows(CoreException.class, () -> userService.signUp(userEntity));

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
}
