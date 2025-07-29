package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User signUp(User user) {
        // 유저 정보 검증
        userRepository.findByLoginId(user.getLoginId())
                .ifPresent(existingUser -> {
                    throw new CoreException(ErrorType.BAD_REQUEST, "이미 가입된 ID 입니다.");
                });

        // 유저 저장
        return userRepository.save(user);
    }

    public User getMe(String loginId) {
        return userRepository.findByLoginId(loginId).orElse(null);
    }
}
