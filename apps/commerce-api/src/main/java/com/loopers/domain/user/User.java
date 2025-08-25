package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import com.loopers.support.constant.Gender;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    private String userId;
    private String email;
    private String dateOfBirth;
    private Gender gender;

    @Builder
    public User(String userId, String email, String dateOfBirth, Gender gender) {
        if (userId == null || !userId.matches("^[a-zA-Z0-9]{1,10}$")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "아이디는 영문 및 숫자 10자 이내로 입력해야 합니다.");
        }

        if (email == null || !email.matches("^[\\w.%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일은 `xx@yy.zz` 형식으로 입력해야 합니다.");
        }

        if (dateOfBirth == null || !dateOfBirth.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일은 `yyyy-MM-dd` 형식으로 입력해야 합니다.");
        }

        if (gender == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "성별은 필수 입력 항목입니다.");
        }

        this.userId = userId;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }
}
