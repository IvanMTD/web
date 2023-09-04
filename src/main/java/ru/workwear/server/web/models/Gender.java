package ru.workwear.server.web.models;

public enum Gender{
    MALE("Male"),FEMALE("Female");

    private String displayName;

    Gender(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }
}
