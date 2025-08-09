package com.loopers.application.user.dto;

import com.loopers.domain.point.Point;
import com.loopers.domain.product.Money;
import com.loopers.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SignUpResult {

    private String email;
    private String loginId;
    private String dateOfBirth;
    private Money balance;

    public static SignUpResult create(User user, Point point) {
        return SignUpResult.builder()
                .email(user.getEmail())
                .loginId(user.getLoginId())
                .dateOfBirth(user.getDateOfBirth())
                .balance(point.getBalance())
                .build();
    }
}
