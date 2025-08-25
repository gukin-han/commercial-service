package com.loopers.common.error

class CoreException(
    val errorType: ErrorType,
    val customMessage: String? = null,
) : RuntimeException(customMessage ?: errorType.message)
