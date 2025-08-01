package com.loopers.domain.product;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {

    private Long value;

    public Money (long value){
        if (value < 0) {
            throw new IllegalArgumentException("금액은 음수일 수 없습니다.");
        }
    }
}
