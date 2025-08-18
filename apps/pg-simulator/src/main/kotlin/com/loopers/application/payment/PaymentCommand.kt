package com.loopers.application.payment

import com.loopers.domain.payment.CardType
import com.loopers.common.error.CoreException
import com.loopers.common.error.ErrorType

object PaymentCommand {
    data class CreateTransaction(
        val userId: String,
        val orderId: String,
        val cardType: CardType,
        val cardNo: String,
        val amount: Long,
        val callbackUrl: String,
    ) {
        fun validate() {
            if (amount <= 0L) {
                throw CoreException(ErrorType.BAD_REQUEST, "요청 금액은 0 보다 큰 정수여야 합니다.")
            }
        }
    }
}
