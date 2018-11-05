package com.mvp.lt.airlineview.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * $activityName
 *
 * @author LiuTao
 * @date 2018/11/3/003
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Gander {
    public enum GenderType {
        Male("男"), Female("女"), Other("未知");
        private String genderStr;

        GenderType(String args) {
            this.genderStr = args;
        }

        @Override
        public String toString() {
            return genderStr;
        }
    }

    GenderType gender() default GenderType.Male;
}
