package com.mvp.lt.airlineview.annotation;

/**
 * $activityName
 * 使用自定义注解：
 *
 * @author LiuTao
 * @date 2018/11/3/003
 */


public class Person {
    @Name("中国")
    private String name;

    @Gander(gender = Gander.GenderType.Male)
    private String gender;

    @Profile(id = 1001, hegiht = 170, nativePlace = "地球")
    private String profile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
