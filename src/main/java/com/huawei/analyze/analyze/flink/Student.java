package com.huawei.analyze.analyze.flink;

/**
 * @Auther: likui
 * @Date: 2019/9/17 22:18
 * @Description:
 */
public class Student {

    private int age;
    private String username;

    public Student(int age, String username) {
        this.age = age;
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Student{" +
                "age=" + age +
                ", username='" + username + '\'' +
                '}';
    }
}
