package com.loopers.application.user;

import com.loopers.application.user.dto.SignUpCommand;
import com.loopers.application.user.dto.SignUpResult;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class UserFacade {

    private final UserService userService;
    private final PointService pointService;

    public SignUpResult signUp(SignUpCommand command) {
        User signedUpUser = userService.signUp(command.toEntity());
        Point point = pointService.initializePoints(signedUpUser);

        return SignUpResult.create(signedUpUser, point);
    }

    @Transactional(readOnly = true)
    public User getUserInfo(String userId) {
        User me = userService.getMe(userId);
        if (me == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        return me;
    }
}
