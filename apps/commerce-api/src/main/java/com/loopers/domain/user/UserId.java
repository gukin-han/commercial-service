package com.loopers.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class UserId {

    @Column(name = "user_id")
    private Long value;

    public UserId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("User ID는 null이 아니며 0보다 커야 합니다.");
        }
        this.value = value;
    }

    public static UserId of(Long value) {
        return new UserId(value);
    }

}
