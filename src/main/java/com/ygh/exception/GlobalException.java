package com.ygh.exception;

import com.ygh.result.CodeMsg;
import lombok.Getter;

/**
 * 自定义全局异常类
 */
@Getter
public class GlobalException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

}
