package com.ygh.vo;

import com.ygh.validator.IsMobile;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 手机号+密码
 */
@Setter
@Getter
public class LoginVO {

    @NotNull
    @IsMobile  //自定义校验手机格式注解
    private String mobile;

    @NotNull
    private String password;

    @Override
    public String toString() {
        return "LoginVO{" +
                "mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
