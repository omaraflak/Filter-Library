package me.aflak.filterlibrary;

import me.aflak.filter_annotation.Filterable;

/**
 * Created by root on 15/08/17.
 */

@Filterable
public class User {
    private int age;
    private Boolean male;
    private String firstName;
    private String lastName;
    private String birthCity;
    private int birthDay;
    private String birthMonth;
    private int birthYear;
    private Spec body;

    public User(int age, Boolean male, String firstName, String lastName, String birthCity, int birthDay, String birthMonth, int birthYear, Spec body) {
        this.age = age;
        this.male = male;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthCity = birthCity;
        this.birthDay = birthDay;
        this.birthMonth = birthMonth;
        this.birthYear = birthYear;
        this.body = body;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Boolean getMale() {
        return male;
    }

    public void setMale(Boolean male) {
        this.male = male;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthCity() {
        return birthCity;
    }

    public void setBirthCity(String birthCity) {
        this.birthCity = birthCity;
    }

    public int getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(int birthDay) {
        this.birthDay = birthDay;
    }

    public String getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(String birthMonth) {
        this.birthMonth = birthMonth;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public Spec getBody() {
        return body;
    }

    public void setBody(Spec body) {
        this.body = body;
    }
}