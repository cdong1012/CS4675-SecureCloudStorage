package com.example.application.data.entity;

public class SecurityQuestion {
    private String address;

    private String favColor;

    private String favAnimal;

    public SecurityQuestion(String address, String favColor, String favAnimal) {
        this.address = address;
        this.favColor = favColor;
        this.favAnimal = favAnimal;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFavColor() {
        return favColor;
    }

    public void setFavColor(String favColor) {
        this.favColor = favColor;
    }

    public String getFavAnimal() {
        return favAnimal;
    }

    public void setFavAnimal(String favAnimal) {
        this.favAnimal = favAnimal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        SecurityQuestion question = (SecurityQuestion) o;
        return this.address.equals(question.getAddress()) && this.favAnimal.equals(question.getFavAnimal()) && this.favColor.equals(question.getFavColor());
    }
}
