package com.loopers.application.user;

import com.loopers.application.user.dto.GetUserInfoQuery;
import com.loopers.application.user.dto.SignUpCommand;
import com.loopers.application.user.dto.SignUpResult;
import com.loopers.application.user.dto.UserInfoView;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import com.loopers.common.error.CoreException;
import com.loopers.common.error.ErrorType;
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
    public UserInfoView getUserInfo(GetUserInfoQuery query) {
        User me = userService.getMe(query.getLoginId());
        if (me == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        return UserInfoView.from(me);
    }
}
