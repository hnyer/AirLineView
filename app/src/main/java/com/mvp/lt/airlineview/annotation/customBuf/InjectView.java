package com.mvp.lt.airlineview.annotation.customBuf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/11/5/005
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InjectView {
    public int id() default -1;

}

