package com.ember.api.global.exception

class BusinessException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.message)
