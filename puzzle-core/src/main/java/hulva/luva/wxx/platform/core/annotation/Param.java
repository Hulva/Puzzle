package hulva.luva.wxx.platform.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import hulva.luva.wxx.platform.core.commons.FieldFormat;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Param {
    String value();

    String defaultValue() default "";

    boolean required() default true;

    // ==============
    String label() default "";

    FieldFormat format() default FieldFormat.text;

    String placeholder() default "";

    int sort() default 0;

    boolean readOnly() default false;// 该参数定义了是否将字段展示到界面中

    String source() default ""; // type 为Select 格式的JSON List数据 type为plugins时候, 该字段表示是否为多个字段
}
