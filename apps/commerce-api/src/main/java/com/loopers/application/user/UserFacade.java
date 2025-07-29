package com.loopers.application.user;

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

    public User signUp(User user) {
        User signedUpUser = userService.signUp(user);

        pointService.initializePoints(signedUpUser);

        return signedUpUser;
    }

    @Transactional(readOnly = true)
    public User getMe(String userId) {
        User me = userService.getMe(userId);
        if (me == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        return me;
    }
}
