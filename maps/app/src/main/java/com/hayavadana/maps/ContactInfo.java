package com.hayavadana.maps;

public class ContactInfo {

    int  id;
    String contactName;
    double contactLatitude;
    double contactLongitude;



    public double getContactLatitude() {
        return contactLatitude;
    }

    public void setContactLatitude(double contactLatitude) {
        this.contactLatitude = contactLatitude;
    }

    public double getContactLongitude() {
        return contactLongitude;
    }

    public void setContactLongitude(double contactLongitude) {
        this.contactLongitude = contactLongitude;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
