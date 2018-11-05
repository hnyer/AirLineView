package com.mvp.lt.airlineview.annotation.customBuf;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;

/**
 * $activityName
 * 手动写注解
 *
 * @author LiuTao
 * @date 2018/11/5/005
 */


public class InjectViewUtils {
    public static void parse(Object object) {
        try {
            injectView(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void injectView(Object object) throws Exception {
        Class clazz = object.getClass();
        View view = null;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(InjectView.class)) {
                InjectView injectView = field.getAnnotation(InjectView.class);
                int id = injectView.id();
                if (id < 0) {
                    throw new Exception("id must not be null");
                } else {
                    field.setAccessible(true);
                    if (object instanceof View) {
                        view = ((View) object).findViewById(id);
                    } else if (object instanceof Activity) {
                        view = ((Activity) object).findViewById(id);
                    }

                    field.set(object, view);
                }

            }

        }

    }


}
