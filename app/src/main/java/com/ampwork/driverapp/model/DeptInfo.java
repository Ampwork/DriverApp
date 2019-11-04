package com.ampwork.driverapp.model;

public class DeptInfo {

    private String name,hod,contact_number,hod_profile,description,amenities ;

    public DeptInfo(String name, String hod, String contact_number, String hod_profile, String description, String amenities) {
        this.name = name;
        this.hod = hod;
        this.contact_number = contact_number;
        this.hod_profile = hod_profile;
        this.description = description;
        this.amenities = amenities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHod() {
        return hod;
    }

    public void setHod(String hod) {
        this.hod = hod;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getHod_profile() {
        return hod_profile;
    }

    public void setHod_profile(String hod_profile) {
        this.hod_profile = hod_profile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }
}
