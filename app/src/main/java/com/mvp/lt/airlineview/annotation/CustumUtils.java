package com.mvp.lt.airlineview.annotation;

import java.lang.reflect.Field;

/**
 * $activityName
 * 自定义注解构造器
 *
 * @author LiuTao
 * @date 2018/11/3/003
 * <p>
 * AnnotatedElement 接口提供了以下四个方法来访问 Annotation 的信息：
 * <p>
 * 方法1：<T extends Annotation> T getAnnotation(Class<T> annotationClass):
 * 返回改程序元素上存在的、指定类型的注解，如果该类型注解不存在，则返回null。
 * <p>
 * 方法2：Annotation[] getAnnotations():
 * 返回该程序元素上存在的所有注解。
 * <p>
 * 方法3：boolean is AnnotationPresent(Class<?extends Annotation> annotationClass):
 * 判断该程序元素上是否包含指定类型的注解，存在则返回true，否则返回false.
 * <p>
 * 方法4：Annotation[] getDeclaredAnnotations()：返回直接存在于此元素上的所有注释。
 * 与此接口中的其他方法不同，该方法将忽略继承的注释。（如果没有注释直接存在于此元素上，则返回长度为零的一个数组。）
 * 该方法的调用者可以随意修改返回的数组；这不会对其他调用者返回的数组产生任何影响。
 */


public class CustumUtils {

    public static void inject(Object object) {
        try {
            getPersonInfo(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void getPersonInfo(Object object) throws IllegalAccessException {
        Class<?> clazz = object.getClass();
        String name = "";
        String gender = "";
        String profile = "";
        Field fields[] = clazz.getFields();
        for (Field field : fields) {

            if (field.isAnnotationPresent(Name.class)) {
                field.setAccessible(true);
                Name name1 = field.getAnnotation(Name.class);
                name = name + name1.value();

                field.set(object, name);

            }
        }
        for (Field field : fields) {
            if (field.isAnnotationPresent(Gander.class)) {
                field.setAccessible(true);
                Gander gender1 = field.getAnnotation(Gander.class);
                gender = gender + gender1.gender().toString();
                field.set(object, gender);
            }
        }
        for (Field field : fields) {
            if (field.isAnnotationPresent(Profile.class)) {
                field.setAccessible(true);
                Profile profile1 = field.getAnnotation(Profile.class);
                profile = profile +
                        "，地区：" + profile1.nativePlace() +
                        "，id：" + profile1.id() +
                        "，高度：" + profile1.hegiht();
                field.set(object, profile);
            }
        }

    }

}
