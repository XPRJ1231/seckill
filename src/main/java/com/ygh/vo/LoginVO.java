package com.ygh.vo;

import com.ygh.validator.IsMobile;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 *
 */
@Setter
@Getter
public class LoginVO {

    @NotNull
    @IsMobile  //因为框架没有校验手机格式注解，所以自己定义
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
