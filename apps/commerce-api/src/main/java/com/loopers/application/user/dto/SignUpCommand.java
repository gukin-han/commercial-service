package com.loopers.application.user.dto;

import com.loopers.domain.user.User;
import com.loopers.support.constant.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SignUpCommand {

    private String email;
    private String loginId;
    private String dateOfBirth;
    private Gender gender;

    public User toEntity() {
        return User.builder()
                .email(this.email)
                .loginId(this.loginId)
                .dateOfBirth(this.dateOfBirth)
                .gender(this.gender)
                .build();
    }
}
