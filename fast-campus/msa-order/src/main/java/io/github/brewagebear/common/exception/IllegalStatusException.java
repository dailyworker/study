package io.github.brewagebear.common.exception;

import io.github.brewagebear.common.response.ErrorCode;

public class IllegalStatusException extends BaseException {
    public IllegalStatusException() {
        super(ErrorCode.COMMON_ILLEGAL_STATUS);
    }

    public IllegalStatusException(String message) {
        super(message, ErrorCode.COMMON_ILLEGAL_STATUS);
    }
}
