package io.github.brewagebear.common.exception;

import io.github.brewagebear.common.response.ErrorCode;

public class EntityNotFoundException extends BaseException {
    public EntityNotFoundException() {
        super(ErrorCode.COMMON_INVALID_PARAMETER);
    }

    public EntityNotFoundException(String message) {
        super(message, ErrorCode.COMMON_INVALID_PARAMETER);
    }
}
