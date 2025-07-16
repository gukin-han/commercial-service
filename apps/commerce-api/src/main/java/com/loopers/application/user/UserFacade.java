package com.loopers.application.user;

import com.loopers.domain.user.UserEntity;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    public UserEntity signUp(UserEntity userEntity) {
        return userService.signUp(userEntity);
    }

    public UserEntity getMe(String loginId) {
        UserEntity me = userService.getMe(loginId);
        if (me == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        return me;
    }
}
