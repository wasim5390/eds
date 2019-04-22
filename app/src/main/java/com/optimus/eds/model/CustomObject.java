package com.optimus.eds.model;

public class CustomObject {


    public CustomObject(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String toString(){
        return getText().toString();
    }

    Long id;
    String text;
}
