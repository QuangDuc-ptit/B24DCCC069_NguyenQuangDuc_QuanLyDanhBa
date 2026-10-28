package com.example.contactmanager.model;

import java.io.Serializable;

public class Contact implements Serializable {
    private String name;
    private String phone;
    private String group;
    private String gender;
    private String note;
    private boolean favorite;

    public Contact(String name, String phone, String group, String gender, String note, boolean favorite) {
        this.name = name;
        this.phone = phone;
        this.group = group;
        this.gender = gender;
        this.note = note;
        this.favorite = favorite;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}
