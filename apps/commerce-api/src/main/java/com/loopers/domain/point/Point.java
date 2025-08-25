package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.user.UserId;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Table(name = "points")
@Entity
public class Point extends BaseEntity {

    private long balance;

    private String loginId;

    @Embedded
    private UserId userId;

    @Builder
    public Point(long balance, String loginId) {
        if (loginId == null || loginId.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "유저 아이디는 필수 입력 항목입니다.");
        }

        if (balance < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트 잔액은 0 이상이어야 합니다.");
        }

        this.balance = balance;
        this.loginId = loginId;
    }

    public void add(long amount) {
        if (amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "충전 금액은 0보다 커야 합니다.");
        }

        this.balance += amount;
    }

    public void deduct(Long amount) {
        long remaining = balance - amount;
        if (remaining < 0) {
            throw new IllegalStateException("포인트가 부족합니다 : " + Math.abs(remaining));
        }
        this.balance = balance - amount;
    }
}
