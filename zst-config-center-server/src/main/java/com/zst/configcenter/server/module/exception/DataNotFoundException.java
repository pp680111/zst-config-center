package com.zst.configcenter.server.module.exception;

/**
 * 表示对应数据不存在的异常
 */
public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException() {
    }

    public DataNotFoundException(String message) {
        super(message);
    }
}
