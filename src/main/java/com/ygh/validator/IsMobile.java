package com.ygh.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 自定义手机格式校验注解
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER}) //定义@IsMobile注解可以应用的Java元素类型。这里指定可以用于方法、字段、注解类型、构造函数和参数。
@Retention(RetentionPolicy.RUNTIME) //指定@IsMobile注解的保留策略。RetentionPolicy.RUNTIME 表示该注解在运行时仍然可用，这样可以通过反射访问。
@Documented //表示@IsMobile注解将包含在Javadoc中，便于文档生成
@Constraint( //定义约束注解
        validatedBy = {IsMobileValidator.class} //指定用于验证的类，这个类需要实现ConstraintValidator接口，负责实际的验证逻辑。
)//引进校验器
public @interface IsMobile {

    /**
     * 表示能否为空
     * */
    boolean required() default true; //默认true表示不能为空

    String message() default "手机号码格式错误"; //校验不通过输出信息

    Class<?>[] groups() default {}; //允许将注解分组，便于在验证时进行分组处理

    Class<? extends Payload>[] payload() default {}; //允许附加元数据到注解中，通常用于提供额外的上下文信息
}
